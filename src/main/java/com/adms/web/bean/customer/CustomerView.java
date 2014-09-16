package com.adms.web.bean.customer;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.adms.bo.customer.CustomerBo;
import com.adms.domain.Customer;
import com.adms.web.bean.base.BaseBean;
import com.adms.web.bean.login.LoginBean;
import com.adms.web.model.customer.CustomerLazyModel;

@ManagedBean
@ViewScoped
public class CustomerView extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6038082994061062532L;

	@ManagedProperty(value = "#{loginBean}")
	LoginBean loginBean;
	
	@ManagedProperty(value = "#{customerBo}")
	private CustomerBo customerBo;
	
	@ManagedProperty(value = "#{customerBean}")
	private CustomerBean customerBean;
	
	private String firstName;
	private String lastName;
	private Date birthDate;
	private CustomerLazyModel customerModel;
	
	public String navToMod() {
		return "customerModify?faces-redirect=true";
	}
	
	@PostConstruct
	public void init() {
		getAllCustomer();
	}
	
	public void submitAddCustomer() {
		Customer customer = new Customer(firstName, lastName, birthDate);
		customer.setCreateBy(loginBean.getUsername());
		try {
			customerBo.addCustomer(customer, loginBean.getUsername());
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getAllCustomer() {
		try {
			customerModel = new CustomerLazyModel(customerBo.findCustomerAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	
	public void setCustomerBo(CustomerBo customerBo) {
		this.customerBo = customerBo;
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

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public CustomerLazyModel getCustomerModel() {
		return customerModel;
	}

	public void setCustomerModel(CustomerLazyModel customerModel) {
		this.customerModel = customerModel;
	}

	public void setCustomerBean(CustomerBean customerBean) {
		this.customerBean = customerBean;
	}
	
}
