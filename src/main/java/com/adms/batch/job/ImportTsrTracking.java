package com.adms.batch.job;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.adms.batch.bean.TsrTrackingBean;
import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrTalkTime;
import com.adms.domain.entities.TsrTalkTimeDetail;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.DateUtil;

public class ImportTsrTracking {

	private static ImportTsrTracking instance;
		
	private List<TsrTrackingBean> tsrTrackingBeans;

	public static ImportTsrTracking getInstance() {
		if(instance == null) {
			instance = new ImportTsrTracking();
		}
		return instance;
	}
	
	public void importFromInputStream(InputStream is) throws Exception {
//		InputStream fileFormat = URLClassLoader.getSystemResourceAsStream(EFileFormat.TSR_TRACKING.getValue());
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.TSR_TRACKING.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		boolean isData = false;
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			if(sheetNames.size() == 0) {
				return;
			}
			
			if(sheetNames.size() > 0 && sheetNames.size() == 3) {
				isData = retriveData(wbHolder, sheetNames.get(2));
			} else {
				isData = retriveData(wbHolder, sheetNames.get(0));
			}
			
			if(isData) {
				System.out.println("To DB");
				prepareDataToDB();
			}
			
			fileFormat.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean retriveData(DataHolder wbHolder, String sheetName) {
		boolean result = false;
		tsrTrackingBeans = getTsrTrackingBeans();
		TsrTrackingBean tsrTrackingBean = null;

		String period = wbHolder.get(sheetName).get("period").getStringValue().substring(0, 10);
		List<DataHolder> datas = wbHolder.get(sheetName).getDataList("tsrTrackingList");
		
		if(datas != null && datas.size() > 0) result = true;
		
		for(DataHolder data : datas) {
			tsrTrackingBean = new TsrTrackingBean();
			tsrTrackingBean.setPeriod(period);
			
			String[] names = data.get("tsrName").getStringValue().split(" ");
			
			String firstName = names[0];
			String lastName = names[1];
			
//			for(int n = 1; n < names.length; n++) {
//				lastName.concat(names[n].concat(" "));
//			}
			
			tsrTrackingBean.setFirstName(firstName);
			tsrTrackingBean.setLastName(lastName);
			BigDecimal val = new BigDecimal(data.get("totalTalkTime").getStringValue()).setScale(14, BigDecimal.ROUND_HALF_UP);
			tsrTrackingBean.setTalkTime(val);
			
			tsrTrackingBeans.add(tsrTrackingBean);
			
		}
		return result;
	}
	
	private void prepareDataToDB() {
		
		for(TsrTrackingBean bean : getTsrTrackingBeans()) {
			
			try {
				List<TsrInfo> tsrInfos = KpiService.getInstance().isTsrExist(bean.getFirstName(), bean.getLastName());
				
				if(tsrInfos != null && !tsrInfos.isEmpty()) {
					addTalkTimeByTsr(tsrInfos.get(0), bean.getTalkTime(), DateUtil.convStringToDate(bean.getPeriod()));
				} else {
					System.out.println("Not found TSR: " + bean.getFirstName() + " " + bean.getLastName());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void addTalkTimeByTsr(TsrInfo tsrInfo, BigDecimal talkTime, Date talkDate) {
		try {
			System.out.println("Checking talk date for this tsr");
			List<TsrTalkTimeDetail> talkDetails = KpiService.getInstance().isTalkDateExist(talkDate, tsrInfo);
			
			if(talkDetails != null && !talkDetails.isEmpty()) {
				System.out.println("Talk Time exist. Do update");
				TsrTalkTimeDetail detail = talkDetails.get(0);
				
				TsrTalkTime time = detail.getTsrTalkTime();
				time.setTotalTalk(talkTime);
				time = KpiService.getInstance().updateTalkTime(time);
				
				detail.setTsrTalkTime(time);
				detail = KpiService.getInstance().updateTalkTimeDetail(detail);
			} else {
				System.out.println("New Talk Time");
				TsrTalkTime tsrTalkTime = new TsrTalkTime();
				tsrTalkTime.setTotalTalk(talkTime);
				
				tsrTalkTime = KpiService.getInstance().addTalkTime(tsrTalkTime);
				TsrTalkTimeDetail detail = new TsrTalkTimeDetail();
				detail.setTalkDate(talkDate);
				detail.setTsrTalkTime(tsrTalkTime);
				detail.setTsrInfo(tsrInfo);
				
				detail = KpiService.getInstance().addTalkTimeDetail(detail);
				System.out.println("Add Talk Time finished");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<TsrTrackingBean> getTsrTrackingBeans() {
		if(tsrTrackingBeans == null) {
			tsrTrackingBeans = new ArrayList<TsrTrackingBean>();
		}
		return tsrTrackingBeans;
	}
	
}
