package com.adms.batch.job.sub;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.adms.batch.enums.EFileFormat;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class ImportTsrTrackingOTO extends ImportTsrTracking {

	private List<Exception> exceptions = new ArrayList<Exception>();

	@Override
	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws Exception {
		System.out.println("ImportTsrTracking OTO");
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.TSR_TRACKING_OTO.getValue());

		ExcelFormat ef = new ExcelFormat(fileFormat);
		try {
			DataHolder wbHolder = ef.readExcel(is);
			List<String> sheetNames = wbHolder.getKeyList();
			if (sheetNames.size() == 0) {
				return;
			}

			for(String sh : sheetNames) {
				if(!sh.equalsIgnoreCase("all")) {
					super.process(wbHolder, sh);
				}
			}

		} catch (Exception e) {
			exceptionList.add(e);
			e.printStackTrace();
		} finally {
			fileFormat.close();
			is.close();
		}
		exceptionList.addAll(exceptions);
	}

}
