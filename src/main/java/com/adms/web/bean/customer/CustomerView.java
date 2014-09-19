package com.adms.web.bean.customer;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.adms.bo.customer.CustomerBo;
import com.adms.domain.entities.Customer;
import com.adms.web.bean.base.AbstractSearchBean;
import com.adms.web.model.customer.CustomerLazyDataModel;

@ManagedBean
@ViewScoped
public class CustomerView extends AbstractSearchBean<Customer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6038082994061062532L;

	@ManagedProperty(value = "#{customerBo}")
	private CustomerBo customerBo;
	
	@ManagedProperty(value = "#{customerBean}")
	private CustomerBean customerBean;
	
	private String firstName;
	private String lastName;
	private Date birthDate;
	private CustomerLazyDataModel customerModel;
	private int count;
	
	public String navToMod() {
		return "customerModify?faces-redirect=true";
	}
	
	@PostConstruct
	public void init() {
		getAllCustomer();
	}
	
	public void submitAddCustomer() {
		Customer customer = new Customer(firstName, lastName, birthDate);
		String userName = super.getLoginBean().getUsername();
		customer.setCreateBy(userName);
		try {
			customerBo.addCustomer(customer, userName);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getAllCustomer() {
		try {
			customerModel = new CustomerLazyDataModel(customerBo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void setCustomerBean(CustomerBean customerBean) {
		this.customerBean = customerBean;
	}

	public CustomerLazyDataModel getCustomerModel() {
		return customerModel;
	}

	public void setCustomerModel(CustomerLazyDataModel customerModel) {
		this.customerModel = customerModel;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
