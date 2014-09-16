package com.adms.web.model.customer;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

import com.adms.domain.Customer;

public class CustomerLazySorter implements Comparator<Customer> {

	private String sortField;
	private SortOrder sortOrder;
	
	public CustomerLazySorter(String sortField, SortOrder sortOrder) {
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}

	public int compare(Customer customer1, Customer customer2) {
		try {
			Object value1 = Customer.class.getClass().getField(this.sortField).get(customer1);
			Object value2 = Customer.class.getClass().getField(this.sortField).get(customer2);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			int value = ((Comparable) value1).compareTo(value2);
			
			return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
		} catch(Exception e) {
			throw new RuntimeException();
		}
	}

}
