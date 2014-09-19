package com.adms.web.model.importfile;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

import com.adms.domain.entities.ImportFile;

public class ImportFileLazySorter implements Comparator<ImportFile> {

	private String sortField;
	private SortOrder sortOrder;
	
	public ImportFileLazySorter(String sortField, SortOrder sortOrder) {
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}

	public int compare(ImportFile file1, ImportFile file2) {
		try {
			Object value1 = ImportFile.class.getClass().getField(this.sortField).get(file1);
			Object value2 = ImportFile.class.getClass().getField(this.sortField).get(file2);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			int value = ((Comparable) value1).compareTo(value2);
			
			return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
		} catch(Exception e) {
			throw new RuntimeException();
		}
	}

}
