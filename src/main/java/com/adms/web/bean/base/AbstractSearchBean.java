package com.adms.web.bean.base;

public abstract class AbstractSearchBean<T> extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 55877643186617076L;

	
	private final Integer rowPerPage = new Integer(10);

	public Integer getRowPerPage() {
		return rowPerPage;
	}
}
