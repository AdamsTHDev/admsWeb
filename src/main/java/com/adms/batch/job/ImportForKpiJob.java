package com.adms.batch.job;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.adms.utils.ExcelFileFilter;

@Component
public class ImportForKpiJob {
	
	public static ImportForKpiJob instance;
	
	public static ImportForKpiJob getInstance() {
		if(instance == null) {
			instance = new ImportForKpiJob();
		}
		return instance;
	}

	public void execute() {
		System.out.println("[START Import for KPI]");
		String dir = "D:/Test/upload/POM/20140703/";
		if(StringUtils.isBlank(dir)) {
			System.err.println("Directory is null");
			return;
		}
		
		File path = new File(dir);
		File[] excels = path.listFiles(new ExcelFileFilter());
		
		for(File excel : excels) {
			autoProcess(excel);
		}
	}
	
	private void autoProcess(File file) {
		System.out.println("file name: " + file.getName());
		String excelName = file.getName();
		try {
			if(excelName.toLowerCase().contains("tsrtracking")) {
				System.out.println("TSR TRACKING");
				ImportTsrTracking.getInstance().importFromInputStream(new FileInputStream(file));
			} else if(excelName.toLowerCase().contains("qc_reconfirm")) {
				System.out.println("QC");
			} else if(excelName.toLowerCase().contains("sales_report_by")) {
				System.out.println("Sales REC");
			} else if(excelName.toLowerCase().contains("dadfily_performance")) {
				System.out.println("Daily Performance");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
