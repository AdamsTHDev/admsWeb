package com.adms.web.bean.base;

import java.io.Serializable;
import java.util.TimeZone;

public abstract class BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int rowPerPage;
	private TimeZone timeZone;
	
	public BaseBean() {
		rowPerPage = 10;
		timeZone = TimeZone.getTimeZone("Asia/Bangkok");
	}
	
	public int getRowPerPage() {
		return rowPerPage;
	}

	public void setRowPerPage(int rowPerPage) {
		this.rowPerPage = rowPerPage;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

}
