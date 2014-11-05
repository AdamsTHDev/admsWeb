package com.adms.batch.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.adms.batch.service.KpiService;
import com.adms.domain.entities.KpiBean;

@Component
public class ExportKpiJob {

	private KpiService kpiService() {
		return KpiService.getInstance();
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
	
	public void execute() {
		
		Date start = Calendar.getInstance().getTime();
		System.out.println("=======================================================================");
		System.out.println("START ExportKpiJob Batch - " + start);
		System.out.println("=======================================================================");
		
		String mDate = "201409";
		try {
			process(mDate);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Date end = Calendar.getInstance().getTime();
		System.out.println("=======================================================================");
		System.out.println("END ExportKpiJob Batch - " + end);
		System.out.println("Batch processing time: " + getProcessTime(start, end));
		System.out.println("=======================================================================");
	}
	
	private void process(String yyyyMM) {
		List<KpiBean> list = kpiService().getKpi(yyyyMM);
		for(KpiBean kpi : list) {
			System.out.println(kpi.toString());
		}
	}
}
