package com.adms.batch.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.Customer;
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.PolicyStatus;
import com.adms.domain.entities.ProductType;
import com.adms.domain.entities.QaStatus;
import com.adms.domain.entities.SalesRecord;
import com.adms.domain.entities.TsrCodeReplacer;
import com.adms.domain.entities.TsrHierarchical;
import com.adms.domain.entities.TsrInfo;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class SalesByRecord implements IExcelData {
	
	protected List<Exception> exs = new ArrayList<Exception>();
	
	private static KpiService kpiService() throws Exception {
		return KpiService.getInstance();
	}
	
	public void importFromInputStream(File file, List<Exception> exceptionList) throws Exception {
		
	}
	
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws IOException {
		System.out.println("SalesByRecord");
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.SALES_BY_RECORD.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			process(wbHolder, sheetNames.get(0));
			
		} catch (Exception e) {
			exceptionList.add(e);
			e.printStackTrace();
		} finally {
			fileFormat.close();
			is.close();
		}
		exceptionList.addAll(exs);
	}
	
	private void checkReplaceTsrCode(String tsrNameOnSale, TsrInfo tsrFromCode, String keyCode) {
		try {
			
			String a = kpiService().removeTitle(tsrFromCode.getFullName()).replaceAll(" ", "");
//			a = a.length() <= 6 ? a : a.substring(a.length() / 2 - 3, a.length() / 2 + 3);
			
			String b = kpiService().removeTitle(tsrNameOnSale).replaceAll(" ", "");
//			b = b.length() <= 6 ? b : b.substring(b.length() / 2 - 3, b.length() / 2 + 3);
			if(!a.equalsIgnoreCase(b)) {
				List<TsrCodeReplacer> list = kpiService().getTsrCodeReplacer(tsrFromCode.getTsrCode(), tsrNameOnSale, tsrFromCode.getFullName(), keyCode);
				if(null == list || list.isEmpty()) {
					kpiService().addTsrCodeReplacer(tsrFromCode.getTsrCode(), tsrNameOnSale, tsrFromCode.getFullName(), keyCode);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			exs.add(e);
		}
	}
	
	private void checkSup(TsrInfo tsrInfo, String tmrCode, CampaignKeyCode keyCode, Date saleDate) {
		try {
			if(!StringUtils.isBlank(tmrCode) && null != keyCode) {

//				<!-- check upline campaign -->
				TsrHierarchical h = kpiService().getTsrHierarchical(tsrInfo.getTsrCode(), null, keyCode.getKeyCode(), null);
				if(null == h) {
					kpiService().addTsrHierarchical(tsrInfo, kpiService().getTsrInfoInMap(tmrCode), keyCode, saleDate, null);
				} else if(!h.getUplineInfo().getTsrCode().equals(tmrCode)) {
					h.setUplineInfo(kpiService().getTsrInfoInMap(tmrCode));
					kpiService().updateTsrHierarchical(h);
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
			exs.add(e);
		}
	}
	
	protected void process(DataHolder wbHolder, String sheetName) throws Exception {
		DataHolder sheetHolder = wbHolder.get(sheetName);
		
		List<DataHolder> dataList = sheetHolder.getDataList("salesByRecordList");
		
		System.out.println("Sales By Rec size: " + dataList.size());
		
		for(DataHolder data : dataList) {
			try {
				TsrInfo tsrFromCode = null;
				String tsrCode = data.get("tsrCode").getStringValue();
				String tsrNameFromFile = kpiService().removeTitle(data.get("tsrName").getStringValue()).replaceAll("  ", " ");
				
//				<!-- Getting TSR_INFO -->
				if(StringUtils.isBlank(tsrCode)) {
					tsrFromCode = kpiService().getTsrInfoByName(tsrNameFromFile.trim());
				} else {
					tsrFromCode = kpiService().getTsrInfoInMap(tsrCode);
					if(null == tsrFromCode) {
						tsrFromCode = kpiService().getTsrInfoByName(tsrNameFromFile.trim());
					}
				}
				
//				<!-- Check if null, throw Exception() -->
				if(null == tsrFromCode) throw new Exception("Not Found TSR: " + "code |" + data.get("tsrCode").getStringValue() + "|");
				
//				<!-- Check is Replace TSR CODE -->
				String listLotName = data.get("listLotName").getStringValue();
				listLotName = kpiService().getKeyCodeFromCampaignListLot(listLotName);
				checkReplaceTsrCode(tsrNameFromFile, tsrFromCode, listLotName);
				
//				<!-- get campaign keycode -->
				CampaignKeyCode keyCode =  kpiService().getCampaignKeyCode(listLotName);
				if(keyCode == null) {
					throw new Exception("Key Code not found: " + listLotName);
				}
				
//				<!-- Check TMR(Sup) -->
				Date saleDate = (Date) data.get("saleDate").getValue();
				String tmrCode = data.get("tmrCode").getStringValue();
				checkSup(tsrFromCode, tmrCode, keyCode, saleDate);
				
//				<!-- getting customer -->
				String customerName = data.get("customerName").getStringValue();
				Customer customer = kpiService().getCustomerByName(customerName);
				
//				<!-- Add new customer if customer is null -->
				if(customer == null) {
					customer = new Customer();
					customer.setFullName(customerName);
					customer = kpiService().addCustomer(customer);
				}
				
//				<!-- getting product type -->
				ProductType productType = kpiService().getProductTypeByValue(data.get("product").getStringValue());
				
//				<!-- Data -->
				Date approveDate = (Date) data.get("approveDate").getValue();
				BigDecimal premium = new BigDecimal(data.get("premium").getStringValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal afyp = new BigDecimal(data.get("afyp").getStringValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal protectAmt = new BigDecimal(data.get("protectAmount").getStringValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
				String qaStatus = data.get("qaStatus").getStringValue();
				
				
				String xRef = data.get("xRef").getStringValue();
				if(StringUtils.isBlank(xRef)) {
					throw new Exception("XRef on this record is null " + xRef + " | saleDate: " + saleDate + " | keyCode: " + listLotName);
				}
				PolicyInfo policy = kpiService().getPolicyInfoByXRef(xRef);
				if(policy != null) {
					policy.setCustomer(customer);
					policy.setCampaignKeyCode(keyCode);
					policy.setProductType(productType);
					policy.setAfyp(afyp);
					policy.setPremium(premium);
					policy.setProtectAmt(protectAmt);
					policy = kpiService().updatePolicyInfo(policy);
				} else {
					policy = new PolicyInfo();
					policy.setxRef(xRef);
					policy.setCustomer(customer);
					policy.setCampaignKeyCode(keyCode);
					policy.setProductType(productType);
					policy.setAfyp(afyp);
					policy.setPremium(premium);
					policy.setProtectAmt(protectAmt);
					policy = kpiService().addPolicyInfo(policy);
				}

				QaStatus qa = kpiService().getQaStatusInMap(qaStatus);
				if(null == qa) {
					throw new Exception("Not Found QaStatus: " + qaStatus);
				}
				PolicyStatus policyStatus = kpiService().getPolicyStatus(saleDate, xRef, qa.getQaValue());
				
				if(policyStatus == null) {
//					System.out.println("Add Policy Status by SaleByRec: " + " xRef: " + xRef + " | saleDate: " + saleDate + " | qa: " + qa.getQaValue());
					policyStatus = new PolicyStatus();
					policyStatus.setPolicyInfo(policy);
					policyStatus.setSaleDate(saleDate);
					policyStatus.setQaStatus(qa);
					policyStatus.setQcDate(approveDate);
					policyStatus.setRemark(data.get("remark").getStringValue());
					policyStatus = kpiService().addPolicyStatus(policyStatus);
				} else {
//					System.out.println("Existed Policy Status by SaleByRec: " + " xRef: " + xRef + " | saleDate: " + saleDate + " | qa: " + qa.getQaValue());
				}
				
				SalesRecord saleRec = kpiService().isSalesRecordExist(saleDate, tsrFromCode, policy);
				
				if(saleRec != null) {
					saleRec.setSaleDate(saleDate);
					saleRec.setTsrInfo(tsrFromCode);
					saleRec.setTsmInfo(kpiService().getTsrInfoInMap(tmrCode));
					saleRec.setPolicyInfo(policy);
					saleRec.setCampaignKeycode(keyCode);
					saleRec = kpiService().updateSalesRecord(saleRec);
				} else {
					saleRec = new SalesRecord();
					saleRec.setSaleDate(saleDate);
					saleRec.setTsrInfo(tsrFromCode);
					saleRec.setTsmInfo(kpiService().getTsrInfoInMap(tmrCode));
					saleRec.setPolicyInfo(policy);
					saleRec.setCampaignKeycode(keyCode);
					saleRec = kpiService().addSalesRecord(saleRec);
				}
			} catch(Exception e) { 
				e.printStackTrace();
				exs.add(e);
			}
			
		}
		
	}

}