package com.adms.batch.job.sub;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface IExcelData {

	public void importFromInputStream(InputStream is, List<Exception> exceptionList) throws Exception;
	public void importFromInputStream(File file, List<Exception> exceptionList) throws Exception;
}
