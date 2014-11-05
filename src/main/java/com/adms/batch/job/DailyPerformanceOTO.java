package com.adms.batch.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class DailyPerformanceOTO extends DailyPerformanceTracking {
	
	@Override
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws IOException {
		System.out.println("DailyPerformanceTracking OTO");
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.DAILY_PERFORMANCE_OTO.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			if(sheetNames == null || (sheetNames != null && sheetNames.isEmpty())) {
				return;
			} else { 
				for(String sn : sheetNames) {
					try {
						if(!sn.equalsIgnoreCase("All") && !sn.equalsIgnoreCase("DailyPerformanceTracking_ByLot") && !sn.equalsIgnoreCase("Summary")) {
							process(wbHolder, sn);
						}
					} catch(Exception e) {
						exceptionList.add(e);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			exceptionList.add(e);
		} finally {
			fileFormat.close();
			is.close();
		}
	}
	
	@Override
	public boolean process(DataHolder wbHolder, String sheetName) throws Exception {
		return super.process(wbHolder, sheetName);
	}

}
