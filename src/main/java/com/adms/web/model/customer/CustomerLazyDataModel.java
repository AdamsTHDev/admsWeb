package com.adms.web.model.customer;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import com.adms.bo.customer.CustomerBo;
import com.adms.domain.entities.Customer;
import com.adms.web.enums.ESqlSort;

/**
 * <p>
 * <b>Just keep for example of lazy data model</b>
 * </p>
 * <p>
 * please, use <code> {@link com.adms.web.model.LazyModel} </code> instead.
 * </p>
 *
 * @see {@link com.adms.web.model.LazyModel}
 */
@Deprecated
public class CustomerLazyDataModel extends LazyDataModel<Customer> {
	private static final long serialVersionUID = -2052048428718174830L;
	
	private List<Customer> datas;
	private CustomerBo customerBo;
	private Customer example;
	
	private int pageSize;
	private int rowIndex;
	private int rowCount;
	
	public CustomerLazyDataModel(final CustomerBo customerBo) {
		this.customerBo = customerBo;
	}
	
	public CustomerLazyDataModel(final CustomerBo customerBo, final Customer example) {
		this.customerBo = customerBo;
		this.example = example;
	}
	
	@Override
	public List<Customer> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		try {
			String sorting = null;
			Direction direction = null;
			PageRequest pageRequest = null;

			if(example == null) {
				example = new Customer();
			}
			if(!StringUtils.isBlank(sortField) && sortOrder != null) {
				ESqlSort eSql = ESqlSort.valueOf(sortOrder.toString());
				sorting = eSql.getValue();
			}
			
			if(sorting != null) {
				direction = Direction.fromString(sorting);
				pageRequest = new PageRequest(first, first + pageSize, direction, sortField);
			} else {
				pageRequest = new PageRequest(first, first + pageSize);
			}
			
			datas = customerBo.findByExamplePaging(example, pageRequest);

			// rowCount
			setRowCount(customerBo.findTotalCount().intValue());
			
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean isRowAvailable() {
		if(datas == null) {
			return false;
		}
		int index = rowIndex % pageSize;
		return index >= 0 && index < datas.size();
	}
	
	@Override
	public Object getRowKey(Customer object) {
		return object.getId();
	}
	
	@Override
	public Customer getRowData() {
		Customer customer = null;
		if(datas != null) {
			int index = rowIndex % pageSize;
			if(index <= datas.size()) {
				customer = datas.get(index);
			}
		}
		return customer;
	}
	
	@Override
	public Customer getRowData(String rowKey) {
		if(datas != null) {
			for(Customer customer : datas) {
				if(customer.getId().toString().equals(rowKey)) {
					return customer;
				}
			}
		}
		return null;
	}
	
	public void setWrappedData(List<Customer> list) {
		this.datas = list;
	}
	
	public List<Customer> getWrappedData() {
		return datas;
	}
	
	@Override
	public int getPageSize() {
		return pageSize;
	}
	
	@Override
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	@Override
	public int getRowIndex() {
		return rowIndex;
	}
	
	@Override
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	@Override
	public int getRowCount() {
		return rowCount;
	}
	
	@Override
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
}
