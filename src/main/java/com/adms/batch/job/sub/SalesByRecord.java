
package com.adms.batch.job.sub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.Customer;
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.PolicyStatus;
import com.adms.domain.entities.QaStatus;
import com.adms.domain.entities.SalesRecord;
import com.adms.domain.entities.TsrCodeReplacer;
import com.adms.domain.entities.TsrHierarchical;
import com.adms.domain.entities.TsrInfo;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.DateUtil;

public class SalesByRecord implements IExcelData {
	
	protected List<Exception> exs = new ArrayList<Exception>();
	
	private static KpiService kpiService() throws Exception {
		return KpiService.getInstance();
	}
	
	public void importFromInputStream(File file, List<Exception> exceptionList) throws Exception {
		importFromInputStream(new FileInputStream(file), exceptionList);
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
	
	private void checkSup(TsrInfo tsrInfo, String tsmCode, CampaignKeyCode keyCode, Date saleDate) {
		try {
			if(!StringUtils.isBlank(tsmCode) && null != keyCode) {
//				<!-- check upline campaign -->
				List<TsrHierarchical> list = kpiService().getTsrHierarchical(tsrInfo.getTsrCode(), null, keyCode.getCampaign().getCode(), saleDate);
				if(list == null) {
					kpiService().addTsrHierarchical(tsrInfo, kpiService().getTsrInfoInMap(tsmCode), keyCode.getCampaign(), saleDate, null);
				} else {
					TsrHierarchical h = list.get(0);
					if(!h.getUplineInfo().getTsrCode().equals(tsmCode)) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(saleDate);
						cal.add(Calendar.DATE, -1);
						//remove time
						Date end = DateUtil.convStringToDate("yyyyMMdd", DateUtil.convDateToString("yyyyMMdd", cal.getTime()));
						h.setEndDate(end);
						kpiService().updateTsrHierarchical(h);
						
						kpiService().addTsrHierarchical(tsrInfo, kpiService().getTsrInfoInMap(tsmCode), keyCode.getCampaign(), saleDate, null);
					}
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
			exs.add(e);
		}
	}
	
	private TsrInfo getDsm(String tsmCode, Date saleDate) throws Exception {
		if(kpiService().isDsm(tsmCode, saleDate)) {
			return kpiService().getTsrInfoInMap(tsmCode);
		} else {
			TsrHierarchical tsm = kpiService().getDsmHierarchical(tsmCode, saleDate);
			if(tsm != null) {
				return tsm.getUplineInfo();
			} else {
				return null;
			}
		}
	}
	
	protected void process(DataHolder wbHolder, String sheetName) throws Exception {
		DataHolder sheetHolder = wbHolder.get(sheetName);
		
		List<DataHolder> dataList = sheetHolder.getDataList("salesByRecordList");
		
		System.out.println("Sales By Rec size: " + dataList.size());
		Date c = DateUtil.convStringToDate("yyyyMMdd", "20141001");
		
		for(DataHolder data : dataList) {
			try {
				Date saleDate = (Date) data.get("saleDate").getValue();
				if(saleDate.before(c)) {
					continue;
				}
				String tsrCode = data.get("tsrCode").getStringValue();
				
				TsrInfo tsrFromFile = null;
				String tsrNameFromFile = kpiService().removeTitle(data.get("tsrName").getStringValue()).replaceAll("  ", " ");
				
//				<!-- Getting TSR_INFO -->
				if(StringUtils.isBlank(tsrCode)) {
//					tsrFromCode = kpiService().getTsrInfoByName(tsrNameFromFile.trim(), saleDate);
					throw new Exception("TSR CODE is NULL");
				} else {
					tsrFromFile = kpiService().getTsrInfoInMap(tsrCode);
					if(null == tsrFromFile) {
						System.out.println("Find tsr by name: " + tsrNameFromFile);
						tsrFromFile = kpiService().getTsrInfoByName(tsrNameFromFile.trim(), saleDate);
					}
				}
				
//				<!-- Check if null, throw Exception() -->
				if(null == tsrFromFile) throw new Exception("TSR Not Found: " + "code |" + data.get("tsrCode").getStringValue() + "|");
				
//				<!-- Check is Replace TSR CODE -->
				String listLotName = data.get("listLotName").getStringValue();
				listLotName = kpiService().getKeyCodeFromCampaignListLot(listLotName);
				checkReplaceTsrCode(tsrNameFromFile, tsrFromFile, listLotName);
				
//				<!-- get campaign keycode -->
				CampaignKeyCode keyCode =  kpiService().getCampaignKeyCode(listLotName);
				if(keyCode == null) {
					throw new Exception("Key Code not found: " + listLotName);
				}
				
//				<!-- Check TMR(Sup) -->
				String tsmCode = data.get("tmrCode").getStringValue();
//				checkSup(tsrFromFile, tsmCode, keyCode, saleDate);
				
//				<!-- get DSM -->
				TsrInfo dsmInfo = getDsm(tsmCode, saleDate);
				
//				<!-- getting customer -->
				String customerName = data.get("customerName").getStringValue();
				Customer customer = kpiService().getCustomerByName(customerName);
				
//				<!-- Add new customer if customer is null -->
				if(customer == null) {
					customer = kpiService().addCustomer(new Customer(customerName));
				}
//				System.out.println("getting customer time: " + getProcessTime(d, new Date()));
				
//				<!-- getting product type -->
//				ProductType productType = kpiService().getProductTypeByValue(data.get("product").getStringValue());
				
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
					if(!policy.getAfyp().equals(afyp) || !policy.getPremium().equals(premium) || !policy.getProtectAmt().equals(protectAmt)) {
						policy.setCustomer(customer);
						policy.setCampaignKeyCode(keyCode);
//						policy.setProductType(productType);
						policy.setAfyp(afyp);
						policy.setPremium(premium);
						policy.setProtectAmt(protectAmt);
						policy = kpiService().updatePolicyInfo(policy);
					}
				} else {
					policy = new PolicyInfo();
					policy.setxRef(xRef);
					policy.setCustomer(customer);
					policy.setCampaignKeyCode(keyCode);
//					policy.setProductType(productType);
					policy.setAfyp(afyp);
					policy.setPremium(premium);
					policy.setProtectAmt(protectAmt);
					policy = kpiService().addPolicyInfo(policy);
				}
//				System.out.println("policy process time: " + getProcessTime(d, new Date()));
//				PolicyStatus policyStatus = null;
				
				boolean isExistedStatus = false;
				if(policy.getPolicyStatuses() != null) {
					for(PolicyStatus ps : policy.getPolicyStatuses()) {
						if(ps.getQaStatus().getQaValue().equals(qaStatus)) {
							isExistedStatus = true;
							break;
						}
					}
					
					if(!isExistedStatus) {
						savePolicyStatus(qaStatus, saleDate, xRef, policy, approveDate, data.get("remark").getStringValue());
					}
				}
				
				SalesRecord saleRec = kpiService().isSalesRecordExist(saleDate, policy);
				if(saleRec != null) {

					saleRec.setPolicyInfo(policy);
					saleRec.setTsmInfo(kpiService().getTsrInfoInMap(tsmCode));
					saleRec.setDsmInfo(dsmInfo);
					
					saleRec = kpiService().updateSalesRecord(saleRec);
				} else {
					saleRec = new SalesRecord();
					saleRec.setSaleDate(saleDate);
					saleRec.setTsrInfo(tsrFromFile);
					saleRec.setTsmInfo(kpiService().getTsrInfoInMap(tsmCode));
					saleRec.setDsmInfo(dsmInfo);
					saleRec.setPolicyInfo(policy);
					saleRec.setCampaignKeycode(keyCode);
					saleRec = kpiService().addSalesRecord(saleRec);
				}
//				kpiService().saveSalesRecordByMonth(saleRec);
			} catch(Exception e) { 
				e.printStackTrace();
				exs.add(e);
			}
			
		}
		
	}
	
	private PolicyStatus savePolicyStatus(String qaStatus, Date saleDate, String xRef, PolicyInfo policyInfo, Date approveDate, String remark) throws Exception {
		PolicyStatus policyStatus = null;
		
		QaStatus qa = kpiService().getQaStatusInMap(qaStatus);
		if(null == qa) {
			throw new Exception("Not Found QaStatus: " + qaStatus);
		}
		
//		policyStatus = kpiService().getPolicyStatus(saleDate, xRef, qa.getQaValue());
		
		policyStatus = new PolicyStatus();
		policyStatus.setPolicyInfo(policyInfo);
		policyStatus.setSaleDate(saleDate);
		policyStatus.setQaStatus(qa);
		policyStatus.setQcDate(approveDate);
		policyStatus.setRemark(remark);
		policyStatus = kpiService().addPolicyStatus(policyStatus);
		
		return policyStatus;
	}
	
//	private static String getProcessTime(Date start, Date end) {
//		Long ms = end.getTime() - start.getTime();
//		Long min = (ms / 1000L) / 60;
//		Long sec = (ms / 1000L) - (min * 60);
//		String result = "";
//		if(min < 1l && sec < 1l) {
//			result = ms.toString() + " ms";
//		} else {
//			result = min.toString() + "." + (sec < 10 ? "0" + sec.toString() : sec) + " min";
//		}
//		return result;
//	}

}