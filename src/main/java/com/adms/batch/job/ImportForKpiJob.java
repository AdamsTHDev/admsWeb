package com.adms.batch.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.adms.batch.bean.TsrTrackingBean;
import com.adms.batch.enums.EFileFormat;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.ExcelFileFilter;

public class ImportForKpiJob {
	
	private static List<TsrTrackingBean> tsrTrackingBeans;

	public static void main(String[] args) {
		String dir = "D:/Test/upload/POM/20140701/";
		String processDate = "20140701";
		importToBeanStep(dir, processDate);
	}

	private static void importToBeanStep(String dir, String processDate) {
		if(StringUtils.isBlank(dir)) {
			System.err.println("Directory is null");
			return;
		}
		
		File path = new File(dir);
		File[] excels = path.listFiles(new ExcelFileFilter());
		
		for(File excel : excels) {
			autoProcess(excel, processDate);
		}
	}
	
	private static void autoProcess(File file, String processDate) {
		String excelName = file.getName();
		try {
			if(excelName.toLowerCase().contains("tsrtracking")) {
				System.out.println("TSR TRACKING");
				importTsrTracking(new FileInputStream(file));
			} else if(excelName.toLowerCase().contains("qc_reconfirm")) {
				System.out.println("QC");
			} else if(excelName.toLowerCase().contains("sales_report_by")) {
				System.out.println("Sales REC");
//				importSalesByRec(new FileInputStream(file));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void importTsrTracking(InputStream is) {
		InputStream fileFormat = URLClassLoader.getSystemResourceAsStream(EFileFormat.TSR_TRACKING.getValue());
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder dataHolder = ef.readExcel(is);
//			boolean is
			List<String> sheetNames = dataHolder.getKeyList();
			
			if(sheetNames.size() == 0) {
				return;
			}
			
			if(sheetNames.size() > 0 && sheetNames.size() == 3) {
				importTrackingData(dataHolder.get(sheetNames.get(2)).getDataList("tsrTrackingList"));
				
			} else {
				importTrackingData(dataHolder.get(sheetNames.get(0)).getDataList("tsrTrackingList"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			fileFormat.close();
		} catch (IOException e) {
		}
	}
	
	private static void importSalesByRec(InputStream is) {
		InputStream fileFormat = URLClassLoader.getSystemResourceAsStream(EFileFormat.SALES_BY_RECORD.getValue());
		ExcelFormat ef = new ExcelFormat(fileFormat);
		
		try {
			DataHolder dataHolder = ef.readExcel(is);
			List<String> sheetNames = dataHolder.getKeyList();
			
			for(String sheetName : sheetNames) {
				importSalesByRecData(dataHolder.get(sheetName).getDataList("salesByRecordList"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			fileFormat.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void importTrackingData(List<DataHolder> datas) {
		tsrTrackingBeans = getTsrTrackingBeans();
		TsrTrackingBean tsrTrackingBean = null;
		for(DataHolder data : datas) {
			tsrTrackingBean = new TsrTrackingBean();
			String[] names = data.get("tsrName").getStringValue().split(" ");
			
			String firstName = names[0];
			String lastName = "";
			
			for(int n = 1; n <= names.length; n++) {
				lastName.concat(names[n].concat(" "));
			}
			lastName.trim();
			tsrTrackingBean.setFirstName(firstName);
			tsrTrackingBean.setLastName(lastName);
			tsrTrackingBean.setTalkTime(data.get("totalTalkTime").getDecimalValue());
			
			tsrTrackingBeans.add(tsrTrackingBean);
			
			System.out.println(data.printValues());
		}
		
		
	}
	
	private static void importSalesByRecData(List<DataHolder> datas) {
		
		for(DataHolder data : datas) {
			try {
				Date salesDate = (Date) data.get("saleDate").getValue();
				Calendar cal = Calendar.getInstance();
				cal.setTime(salesDate);
				if(cal.get(Calendar.MONTH) == Calendar.JULY) {
					System.out.println(data.printValues());
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void importQcReConfirmData(List<DataHolder> datas) {
		
		for(DataHolder data : datas) {
			System.out.println(data.printValues());
		}
	}
	
	private static List<TsrTrackingBean> getTsrTrackingBeans() {
		if(tsrTrackingBeans == null) {
			tsrTrackingBeans = new ArrayList<TsrTrackingBean>();
		}
		return tsrTrackingBeans;
	}
}
