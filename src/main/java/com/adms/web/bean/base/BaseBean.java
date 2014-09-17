package com.adms.web.bean.base;

import java.io.Serializable;
import java.util.TimeZone;

import com.adms.web.bean.login.LoginBean;
import com.adms.web.utils.FacesUtils;

public class BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private TimeZone timeZone;
	
	public BaseBean() {
		timeZone = TimeZone.getTimeZone("Asia/Bangkok");
	}
	
	public LoginBean getLoginBean() {
		return (LoginBean) FacesUtils.getManagedBean("loginBean");
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

}
