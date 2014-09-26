package com.adms.web.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import com.adms.web.bean.base.ISearchBean;
import com.adms.web.enums.ESqlSort;
/**
 * <p><b> Generic of Lazy Data Model </b>
 *
 * @param <T> Entity Class
 */
public class LazyModel<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = 4403329614270854588L;
	
	private ISearchBean<T> searchBean;
	
	private T example;
	private List<T> datas;
	
	private int pageSize;
	private int rowIndex;
	private int rowCount;
	
	public LazyModel(final T example, ISearchBean<T> searchBean) {
		this.example = example;
		this.searchBean = searchBean;
	}
	
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		try {
			String sorting = null;
			Direction direction = null;
			PageRequest pageRequest = null;
			
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
			
//			datas = customerBo.findByExamplePaging(example, pageRequest);
			datas = searchBean.search(example, pageRequest);

			// rowCount
			setRowCount(searchBean.getTotalCount(example).intValue());
			
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

	public void setWrappedData(List<T> list) {
		this.datas = list;
	}
	
	public List<T> getWrappedData() {
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
	
	@Override
	public T getRowData() {
		T object = null;
		if(datas != null) {
			int index = rowIndex % pageSize;
			if(index <= datas.size()) {
				object = datas.get(index);
			}
		}
		return object;
	}
	
}
