package com.adms.batch.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;

import com.adms.batch.app.AppConfig;
import com.adms.bo.campaign.CampaignBo;
import com.adms.bo.campaignkeycode.CampaignKeyCodeBo;
import com.adms.bo.campaignlead.CampaignLeadBo;
import com.adms.bo.customer.CustomerBo;
import com.adms.bo.kpibean.KpiBeanBo;
import com.adms.bo.kpicategory.KpiCategorySetupBo;
import com.adms.bo.kpieoc.KpiEocBo;
import com.adms.bo.kpiresult.KpiResultBo;
import com.adms.bo.kpiretention.KpiRetentionBo;
import com.adms.bo.kpiscorerate.KpiScoreRateBo;
import com.adms.bo.policyinfo.PolicyInfoBo;
import com.adms.bo.policyinfoextrcf.PolicyInfoExtRcfBo;
import com.adms.bo.policystatus.PolicyStatusBo;
import com.adms.bo.producttype.ProductTypeBo;
import com.adms.bo.qastatus.QaStatusBo;
import com.adms.bo.salesrecord.SalesRecordBo;
import com.adms.bo.tsrcodereplacer.TsrCodeReplacerBo;
import com.adms.bo.tsrcontract.TsrContractBo;
import com.adms.bo.tsrhierarchical.TsrHierarchicalBo;
import com.adms.bo.tsrinfo.TsrInfoBo;
import com.adms.bo.tsrlevel.TsrLevelBo;
import com.adms.bo.tsrperformance.TsrPerformanceBo;
import com.adms.bo.tsrposition.TsrPositionBo;
import com.adms.bo.tsrsite.TsrSiteBo;
import com.adms.bo.tsrstatus.TsrStatusBo;
import com.adms.bo.tsrtalktime.TsrTalkTimeBo;
import com.adms.domain.entities.Campaign;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.CampaignLead;
import com.adms.domain.entities.Customer;
import com.adms.domain.entities.KpiBean;
import com.adms.domain.entities.KpiCategorySetup;
import com.adms.domain.entities.KpiEoc;
import com.adms.domain.entities.KpiResult;
import com.adms.domain.entities.KpiRetention;
import com.adms.domain.entities.KpiScoreRate;
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.PolicyInfoExtRcf;
import com.adms.domain.entities.PolicyStatus;
import com.adms.domain.entities.ProductType;
import com.adms.domain.entities.QaStatus;
import com.adms.domain.entities.SalesRecord;
import com.adms.domain.entities.TsrCodeReplacer;
import com.adms.domain.entities.TsrContract;
import com.adms.domain.entities.TsrHierarchical;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrLevel;
import com.adms.domain.entities.TsrPosition;
import com.adms.domain.entities.TsrSite;
import com.adms.domain.entities.TsrStatus;
import com.adms.domain.entities.TsrTalkTime;
import com.adms.domain.entities.TsrPerformance;
import com.adms.utils.DateUtil;

public class KpiService {

	public static KpiService getInstance() {
		if(instance == null) {
			instance = new KpiService();
		}
		return instance;
	}
	
	public static KpiService renewInstance() {
		instance = new KpiService();
		return instance;
	}
	
	private final String USER_LOGIN = "System Admin";
	private Map<String, QaStatus> qaMap;
	private Map<String, TsrInfo> tsrMap;

	private Map<String, Campaign> campaignMap;
	private Map<String, CampaignKeyCode> keyCodeMap;
	private Map<String, Customer> customerMap;
	private List<TsrHierarchical> dsmList;
	
	private List<KpiScoreRate> kpiScoreRates;
	
//	private Map<String, Map<String, Map<String, SalesRecord>>> salesRecordByMonthMap;
	
	private static KpiService instance;

	private final List<String> TITLES = Arrays.asList(new String[]{"นาย", "น.ส.", "นาง", "ว่าที่", "นส.", "นางสาว", "สาว"});
	
	public KpiService() {
		System.out.println("New Kpi Service");
		prepareQaStatus();
		prepareTsrInfo();
		prepareCampaignCode();
		prepareKpiScoreRate();
		initCampaignKeyCodeMap();
		initCustomerMap();
		initDsmMap();
	}
	
	public Campaign addCampaign(Campaign campaign) throws Exception {
		if(campaign != null) {
			CampaignBo bo = (CampaignBo) AppConfig.getInstance().getBean("campaignBo");
			campaign = bo.addCampaign(campaign, USER_LOGIN);
			if(campaign != null) {
				prepareCampaignCode();
			}
			return campaign;
		}
		return null;
	}
	
	public CampaignKeyCode addCampaignKeyCode(CampaignKeyCode campaignKeyCode) throws Exception {
		if(campaignKeyCode != null) {
			CampaignKeyCodeBo bo = (CampaignKeyCodeBo) AppConfig.getInstance().getBean("campaignKeyCodeBo");
			campaignKeyCode = bo.addCampaignKeyCode(campaignKeyCode, USER_LOGIN);
			if(campaignKeyCode != null) {
				initCampaignKeyCodeMap();
			}
			return campaignKeyCode;
		}
		return null;
	}
	
	public CampaignLead addCampaignLead(CampaignLead campaignLead) throws Exception {
		if(campaignLead != null) {
			CampaignLeadBo bo = (CampaignLeadBo) AppConfig.getInstance().getBean("campaignLeadBo");
			return bo.addCampaignLead(campaignLead, USER_LOGIN);
		}
		return null;
	}
	
	public Customer addCustomer(Customer customer) throws Exception {
		if(customer != null) {
			CustomerBo bo = (CustomerBo) AppConfig.getInstance().getBean("customerBo");
			Customer c = bo.addCustomer(customer, USER_LOGIN);
			customerMap.put(c.getFullName().replaceAll(" ", ""), c);
			return c;
		}
		return null;
	}
	
