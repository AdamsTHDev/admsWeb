package com.adms.web.bean.customer;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.adms.bo.customer.CustomerBo;
import com.adms.domain.entities.Customer;
import com.adms.web.bean.base.BaseBean;
import com.adms.web.bean.login.LoginBean;

@ManagedBean
@ViewScoped
public class CustomerModify extends BaseBean {

	private static final long serialVersionUID = 8205358206316253401L;

	@ManagedProperty(value="#{loginBean}")
	private LoginBean  loginBean;
	
	@ManagedProperty(value="#{customerBo}")
	private CustomerBo customerBo;
	
	@ManagedProperty(value="#{customerBean}")
	private CustomerBean customerBean;
	
	private boolean readOnly;
	private boolean editMode;
	
	private Long id;
	private String firstName;
	private String lastName;
	private Date birthDate;
	
	private String createBy;
	private String updateBy;

	private Date createDate;
	private Date updateDate;
	
	private String exFirstName;
	private String exLastName;
	private Date exBirthDate;
	
	@PostConstruct
	public void init() {
		prepareData(customerBean.getEditCustomer());
	}
	
	public void prepareData(Customer customer) {
		try {
			if(customer == null) {
				customer = new Customer();
				readOnly = false;
				editMode = false;
			} else {
				readOnly = true;
				editMode = true;
			}
			
			setValue(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handleEditValue() {
		this.readOnly = false;
	}
	
	public void submit() {
		Customer customer = new Customer(firstName, lastName, birthDate);
		try {
			if(isEditMode()) {
				customer.setId(id);
				customer.setCreateBy(createBy);
				customer.setCreateDate(createDate);
				customerBo.updateCustomer(customer, loginBean.getUsername());
				prepareData(customer);
			} else {
				customerBo.addCustomer(customer, loginBean.getUsername());
				prepareData(null);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setValue(Customer customer) {
		id = customer.getId();
		firstName = customer.getFirstName();
		lastName = customer.getLastName();
		birthDate = customer.getBirthDate();
		createBy = customer.getCreateBy();
		createDate = customer.getCreateDate();
		updateBy = customer.getUpdateBy();
		updateDate = customer.getUpdateDate();
		
		exFirstName = customer.getFirstName();
		exLastName = customer.getLastName();
		birthDate = customer.getBirthDate();
	}
	
//	<!-- Getter & Setter -->
	
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

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean edit) {
		this.readOnly = edit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCustomerBean(CustomerBean customerBean) {
		this.customerBean = customerBean;
	}

	public String getExFirstName() {
		return exFirstName;
	}

	public void setExFirstName(String exFirstName) {
		this.exFirstName = exFirstName;
	}

	public String getExLastName() {
		return exLastName;
	}

	public void setExLastName(String exLastName) {
		this.exLastName = exLastName;
	}

	public Date getExBirthDate() {
		return exBirthDate;
	}

	public void setExBirthDate(Date exBirthDate) {
		this.exBirthDate = exBirthDate;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

}
