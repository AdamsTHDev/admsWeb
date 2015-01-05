package com.adms.batch.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class SalesByRecordMSIGUOB extends SalesByRecord {
	
	protected List<Exception> exs = new ArrayList<Exception>();
	
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws IOException {
		System.out.println("SalesByRecord");
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.SALES_BY_RECORD_MSIG_UOB.getValue());
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			process(wbHolder, sheetNames.get(0));
			
		} catch (Exception e) {
			exceptionList.add(e);
			e.printStackTrace();
		} finally {
			fileFormat.close();
			is.close();
		}
		exceptionList.addAll(exs);
	}

}