	public KpiCategorySetup addKpiCategorySetup(KpiCategorySetup kpiCategorySetup) {
		KpiCategorySetupBo bo = (KpiCategorySetupBo) AppConfig.getInstance().getBean("kpiCategorySetupBo");
		try {
			return bo.addKpiCategory(kpiCategorySetup, USER_LOGIN);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void deleteKpiCategorySetupByEffectiveMonth(String yearMonth) {
		KpiCategorySetupBo bo = (KpiCategorySetupBo) AppConfig.getInstance().getBean("kpiCategorySetupBo");
		String hql = " from KpiCategorySetup d"
				+ " where 1 = 1"
				+ " and CONVERT(nvarchar(6), d.effectiveDate, 112) = ? ";
		
		try {

			for(KpiCategorySetup k : bo.findByHql(hql, yearMonth)) {
				bo.deleteKpiCategory(k);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public KpiCategorySetup updateKpiCategorySetup(KpiCategorySetup kpiCategorySetup) {
		KpiCategorySetupBo bo = (KpiCategorySetupBo) AppConfig.getInstance().getBean("kpiCategorySetupBo");
		try {
			return bo.updateKpiCategory(kpiCategorySetup, USER_LOGIN);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public KpiScoreRate addKpiScoreRate() {
		try {
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public KpiScoreRate addKpiScoreRate(KpiScoreRate kpiScoreRate) {
		try {
			KpiScoreRateBo bo = (KpiScoreRateBo) AppConfig.getInstance().getBean("kpiScoreRateBo");
			return bo.addKpiScoreRate(kpiScoreRate, USER_LOGIN);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void refreshDsmMap() {
		initDsmMap();
	}
	
	private void initDsmMap() {
		dsmList = new ArrayList<>();
		TsrHierarchicalBo bo = (TsrHierarchicalBo) AppConfig.getInstance().getBean("tsrHierarchicalBo");
		
		String hql = " from TsrHierarchical d "
				+ " where d.campaign is null";
		try {
			dsmList = bo.findByHql(hql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isDsm(String code, Date date) {
		try {
			for(TsrHierarchical h : dsmList) {
				if(h.getUplineInfo().getTsrCode().equals(code)
						&& (h.getStartDate().compareTo(date) <= 0 && h.getEndDate().compareTo(date) >= 0)) {
					return true;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addKpiResult(List<KpiBean> kpiBeans, String yearMonth, String delimKeyCodeMSIGWB) throws Exception {
		try {
			
			Map<String, Map<String, Map<String, Map<String, KpiResult>>>> campaignMap = new HashMap<>();
			Map<String, Map<String, Map<String, KpiResult>>> dsmMap = null;
			Map<String, Map<String, KpiResult>> tsmMap = null;
			Map<String, KpiResult> tsrMap = null;

			String campaignCode = "";
			String keyCode = "";
			
			String dsmCode = "";
			String tsmCode = "";
			String tsrCode = "";
			
			KpiResult kpi = null;
			
			BigDecimal afyp = null;
			Integer talkDate = null;
			BigDecimal talkHrs = null;
			Integer fcs = null;
			Integer sale = null;
			Integer successEoc = null;
			Integer allEoc = null;
			
			for(KpiBean kpiBean : kpiBeans) {
				
				if(kpiBean.getKeyCode().contains(delimKeyCodeMSIGWB)) {
					campaignCode = new String(kpiBean.getCampaignCode() + "_" + kpiBean.getKeyCode());
					keyCode = new String(kpiBean.getKeyCode());
				} else {
					campaignCode = new String(kpiBean.getCampaignCode());
					keyCode = "";
				}
				
				dsmCode = kpiBean.getDsmCode();
				tsmCode = kpiBean.getTsmCode();
				tsrCode = kpiBean.getTsrCode();

				dsmMap = campaignMap.get(campaignCode);
				if(dsmMap == null) {
					dsmMap = new HashMap<>();
					campaignMap.put(campaignCode, dsmMap);
				}
				
				tsmMap = dsmMap.get(dsmCode);
				if(tsmMap == null) {
					tsmMap = new HashMap<>();
					dsmMap.put(dsmCode, tsmMap);
				}
				
				tsrMap = tsmMap.get(tsmCode);
				if(tsrMap == null) {
					tsrMap = new HashMap<>();
					tsmMap.put(tsmCode, tsrMap);
				}
				
				kpi = tsrMap.get(tsrCode);
				if(kpi == null) {
					kpi = new KpiResult();
					
					kpi.setYearMonth(yearMonth);
					kpi.setCampaign(getCampaignInMap(kpiBean.getCampaignCode()));
					kpi.setDsmInfo(getTsrInfoInMap(dsmCode));
					kpi.setTsmInfo(getTsrInfoInMap(tsmCode));
					kpi.setTsrInfo(getTsrInfoInMap(tsrCode));
					
					kpi.setKeyCode(keyCode);
					
					afyp = (kpiBean.getSumOfAfyp() == null ? new BigDecimal(0) : kpiBean.getSumOfAfyp());
					talkDate = (kpiBean.getAttendant() == null ? new Integer(0) : kpiBean.getAttendant());
					talkHrs = (kpiBean.getSumTotalTalk() == null ? new BigDecimal(0) : kpiBean.getSumTotalTalk());
					fcs = (kpiBean.getFirstConfirmSale() == null ? new Integer(0) : kpiBean.getFirstConfirmSale().intValue());
					sale = (kpiBean.getCountAll() == null ? new Integer(0) : kpiBean.getCountAll().intValue());
					successEoc = (kpiBean.getSuccessEoc() == null ? new Integer(0) : kpiBean.getSuccessEoc());
					allEoc = (kpiBean.getAllEoc() == null ? new Integer(0) : kpiBean.getAllEoc());
					
				} else {
					afyp = (kpiBean.getSumOfAfyp() == null ? new BigDecimal(0) : kpiBean.getSumOfAfyp()).add(kpi.getSumOfAfyp());
					talkDate = (kpiBean.getAttendant() == null ? new Integer(0) : kpiBean.getAttendant());
					talkHrs = (kpiBean.getSumTotalTalk() == null ? new BigDecimal(0) : kpiBean.getSumTotalTalk());
					fcs = (kpiBean.getFirstConfirmSale() == null ? new Integer(0) : kpiBean.getFirstConfirmSale().intValue()) + kpi.getFirstConfirmSale().intValue();
					sale = (kpiBean.getCountAll() == null ? new Integer(0) : kpiBean.getCountAll().intValue()) + kpi.getAllSale();
					successEoc = (kpiBean.getSuccessEoc() == null ? new Integer(0) : kpiBean.getSuccessEoc()) + kpi.getSuccessEoc();
					allEoc = (kpiBean.getAllEoc() == null ? new Integer(0) : kpiBean.getAllEoc()) + kpi.getAllEoc();
				}

				kpi.setSumOfAfyp(afyp);
				kpi.setCountTalkDate(talkDate);
				kpi.setTotalTalkHrs(talkHrs);
				kpi.setFirstConfirmSale(fcs);
				kpi.setAllSale(sale);
				kpi.setSuccessEoc(successEoc);
				kpi.setAllEoc(allEoc);
				
				tsrMap.put(tsrCode, kpi);
				
			}
			
			addKpiResultFromSuperMap(campaignMap);
			
		} catch(Exception e) {
			throw e;
		}
	}
	
	private void addKpiResultFromSuperMap(Object obj) throws Exception {
		if(obj instanceof Map<?, ?>) {
			for(Object key : ((Map<?, ?>) obj).keySet()) {
				addKpiResultFromSuperMap(((Map<?, ?>) obj).get(key));
			}
		} else if(obj instanceof KpiResult) {
			addKpiResult((KpiResult) obj);
		} else {
			throw new Exception("Not found instance of " + obj.getClass());
		}
	}
	
	public KpiResult addKpiResult(KpiResult kpiResult) throws Exception {
		KpiResultBo bo = (KpiResultBo) AppConfig.getInstance().getBean("kpiResultBo");
		try {
			return bo.addKpiResult(kpiResult, USER_LOGIN);
		} catch(Exception e) {
			throw e;
		}
	}
	
//	public KpiResult addOrUpdateKpiResult(KpiBean kpiBean, String yearMonth) {
	public KpiResult addOrUpdateKpiResult(KpiBean kpiBean, String yearMonth, String delimKeyCodeWB) {
		if(kpiBean != null) {
			try {
				
				KpiResultBo bo = (KpiResultBo) AppConfig.getInstance().getBean("kpiResultBo");

				String campaignCode = kpiBean.getCampaignCode();
				String dsmCode = kpiBean.getDsmCode();
				String tsmCode = kpiBean.getTsmCode();
				String tsrCode = kpiBean.getTsrCode();
				
//				<Process>
				String hql = " from KpiResult d"
						+ " where 1 = 1"
						+ " and d.yearMonth = ? "
						+ " and d.campaign.code = ? "
						+ " and d.dsmInfo.tsrCode = ? "
						+ " and d.tsmInfo.tsrCode = ? "
						+ " and d.tsrInfo.tsrCode = ? ";
				
				KpiResult k = null;
				
				BigDecimal afyp = null;
				Integer talkDate = null;
				BigDecimal talkHrs = null;
				Integer fcs = null;
				Integer sale = null;
				Integer successEoc = null;
				Integer allEoc = null;
				boolean isNew = false;
				
				
				List<KpiResult> list = bo.findByHql(hql, yearMonth, campaignCode, dsmCode, tsmCode, tsrCode);
				if(list.size() == 1) {
					k = list.get(0);
				}

				if(k == null) {
					k = new KpiResult();
					
					k.setTsrInfo(this.getTsrInfoInMap(tsrCode));
					k.setTsmInfo(this.getTsrInfoInMap(tsmCode));
					k.setDsmInfo(this.getTsrInfoInMap(dsmCode));
					k.setCampaign(this.getCampaignInMap(campaignCode));
					k.setKeyCode(kpiBean.getKeyCode());
					k.setYearMonth(yearMonth);
					
					afyp = (kpiBean.getSumOfAfyp() == null ? new BigDecimal(0) : kpiBean.getSumOfAfyp());
					talkDate = (kpiBean.getAttendant() == null ? new Integer(0) : kpiBean.getAttendant());
					talkHrs = (kpiBean.getSumTotalTalk() == null ? new BigDecimal(0) : kpiBean.getSumTotalTalk());
					fcs = (kpiBean.getFirstConfirmSale() == null ? new Integer(0) : kpiBean.getFirstConfirmSale().intValue());
					sale = (kpiBean.getCountAll() == null ? new Integer(0) : kpiBean.getCountAll().intValue());
					successEoc = (kpiBean.getSuccessEoc() == null ? new Integer(0) : kpiBean.getSuccessEoc());
					allEoc = (kpiBean.getAllEoc() == null ? new Integer(0) : kpiBean.getAllEoc());
					
					isNew = true;
				} else {

					afyp = (kpiBean.getSumOfAfyp() == null ? new BigDecimal(0) : kpiBean.getSumOfAfyp()).add(k.getSumOfAfyp());
					talkDate = (kpiBean.getAttendant() == null ? new Integer(0) : kpiBean.getAttendant()) + k.getCountTalkDate();
					talkHrs = (kpiBean.getSumTotalTalk() == null ? new BigDecimal(0) : kpiBean.getSumTotalTalk()).add(k.getTotalTalkHrs());
					fcs = (kpiBean.getFirstConfirmSale() == null ? new Integer(0) : kpiBean.getFirstConfirmSale().intValue()) + k.getFirstConfirmSale().intValue();
					sale = (kpiBean.getCountAll() == null ? new Integer(0) : kpiBean.getCountAll().intValue()) + k.getAllSale();
					successEoc = (kpiBean.getSuccessEoc() == null ? new Integer(0) : kpiBean.getSuccessEoc()) + k.getSuccessEoc();
					allEoc = (kpiBean.getAllEoc() == null ? new Integer(0) : kpiBean.getAllEoc()) + k.getAllEoc();
				}
				
				k.setSumOfAfyp(afyp);
				k.setCountTalkDate(talkDate);
				k.setTotalTalkHrs(talkHrs);
				k.setFirstConfirmSale(fcs);
				k.setAllSale(sale);
				k.setSuccessEoc(successEoc);
				k.setAllEoc(allEoc);
				return isNew ? bo.addKpiResult(k, USER_LOGIN) : bo.updateKpiResult(k, USER_LOGIN);

				
//				<!-- Old method -->
//				KpiResult k = findKpiResultByDateAndCampaign(yearMonth, kpiBean.getCampaignCode(), kpiBean.getKeyCode(), kpiBean.getTsrCode(), tsmCode, dsmCode);
//				if(k != null) {
//					if(!k.getTsrInfo().getTsrCode().equals(kpiBean.getTsrCode())) k.setTsrInfo(getTsrInfoInMap(kpiBean.getTsrCode()));
//					if(!k.getTsmInfo().getTsrCode().equals(tsmCode)) k.setTsmInfo(getTsrInfoInMap(tsmCode));
//					if(!k.getDsmInfo().getTsrCode().equals(dsmCode)) k.setDsmInfo(getTsrInfoInMap(dsmCode));
//					
//					k.setSumOfAfyp(kpiBean.getSumOfAfyp());
//					k.setCountTalkDate(kpiBean.getCountTalkDate());
//					k.setTotalTalkHrs(kpiBean.getSumTotalTalk());
//					k.setFirstConfirmSale(kpiBean.getFirstConfirmSale().intValue());
//					k.setAllSale(kpiBean.getCountAll().intValue());
//					k.setSuccessEoc(kpiBean.getSuccessEoc());
//					k.setAllEoc(kpiBean.getAllEoc());
//					return bo.updateKpiResult(k, USER_LOGIN);
//				} else {
//					k = new KpiResult();
//					k.setTsrInfo(this.getTsrInfoInMap(kpiBean.getTsrCode()));
//					k.setTsmInfo(this.getTsrInfoInMap(tsmCode));
//					k.setDsmInfo(this.getTsrInfoInMap(dsmCode));
//					k.setCampaign(this.getCampaignInMap(kpiBean.getCampaignCode()));
//					k.setKeyCode(kpiBean.getKeyCode());
//					k.setYearMonth(yearMonth);
//					
//					k.setSumOfAfyp(kpiBean.getSumOfAfyp());
//					k.setCountTalkDate(kpiBean.getCountTalkDate());
//					k.setTotalTalkHrs(kpiBean.getSumTotalTalk());
//					k.setFirstConfirmSale(kpiBean.getFirstConfirmSale().intValue());
//					k.setAllSale(kpiBean.getCountAll().intValue());
//					k.setSuccessEoc(kpiBean.getSuccessEoc());
//					k.setAllEoc(kpiBean.getAllEoc());
//					return bo.addKpiResult(k, USER_LOGIN);
//				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void saveTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfo check = getTsrInfoInMap(tsrInfo.getTsrCode());
		if(check == null) {
			tsrInfo = addTsrInfo(tsrInfo);
			System.out.println("ADD Tsr: " + tsrInfo.toString());
		} else {
			check.setFirstName(tsrInfo.getFirstName());
			check.setLastName(tsrInfo.getLastName());
			check.setFullName(tsrInfo.getFullName());
			check.setResignDate(tsrInfo.getResignDate());
			tsrInfo = updateTsrInfo(check);
			System.out.println("UPDATE Tsr: " + tsrInfo);
		}
	}
	
	public void saveTsrInfoAndContract(TsrInfo tsrInfo, TsrSite tsrSite, TsrStatus tsrStatus, Date startDate, String tsrCampaign, Date lastDateOfWork) throws Exception {

		TsrInfo check = this.getTsrInfoInMap(tsrInfo.getTsrCode());
		
		if(check != null) {
			if(StringUtils.isNotBlank(check.getTsrCode())) {
				check.setStartDate(startDate);
				check.setResignDate(lastDateOfWork);
				check.setFirstName(tsrInfo.getFirstName());
				check.setLastName(tsrInfo.getLastName());
				check.setFullName(tsrInfo.getFullName());
				
				tsrInfo = updateTsrInfo(tsrInfo);
				
//				TsrContract tsrContract = tsrInfo.getTsrContract();
//				if(tsrContract != null) {
//					tsrContract.setTsrSite(tsrSite);
//					tsrContract.setTsrStatus(tsrStatus);
//					tsrContract.setStartDate(startDate);
////					tsrContract.setResignDate(lastDateOfWork);
//					tsrContract.setTsrCampaign(tsrCampaign);
//					tsrContract.setResignDate(lastDateOfWork);
//					updateTsrContract(tsrContract);
//				}
			}
		} else {
			tsrInfo = addTsrInfo(tsrInfo);
			
//			TsrContract tsrContract = new TsrContract();
//			tsrContract.setTsrInfo(tsrInfo);
//			tsrContract.setTsrSite(tsrSite);
//			tsrContract.setTsrStatus(tsrStatus);
//			tsrContract.setStartDate(startDate);
//			tsrContract.setResignDate(lastDateOfWork);
//			tsrContract.setTsrCampaign(tsrCampaign);
			
//			addTsrContract(tsrContract);
		}
		
	}
	
	public PolicyInfo addPolicyInfo(PolicyInfo policyInfo) throws Exception {
		if(policyInfo != null) {
			PolicyInfoBo bo = (PolicyInfoBo) AppConfig.getInstance().getBean("policyInfoBo");
			return bo.addPolicyInfo(policyInfo, USER_LOGIN);
		}
		return null;
	}
	
	public PolicyInfoExtRcf savePolicyInfoExtRcf(PolicyInfo policyInfo) throws Exception {
		if(policyInfo != null) {
			PolicyInfoExtRcfBo bo = (PolicyInfoExtRcfBo) AppConfig.getInstance().getBean("policyInfoExtRcfBo");
			
			List<PolicyInfoExtRcf> list = bo.findByHql(" from PolicyInfoExtRcf d "
					+ " where d.policyInfo.xRef = ? ", policyInfo.getxRef());
			
			if(list != null) {
				if(list.size() == 1) {
//					do nothing
				} else if(list.size() > 1) {
					throw new Exception("ERROR: Found Policy Info Ext Rcf more than 1 => " + Arrays.toString(list.toArray()));
				}
			} else {
				return bo.add(new PolicyInfoExtRcf(policyInfo, "Y"), USER_LOGIN);
			}
			
		}
		return null;
	}
	
	public PolicyStatus addPolicyStatus(PolicyStatus policyStatus) throws Exception {
		if(policyStatus != null) {
			PolicyStatusBo bo = (PolicyStatusBo) AppConfig.getInstance().getBean("policyStatusBo");
			return bo.addPolicyStatus(policyStatus, USER_LOGIN);
		}
		return policyStatus;
	}
	
	public SalesRecord addSalesRecord(SalesRecord salesRecord) throws Exception {
		if(salesRecord != null) {
			SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
//			initSalesRecordByMonth(salesRecord.getSaleDate());
			return bo.addSalesRecord(salesRecord, USER_LOGIN);
		}
		return null;
	}
	
	public TsrTalkTime addTalkTime(TsrTalkTime tsrTalkTime) throws Exception {
		TsrTalkTimeBo tsrTalkTimeBo = (TsrTalkTimeBo) AppConfig.getInstance().getBean("tsrTalkTimeBo");
		
		return tsrTalkTimeBo.addTsrTalkTime(tsrTalkTime, USER_LOGIN);
	}
	
	public TsrPerformance addTsrPerformance(TsrPerformance tsrPerformance) throws Exception {
		TsrPerformanceBo bo = (TsrPerformanceBo) AppConfig.getInstance().getBean("tsrPerformanceBo");
		return bo.addTsrPerformance(tsrPerformance, USER_LOGIN);
	}

	public TsrCodeReplacer addTsrCodeReplacer(String tsrCode, String ownerFullName, String replacerFullName, String keyCode) throws Exception {
		TsrCodeReplacer t = new TsrCodeReplacer(tsrCode, ownerFullName, replacerFullName, keyCode);
		return addTsrCodeReplacer(t);
	}
	
	public TsrCodeReplacer addTsrCodeReplacer(TsrCodeReplacer tsrCodeReplacer) throws Exception {
		TsrCodeReplacerBo bo = (TsrCodeReplacerBo) AppConfig.getInstance().getBean("tsrCodeReplacerBo");
		return bo.addTsrCodeReplacer(tsrCodeReplacer, USER_LOGIN);
	}
	
	public TsrContract addTsrContract(TsrContract tsrContract) throws Exception {
		TsrContractBo tsrContractBo = (TsrContractBo) AppConfig.getInstance().getBean("tsrContractBo");
		return tsrContractBo.addTsrContract(tsrContract, USER_LOGIN);
	}
	
	public TsrHierarchical addTsrHierarchical(TsrInfo tsrInfo, TsrInfo uplineInfo, Campaign campaign, Date startDate, Date endDate) throws Exception {
		TsrHierarchicalBo bo = (TsrHierarchicalBo) AppConfig.getInstance().getBean("tsrHierarchicalBo");
		TsrHierarchical t = new TsrHierarchical();
		t.setTsrInfo(tsrInfo);
		t.setUplineInfo(uplineInfo);
		t.setCampaign(campaign);
		t.setStartDate(startDate);
		t.setEndDate(endDate);
		return bo.addTsrHierarchical(t, USER_LOGIN);
	}
	
	public TsrInfo addTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfoBo bo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		TsrInfo result = bo.addTsrInfo(tsrInfo, USER_LOGIN);
		return result;
	}
	
	public TsrPosition addTsrPosition(TsrPosition tsrPosition) throws Exception {
		TsrPositionBo bo = (TsrPositionBo) AppConfig.getInstance().getBean("tsrPositionBo");
		return bo.addTsrPosition(tsrPosition, USER_LOGIN);
	}
	
	public KpiEoc addUpdateKpiEoc(String keyCode, String tsrCode, String countSuccess, String countAllEoc) {
		KpiEocBo bo = (KpiEocBo) AppConfig.getInstance().getBean("kpiEocBo");
		try {
			String hql = " from KpiEoc d "
						+ " where 1 = 1"
						+ " and d.campaignKeyCode.keyCode = ? "
						+ " and d.tsrInfo.tsrCode = ? ";
			
			List<KpiEoc> kpiEocs = bo.findByHql(hql, keyCode, tsrCode);
			if(null == kpiEocs || kpiEocs.isEmpty()) {
				return bo.addKpiEoc(new KpiEoc(getCampaignKeyCode(keyCode), getTsrInfoInMap(tsrCode), Integer.parseInt(countSuccess), Integer.parseInt(countAllEoc)), USER_LOGIN);
			} else if(kpiEocs.size() == 1) {
				KpiEoc kpiEoc = kpiEocs.get(0);
				kpiEoc.setCountSuccess(Integer.parseInt(countSuccess));
				kpiEoc.setCountAllEoc(Integer.parseInt(countAllEoc));
				return bo.updateKpiEoc(kpiEoc, USER_LOGIN);
			} else if(kpiEocs.size() > 1) {
				throw new Exception("Found Kpi EOC more than 1 -> keyCode: " + keyCode + " | tsrCode: " + tsrCode);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<KpiCategorySetup> findKpiCategorySetup(Date effectiveDate, Date endDate, String campaignCode, String tsrLevel, String tsrCode) {
		KpiCategorySetupBo bo = (KpiCategorySetupBo) AppConfig.getInstance().getBean("kpiCategorySetupBo");
		try {
			List<Object> objs = new ArrayList<Object>();
			String hql = " from KpiCategorySetup d "
						+ " where d.effectiveDate = ? "
						+ " and d.endDate = ? ";
			objs.add(effectiveDate);
			objs.add(endDate);
			
			if(!StringUtils.isBlank(tsrLevel)) {
				hql += " and d.tsrLevel = ?";
				objs.add(tsrLevel);
			}
			if(!StringUtils.isBlank(campaignCode)) {
				hql += " and d.campaign.code = ? ";
				objs.add(campaignCode);
			}
			if(!StringUtils.isBlank(tsrCode)) {
				hql += " and d.personal.tsrCode = ?";
				objs.add(tsrCode);
			}
			
			List<KpiCategorySetup> list = bo.findByHql(hql, objs.toArray());
			if(list != null && !list.isEmpty()) {
				return list;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void deleteKpiResultByYearMonth(String yearMonth) throws Exception {
		KpiResultBo bo = (KpiResultBo) AppConfig.getInstance().getBean("kpiResultBo");
		KpiResult example = new KpiResult();
		example.setYearMonth(yearMonth);
		List<KpiResult> list = bo.findKpiResult(example);
		for(KpiResult k : list) {
			bo.deleteKpiResult(k);
		}
	}
	
	public KpiResult findKpiResultByDateAndCampaign(String yearMonth, String campaignCode, String keyCode, String tsrCode, String tsmCode, String dsmCode) {
		try {
			KpiResultBo bo = (KpiResultBo) AppConfig.getInstance().getBean("kpiResultBo");
			List<String> objs = new ArrayList<>();
			
			String hql = " from KpiResult d "
						+ " where 1 = 1"
						+ " and d.yearMonth = ? ";
			objs.add(yearMonth);
			
			if(StringUtils.isNoneBlank(campaignCode)) {
				hql += " and d.campaign.code = ? ";
				objs.add(campaignCode);
			}

			if(StringUtils.isNoneBlank(keyCode)) {
				hql += " and d.keyCode = ? ";
				objs.add(keyCode);
			}
			
			if(StringUtils.isNoneBlank(tsrCode)) {
				hql += " and d.tsrInfo.tsrCode = ? ";
				objs.add(tsrCode);
			}
			
			if(StringUtils.isNoneBlank(tsmCode)) {
				hql += " and d.tsmInfo.tsrCode = ? ";
				objs.add(tsmCode);
			}
			
			if(StringUtils.isNoneBlank(dsmCode)) {
				hql += " and d.dsmInfo.tsrCode = ? ";
				objs.add(dsmCode);
			}
			
			List<KpiResult> list = bo.findByHql(hql, objs.toArray());
			if(null != list && !list.isEmpty() && list.size() == 1) {
				return list.get(0);
			} else if(null != list && !list.isEmpty()) {
				System.out.println("Size more than 1");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Campaign> getCampaign(Campaign example) throws Exception {
		CampaignBo campaignBo = (CampaignBo) AppConfig.getInstance().getBean("campaignBo");
		return campaignBo.findCampaign(example);
	}

	public Campaign getCampaignByCode(String code) throws Exception {
		Campaign ex = new Campaign();
		ex.setCode(code);
		List<Campaign> list = getCampaign(ex);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public Campaign getCampaignByKeyCode(String keyCode) throws Exception {
		return getCampaignKeyCode(keyCode).getCampaign();
	}

	public Campaign getCampaignInMap(String campaignCode) {
		if(campaignMap != null) {
			return campaignMap.get(campaignCode);
		}
		return null;
	}
	
	public List<KpiScoreRate> getKpiScoreRateAll() {
		return kpiScoreRates;
	}
	
	private void initCampaignKeyCodeMap() {
		CampaignKeyCodeBo bo = (CampaignKeyCodeBo) AppConfig.getInstance().getBean("campaignKeyCodeBo");
		List<CampaignKeyCode> list = null;
		try {
			list = bo.findCampaignKeycodeAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		keyCodeMap = new HashMap<>();
		for(CampaignKeyCode k : list) {
			keyCodeMap.put(k.getKeyCode(), k);
		}
	}
	
	public CampaignKeyCode getCampaignKeyCode(String keyCode) throws Exception {
		return keyCodeMap.get(keyCode);
	}

	private void initCustomerMap() {
		try {
			CustomerBo bo = (CustomerBo) AppConfig.getInstance().getBean("customerBo");
			List<Customer> list = bo.findCustomerAll();
			customerMap = new HashMap<>();
			for(Customer c : list) {
				customerMap.put(c.getFullName().replaceAll(" ", ""), c);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Customer getCustomerByName(String name) throws Exception {
		if(name != null) {
//			String hql = "from Customer d "
//					+ "where replace(replace(FULL_NAME, ' ', ''), ' ', '') = ?";
//			
//			CustomerBo bo = (CustomerBo) AppConfig.getInstance().getBean("customerBo");
//			
//			String param = name.replaceAll(" ", "");
//			List<Customer> list = bo.findByHql(hql, param);
//			
//			if(null != list && !list.isEmpty()) {
//				return list.get(0);
//			}
			return customerMap.get(name.replaceAll(" ", ""));
		}
		return null;
	}
	
	public String getKeyCodeFromCampaignListLot(String val) {
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
	
	public List<KpiBean> getKpi(String yyyyMM) {
		KpiBeanBo bo = (KpiBeanBo) AppConfig.getInstance().getBean("kpiBeanBo");
		try {
			return bo.getKpi(yyyyMM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<KpiRetention> findKpiRetentionByYearMonth(String yyyyMM) {
		KpiRetentionBo bo = (KpiRetentionBo) AppConfig.getInstance().getBean("kpiRetentionBo");
		try {
			return bo.getKpiRetentionByYearMonth(yyyyMM);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<KpiResult> getKpiResults(String yearMonth) {
		KpiResultBo bo = (KpiResultBo) AppConfig.getInstance().getBean("kpiResultBo");
		try {
			String hql = " from KpiResult d"
						+ " where d.yearMonth = ? "
						+ " order by d.campaign, d.dsmInfo.tsrCode, d.tsmInfo.tsrCode, d.tsrInfo.tsrCode";
			List<KpiResult> list = bo.findByHql(hql, yearMonth);
			if(null != list && !list.isEmpty()) {
				return list;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<KpiCategorySetup> getKpiCategorySetups(KpiCategorySetup example) {
		try {
			KpiCategorySetupBo bo = (KpiCategorySetupBo) AppConfig.getInstance().getBean("kpiCategorySetupBo");
			List<KpiCategorySetup> list = bo.findKpiCategory(example);
			if(null != list && !list.isEmpty()) {
				return list;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getMsigWBKeycode(String processDate) throws Exception {
		KpiCategorySetupBo bo = (KpiCategorySetupBo) AppConfig.getInstance().getBean("kpiCategorySetupBo");
		
		String hql = " from KpiCategorySetup d "
				+ " where 1 = 1 "
				+ " and d.keyCode is not null "
				+ "and CONVERT(nvarchar(6), d.effectiveDate, 112) <= ? "
				+ "and CONVERT(nvarchar(6), d.endDate, 112) >= ? ";

		List<KpiCategorySetup> list = bo.findByHql(hql, processDate, processDate);
		
		String result = "";
		for(KpiCategorySetup data : list) {
			if(!result.contains(data.getKeyCode())) {
				result += data.getKeyCode() + ",";
			}
		}
		
		if(StringUtils.isNoneBlank(result) && result.endsWith(",")) {
			result = result.substring(0, result.lastIndexOf(","));
		}
		
		return result;
	}
	
	public List<KpiCategorySetup> getKpiCategorySetups(String tsrLevel, String campaignCode, String keyCode, String tsrCode, String processYearMonth) {
		try {
			KpiCategorySetupBo bo = (KpiCategorySetupBo) AppConfig.getInstance().getBean("kpiCategorySetupBo");
			List<Object> objs = new ArrayList<>();
			
			String hql = " from KpiCategorySetup d "
					+ " where 1 = 1 ";
			
			if(!StringUtils.isBlank(tsrLevel)) {
				hql += " and d.tsrLevel = ? ";
				objs.add(tsrLevel);
			}
			
			if(!StringUtils.isBlank(campaignCode)) {
				hql += " and d.campaign.code = ? ";
				objs.add(campaignCode);
			}
			
			if(StringUtils.isBlank(keyCode)) {
				hql += " and d.keyCode is null ";
			} else {
				hql += " and d.keyCode = ? ";
				objs.add(keyCode);
			}
			
			if(!StringUtils.isBlank(tsrCode)) {
				hql += " and d.personal.tsrCode = ? ";
				objs.add(tsrCode);
			}
			
			if(!StringUtils.isBlank(processYearMonth)) {
				hql += "and CONVERT(nvarchar(6), d.effectiveDate, 112) <= ? ";
				objs.add(processYearMonth);
				hql += "and CONVERT(nvarchar(6), d.endDate, 112) >= ? ";
				objs.add(processYearMonth);
			}
			
			hql += " order by d.id ";
			
			List<KpiCategorySetup> list = bo.findByHql(hql, objs.toArray());
			if(null != list && !list.isEmpty()) {
				return list;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<KpiScoreRate> getKpiScoreRate(KpiScoreRate kpiScoreRate) {
		KpiScoreRateBo bo = (KpiScoreRateBo) AppConfig.getInstance().getBean("kpiScoreRateBo");
		try {
			List<KpiScoreRate> list = bo.findKpiScoreRate(kpiScoreRate);
			if(list != null && !list.isEmpty()) {
				return list;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getMid3Letter(String v) throws Exception {
		return v.length() < 3 ? v : v.substring(v.length() / 2 - 1, v.length() / 2 + 2);
	}
	
	public PolicyInfo getPolicyInfoByCustomerName(String customerName, Date saleDate) throws Exception {
		if(!StringUtils.isBlank(customerName)) {
			
			String hql = " from PolicyInfo d "
					+ " where replace(replace(d.customer.fullName, ' ', ''), ' ', '') like ? "
					+ " order by d.id";
			PolicyInfoBo bo = (PolicyInfoBo) AppConfig.getInstance().getBean("policyInfoBo");
			String param = "%" + customerName.replaceAll("  ", "").replaceAll(" ", "").trim() + "%";
			
			List<PolicyInfo> list = bo.findByHql(hql, param);
			if(null != list && !list.isEmpty()) {
				if(list.size() == 1) {
					return list.get(0);
				} else {
					String sDate = DateUtil.convDateToString("yyyy-MM-dd", saleDate);
					SalesRecordBo srbo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
					String hql2 = " from SalesRecord s "
							+ " where 1 = 1"
							+ " and s.policyInfo.xRef = ? "
							+ " and s.saleDate = ? ";
					
					System.err.println("Found Policy more than one > name: " + customerName);
					for(PolicyInfo info : list) {
						System.out.println("xRef: " + info.getxRef() + " | saleDate: " + sDate);
						List<SalesRecord> sales = srbo.findByHQL(hql2, info.getxRef(), DateUtil.convStringToDate("yyyy-MM-dd", sDate));
						if(sales != null && !sales.isEmpty() && sales.size() == 1) {
							return sales.get(0).getPolicyInfo();
						}
					}
					
					throw new Exception("Found Policy more than one > name: " + customerName 
							+ " | param: " + param + " | policy: " + Arrays.toString(list.toArray()));
				}
			} else {
				throw new Exception("Policy not found: " + customerName + " | param: " + param + " | with hql: " + hql);
			}
		} else {
			throw new Exception("Customer Name is Blank");
		}
		
	}

	public PolicyInfo getPolicyInfoByXRef(String xRef) throws Exception {
		PolicyInfoBo bo = (PolicyInfoBo) AppConfig.getInstance().getBean("policyInfoBo");
		PolicyInfo ex = new PolicyInfo();
		ex.setxRef(xRef);
		List<PolicyInfo> list = bo.findPolicyInfo(ex);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
//	public TsrInfo getTsrInfoByNameAdvanceMode(String fullName, String keyCode) {
//		
//		return null;
//	}
	
	public PolicyStatus getPolicyStatus(Date saleDate, String xRef, String qaStatus) throws Exception {
		PolicyStatusBo bo = (PolicyStatusBo) AppConfig.getInstance().getBean("policyStatusBo");
		String hql = " from PolicyStatus d "
				+ " where d.saleDate = ? "
				+ " and d.policyInfo.xRef = ? "
				+ " and d.qaStatus.qaValue = ? ";
		List<PolicyStatus> list = bo.findByHql(hql, saleDate, xRef.trim(), qaStatus.trim());
		if(null != list && !list.isEmpty()) {
			if(list.size() == 1) {
				return list.get(0);
			} else {
				throw new Exception("Found duplicate policy status: " + saleDate + " | " + xRef + " | " + qaStatus);
			}
		}
		return null;
	}
	
	public ProductType getProductTypeByValue(String value) throws Exception {
		ProductTypeBo bo = (ProductTypeBo) AppConfig.getInstance().getBean("productTypeBo");
		ProductType ex = new ProductType();
		ex.setValue(value);
		List<ProductType> list = bo.findProductType(ex);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public QaStatus getQaStatus(QaStatus example) throws Exception {
		QaStatusBo bo = (QaStatusBo) AppConfig.getInstance().getBean("qaStatusBo");
		List<QaStatus> list = bo.findQaStatus(example);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		} else if(list != null && list.size() > 1) {
			throw new Exception("Found QA Status more than 1: " + example.toString());
		} else {
			throw new Exception("QA Status not found");
		}
	}
	
	public QaStatus getQaStatus(String value) throws Exception {
		QaStatus q = new QaStatus();
		q.setQaValue(value);
		return getQaStatus(q);
	}
	
	public List<QaStatus> getQaStatusAll() throws Exception {
		QaStatusBo bo = (QaStatusBo) AppConfig.getInstance().getBean("qaStatusBo");
		return bo.findQaStatusAll();
	}
	
	public QaStatus getQaStatusInMap(String qaValue) {
		if(qaMap != null) {
			return qaMap.get(qaValue);
		}
		return null;
	}
	
	public List<SalesRecord> getSalesRecordByDate(String mDate) throws Exception {
		SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
		String hql = " from SalesRecord d "
					+ " where CONVERT(nvarchar(6), d.saleDate, 112) = ?";
		
		return bo.findByHQL(hql, mDate);
	}
	
	public List<TsrCodeReplacer> getTsrCodeReplacer(String tsrCode, String ownerFullName, String replacerFullName, String keyCode) throws Exception {
		TsrCodeReplacer t = new TsrCodeReplacer(tsrCode, ownerFullName, replacerFullName, keyCode);
		return getTsrCodeReplacer(t);
	}
	
	public List<TsrCodeReplacer> getTsrCodeReplacer(TsrCodeReplacer tsrCodeReplacer) throws Exception {
		TsrCodeReplacerBo bo = (TsrCodeReplacerBo) AppConfig.getInstance().getBean("tsrCodeReplacerBo");
		return bo.findTsrCodeReplacer(tsrCodeReplacer);
	}
	
	public TsrContract getTsrContractByTsrId(Long tsrId) throws Exception {
		TsrContractBo bo = (TsrContractBo) AppConfig.getInstance().getBean("tsrContractBo");
		String hql = " from TsrContract d "
				+ " where 1 = 1 "
				+ " and d.id = ?";
		List<TsrContract> list = bo.findByHql(hql, tsrId);
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public TsrHierarchical getTsrHierarchical(String tsrCode, String uplineCode, String campaignCode, Date startDate, Date endDate) throws Exception {
		TsrHierarchicalBo bo = (TsrHierarchicalBo) AppConfig.getInstance().getBean("tsrHierarchicalBo");
		List<Object> objs = new ArrayList<>();
		
		String hql = " from TsrHierarchical d "
				+ " where 1 = 1";
		
		if(!StringUtils.isBlank(tsrCode)) {
			hql += " and d.tsrInfo.tsrCode = ? ";
			objs.add(tsrCode);
		}
		
		if(!StringUtils.isBlank(uplineCode)) {
			hql += " and d.uplineInfo.tsrCode = ? ";
			objs.add(uplineCode);
		}
		
		if(!StringUtils.isBlank(campaignCode)) {
			hql += " and d.campaign.code = ? ";
			objs.add(campaignCode);
		}
		
		if(startDate != null) {
			hql += " and d.startDate = ? ";
			objs.add(startDate);
		}
		
		if(endDate != null) {
			hql += " and d.endDate = ? ";
			objs.add(endDate);
		}
		
		List<TsrHierarchical> list = bo.findByHql(hql, objs.toArray());
		if(list != null && !list.isEmpty() && list.size() == 1) {
			return list.get(0);
		} else if(list !=null && list.size() > 1) {
			throw new Exception("Found Tsr Hierarchical more than 1 ==> tsrCode: " + tsrCode + " | uplineCode: " + uplineCode + " | campaignCode: " + campaignCode 
					+ " | startDate:" + startDate + " | endDate: " + endDate);
		} else {
			return null;
		}
	}
	
	public List<TsrHierarchical> getTsrHierarchical(String tsrCode, String tmrCode, String campaignCode, Date saleDate) throws Exception {
		TsrHierarchicalBo bo = (TsrHierarchicalBo) AppConfig.getInstance().getBean("tsrHierarchicalBo");
		List<Object> vals = new ArrayList<>();
		String hql = " from TsrHierarchical d "
				+ " where 1 = 1";
				
				if(!StringUtils.isBlank(tsrCode)) {
					hql += " and d.tsrInfo.tsrCode = ? ";
					vals.add(tsrCode);
				}
				
				if(!StringUtils.isBlank(tmrCode)) {
					hql += " and d.uplineInfo.tsrCode = ? ";
					vals.add(tmrCode);
				}
				
				if(!StringUtils.isBlank(campaignCode)) {
					hql += " and d.campaign.code = ? ";
					vals.add(campaignCode);
				}
				
				if(saleDate != null) {
					hql += " and (d.startDate <= ? and (d.endDate is null or d.endDate >= ? ))";
					vals.add(saleDate);
					vals.add(saleDate);
				}
		
		List<TsrHierarchical> list = bo.findByHql(hql, vals.toArray());
		if(null != list && !list.isEmpty() && list.size() == 1) {
			return list;
		} else if(null != list && list.size() > 1) {
//			log
			System.err.println("Found TSR Hierarchical more than 1 ==> tsrCode: " + tsrCode + " | tmrCode: " + tmrCode + " | campaignCode: " + campaignCode + " | saleDate: " + saleDate);
			return list;
		}
		return null;
	}
	
	public TsrHierarchical getDsmHierarchical(String code, Date saleDate) throws Exception {
		for(TsrHierarchical h : dsmList) {
//			String format = "yyyyMMdd";
//			saleDate = DateUtil.convStringToDate(format, DateUtil.convDateToString(format, saleDate));
//			System.out.println("dsm: " + h.getUplineInfo().getTsrCode() + " | startDate: " + h.getStartDate() + " | endDate: " + h.getEndDate());
			
			if(h.getTsrInfo().getTsrCode().equals(code) 
					&& (h.getStartDate().compareTo(saleDate) <= 0 && h.getEndDate().compareTo(saleDate) >= 0)) {
				return h;
			}
		}
		System.err.println("not found DSM for " + code + " | saleDate: " + saleDate);
		return null;
	}
	
	public TsrInfo getTsrInfo(String tsrCode) throws Exception {
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		TsrInfo ex = new TsrInfo();
		ex.setTsrCode(tsrCode);
		List<TsrInfo> list = tsrInfoBo.findTsrInfo(ex);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public TsrInfo getTsrInfo(String first, String last) throws Exception {
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		
		String hql = " from TsrInfo d "
				+ " where (d.firstName like ? or d.lastName like ?) ";
		String p1 = "%" + first.trim() + "%";
		String p2 = "%" + last.trim() + "%";
		List<TsrInfo> list = tsrInfoBo.findByHql(hql, p1, p2);
		if(null != list && !list.isEmpty() && list.size() == 1) {
			return list.get(0);
		} else if(null != list && !list.isEmpty()) {
			System.out.println("Found TsrInfo more than 1");
			
			int countFirstName = 0;
			int countLastName = 0;
			
			for(TsrInfo t : list) {
				if(t.getFirstName().equalsIgnoreCase(first)) {
					countFirstName++;
				}
				if(t.getLastName().equalsIgnoreCase(last)) {
					countLastName++;
				}
			}
			
			if(countFirstName > countLastName) {
				for(TsrInfo t : list) {
					if(t.getFirstName().equalsIgnoreCase(first)) {
						if(t.getFirstName().equals(first) && t.getLastName().contains(getMid3Letter(last))) {
							return t;
						}
					}
				}
			} else if(countFirstName < countLastName) {
				for(TsrInfo t : list) {
					if(t.getFirstName().equalsIgnoreCase(first)) {
						if(t.getLastName().equals(last) && t.getFirstName().contains(getMid3Letter(first))) {
							return t;
						}
					}
				}
			}
		} else {
			
		}
		return null;
	}
	
	public List<TsrInfo> getTsrInfoAll() throws Exception {
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		return tsrInfoBo.findTsrInfoAll();
	}
	
//	public TsrInfo getTsrInfoByName(String fullName) throws Exception {
//		if(!StringUtils.isBlank(fullName)) {
//			TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
//			String hql = " from TsrInfo d "
//					+ " where replace(replace(d.fullName, ' ', ''), ' ', '') like ? ";
//			String param = removeTitle(fullName).replaceAll(" ", "").replaceAll(" ", "");
//			List<TsrInfo> list = tsrInfoBo.findByHql(hql, "%" + param + "%");
//			if(null != list && !list.isEmpty() && list.size() == 1) {
//				return list.get(0);
//			} else if(null != list && !list.isEmpty() && list.size() > 1) {
////				for(TsrInfo tsr : list) {
////					for(TsrContract c : tsr.getTsrContracts()) {
////						if(c.getTsrStatus().getParam().equalsIgnoreCase("A")) {
////							return tsr;
////						}
////					}
////				}
////				throw new Exception("Cannot get TsrInfo or already resign: " + fullName);
//				return list.get(1);
//			} else {
//				param = param.length() <= 6 ? null : param.substring(param.length() / 2 - 3, param.length() / 2 + 3);
//				return getTsrInfoByName(param);
//			}
//		}
//		return null;
//	}
	
	public TsrInfo getTsrInfoByName(String fullName, Date saleDate) throws Exception {
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		String hql = " from TsrInfo d "
				+ " where replace(replace(d.fullName, ' ', ''), ' ', '') = ?";
		String param = removeTitle(fullName).replaceAll(" ", "").replaceAll(" ", "");
		List<TsrInfo> list = tsrInfoBo.findByHql(hql, param);
		if(list != null && !list.isEmpty()) {
			if(saleDate != null) {
				for(TsrInfo t : list) {
					if(t.getResignDate() == null) {
						return t;
					} else if(t.getResignDate().compareTo(saleDate) >= 0) {
						return t;
					}
				}
			} else {
				return list.size() != 0 && list.size() == 1 ? list.get(0) : list.get(1);
			}
		}
		return null;
	}
	
	public TsrInfo getTsrInfoByNameAdvanceMode(String fullName, String keyCode, Date saleDate) throws Exception {
		TsrInfo tsrInfo = null;
		String unTitle = this.removeTitle(fullName).replaceAll("  ", " ");
		List<TsrCodeReplacer> rs = null;
		String campaignCode = getCampaignByKeyCode(keyCode).getCode();
		try {
			String hql = " from TsrCodeReplacer d"
					+ " where 1 = 1 "
					+ " and d.ownerFullName = ? "
					+ " and (d.keyCode = ? or d.campaignCode = ?)";
			TsrCodeReplacerBo bo = (TsrCodeReplacerBo) AppConfig.getInstance().getBean("tsrCodeReplacerBo");
			rs = bo.findByHql(hql, unTitle, keyCode, campaignCode);
			
			if(null != rs && !rs.isEmpty()) {
				tsrInfo = KpiService.getInstance().getTsrInfoInMap(rs.get(0).getTsrCode());
			} else {
				tsrInfo = this.getTsrInfoByName(unTitle, saleDate);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return tsrInfo;
	}

	public TsrInfo getTsrInfoInMap(String tsrCode) {
		if(tsrMap != null) {
			return tsrMap.get(tsrCode);
		}
		return null;
	}
	
	public TsrLevel getTsrLevel(String displayLv) throws Exception {
		TsrLevelBo tsrLevelBo = (TsrLevelBo) AppConfig.getInstance().getBean("tsrLevelBo");
		TsrLevel ex = new TsrLevel();
		ex.setDisplay(displayLv);
		List<TsrLevel> list = tsrLevelBo.findTsrLevel(ex);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public TsrPosition getTsrPosition(String positionName) throws Exception {
		return getTsrPosition(new TsrPosition(positionName));
	}
	
	public TsrPosition getTsrPosition(TsrPosition tsrPosition) throws Exception {
		TsrPositionBo bo = (TsrPositionBo) AppConfig.getInstance().getBean("tsrPositionBo");
		List<TsrPosition> list = bo.findTsrPosition(tsrPosition);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public TsrSite getTsrSite(String siteName) throws Exception {
		TsrSiteBo tsrSiteBo = (TsrSiteBo) AppConfig.getInstance().getBean("tsrSiteBo");
		TsrSite ex = new TsrSite();
		ex.setSiteName(siteName);
		List<TsrSite> list = tsrSiteBo.findTsrSite(ex);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public TsrStatus getTsrStatus(String status) throws Exception {
		TsrStatusBo tsrStatusBo = (TsrStatusBo) AppConfig.getInstance().getBean("tsrStatusBo");
		TsrStatus ex = new TsrStatus();
		ex.setParam(status);
		List<TsrStatus> list = tsrStatusBo.findTsrStatus(ex);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public boolean isAlreadyQc(Date qcDate, String xRef) throws Exception {
		boolean result = false;
		PolicyStatusBo bo = (PolicyStatusBo) AppConfig.getInstance().getBean("policyStatusBo");
		String hql = " from PolicyStatus d "
				+ " where d.qcDate = ? "
				+ " and d.policyInfo.xRef = ? ";
		List<PolicyStatus> list = bo.findByHql(hql, qcDate, xRef);
		if(null != list && !list.isEmpty()) {
			result = true;
		}
		return result;
	}
	
	public Campaign isCampaignExist(Campaign example) throws Exception {
		CampaignBo bo = (CampaignBo) AppConfig.getInstance().getBean("campaignBo");
		List<Campaign> list = bo.findCampaign(example);
		if(null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public Campaign isCampaignExist(String campaignCode) throws Exception {
		Campaign ex = new Campaign();
		ex.setCode(campaignCode);
		return isCampaignExist(ex);
	}
	
	public CampaignLead isCampaignLeadExist(Date performanceDate, String keyCode) throws Exception {
		CampaignLeadBo bo = (CampaignLeadBo) AppConfig.getInstance().getBean("campaignLeadBo");
		String hql = "from CampaignLead d"
				+ " where d.performanceDate = ?"
				+ " and d.campaignKeyCode.keyCode = ?";
		List<CampaignLead> list = bo.findByHQL(hql, performanceDate, keyCode);
		if(null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
//	public void saveSalesRecordByMonth(SalesRecord salesRecord) throws Exception {
//		initSalesRecordByMonth(salesRecord.getSaleDate());
//	}
//	
//	private void initSalesRecordByMonth(Date date) throws Exception {
//		SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
//		
//		String hql = " from SalesRecord d "
//				+ " where CONVERT(nvarchar(6), d.saleDate, 112) = ? "
//				+ " order by d.saleDate, d.tsrInfo.tsrCode, d.policyInfo.xRef";
//		List<SalesRecord> list = bo.findByHQL(hql, DateUtil.convDateToString("yyyyMM", date));
//		
//		salesRecordByMonthMap = new HashMap<>();
//		Map<String, Map<String, SalesRecord>> byTsr = null;
//		Map<String, SalesRecord> byXref = null;
//		
//		String saleDate = "";
//		for(SalesRecord sr : list) {
//			saleDate = DateUtil.convDateToString("yyyyMMdd", date);
//			
//			byTsr = salesRecordByMonthMap.get(saleDate);
//			if(byTsr == null) {
//				byTsr = new HashMap<>();
//				salesRecordByMonthMap.put(saleDate, byTsr);
//			}
//			
//			byXref = byTsr.get(sr.getTsrInfo().getTsrCode());
//			if(byXref == null) {
//				byXref = new HashMap<>();
//				byTsr.put(sr.getTsrInfo().getTsrCode(), byXref);
//			}
//			
//			SalesRecord sale = byXref.get(sr.getPolicyInfo().getxRef());
//			if(sale == null) {
//				byXref.put(sr.getPolicyInfo().getxRef(), sr);
//			}
//			
//		}
//		
//		System.out.println("init SalesRecord finished: " + salesRecordByMonthMap.size() + " dates");
//	}
	
	public SalesRecord isSalesRecordExist(Date saleDate, PolicyInfo policyInfo) throws Exception {
		
		SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
		
		String hql = "from SalesRecord d "
				+ " where d.saleDate = ? "
				+ " and d.policyInfo.xRef = ? ";
		
		List<SalesRecord> list = bo.findByHQL(hql, saleDate, policyInfo.getxRef());
		if(null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public List<TsrPerformance> isTalkDateExist(Date talkDate, TsrInfo tsrInfo, String keyCode) throws Exception {
		TsrPerformance tsrPerformance = new TsrPerformance();
		tsrPerformance.setTalkDate(talkDate);
		tsrPerformance.setKeyCode(keyCode);
		
		Example talkTimeExample = Example.create(tsrPerformance);
		Example tsrInfoExample = Example.create(tsrInfo);
		DetachedCriteria criteria = DetachedCriteria.forClass(TsrPerformance.class)
				.add(talkTimeExample).createCriteria("tsrInfo").add(tsrInfoExample);
		
		TsrPerformanceBo bo = (TsrPerformanceBo) AppConfig.getInstance().getBean("tsrPerformanceBo");
		return bo.findByCriteria(criteria);
	}
	
	private void prepareCampaignCode() {
		try {
			campaignMap = new HashMap<>();
			for(Campaign t : getCampaign(new Campaign())) {
				campaignMap.put(t.getCode(), t);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void prepareKpiScoreRate() {
		try {
			kpiScoreRates = getKpiScoreRate(new KpiScoreRate());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void prepareQaStatus() {
		try {
			qaMap = new HashMap<String, QaStatus>();
			for(QaStatus s : this.getQaStatusAll()) {
				qaMap.put(s.getQaValue(), s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshTsrInfoMap() {
		prepareTsrInfo();
	}
	
	private void prepareTsrInfo() {
		try {
			tsrMap = new HashMap<String, TsrInfo>();
			for(TsrInfo t : this.getTsrInfoAll()) {
				tsrMap.put(t.getTsrCode(), t);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String removeTitle(String val) {
		for(String t : TITLES) {
			if(val.startsWith(t)) {
				val = val.replace(t, "").trim();
			}
		}
		return val;
	}
	
	public Campaign updateCampaign(Campaign campaign) throws Exception {
		if(campaign != null) {
			CampaignBo bo = (CampaignBo) AppConfig.getInstance().getBean("campaignBo");
			return bo.updateCampaign(campaign, USER_LOGIN);
		}
		return null;
	}
	
	public CampaignKeyCode updateCampaignKeyCode(CampaignKeyCode campaignKeyCode) throws Exception {
		if(campaignKeyCode != null) {
			CampaignKeyCodeBo bo = (CampaignKeyCodeBo) AppConfig.getInstance().getBean("campaignKeyCodeBo");
			return bo.updateCampaignKeyCode(campaignKeyCode, USER_LOGIN);
		}
		return null;
	}
	
	public CampaignLead updateCampaignLead(CampaignLead campaignLead) throws Exception {
		if(campaignLead != null) {
			CampaignLeadBo bo = (CampaignLeadBo) AppConfig.getInstance().getBean("campaignLeadBo");
			return bo.updateCampaignLead(campaignLead, USER_LOGIN);
		}
		return null;
	}
	
	public PolicyInfo updatePolicyInfo(PolicyInfo policyInfo) throws Exception {
		if(policyInfo != null) {
			PolicyInfoBo bo = (PolicyInfoBo) AppConfig.getInstance().getBean("policyInfoBo");
			return bo.updatePolicyInfo(policyInfo, USER_LOGIN);
		}
		return null;
	}
	
	public PolicyStatus updatePolicyStatus(PolicyStatus policyStatus) throws Exception {
		if(policyStatus != null) {
			PolicyStatusBo bo = (PolicyStatusBo) AppConfig.getInstance().getBean("policyStatusBo");
			return bo.updatePolicyStatus(policyStatus, USER_LOGIN);
		}
		return null;
	}
	
	public SalesRecord updateSalesRecord(SalesRecord salesRecord) throws Exception {
		if(salesRecord != null) {
			SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
			return bo.updateSalesRecord(salesRecord, USER_LOGIN);
		}
		return null;
	}
	
	public TsrTalkTime updateTalkTime(TsrTalkTime tsrTalkTime) throws Exception {
		TsrTalkTimeBo tsrTalkTimeBo = (TsrTalkTimeBo) AppConfig.getInstance().getBean("tsrTalkTimeBo");
		return tsrTalkTimeBo.updateTsrTalkTime(tsrTalkTime, USER_LOGIN);
	}
	
	public TsrPerformance updateTsrPerformance(TsrPerformance tsrPerformance) throws Exception {
		TsrPerformanceBo bo = (TsrPerformanceBo) AppConfig.getInstance().getBean("tsrPerformanceBo");
		return bo.updateTsrPerformance(tsrPerformance, USER_LOGIN);
	}
	
	public TsrContract updateTsrContract(TsrContract tsrContract) throws Exception {
		TsrContractBo tsrContractBo = (TsrContractBo) AppConfig.getInstance().getBean("tsrContractBo");
		return tsrContractBo.updateTsrContract(tsrContract, USER_LOGIN);
	}
	
	public TsrInfo updateTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfoBo bo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		TsrInfo t = bo.updateTsrInfo(tsrInfo, USER_LOGIN);
		return t;
	}
	
	public TsrHierarchical updateTsrHierarchical(TsrHierarchical tsrHierarchical) throws Exception {
		TsrHierarchicalBo bo = (TsrHierarchicalBo) AppConfig.getInstance().getBean("tsrHierarchicalBo");
		return bo.updateTsrHierarchical(tsrHierarchical, USER_LOGIN);
	}
}
