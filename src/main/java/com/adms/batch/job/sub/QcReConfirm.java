package com.adms.batch.job.sub;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.PolicyStatus;
import com.adms.domain.entities.QaStatus;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class QcReConfirm implements IExcelData {
	
	protected List<Exception> exceptions = new ArrayList<Exception>();
	
	public void importFromInputStream(File file, List<Exception> exceptionList) throws Exception {
		
	}
	
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws Exception {
		System.out.println("QcReConfirm");
		
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.QC_RECONFIRM_NEW.getValue());
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
	
	protected boolean process(DataHolder wbHolder, String sheetName) throws Exception {
		boolean result = false;
		
		DataHolder sheet = wbHolder.get(sheetName);
		
		List<DataHolder> datas = sheet.getDataList("qcReconfirmList");
		System.out.println("QC Reconfirm size: " + datas.size());
		for(DataHolder data : datas) {
			
			String customerName = data.get("customerName").getStringValue();
			
			String xRef = data.get("xRef") != null ? data.get("xRef").getStringValue() : null;
			Date saleDate = (Date) data.get("saleDate").getValue();
			Date qcDate = (Date) data.get("qcDate").getValue();
			String qcCode = data.get("qcCode").getStringValue();
//			String tsrName = data.get("tsrName").getStringValue();
			String tsrStatus = data.get("tsrStatus").getStringValue();
			String qcStatus = data.get("qcStatus").getStringValue();
			String reason = data.get("reason").getStringValue();
			String remark = data.get("remark").getStringValue();
			String currReason = data.get("currentReason").getStringValue();
			String currRemark = data.get("currentRemark").getStringValue();
			
			PolicyInfo policyInfo = null;
			try {
				if(StringUtils.isNotBlank(xRef)) policyInfo = KpiService.getInstance().getPolicyInfoByXRef(xRef);
				if(policyInfo == null) policyInfo = KpiService.getInstance().getPolicyInfoByCustomerName(customerName, saleDate);

//				<!-- New Extension for Re-confirm --> 
				KpiService.getInstance().savePolicyInfoExtRcf(policyInfo);
//				</!-- New Extension for Re-confirm -->
				
				QaStatus qaStatus = null;
				PolicyStatus policyStatus = null;
				
				if(StringUtils.isNotBlank(tsrStatus) && tsrStatus.equalsIgnoreCase(qcStatus)) {
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
				} else if(StringUtils.isNotBlank(tsrStatus)) {
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
