package com.adms.batch.job;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.CampaignLead;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class DailyPerformanceTracking implements IExcelData {

	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws IOException {
		System.out.println("DailyPerformanceTracking");
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.DAILY_PERFORMANCE.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			if(sheetNames == null || (sheetNames != null && sheetNames.isEmpty())) {
				return;
			} else { 
				for(String sn : sheetNames) {
					try {
						if(!sn.equalsIgnoreCase("Summary") || !sn.equalsIgnoreCase("DailyPerformanceTracking")) {
							process(wbHolder, sn);
						}
					} catch(Exception e) {
						exceptionList.add(e);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			exceptionList.add(e);
		} finally {
			fileFormat.close();
			is.close();
		}
	}
	
	public boolean process(DataHolder wbHolder, String sheetName) throws Exception {
		boolean result = false;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		DataHolder sheet = wbHolder.get(sheetName);
		
		String keyCode = getKeyCodeFromCell(sheet.get("listLotName").getStringValue());
		Date fromDate = (Date) sheet.get("fromDate").getValue();
		Date toDate = (Date) sheet.get("toDate").getValue();

		CampaignKeyCode campaignKeyCode = KpiService.getInstance().getCampaignKeyCode(keyCode);
		if(campaignKeyCode != null) {
			CampaignLead campaignLead = null;
			List<DataHolder> datas = sheet.getDataList("dailyPerformanceList");
			System.out.println("Daily Performance size: " + datas.size());
			for(DataHolder data : datas) {
				Date performanceDate = (Date) data.get("performanceDate").getValue();

				Integer tsrNo = data.get("tsrNo").getIntValue();
//				Integer newLeadUsedPerTsr = data.get("newLeadUsedPerTsr").getIntValue();
				Integer newLeadUsed = data.get("newLeadUsed").getIntValue();
				Integer productionSale = data.get("productionSale").getIntValue();
				
				
				campaignLead = KpiService.getInstance().isCampaignLeadExist(performanceDate, keyCode);
				
				if(campaignLead == null) {
					campaignLead = new CampaignLead();
					campaignLead.setPerformanceDate(performanceDate);
					campaignLead.setCampaignKeyCode(campaignKeyCode);
					campaignLead.setTsrNo(tsrNo);
					Integer newLeadPerTsr = (null != tsrNo && tsrNo != 0) ? newLeadUsed/tsrNo : 0;
					campaignLead.setNewLeadUsedPerTsr(newLeadPerTsr);
					campaignLead.setNewLeadUsed(newLeadUsed);
					campaignLead.setProductionSale(productionSale);
					campaignLead = KpiService.getInstance().addCampaignLead(campaignLead);
				} else {
					campaignLead.setPerformanceDate(performanceDate);
					campaignLead.setCampaignKeyCode(campaignKeyCode);
					campaignLead.setTsrNo(tsrNo);
					Integer newLeadPerTsr = (null != tsrNo && tsrNo != 0) ? newLeadUsed/tsrNo : 0;
					campaignLead.setNewLeadUsedPerTsr(newLeadPerTsr);
					campaignLead.setNewLeadUsed(newLeadUsed);
					campaignLead.setProductionSale(productionSale);
					campaignLead = KpiService.getInstance().updateCampaignLead(campaignLead);
				}
				
			}
		} else {
			throw new Exception("KEY CODE NOT FOUND !: " + keyCode + " | listLotName: " + sheet.get("listLotName").getStringValue() + " | sheetName: " + sheetName);
		}
		
		
		return result;
	}
	
	public String getKeyCodeFromCell(String val) {
		String result = "";
		if(!StringUtils.isBlank(val)) {
			int count = 0;
			
//			<!-- Check -->
			for(int i = 0; i < val.length(); i++) {
				if(val.charAt(i) == '(') {
					count++;
				}
			}
			
//			<!-- process -->
			if(count == 1) {
				return val.substring(val.indexOf("(") + 1, val.indexOf(")")).trim();
			} else if(count == 2) {
				return val.substring(val.indexOf("(", val.indexOf("(") + 1) + 1, val.length() - 1).trim();
			} else {
				System.err.println("Cannot find Keycode");
			}
		}
		return result;
	}
	
}
