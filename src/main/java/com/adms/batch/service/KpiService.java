package com.adms.batch.service;

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
import com.adms.bo.policystatus.PolicyStatusBo;
import com.adms.bo.producttype.ProductTypeBo;
import com.adms.bo.qastatus.QaStatusBo;
import com.adms.bo.salesrecord.SalesRecordBo;
import com.adms.bo.tsrcodereplacer.TsrCodeReplacerBo;
import com.adms.bo.tsrcontract.TsrContractBo;
import com.adms.bo.tsrhierarchical.TsrHierarchicalBo;
import com.adms.bo.tsrinfo.TsrInfoBo;
import com.adms.bo.tsrlevel.TsrLevelBo;
import com.adms.bo.tsrposition.TsrPositionBo;
import com.adms.bo.tsrsite.TsrSiteBo;
import com.adms.bo.tsrstatus.TsrStatusBo;
import com.adms.bo.tsrtalktime.TsrTalkTimeBo;
import com.adms.bo.tsrtalktimedetail.TsrTalkTimeDetailBo;
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
import com.adms.domain.entities.TsrTalkTimeDetail;
import com.adms.utils.DateUtil;

public class KpiService {

	public static KpiService getInstance() {
		if(instance == null) {
			instance = new KpiService();
		}
		return instance;
	}
	
	private final String USER_LOGIN = "System Admin";
	private Map<String, QaStatus> qaMap;
	private Map<String, TsrInfo> tsrMap;

	private Map<String, Campaign> campaignMap;
	
	List<KpiScoreRate> kpiScoreRates;
	
	private static KpiService instance;

	private final List<String> TITLES = Arrays.asList(new String[]{"นาย", "น.ส.", "นาง", "ว่าที่", "นส."});
	
	public KpiService() {
		System.out.println("New Kpi Service");
		prepareQaStatus();
		prepareTsrInfo();
		prepareCampaignCode();
		prepareKpiScoreRate();
	}
	
	public Campaign addCampaign(Campaign campaign) throws Exception {
		if(campaign != null) {
			CampaignBo bo = (CampaignBo) AppConfig.getInstance().getBean("campaignBo");
			return bo.addCampaign(campaign, USER_LOGIN);
		}
		return null;
	}
	
