package com.adms.batch.job;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.PolicyStatus;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class QcReConfirm {

	private static QcReConfirm instance;
	
	public static QcReConfirm getInstance() {
		if(instance == null) {
			instance = new QcReConfirm();
		}
		return instance;
	}
	
	public void importFromInpuStream(InputStream is) {
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.QC_RECONFIRM.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			
			process(wbHolder, sheetNames.get(0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean process(DataHolder wbHolder, String sheetName) throws Exception {
		boolean result = false;
		
		/*
		 * <DataRecord listSourceName="qcReconfirmList" beginRow="16" endRow="100">
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
		for(DataHolder data : datas) {
			
			String customerName = data.get("customerName").getStringValue();
//			customerName = removeTitleAndSpace(customerName);
			
//			String tsrName = data.get("tsrName").getStringValue();
//			tsrName = removeTitleAndSpace(tsrName);
			
			Date qcDate = (Date) data.get("qcDate").getValue();
			String qcCode = data.get("qcCode").getStringValue();
			String qcStatus = data.get("qcStatus").getStringValue();
			String reason = data.get("reason").getStringValue();
			String remark = data.get("remark").getStringValue();
			String currReason = data.get("currentReason").getStringValue();
			String currRemark = data.get("currentRemark").getStringValue();
			
			PolicyInfo policyInfo = null;
			try {
				policyInfo = KpiService.getInstance().getPolicyInfoByCustomerName(customerName);
				
				if(!KpiService.getInstance().isAlreadyQc(qcDate, policyInfo.getxRef())) {
					PolicyStatus policyStatus = new PolicyStatus();
					policyStatus.setPolicyInfo(policyInfo);
					policyStatus.setQcDate(qcDate);
					policyStatus.setQcCode(qcCode);
					policyStatus.setQaStatus(KpiService.getInstance().getQaStatus(qcStatus));
					policyStatus.setReason(reason);
					policyStatus.setRemark(remark);
					policyStatus.setCurrentReason(currReason);
					policyStatus.setCurrentRemark(currRemark);
					policyStatus = KpiService.getInstance().addPolicyStatus(policyStatus);
					result = true;
				}
			} catch(Exception e) {
				e.printStackTrace();
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
