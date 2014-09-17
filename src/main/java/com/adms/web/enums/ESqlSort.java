package com.adms.web.enums;

public enum ESqlSort {
	ASC("ASCENDING", "ASC"),
	DESC("DESCENDING", "DESC");
	
	private String code;
	private String value;
	
	private ESqlSort(String code, String value) {
		this.code = code;
		this.value = value;
	}
	public String getCode() {
		return code;
	}
	public String getValue() {
		return value;
	}
}
