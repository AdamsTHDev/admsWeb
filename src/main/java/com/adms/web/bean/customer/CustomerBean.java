package com.adms.web.bean.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.adms.domain.entities.Customer;
import com.adms.web.bean.base.BaseBean;

@ManagedBean
@SessionScoped
public class CustomerBean extends BaseBean {

	private static final long serialVersionUID = 469642175377311483L;

	private Customer editCustomer;

	public Customer getEditCustomer() {
		return editCustomer;
	}

	public void setEditCustomer(Customer editCustomer) {
		this.editCustomer = editCustomer;
	}
}
