package com.adms.web.bean.policyservice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import com.adms.web.bean.base.BaseBean;

@ManagedBean
@ViewScoped
public class PolicyServiceView extends BaseBean {

	private static final long serialVersionUID = -580474516330907376L;
	
	private String fileOwner;
	private String insuredName;
	private String mobileNo;
	private Date policyInceptionDate;
	private String policyNo;
	private Date birthDate;
	private String policyId;
	
	private SelectItem selectedChannel;
	private String changedName;
	private SelectItem selectedCallNature;
	private String changedThaiId;
	private SelectItem selectedCategory;
	private SelectItem selectedGender;
	private SelectItem selectedSubCategory;
	private Date changedBirthDate;
	private SelectItem selectedMaritalStatus;
	private SelectItem selectedCallResult;
	private String changedMobileNo;
	private SelectItem selectedCancelReason;
	private String changedHomeContactNo;
	private String optOut;
	private String changedOfficeNo;
	private String followUpUser;
	private String changedEmailId;
	private String caseDetail;
	private String changedCity;
	private String changedZipcode;
	private String changedAddress;
	private SelectItem rePrintPolicy;
	
	public void callAddModalDialog() {
		Map<String, Object> options = new HashMap<>();
		options.put("modal", true);
		options.put("contentHeight", 600);
		options.put("contentWidth", 800);
		options.put("closable", false);
		RequestContext.getCurrentInstance().openDialog("modalAddCase", options, null);
		
	}
	
	public void callSearchModalDialog() {
		Map<String, Object> options = new HashMap<>();
		options.put("modal", true);
		options.put("contentHeight", 600);
		options.put("contentWidth", 800);
		options.put("closable", false);
		RequestContext.getCurrentInstance().openDialog("modalSearch", options, null);
	}
	
	public void closeAddModalDialog() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	
	public void submitAdd() {
		
	}

	public String getFileOwner() {
		return fileOwner;
	}

	public void setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Date getPolicyInceptionDate() {
		return policyInceptionDate;
	}

	public void setPolicyInceptionDate(Date policyInceptionDate) {
		this.policyInceptionDate = policyInceptionDate;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public SelectItem getSelectedChannel() {
		return selectedChannel;
	}

	public void setSelectedChannel(SelectItem selectedChannel) {
		this.selectedChannel = selectedChannel;
	}

	public String getChangedName() {
		return changedName;
	}

	public void setChangedName(String changedName) {
		this.changedName = changedName;
	}

	public SelectItem getSelectedCallNature() {
		return selectedCallNature;
	}

	public void setSelectedCallNature(SelectItem selectedCallNature) {
		this.selectedCallNature = selectedCallNature;
	}

	public String getChangedThaiId() {
		return changedThaiId;
	}

	public void setChangedThaiId(String changedThaiId) {
		this.changedThaiId = changedThaiId;
	}

	public SelectItem getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(SelectItem selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public SelectItem getSelectedGender() {
		return selectedGender;
	}

	public void setSelectedGender(SelectItem selectedGender) {
		this.selectedGender = selectedGender;
	}

	public SelectItem getSelectedSubCategory() {
		return selectedSubCategory;
	}

	public void setSelectedSubCategory(SelectItem selectedSubCategory) {
		this.selectedSubCategory = selectedSubCategory;
	}

	public Date getChangedBirthDate() {
		return changedBirthDate;
	}

	public void setChangedBirthDate(Date changedBirthDate) {
		this.changedBirthDate = changedBirthDate;
	}

	public SelectItem getSelectedMaritalStatus() {
		return selectedMaritalStatus;
	}

	public void setSelectedMaritalStatus(SelectItem selectedMaritalStatus) {
		this.selectedMaritalStatus = selectedMaritalStatus;
	}

	public SelectItem getSelectedCallResult() {
		return selectedCallResult;
	}

	public void setSelectedCallResult(SelectItem selectedCallResult) {
		this.selectedCallResult = selectedCallResult;
	}

	public String getChangedMobileNo() {
		return changedMobileNo;
	}

	public void setChangedMobileNo(String changedMobileNo) {
		this.changedMobileNo = changedMobileNo;
	}

	public SelectItem getSelectedCancelReason() {
		return selectedCancelReason;
	}

	public void setSelectedCancelReason(SelectItem selectedCancelReason) {
		this.selectedCancelReason = selectedCancelReason;
	}

	public String getChangedHomeContactNo() {
		return changedHomeContactNo;
	}

	public void setChangedHomeContactNo(String changedHomeContactNo) {
		this.changedHomeContactNo = changedHomeContactNo;
	}

	public String getOptOut() {
		return optOut;
	}

	public void setOptOut(String optOut) {
		this.optOut = optOut;
	}

	public String getChangedOfficeNo() {
		return changedOfficeNo;
	}

	public void setChangedOfficeNo(String changedOfficeNo) {
		this.changedOfficeNo = changedOfficeNo;
	}

	public String getFollowUpUser() {
		return followUpUser;
	}

	public void setFollowUpUser(String followUpUser) {
		this.followUpUser = followUpUser;
	}

	public String getChangedEmailId() {
		return changedEmailId;
	}

	public void setChangedEmailId(String changedEmailId) {
		this.changedEmailId = changedEmailId;
	}

	public String getCaseDetail() {
		return caseDetail;
	}

	public void setCaseDetail(String caseDetail) {
		this.caseDetail = caseDetail;
	}

	public String getChangedCity() {
		return changedCity;
	}

	public void setChangedCity(String changedCity) {
		this.changedCity = changedCity;
	}

	public String getChangedZipcode() {
		return changedZipcode;
	}

	public void setChangedZipcode(String changedZipcode) {
		this.changedZipcode = changedZipcode;
	}

	public String getChangedAddress() {
		return changedAddress;
	}

	public void setChangedAddress(String changedAddress) {
		this.changedAddress = changedAddress;
	}

	public SelectItem getRePrintPolicy() {
		return rePrintPolicy;
	}

	public void setRePrintPolicy(SelectItem rePrintPolicy) {
		this.rePrintPolicy = rePrintPolicy;
	}
	
}
