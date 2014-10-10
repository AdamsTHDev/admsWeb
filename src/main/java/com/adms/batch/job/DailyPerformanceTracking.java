package com.adms.batch.job;

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

public class DailyPerformanceTracking {
	
	private static DailyPerformanceTracking instance;
	
	public static DailyPerformanceTracking getInstance() {
		if(instance == null) {
			instance = new DailyPerformanceTracking();
		}
		return instance;
	}
	
	public void importFromInpuStream(InputStream is) {
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.DAILY_PERFORMANCE.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			if(sheetNames == null || (sheetNames != null && sheetNames.isEmpty())) {
				return;
			} else if(sheetNames.size() == 3) {
				process(wbHolder, sheetNames.get(1));
				process(wbHolder, sheetNames.get(2));
			} else {
				process(wbHolder, sheetNames.get(0));
			}
			
			fileFormat.close();
			is.close();
			
		} catch (Exception e) {
			e.printStackTrace();
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
			
			for(DataHolder data : sheet.getDataList("dailyPerformanceList")) {
				Date performanceDate = (Date) data.get("performanceDate").getValue();

				Integer tsrNo = data.get("tsrNo").getIntValue();
				Integer newLeadUsedPerTsr = data.get("newLeadUsedPerTsr").getIntValue();
				Integer newLeadUsed = data.get("newLeadUsed").getIntValue();
				Integer productionSale = data.get("productionSale").getIntValue();
				
				
				campaignLead = KpiService.getInstance().isCampaignLeadExist(performanceDate, keyCode);
				
				if(campaignLead == null) {
					campaignLead = new CampaignLead();
					campaignLead.setPerformanceDate(performanceDate);
					campaignLead.setCampaignKeyCode(campaignKeyCode);
					campaignLead.setTsrNo(tsrNo);
					campaignLead.setNewLeadUsedPerTsr(newLeadUsedPerTsr);
					campaignLead.setNewLeadUsed(newLeadUsed);
					campaignLead.setProductionSale(productionSale);
					campaignLead = KpiService.getInstance().addCampaignLead(campaignLead);
				} else {
					campaignLead.setPerformanceDate(performanceDate);
					campaignLead.setCampaignKeyCode(campaignKeyCode);
					campaignLead.setTsrNo(tsrNo);
					campaignLead.setNewLeadUsedPerTsr(newLeadUsedPerTsr);
					campaignLead.setNewLeadUsed(newLeadUsed);
					campaignLead.setProductionSale(productionSale);
					campaignLead = KpiService.getInstance().updateCampaignLead(campaignLead);
				}
				
			}
		} else {
			System.err.println("KEY CODE NOT FOUND !!");
		}
		
		
		return result;
	}
	
	public String getKeyCodeFromCell(String val) {
		String result = "";
		if(!StringUtils.isBlank(val)) {
			return val.substring(val.indexOf("(", val.indexOf("(") + 1) + 1, val.length() - 1);
		}
		return result;
	}
	
}
