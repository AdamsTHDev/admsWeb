package com.adms.web.bean.base;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class NavBean extends BaseBean {

	private static final long serialVersionUID = 2295419786029799484L;

	public String navToImportFile() {
		return "importFile?faces-redirect=true";
	}
	
	public String navToCustomer() {
		return "customer/customer?faces-redirect=true";
	}
	
	public String navToPolicyService() {
		return "policyServicing/policyservice?faces-redirect=true";
	}
}
