package com.adms.web.bean.customer;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.PageRequest;

import com.adms.bo.customer.CustomerBo;
import com.adms.domain.entities.Customer;
import com.adms.web.bean.base.AbstractSearchBean;
import com.adms.web.model.LazyModel;

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
	private int count;
	
	private LazyModel<Customer> dataModel;
	
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
//			customerModel = new CustomerLazyDataModel(customerBo);
			dataModel = new LazyModel<Customer>(new Customer(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Customer> search(Customer object, PageRequest pageRequest) {
		try {
			return customerBo.findByExamplePaging(object, pageRequest);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Long getTotalCount(Customer object) {
		try {
			return customerBo.findTotalCount(object);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public LazyModel<Customer> getDataModel() {
		return dataModel;
	}

	public void setDataModel(LazyModel<Customer> dataModel) {
		this.dataModel = dataModel;
	}

}
