package com.adms.web.model.customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.adms.domain.Customer;

public class CustomerLazyModel extends LazyDataModel<Customer> {

	private static final long serialVersionUID = 7709360211524209919L;
	private List<Customer> list;
	
	public CustomerLazyModel(List<Customer> list) {
		this.list = list;
	}
	
	@Override
	public Customer getRowData(String rowKey) {
		for(Customer object : list) {
			if(object.getId().equals(Long.parseLong(rowKey))) {
				return object;
			}
		}
		return null;
	}
	
	@Override
	public Object getRowKey(Customer object) {
		return object.getId();
	}
	
	@Override
	public List<Customer> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		
		List<Customer> datas = new ArrayList<Customer>();
		
		for(Customer object : list) {
			boolean match = true;
			
			if(filters != null) {
				for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
					try {
						String filterProperty = it.next();
						Object filterValue = filters.get(filterProperty);
						String fieldValue = String.valueOf(object.getClass().getField(filterProperty).get(object));
						
						if(filterValue == null || fieldValue.startsWith(filterValue.toString())) {
							match = true;
						} else {
							match = false;
							break;
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if(match) {
				datas.add(object);
			}
		}
		
		// sort
		if(sortField != null) {
			Collections.sort(datas, new CustomerLazySorter(sortField, sortOrder));
		}
		
		// rowCount
		int dataSize = datas.size();
		this.setRowCount(dataSize);
		
		// paginate
		if(dataSize > pageSize) {
			try {
				return datas.subList(first, first + pageSize);
			} catch(IndexOutOfBoundsException e) {
				return datas.subList(first, first + (dataSize % pageSize));
			}
		} else {
			return datas;
		}
		
	}
}
