package com.adms.batch.job;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.Customer;
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.ProductType;
import com.adms.domain.entities.SalesRecord;
import com.adms.domain.entities.TsrInfo;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class SalesByRecord {
	
	private static SalesByRecord instance;
	
	public static SalesByRecord getInstance() {
		if(instance == null) {
			instance = new SalesByRecord();
		}
		return instance;
	}
	
	public void importFromInpuStream(InputStream is) {
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.SALES_BY_RECORD.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		boolean isData = false;
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();

			if(sheetNames.size() == 0) {
				return;
			}
			
			isData = process(wbHolder, sheetNames.get(0));
			
			if(isData) {
				prepareDataToDB();
			}
			
			fileFormat.close();
			is.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean process(DataHolder wbHolder, String sheetName) throws Exception {
		boolean result = false;
		DataHolder sheetHolder = wbHolder.get(sheetName);
		
//		String reportName = sheetHolder.get("reportName").getStringValue();
//		String campaignName = sheetHolder.get("campaignName").getStringValue();
//		String period = sheetHolder.get("period").getStringValue();
		
		List<DataHolder> dataList = sheetHolder.getDataList("salesByRecordList");
		if(dataList != null && !dataList.isEmpty()) result = true;
		
		for(DataHolder data : dataList) {

			TsrInfo tsr = KpiService.getInstance().getTsrInfo(data.get("tsrCode").getStringValue());
			tsr.setUpline(KpiService.getInstance().getTsrInfo(data.get("tmrCode").getStringValue()));
			tsr = KpiService.getInstance().updateTsrInfo(tsr);
			
//			QaStatus qaStatus = KpiService.getInstance().getQaStatusByValue(data.get("qaStatus").getStringValue());
			
			Customer customer = KpiService.getInstance().addOrUpdateCustomer(data.get("customerName").getStringValue());
			ProductType productType = KpiService.getInstance().getProductTypeByValue(data.get("product").getStringValue());
			Date approveDate = (Date) data.get("approveDate").getValue();
			BigDecimal premium = new BigDecimal(data.get("premium").getStringValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal afyp = new BigDecimal(data.get("afyp").getStringValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal protectAmt = new BigDecimal(data.get("protectAmount").getStringValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
			Date saleDate = (Date) data.get("saleDate").getValue();
			
			String listLotName = data.get("listLotName").getStringValue();
			listLotName = listLotName.substring(listLotName.indexOf("(") + 1, listLotName.indexOf(")"));

			String xRef = data.get("xRef").getStringValue();
			
			CampaignKeyCode campaignKeyCode =  KpiService.getInstance().getCampaignKeyCode(listLotName);
			
			PolicyInfo policy = KpiService.getInstance().getPolicyInfoByXRef(xRef);
			if(policy != null) {
				policy.setCustomer(customer);
				policy.setCampaignKeyCode(campaignKeyCode);
				policy.setProductType(productType);
				policy.setAfyp(afyp);
				policy.setPremium(premium);
				policy.setProtectAmt(protectAmt);
				policy.setApproveDate(approveDate);
				policy = KpiService.getInstance().updatePolicyInfo(policy);
			} else {
				policy = new PolicyInfo();
				policy.setxRef(xRef);
				policy.setCustomer(customer);
				policy.setCampaignKeyCode(campaignKeyCode);
				policy.setProductType(productType);
				policy.setAfyp(afyp);
				policy.setPremium(premium);
				policy.setProtectAmt(protectAmt);
				policy.setApproveDate(approveDate);
				policy = KpiService.getInstance().addPolicyInfo(policy);
			}
			
			SalesRecord saleRec = KpiService.getInstance().isSalesRecordExist(saleDate, tsr, policy);
			
			if(saleRec != null) {
				saleRec.setSaleDate(saleDate);
				saleRec.setTsrInfo(tsr);
				saleRec.setPolicyInfo(policy);
				saleRec.setCampaignKeycode(campaignKeyCode);
				saleRec = KpiService.getInstance().updateSalesRecord(saleRec);
			} else {
				saleRec = new SalesRecord();
				saleRec.setSaleDate(saleDate);
				saleRec.setTsrInfo(tsr);
				saleRec.setPolicyInfo(policy);
				saleRec.setCampaignKeycode(campaignKeyCode);
				saleRec = KpiService.getInstance().addSalesRecord(saleRec);
			}
			
		}
		
		/*
		 * <DataRecord listSourceName="salesByRecordList" beginRow="7" endRow="9999">
			
				<DataCell row="1" column="B" dataType="TEXT" fieldName="campaignCode" />
				<DataCell row="1" column="C" dataType="TEXT" fieldName="listLotName" />
				<DataCell row="1" column="D" dataType="DATE" dataFormat="yyyy-MM-dd" fieldName="approveDate" />
				<DataCell row="1" column="E" dataType="TEXT" fieldName="xRef"/>
				<DataCell row="1" column="G" dataType="TEXT" fieldName="customerName"/>
				<DataCell row="1" column="H" dataType="TEXT" fieldName="product"/>
				<DataCell row="1" column="I" dataType="NUMBER" dataFormat="#,##0.00" fieldName="premium" />
				<DataCell row="1" column="J" dataType="NUMBER" dataFormat="#,##0.00" fieldName="afyp" />
				<DataCell row="1" column="K" dataType="NUMBER" dataFormat="#,##0.00" fieldName="protectAmount" />
				<DataCell row="1" column="L" dataType="TEXT" fieldName="paymentChannel" />
				<DataCell row="1" column="M" dataType="TEXT" fieldName="paymentMode" /> 
				<DataCell row="1" column="N" dataType="TEXT" fieldName="qaStatus" />
				<DataCell row="1" column="P" dataType="DATE" dataFormat="yyyy-MM-dd" fieldName="saleDate" />
				<DataCell row="1" column="Q" dataType="TEXT" fieldName="tsrCode" />
				<DataCell row="1" column="R" dataType="TEXT" fieldName="tsrName" />
				<DataCell row="1" column="S" dataType="TEXT" fieldName="tmrCode" />
				<DataCell row="1" column="T" dataType="TEXT" fieldName="tmrName" />
		 */
		
		return result;
	}
	
	private void prepareDataToDB() {
		
	}

}