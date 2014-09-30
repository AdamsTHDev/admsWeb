package com.adms.batch.service;

import java.util.Date;
import java.util.List;

import com.adms.batch.app.AppConfig;
import com.adms.bo.tsrinfo.TsrInfoBo;
import com.adms.bo.tsrtalktime.TsrTalkTimeBo;
import com.adms.bo.tsrtalktimedetail.TsrTalkTimeDetailBo;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrTalkTime;
import com.adms.domain.entities.TsrTalkTimeDetail;

public class KpiService {

	private final String USER_LOGIN = "System Admin";
	
	private static KpiService instance;
	
	public static KpiService getInstance() {
		if(instance == null) {
			instance = new KpiService();
		}
		return instance;
	}
	
	public List<TsrInfo> isTsrExist(String firstName, String lastName) throws Exception {
		TsrInfo tsrInfo = new TsrInfo();
		
		tsrInfo.setFirstName("%" + firstName + "%");
		tsrInfo.setLastName("%" + lastName + "%");
		
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		List<TsrInfo> list = tsrInfoBo.searchByExamplePaging(tsrInfo, null);
		
		return list;
	}
	
	public TsrInfo addTsr(String firstName, String lastName) throws Exception {
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		TsrInfo tsrInfo = new TsrInfo();
		tsrInfo.setFirstName(firstName);
		tsrInfo.setLastName(lastName);
		
		return tsrInfoBo.addTsrInfo(tsrInfo, USER_LOGIN);
	}
	
	public TsrTalkTime addTalkTime(TsrTalkTime tsrTalkTime) throws Exception {
		TsrTalkTimeBo tsrTalkTimeBo = (TsrTalkTimeBo) AppConfig.getInstance().getBean("tsrTalkTimeBo");
		
		return tsrTalkTimeBo.addTsrTalkTime(tsrTalkTime, USER_LOGIN);
	}
	
	public TsrTalkTimeDetail addTalkTimeDetail(TsrTalkTimeDetail tsrTalkTimeDetail) throws Exception {
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
		return bo.addTsrTalkTimeDetail(tsrTalkTimeDetail, USER_LOGIN);
	}
	
	public List<TsrTalkTimeDetail> isTalkDateExist(Date talkDate) throws Exception {
		TsrTalkTimeDetail detail = new TsrTalkTimeDetail();
		detail.setTalkDate(talkDate);
		
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
		return bo.findTsrTalkTimeDetail(detail);
	}
}
