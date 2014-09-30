package com.adms.batch.bean;

import java.math.BigDecimal;


public class TsrTrackingBean extends TsrBaseBean {

	private static final long serialVersionUID = -3752634528208440338L;
	
	private String period;
	
	private BigDecimal talkTime;

	public BigDecimal getTalkTime() {
		return talkTime;
	}

	public void setTalkTime(BigDecimal talkTime) {
		this.talkTime = talkTime;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	
}
