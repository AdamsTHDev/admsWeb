package com.adms.batch.job.sub;

import java.io.InputStream;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class QcReConfirmOTO extends QcReConfirm {
	
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws Exception {
		System.out.println("QcReConfirm MSIGUOB");
		
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.QC_RECONFIRM_OTO.getValue());
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			
			super.process(wbHolder, sheetNames.get(0));
			
		} catch (Exception e) {
			e.printStackTrace();
			exceptionList.add(e);
		} finally {
			fileFormat.close();
			is.close();
		}
		exceptionList.addAll(super.exceptions);
	}
	
}
