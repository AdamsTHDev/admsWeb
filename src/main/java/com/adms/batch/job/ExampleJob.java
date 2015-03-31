package com.adms.batch.job;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.adms.utils.DateUtil;

@Component
public class ExampleJob {

	public static void main(String[] args) {
		String processDate = "20120228";
		
		importProcess(processDate);
		
		exportProcess(processDate);
		
	}
	
	private static void importProcess(String processDate) {
		Date start = DateUtil.getCurrentDate();
		System.out.println("=======================================================================");
		System.out.println("START Import Batch - " + start);
		
		try {
			ImportForKpiJob importKpi = ImportForKpiJob.getInstance(processDate);
			
			importKpi.importTsr("D:/Test/upload/TSRUpdate/Update New Staff by month for comm_March-2015.xlsx");
			importKpi.importSupDsmHierarchy("D:/Test/upload/TSRUpdate", "UPDATE_SUP_DSM.xlsx");
			importKpi.importKpiTargetSetup("D:/Test/upload/KpiTarget", "Sales KPIs Target Setup - #yyyyMM.xlsx");
			importKpi.importDataForKPI("D:/Test/upload/kpi");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Date end = DateUtil.getCurrentDate();
		System.out.println("=======================================================================");
		System.out.println("END Import Batch- " + end);
		System.out.println("Batch processing time: " + getProcessTime(start, end));
	}
	
	private static void exportProcess(String processDate) {
		Date start = DateUtil.getCurrentDate();
		System.out.println("=======================================================================");
		System.out.println("START Export Batch - " + start);
		
		try {
			ExportKpiJob exportKpi = ExportKpiJob.getInstance(processDate);
			
			exportKpi.getDataToTable();
			exportKpi.exportKpiReport();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Date end = DateUtil.getCurrentDate();
		System.out.println("=======================================================================");
		System.out.println("END Export Batch- " + end);
		System.out.println("Batch processing time: " + getProcessTime(start, end));
	}
	
	private static String getProcessTime(Date start, Date end) {
		Long ms = end.getTime() - start.getTime();
		Long min = (ms / 1000L) / 60;
		Long sec = (ms / 1000L) - (min * 60);
		String result = "";
		if(min < 1l && sec < 1l) {
			result = ms.toString() + " ms";
		} else {
			result = min.toString() + "." + (sec < 10 ? "0" + sec.toString() : sec) + " min";
		}
		return result;
	}
}
