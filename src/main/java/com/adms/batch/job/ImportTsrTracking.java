package com.adms.batch.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrTalkTime;
import com.adms.domain.entities.TsrTalkTimeDetail;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.DateUtil;

public class ImportTsrTracking implements IExcelData {
		
	private List<Exception> exceptions = new ArrayList<Exception>();
	
	public void importFromInputStream(File file, List<Exception> exceptionList) throws Exception {
		System.out.println("ImportTsrTracking processing..");
//		file.getName()
		
//		InputStream fileFormat = URLClassLoader.getSystemResourceAsStream(EFileFormat.TSR_TRACKING.getValue());
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.TSR_TRACKING.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
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
					BigDecimal talkTime = new BigDecimal(data.get("totalTalkTime").getStringValue()).setScale(14, BigDecimal.ROUND_HALF_UP);
					
					TsrInfo tsrInfo = KpiService.getInstance().getTsrInfoByNameAdvanceMode(name, keyCode, null);
					if(null == tsrInfo)	{ 
						throw new Exception("Not found TSR: " + name + " | keyCode: " + keyCode);
					}
					
					addTalkTimeByTsr(tsrInfo, talkTime, DateUtil.convStringToDate(period), keyCode);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void addTalkTimeByTsr(TsrInfo tsrInfo, BigDecimal talkTime, Date talkDate, String keyCode) throws Exception {
		try {
			List<TsrTalkTimeDetail> talkDetails = KpiService.getInstance().isTalkDateExist(talkDate, tsrInfo, keyCode);
			
			if(talkDetails != null && !talkDetails.isEmpty()) {
				TsrTalkTimeDetail detail = talkDetails.get(0);
				
				TsrTalkTime time = detail.getTsrTalkTime();
				time.setTotalTalk(talkTime);
//				System.out.println("Update Talk Time: " + time.toString());
				time = KpiService.getInstance().updateTalkTime(time);
				
				detail.setTsrTalkTime(time);
				detail = KpiService.getInstance().updateTalkTimeDetail(detail);
			} else {
				TsrTalkTime tsrTalkTime = new TsrTalkTime();
				tsrTalkTime.setTotalTalk(talkTime);
				
				tsrTalkTime = KpiService.getInstance().addTalkTime(tsrTalkTime);
				TsrTalkTimeDetail detail = new TsrTalkTimeDetail();
				detail.setTalkDate(talkDate);
				detail.setTsrTalkTime(tsrTalkTime);
				detail.setTsrInfo(tsrInfo);
				
				detail.setKeyCode(keyCode);
				
				detail = KpiService.getInstance().addTalkTimeDetail(detail);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			exceptions.add(e);
		}
	}
	
}
