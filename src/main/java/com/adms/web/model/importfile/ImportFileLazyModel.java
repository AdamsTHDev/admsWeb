package com.adms.web.model.importfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.adms.domain.entities.ImportFile;

public class ImportFileLazyModel extends LazyDataModel<ImportFile> {

	private static final long serialVersionUID = 7709360211524209919L;
	private List<ImportFile> list;
	
	public ImportFileLazyModel(List<ImportFile> list) {
		this.list = list;
	}
	
	@Override
	public ImportFile getRowData(String rowKey) {
		for(ImportFile file : list) {
			if(file.getId().equals(Long.parseLong(rowKey))) {
				return file;
			}
		}
		return null;
	}
	
	@Override
	public Object getRowKey(ImportFile file) {
		return file.getId();
	}
	
	@Override
	public List<ImportFile> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		
		List<ImportFile> datas = new ArrayList<ImportFile>();
		
		for(ImportFile file : list) {
			boolean match = true;
			
			if(filters != null) {
				for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
					try {
						String filterProperty = it.next();
						Object filterValue = filters.get(filterProperty);
						String fieldValue = String.valueOf(file.getClass().getField(filterProperty).get(file));
						
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
				datas.add(file);
			}
		}
		
		// sort
		if(sortField != null) {
			Collections.sort(datas, new ImportFileLazySorter(sortField, sortOrder));
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
