package com.adms.batch.job;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.PolicyStatus;
import com.adms.domain.entities.QaStatus;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class QcReConfirm implements IExcelData {
	
	private List<Exception> exceptions = new ArrayList<Exception>();
	
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws Exception {
		System.out.println("QcReConfirm");
		
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.QC_RECONFIRM.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			
			process(wbHolder, sheetNames.get(0));
			
		} catch (Exception e) {
			e.printStackTrace();
			exceptionList.add(e);
		} finally {
			fileFormat.close();
			is.close();
		}
		exceptionList.addAll(exceptions);
	}
	
	private QaStatus getQaStatusByValue(String val) throws Exception {
		QaStatus result = KpiService.getInstance().getQaStatusInMap(val);
		if(result == null) throw new Exception("QA Status not found: " + val);
		return result;
	}
	
	private boolean process(DataHolder wbHolder, String sheetName) throws Exception {
		boolean result = false;
		
		/*
		 * <DataRecord listSourceName="qcReconfirmList" beginRow="16" endRow="100">
		 * 		<DataCell row="1" column="E" dataType="DATE" fieldName="saleDate" />
				<DataCell row="1" column="G" dataType="TEXT" fieldName="customerName" />
				<DataCell row="1" column="H" dataType="TEXT" fieldName="tsrName" />
				<DataCell row="1" column="J" dataType="DATE" dataFormat="d/M/yyyy hh:mm:ss" fieldName="qcDate" />
				<DataCell row="1" column="K" dataType="TEXT" fieldName="qcCode" />
				<DataCell row="1" column="N" dataType="TEXT" fieldName="tsrStatus" />
				<DataCell row="1" column="O" dataType="TEXT" fieldName="qcStatus" />
				<DataCell row="1" column="P" dataType="TEXT" fieldName="reason" />
				<DataCell row="1" column="Q" dataType="TEXT" fieldName="remark" />
				<DataCell row="1" column="R" dataType="TEXT" fieldName="currentReason" />
				<DataCell row="1" column="S" dataType="TEXT" fieldName="currentRemark" />
		 */
		
		DataHolder sheet = wbHolder.get(sheetName);
		
		List<DataHolder> datas = sheet.getDataList("qcReconfirmList");
		System.out.println("QC Reconfirm size: " + datas.size());
		for(DataHolder data : datas) {
			
			String customerName = data.get("customerName").getStringValue();
//			customerName = removeTitleAndSpace(customerName);
			
//			String tsrName = data.get("tsrName").getStringValue();
//			tsrName = removeTitleAndSpace(tsrName);
			Date saleDate = (Date) data.get("saleDate").getValue();
			Date qcDate = (Date) data.get("qcDate").getValue();
			String qcCode = data.get("qcCode").getStringValue();
			String tsrStatus = data.get("tsrStatus").getStringValue();
			String qcStatus = data.get("qcStatus").getStringValue();
			String reason = data.get("reason").getStringValue();
			String remark = data.get("remark").getStringValue();
			String currReason = data.get("currentReason").getStringValue();
			String currRemark = data.get("currentRemark").getStringValue();
			
			PolicyInfo policyInfo = null;
			try {
				policyInfo = KpiService.getInstance().getPolicyInfoByCustomerName(customerName);
				QaStatus qaStatus = null;
				PolicyStatus policyStatus = null;
				
				if(tsrStatus.equalsIgnoreCase(qcStatus)) {
					qaStatus = getQaStatusByValue(qcStatus);
					
					policyStatus = KpiService.getInstance().getPolicyStatus(saleDate, policyInfo.getxRef(), qaStatus.getQaValue());
					
					if(policyStatus != null) {
						if(policyStatus.getSaleDate() == null) {
							policyStatus.setSaleDate(saleDate);
						}
						policyStatus.setCurrentReason(currReason);
						policyStatus.setCurrentRemark(currRemark);
						policyStatus.setQcCode(qcCode);
						policyStatus.setQcDate(qcDate);
						policyStatus = KpiService.getInstance().updatePolicyStatus(policyStatus);
						result = true;
					}
				} else {
					QaStatus qaFromQc = getQaStatusByValue(qcStatus);
					PolicyStatus psFromQc = KpiService.getInstance().getPolicyStatus(saleDate, policyInfo.getxRef(), qaFromQc.getQaValue());
					if(psFromQc == null) {
						psFromQc = new PolicyStatus();
						psFromQc.setPolicyInfo(policyInfo);
						psFromQc.setQcDate(qcDate);
						psFromQc.setQcCode(qcCode);
						psFromQc.setSaleDate(saleDate);
						psFromQc.setQaStatus(qaStatus);
						psFromQc.setReason(reason);
						psFromQc.setRemark(remark);
						psFromQc.setCurrentReason(currReason);
						psFromQc.setCurrentRemark(currRemark);
						psFromQc = KpiService.getInstance().addPolicyStatus(policyStatus);
					} else {
						if(psFromQc.getSaleDate() == null) {
							psFromQc.setSaleDate(saleDate);
						}
						psFromQc.setCurrentReason(currReason);
						psFromQc.setCurrentRemark(currRemark);
						psFromQc.setQcCode(qcCode);
						psFromQc.setQcDate(qcDate);
						psFromQc = KpiService.getInstance().updatePolicyStatus(policyStatus);
					}
					
					QaStatus qaFromSale = getQaStatusByValue(tsrStatus);
					PolicyStatus psFromSale = KpiService.getInstance().getPolicyStatus(saleDate, policyInfo.getxRef(), qaFromSale.getQaValue());
					if(psFromSale == null) {
						psFromSale = new PolicyStatus(policyInfo, qaFromSale);
						psFromSale.setReason(reason);
						psFromSale.setRemark(remark);
						psFromSale.setCurrentReason(currReason);
						psFromSale.setCurrentRemark(currRemark);
						psFromSale.setQcDate(qcDate);
						psFromSale.setQcCode(qcCode);
						psFromSale.setSaleDate(saleDate);
						psFromSale = KpiService.getInstance().addPolicyStatus(psFromSale);
					} else {
						psFromSale.setReason(reason);
						psFromSale.setRemark(remark);
						psFromSale.setCurrentReason(currReason);
						psFromSale.setCurrentRemark(currRemark);
						psFromSale.setQcCode(qcCode);
						psFromSale = KpiService.getInstance().updatePolicyStatus(psFromSale);
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				exceptions.add(e);
			}
			
		}
		
		return result;
	}
	
	@SuppressWarnings("unused")
	private String removeTitleAndSpace(String val) {
		String[] titles = {"นาย", "นางสาว", "น.ส.", "นาง"};

		for(String title : titles) {
			if(val.contains(title)) {
				val = val.replaceAll(" ", "").replace(title, "");
			}
		}
		
		return val;
	}
}
