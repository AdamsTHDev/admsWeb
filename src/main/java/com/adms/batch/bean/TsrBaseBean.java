package com.adms.batch.bean;

import java.io.Serializable;

public abstract class TsrBaseBean extends BaseImportBean implements Serializable {

	private static final long serialVersionUID = 1141846408405718780L;

	private String tsrCode;
	private String firstName;
	private String lastName;
	
	public String getTsrCode() {
		return tsrCode;
	}
	public void setTsrCode(String tsrCode) {
		this.tsrCode = tsrCode;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
}
