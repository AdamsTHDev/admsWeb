package com.adms.web.bean.login;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.adms.web.bean.base.BaseBean;

@ManagedBean
@SessionScoped
public class LoginBean extends BaseBean {

	private static final long serialVersionUID = 8499800092146667051L;
	
	private String username;
	private Date loginDate;
	
	public String login() {
		
		if(username != null && username.length() > 0) {
			loginDate = new Date();
			return "pages/navigator?faces-redirect=true";
		}
		
		return null;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}
	

}