	public CampaignKeyCode addCampaignKeyCode(CampaignKeyCode campaignKeyCode) throws Exception {
		if(campaignKeyCode != null) {
			CampaignKeyCodeBo bo = (CampaignKeyCodeBo) AppConfig.getInstance().getBean("campaignKeyCodeBo");
			return bo.addCampaignKeyCode(campaignKeyCode, USER_LOGIN);
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
			return bo.addCustomer(customer, USER_LOGIN);
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
	
	public KpiResult addOrUpdateKpiResult(KpiBean kpiBean, String yearMonth) {
		if(kpiBean != null) {
			try {
				KpiResultBo bo = (KpiResultBo) AppConfig.getInstance().getBean("kpiResultBo");
				KpiResult k = findKpiResultByDateAndCampaign(yearMonth, kpiBean.getCampaignCode(), kpiBean.getTsrCode());
				if(k != null) {
					k.setSumOfAfyp(kpiBean.getSumOfAfyp());
					k.setCountTalkDate(kpiBean.getCountTalkDate());
					k.setTotalTalkHrs(kpiBean.getSumTotalTalk());
					k.setFirstConfirmSale(kpiBean.getFirstConfirmSale().intValue());
					k.setAllSale(kpiBean.getCountAll().intValue());
					k.setSuccessEoc(kpiBean.getSuccessEoc());
					k.setAllEoc(kpiBean.getAllEoc());
					return bo.updateKpiResult(k, USER_LOGIN);
				} else {
					k = new KpiResult();
					k.setTsrInfo(this.getTsrInfoInMap(kpiBean.getTsrCode()));
					k.setTsmInfo(this.getTsrInfoInMap(kpiBean.getTsmCode()));
					k.setDsmInfo(this.getTsrInfoInMap(kpiBean.getDsmCode()));
					k.setCampaign(this.getCampaignInMap(kpiBean.getCampaignCode()));
					
					k.setYearMonth(yearMonth);
					
					k.setSumOfAfyp(kpiBean.getSumOfAfyp());
					k.setCountTalkDate(kpiBean.getCountTalkDate());
					k.setTotalTalkHrs(kpiBean.getSumTotalTalk());
					k.setFirstConfirmSale(kpiBean.getFirstConfirmSale().intValue());
					k.setAllSale(kpiBean.getCountAll().intValue());
					k.setSuccessEoc(kpiBean.getSuccessEoc());
					k.setAllEoc(kpiBean.getAllEoc());
					return bo.addKpiResult(k, USER_LOGIN);
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public TsrInfo addOrUpdateTsrInfo(String firstName, String lastName) throws Exception {
		TsrInfo tsrInfo = new TsrInfo();
		tsrInfo.setFirstName(firstName);
		tsrInfo.setLastName(lastName);
		return addOrUpdateTsrInfo(tsrInfo);
	}
	
	public TsrInfo addOrUpdateTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		
		if(!StringUtils.isBlank(tsrInfo.getTsrCode())) {
			TsrInfo isExist = new TsrInfo();
			isExist.setTsrCode(tsrInfo.getTsrCode());
			
			List<TsrInfo> exists = tsrInfoBo.findTsrInfo(isExist);
			if(exists != null && !exists.isEmpty()) {
				tsrInfo.setId(exists.get(0).getId());
				
				tsrInfo.setCreateBy(exists.get(0).getCreateBy());
				tsrInfo.setCreateDate(exists.get(0).getCreateDate());
				return updateTsrInfo(tsrInfo);
			}
		}
		return addTsrInfo(tsrInfo);
	}
	
	public PolicyInfo addPolicyInfo(PolicyInfo policyInfo) throws Exception {
		if(policyInfo != null) {
			PolicyInfoBo bo = (PolicyInfoBo) AppConfig.getInstance().getBean("policyInfoBo");
			return bo.addPolicyInfo(policyInfo, USER_LOGIN);
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
			return bo.addSalesRecord(salesRecord, USER_LOGIN);
		}
		return null;
	}
	
	public TsrTalkTime addTalkTime(TsrTalkTime tsrTalkTime) throws Exception {
		TsrTalkTimeBo tsrTalkTimeBo = (TsrTalkTimeBo) AppConfig.getInstance().getBean("tsrTalkTimeBo");
		
		return tsrTalkTimeBo.addTsrTalkTime(tsrTalkTime, USER_LOGIN);
	}
	
	public TsrTalkTimeDetail addTalkTimeDetail(TsrTalkTimeDetail tsrTalkTimeDetail) throws Exception {
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
		return bo.addTsrTalkTimeDetail(tsrTalkTimeDetail, USER_LOGIN);
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
	
	public TsrHierarchical addTsrHierarchical(TsrInfo tsrInfo, TsrInfo tmrInfo, CampaignKeyCode campaignKeyCode, Date startDate, Date endDate) throws Exception {
		TsrHierarchicalBo bo = (TsrHierarchicalBo) AppConfig.getInstance().getBean("tsrHierarchicalBo");
		TsrHierarchical t = new TsrHierarchical();
		t.setTsrInfo(tsrInfo);
		t.setUplineInfo(tmrInfo);
		t.setCampaignKeyCode(campaignKeyCode);
		t.setStartDate(startDate);
		if(endDate != null) {
			t.setEndDate(endDate);
		}
		return bo.addTsrHierarchical(t, USER_LOGIN);
	}
	
	public TsrInfo addTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfoBo bo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		TsrInfo result = bo.addTsrInfo(tsrInfo, USER_LOGIN);
		if(null != result) {
			prepareTsrInfo();
		}
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
			bo.findByHql(hql, keyCode, tsrCode);
			
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
	
	public KpiResult findKpiResultByDateAndCampaign(String yearMonth, String campaignCode, String tsrCode) {
		try {
			KpiResultBo bo = (KpiResultBo) AppConfig.getInstance().getBean("kpiResultBo");
			
			String hql = " from KpiResult d "
						+ " where 1 = 1"
						+ " and d.yearMonth = ? "
						+ " and d.campaign.code = ? "
						+ " and d.tsrInfo.tsrCode = ? ";
			
			List<KpiResult> list = bo.findByHql(hql, yearMonth, campaignCode, tsrCode);
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

	public Campaign getCampaignByName(String name) throws Exception {
		Campaign ex = new Campaign();
		ex.setFullName(name);
		List<Campaign> list = getCampaign(ex);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
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
	
	public CampaignKeyCode getCampaignKeyCode(CampaignKeyCode example) throws Exception {
		CampaignKeyCodeBo bo = (CampaignKeyCodeBo) AppConfig.getInstance().getBean("campaignKeyCodeBo");
		List<CampaignKeyCode> list = bo.findCampaignKeycode(example);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public CampaignKeyCode getCampaignKeyCode(String keyCode) throws Exception {
		CampaignKeyCode ex = new CampaignKeyCode();
		ex.setKeyCode(keyCode);
		return getCampaignKeyCode(ex);
	}
	
	public Customer getCustomerByName(String name) throws Exception {
		if(name != null) {

			String hql = "from Customer d "
					+ "where replace(replace(FULL_NAME, ' ', ''), ' ', '') like ?";
			
			CustomerBo bo = (CustomerBo) AppConfig.getInstance().getBean("customerBo");
			
			String param = "%" + name.replaceAll(" ", "") + "%";
			List<Customer> list = bo.findByHql(hql, param);
			
			if(null != list && !list.isEmpty()) {
				return list.get(0);
			}
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
						+ " order by d.campaign, d.dsmInfo.tsrCode, d.tsmInfo.tsrCode, d.tsrInfo.tsrCode";
			List<KpiResult> list = bo.findByHql(hql);
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
	
	public List<KpiCategorySetup> getKpiCategorySetups(String tsrLevel, String campaignCode, String tsrCode, String processYearMonth) {
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
	
	public TsrHierarchical getTsrHierarchical(String tsrCode, String tmrCode, String keyCode, Date date) throws Exception {
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
				
				if(!StringUtils.isBlank(keyCode)) {
					hql += " and d.campaignKeyCode.keyCode = ? ";
					vals.add(keyCode);
				}
				
				if(date != null) {
					hql += " and (d.startDate <= ? and (d.endDate is null or d.endDate >= ? ))";
					vals.add(date);
					vals.add(date);
				}
		
		List<TsrHierarchical> list = bo.findByHql(hql, vals.toArray());
		if(null != list && !list.isEmpty() && list.size() == 1) {
			return list.get(0);
		} else if(null != list && list.size() > 1) {
			throw new Exception("FOUND TSR Hierarchical more than 1: tsrCode: " + tsrCode + " | tmrCode: " + tmrCode + " | keyCode: " + keyCode + " | date: " + date);
		}
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
				if(t.getTsrContracts().get(0).getId() == 1L) {
					if(t.getFirstName().equalsIgnoreCase(first)) {
						countFirstName++;
					}
					if(t.getLastName().equalsIgnoreCase(last)) {
						countLastName++;
					}
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
	
	public TsrInfo getTsrInfoByName(String fullName) throws Exception {
		if(!StringUtils.isBlank(fullName)) {
			TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
			String hql = " from TsrInfo d "
					+ " where replace(replace(d.fullName, ' ', ''), ' ', '') like ? ";
			String param = removeTitle(fullName).replaceAll(" ", "").replaceAll(" ", "");
			List<TsrInfo> list = tsrInfoBo.findByHql(hql, "%" + param + "%");
			if(null != list && !list.isEmpty() && list.size() == 1) {
				return list.get(0);
			} else if(null != list && !list.isEmpty() && list.size() > 1) {
//				for(TsrInfo tsr : list) {
//					for(TsrContract c : tsr.getTsrContracts()) {
//						if(c.getTsrStatus().getParam().equalsIgnoreCase("A")) {
//							return tsr;
//						}
//					}
//				}
//				throw new Exception("Cannot get TsrInfo or already resign: " + fullName);
				return list.get(1);
			} else {
				param = param.length() <= 6 ? null : param.substring(param.length() / 2 - 3, param.length() / 2 + 3);
				return getTsrInfoByName(param);
			}
		}
		return null;
	}
	
	public TsrInfo getTsrInfoByNameAdvanceMode(String fullName, String keyCode) throws Exception {
		TsrInfo tsrInfo = null;
		String unTitle = this.removeTitle(fullName).replaceAll("  ", " ");
		List<TsrCodeReplacer> rs = null;
		try {
			String hql = " from TsrCodeReplacer d"
					+ " where 1 = 1 "
					+ " and d.ownerFullName = ? "
					+ " and d.keyCode = ? ";
			TsrCodeReplacerBo bo = (TsrCodeReplacerBo) AppConfig.getInstance().getBean("tsrCodeReplacerBo");
			rs = bo.findByHql(hql, unTitle, keyCode);
			
			if(null != rs && !rs.isEmpty()) {
				tsrInfo = KpiService.getInstance().getTsrInfoInMap(rs.get(0).getTsrCode());
			} else {
				tsrInfo = this.getTsrInfoByName(fullName);
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
	
	public SalesRecord isSalesRecordExist(Date saleDate, TsrInfo tsrInfo, PolicyInfo policyInfo) throws Exception {
		SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
		
//		SalesRecord sale = new SalesRecord();
//		sale.setSaleDate(saleDate);
//		
//		Example saleEx = Example.create(sale);
//		Example tsrEx = Example.create(new TsrInfo(tsrInfo.getTsrCode()));
//		Example policyEx = Example.create(new PolicyInfo(policyInfo.getxRef()));
//		
//		DetachedCriteria criteria = DetachedCriteria.forClass(SalesRecord.class).add(saleEx)
//				.createCriteria("tsrInfo").add(tsrEx);
//		criteria.createCriteria("policyInfo").add(policyEx);
//		
//		List<SalesRecord> results = bo.findByCriteria(criteria);
//		if(results != null && !results.isEmpty()) {
//			return results.get(0);
//		}
		
		String hql = "from SalesRecord d "
//				+ " left join TsrInfo d2 on d.tsrInfo.id = d2.id"
//				+ " left join PolicyInfo d3 on d.policyInfo.id = d3.id"
				+ " where d.saleDate = ? "
				+ " and d.tsrInfo.tsrCode = ? "
				+ " and d.policyInfo.xRef = ? ";
		
		List<SalesRecord> list = bo.findByHQL(hql, saleDate, tsrInfo.getTsrCode(), policyInfo.getxRef());
		if(null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public List<TsrTalkTimeDetail> isTalkDateExist(Date talkDate, TsrInfo tsrInfo, String keyCode) throws Exception {
		TsrTalkTimeDetail detail = new TsrTalkTimeDetail();
		detail.setTalkDate(talkDate);
		detail.setKeyCode(keyCode);
		
		Example talkTimeExample = Example.create(detail);
		Example tsrInfoExample = Example.create(tsrInfo);
		DetachedCriteria criteria = DetachedCriteria.forClass(TsrTalkTimeDetail.class)
				.add(talkTimeExample).createCriteria("tsrInfo").add(tsrInfoExample);
		
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
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
				return val.replace(t, "").trim();
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
	
	public TsrTalkTimeDetail updateTalkTimeDetail(TsrTalkTimeDetail tsrTalkTimeDetail) throws Exception {
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
		return bo.updateTsrTalkTimeDetail(tsrTalkTimeDetail, USER_LOGIN);
	}
	
	public TsrContract updateTsrContract(TsrContract tsrContract) throws Exception {
		TsrContractBo tsrContractBo = (TsrContractBo) AppConfig.getInstance().getBean("tsrContractBo");
		return tsrContractBo.updateTsrContract(tsrContract, USER_LOGIN);
	}
	
	public TsrInfo updateTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfoBo bo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		TsrInfo t = bo.updateTsrInfo(tsrInfo, USER_LOGIN);
		if(tsrInfo != null) {
			prepareTsrInfo();
		}
		return t;
	}
	
	public TsrHierarchical updateTsrHierarchical(TsrHierarchical tsrHierarchical) throws Exception {
		TsrHierarchicalBo bo = (TsrHierarchicalBo) AppConfig.getInstance().getBean("tsrHierarchicalBo");
		return bo.updateTsrHierarchical(tsrHierarchical, USER_LOGIN);
	}
}
