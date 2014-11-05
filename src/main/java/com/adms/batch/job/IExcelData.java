package com.adms.batch.job;

import java.io.InputStream;
import java.util.List;

public interface IExcelData {

	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws Exception;
}
