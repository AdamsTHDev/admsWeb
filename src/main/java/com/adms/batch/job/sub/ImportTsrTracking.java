package com.adms.batch.job.sub;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrTalkTime;
import com.adms.domain.entities.TsrPerformance;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.DateUtil;

public class ImportTsrTracking implements IExcelData {
		
	private List<Exception> exceptions = new ArrayList<Exception>();
	
	public void importFromInputStream(File file, List<Exception> exceptionList) throws Exception {
		importFromInputStream(new FileInputStream(file), exceptionList);
	}
	
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws Exception {
		System.out.println("ImportTsrTracking processing..");
//		InputStream fileFormat = URLClassLoader.getSystemResourceAsStream(EFileFormat.TSR_TRACKING.getValue());
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.TSR_TRACKING.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			if(sheetNames.size() == 0) {
				return;
			}
		
			for(String sheetName : sheetNames) {
				process(wbHolder, sheetName);
			}
			
		} catch (Exception e) {
			exceptionList.add(e);
			e.printStackTrace();
		} finally {
//			fileFormat.close();
			is.close();
		}
		exceptionList.addAll(exceptions);
	}
	
	public boolean process(DataHolder wbHolder, String sheetName) throws Exception {
		boolean result = false;
		
		try {
			
			String period = wbHolder.get(sheetName).get("period").getStringValue().trim().substring(0, 10);
			List<DataHolder> datas = wbHolder.get(sheetName).getDataList("tsrTrackingList");
			String listLotName = wbHolder.get(sheetName).get("listLotName").getStringValue();
			if(listLotName.contains(",")) {
				return false;
			}
			String keyCode = KpiService.getInstance().getKeyCodeFromCampaignListLot(listLotName);
			
			if(null != datas && datas.size() > 0) result = true;
			
			System.out.println("TSR Tracking Size: " + datas.size());
			for(DataHolder data : datas) {
				try {
//					System.out.println("workday: " + data.get("workday").getDecimalValue());
					
					Integer workday = Integer.valueOf(data.get("workday").getDecimalValue() != null 
							? Integer.valueOf(data.get("workday").getDecimalValue().intValue()) : new Integer(0));
					
					if(workday == 0) {
						continue;
					}
					
					String name = data.get("tsrName").getStringValue();
					BigDecimal hours = data.get("hours") != null ? data.get("hours").getDecimalValue().setScale(14, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
					BigDecimal talkTime = new BigDecimal(data.get("totalTalkTime").getStringValue()).setScale(14, BigDecimal.ROUND_HALF_UP);
					
					Integer newUsed = data.get("newUsed") != null ? data.get("newUsed").getIntValue() : 0;
					Integer totalPolicy = data.get("totalPolicy") != null ? data.get("totalPolicy").getIntValue() : 0;
					
					TsrInfo tsrInfo = KpiService.getInstance().getTsrInfoByNameAdvanceMode(name, keyCode, null);
					if(null == tsrInfo)	{ 
						throw new Exception("Not found TSR: " + name + " | keyCode: " + keyCode);
					}
					
					Date talkdate = null;
					try {
						talkdate = DateUtil.convStringToDate(period);
					} catch(Exception e) {
						System.err.println("Cannot convert String to date: " + period + " | try to use another format => dd-MM-yyyy");
						talkdate = DateUtil.convStringToDate("dd-MM-yyyy", period);
					}
					addTalkTimeByTsr(tsrInfo, hours, talkTime, talkdate, keyCode, newUsed, totalPolicy);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void addTalkTimeByTsr(TsrInfo tsrInfo, BigDecimal hours, BigDecimal talkTime, Date talkDate, String keyCode, Integer newUsed, Integer totalPolicy) throws Exception {
		try {
			List<TsrPerformance> tsrPerformances = KpiService.getInstance().isTalkDateExist(talkDate, tsrInfo, keyCode);
			if(tsrPerformances == null || tsrPerformances != null && tsrPerformances.size() > 1) throw new Exception("found TSR Performance more than 1 or null: " + Arrays.toString(tsrPerformances.toArray()));
			
			if(tsrPerformances != null && !tsrPerformances.isEmpty()) {
				TsrPerformance performance = tsrPerformances.get(0);
				
				TsrTalkTime tsrTalkTime = performance.getTsrTalkTime();
				tsrTalkTime.setTotalTalk(talkTime);
				tsrTalkTime.setHours(hours);
//				System.out.println("Update Talk Time: " + time.toString());
				tsrTalkTime = KpiService.getInstance().updateTalkTime(tsrTalkTime);
				
				performance.setTsrTalkTime(tsrTalkTime);
				performance.setNewUsed(newUsed);
				performance.setTotalPolicy(totalPolicy);
				performance = KpiService.getInstance().updateTsrPerformance(performance);
			} else {
				TsrTalkTime tsrTalkTime = new TsrTalkTime();
				tsrTalkTime.setTotalTalk(talkTime);
				tsrTalkTime.setHours(hours);
				
				tsrTalkTime = KpiService.getInstance().addTalkTime(tsrTalkTime);
				TsrPerformance performance = new TsrPerformance();
				performance.setTalkDate(talkDate);
				performance.setTsrTalkTime(tsrTalkTime);
				performance.setTsrInfo(tsrInfo);
				
				performance.setKeyCode(keyCode);
				
				performance.setNewUsed(newUsed);
				performance.setTotalPolicy(totalPolicy);
				performance = KpiService.getInstance().addTsrPerformance(performance);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			exceptions.add(e);
		}
	}
	
}
