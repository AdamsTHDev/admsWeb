package com.adms.batch.job;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.adms.batch.app.AppConfig;
import com.adms.batch.bean.TsrTrackingBean;
import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.bo.tsrtalktime.TsrTalkTimeBo;
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
//				prepareDataToDB();
			}
			
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
				
				if(tsrInfos != null && tsrInfos.size() > 0) {
					System.out.println("Tsr Existed");
					addTalkTimeByTsr(tsrInfos.get(0), bean.getTalkTime(), DateUtil.convStringToDate(bean.getPeriod()));
				} else {
					System.out.println("Not found TSR: " + bean.getFirstName() + " " + bean.getLastName());
					System.out.println("Start adding new TSR");
					TsrInfo newTsr = KpiService.getInstance().addTsr(bean.getFirstName(), bean.getLastName());
					System.out.println("New Tsr Added");
					
					System.out.println("Start add Talk Time");
					addTalkTimeByTsr(newTsr, bean.getTalkTime(), DateUtil.convStringToDate(bean.getPeriod()));
					System.out.println("Finish added");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void addTalkTimeByTsr(TsrInfo tsrInfo, BigDecimal talkTime, Date talkDate) {
		try {
			System.out.println(tsrInfo.toString());
			System.out.println("Check is this talk date already exist: " + DateUtil.convDateToString(talkDate));
			List<TsrTalkTimeDetail> talkDetails = KpiService.getInstance().isTalkDateExist(talkDate);
			
			if(talkDetails != null && talkDetails.size() > 0) {
				System.out.println("This talkDate: " 
						+ DateUtil.convDateToString(talkDate) 
						+ ", and TSR: " + tsrInfo.getFirstName() + " " + tsrInfo.getLastName() 
						+ " is existed");
				System.out.println("Retriving old data of Talk Time");
				for(TsrTalkTimeDetail talkDetail : talkDetails) {
					TsrTalkTime tsrTalk = talkDetail.getTsrTalkTime();
					System.out.println(tsrTalk.toString());
				}
			} else {
				System.out.println("New record");
				TsrTalkTime tsrTalkTime = KpiService.getInstance().addTalkTime(new TsrTalkTime(talkTime));
				
				TsrTalkTimeDetail detail = new TsrTalkTimeDetail();
				detail.setTsrInfo(tsrInfo);
				detail.setTsrTalkTime(tsrTalkTime);
				detail.setTalkDate(talkDate);
				KpiService.getInstance().addTalkTimeDetail(detail);
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
