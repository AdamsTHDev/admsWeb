package com.adms.batch.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;

import com.adms.batch.app.AppConfig;
import com.adms.bo.campaign.CampaignBo;
import com.adms.bo.campaignkeycode.CampaignKeyCodeBo;
import com.adms.bo.campaignlead.CampaignLeadBo;
import com.adms.bo.customer.CustomerBo;
import com.adms.bo.policyinfo.PolicyInfoBo;
import com.adms.bo.policystatus.PolicyStatusBo;
import com.adms.bo.producttype.ProductTypeBo;
import com.adms.bo.qastatus.QaStatusBo;
import com.adms.bo.salesrecord.SalesRecordBo;
import com.adms.bo.tsrcontract.TsrContractBo;
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
import com.adms.domain.entities.PolicyInfo;
import com.adms.domain.entities.PolicyStatus;
import com.adms.domain.entities.ProductType;
import com.adms.domain.entities.QaStatus;
import com.adms.domain.entities.SalesRecord;
import com.adms.domain.entities.TsrContract;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrLevel;
import com.adms.domain.entities.TsrPosition;
import com.adms.domain.entities.TsrSite;
import com.adms.domain.entities.TsrStatus;
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
				
				return tsrInfoBo.updateTsrInfo(tsrInfo, USER_LOGIN);
			}
		}
		return tsrInfoBo.addTsrInfo(tsrInfo, USER_LOGIN);
	}
	
	public TsrInfo addOrUpdateTsrInfo(String firstName, String lastName) throws Exception {
		TsrInfo tsrInfo = new TsrInfo();
		tsrInfo.setFirstName(firstName);
		tsrInfo.setLastName(lastName);
		
		return addOrUpdateTsrInfo(tsrInfo);
	}
	
	public TsrInfo addTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfoBo bo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		return bo.addTsrInfo(tsrInfo, USER_LOGIN);
	}
	
	public TsrContract addTsrContract(TsrContract tsrContract) throws Exception {
		TsrContractBo tsrContractBo = (TsrContractBo) AppConfig.getInstance().getBean("tsrContractBo");
		return tsrContractBo.addTsrContract(tsrContract, USER_LOGIN);
	}
	
	public TsrPosition addTsrPosition(TsrPosition tsrPosition) throws Exception {
		TsrPositionBo bo = (TsrPositionBo) AppConfig.getInstance().getBean("tsrPositionBo");
		return bo.addTsrPosition(tsrPosition, USER_LOGIN);
	}
	
	public TsrTalkTime addTalkTime(TsrTalkTime tsrTalkTime) throws Exception {
		TsrTalkTimeBo tsrTalkTimeBo = (TsrTalkTimeBo) AppConfig.getInstance().getBean("tsrTalkTimeBo");
		
		return tsrTalkTimeBo.addTsrTalkTime(tsrTalkTime, USER_LOGIN);
	}

	public TsrTalkTimeDetail addTalkTimeDetail(TsrTalkTimeDetail tsrTalkTimeDetail) throws Exception {
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
		return bo.addTsrTalkTimeDetail(tsrTalkTimeDetail, USER_LOGIN);
	}
	
	public Customer addOrUpdateCustomer(String fullName) throws Exception {
		if(!StringUtils.isEmpty(fullName)) {
			CustomerBo bo = (CustomerBo) AppConfig.getInstance().getBean("customerBo");
			Customer ex = new Customer();
			ex.setFullName(fullName);
			List<Customer> list = bo.findCustomer(ex);
			if(list != null && !list.isEmpty()) {
				return bo.updateCustomer(list.get(0), USER_LOGIN);
			} else {
				return bo.addCustomer(ex, USER_LOGIN);
			}
		}
		return new Customer();
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
	
	public SalesRecord addSalesRecord(SalesRecord salesRecord) throws Exception {
		if(salesRecord != null) {
			SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
			return bo.addSalesRecord(salesRecord, USER_LOGIN);
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

	public TsrContract updateTsrContract(TsrContract tsrContract) throws Exception {
		TsrContractBo tsrContractBo = (TsrContractBo) AppConfig.getInstance().getBean("tsrContractBo");
		return tsrContractBo.updateTsrContract(tsrContract, USER_LOGIN);
	}

	public TsrTalkTime updateTalkTime(TsrTalkTime tsrTalkTime) throws Exception {
		TsrTalkTimeBo tsrTalkTimeBo = (TsrTalkTimeBo) AppConfig.getInstance().getBean("tsrTalkTimeBo");
		return tsrTalkTimeBo.updateTsrTalkTime(tsrTalkTime, USER_LOGIN);
	}

	public TsrTalkTimeDetail updateTalkTimeDetail(TsrTalkTimeDetail tsrTalkTimeDetail) throws Exception {
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
		return bo.updateTsrTalkTimeDetail(tsrTalkTimeDetail, USER_LOGIN);
	}
	
	public TsrInfo updateTsrInfo(TsrInfo tsrInfo) throws Exception {
		TsrInfoBo bo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		return bo.updateTsrInfo(tsrInfo, USER_LOGIN);
	}
	
	public SalesRecord updateSalesRecord(SalesRecord salesRecord) throws Exception {
		if(salesRecord != null) {
			SalesRecordBo bo = (SalesRecordBo) AppConfig.getInstance().getBean("salesRecordBo");
			return bo.updateSalesRecord(salesRecord, USER_LOGIN);
		}
		return null;
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
	
	public Campaign getCampaignByName(String name) throws Exception {
		Campaign ex = new Campaign();
		ex.setFullName(name);
		List<Campaign> list = getCampaign(ex);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
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
	
	private List<Campaign> getCampaign(Campaign example) throws Exception {
		CampaignBo campaignBo = (CampaignBo) AppConfig.getInstance().getBean("campaignBo");
		return campaignBo.findCampaign(example);
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
	
	public PolicyInfo getPolicyInfoByCustomerName(String name) throws Exception {
		if(name != null) {
			String hql = " from PolicyInfo d "
					+ " where replace(replace(d.customer.fullName, ' ', ''), ' ', '')";
			PolicyInfoBo bo = (PolicyInfoBo) AppConfig.getInstance().getBean("policyInfoBo");
			String param = "%" + name.replaceAll(" ", "") + "%";
			List<PolicyInfo> list = bo.findByHql(hql, param);
			if(null != list && !list.isEmpty()) {
				if(list.size() == 1) {
					return list.get(0);
				} else {
					throw new Exception("Found Policy more than one");
				}
			} else {
				throw new Exception("Policy not found");
			}
		} else {
			throw new Exception("Name is null");
		}
		
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
	
	public QaStatus getQaStatus(String value) throws Exception {
		QaStatus q = new QaStatus();
		q.setQaValue(value);
		return getQaStatus(q);
	}
	
	public QaStatus getQaStatus(QaStatus example) throws Exception {
		QaStatusBo bo = (QaStatusBo) AppConfig.getInstance().getBean("qaStatusBo");
		List<QaStatus> list = bo.findQaStatus(example);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public List<TsrInfo> isTsrExist(String firstName, String lastName) throws Exception {
		TsrInfo tsrInfo = new TsrInfo();
		
		tsrInfo.setFirstName("%" + firstName + "%");
		tsrInfo.setLastName("%" + lastName + "%");
		
		TsrInfoBo tsrInfoBo = (TsrInfoBo) AppConfig.getInstance().getBean("tsrInfoBo");
		List<TsrInfo> list = tsrInfoBo.searchByExamplePaging(tsrInfo, null);
		
		return list;
	}
	
	public List<TsrTalkTimeDetail> isTalkDateExist(Date talkDate, TsrInfo tsrInfo) throws Exception {
		TsrTalkTimeDetail detail = new TsrTalkTimeDetail();
		detail.setTalkDate(talkDate);
		
		Example talkTimeExample = Example.create(detail);
		Example tsrInfoExample = Example.create(tsrInfo);
		DetachedCriteria criteria = DetachedCriteria.forClass(TsrTalkTimeDetail.class)
				.add(talkTimeExample).createCriteria("tsrInfo").add(tsrInfoExample);
		
		TsrTalkTimeDetailBo bo = (TsrTalkTimeDetailBo) AppConfig.getInstance().getBean("tsrTalkTimeDetailBo");
		return bo.findByCriteria(criteria);
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
	
	public Campaign isCampaignExist(String campaignCode) throws Exception {
		Campaign ex = new Campaign();
		ex.setCode(campaignCode);
		return isCampaignExist(ex);
	}
	
	public Campaign isCampaignExist(Campaign example) throws Exception {
		CampaignBo bo = (CampaignBo) AppConfig.getInstance().getBean("campaignBo");
		List<Campaign> list = bo.findCampaign(example);
		if(null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
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
	
	public boolean isAlreadyQc(Date qcDate, String xRef) throws Exception {
		boolean result = false;
		PolicyStatusBo bo = (PolicyStatusBo) AppConfig.getInstance().getBean("policyStatsuBo");
		String hql = " from PolicyStatus d "
				+ " where d.qcDate = ? "
				+ " and d.policyInfo.xRef = ? ";
		List<PolicyStatus> list = bo.findByHql(hql, qcDate, xRef);
		if(null != list && !list.isEmpty()) {
			if(list.size() == 1) {
				result = true;
			} else {
				throw new Exception("Found more than one record");
			}
		}
		return result;
	}
}
