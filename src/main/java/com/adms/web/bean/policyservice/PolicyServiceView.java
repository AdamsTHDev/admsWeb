package com.adms.web.bean.policyservice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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
	
	
	public void callAddModalDialog() {
		Map<String, Object> options = new HashMap<>();
		options.put("modal", true);
		options.put("contentHeight", 600);
		options.put("contentWidth", 800);
		options.put("closable", false);
		RequestContext.getCurrentInstance().openDialog("modalAddCase", options, null);
		
	}
	
	public void closeAddModalDialog() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	
	public void submitAdd() {
		
	}
	
}
