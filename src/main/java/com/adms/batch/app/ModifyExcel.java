package com.adms.batch.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.adms.utils.FileFilterByName;
import com.adms.web.utils.FileUtils;

public class ModifyExcel {

	public static void main(String[] args) {
		String path = "D:/Test/upload/kpi/201409/";
		System.out.println("Start");
		try {
			File dir = new File(path);
			for(File dig : dir.listFiles(new FileFilterByName("MSIG UOB"))) {
				if(dig.isDirectory()) {
					for(File d : dig.listFiles()) {
						if(d.isDirectory()) {
							for(File xls : d.listFiles(new FileFilterByName("Daily_Performance_Tracking_ByLot.xls"))) {
								System.out.println("do: " + xls.getAbsolutePath());
								Workbook wb = new HSSFWorkbook(new FileInputStream(xls));
								if(wb.getNumberOfSheets() == 3) {
									backup(xls);
									moveSheet(wb);
									modifyHeaderOf2ndSheet(wb);
									save(xls, wb);
								} else if(wb.getNumberOfSheets() == 2) {
									backup(xls);
									modifyHeaderOf2ndSheet(wb);
									save(xls, wb);
								}
								wb.close();
							}
						}
					}
				}
			}
			System.out.println("Finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void backup(File file) throws IOException {
		System.out.println("parent: " + file.getParent());
		File bk = new File(file.getParent() + "\\" + "backupOfDailyPerform.xls");
		if(!bk.exists()) {
			bk.createNewFile();
		}
		FileUtils.getInstance().copyFile(file, bk);
	}
	
	private static void moveSheet(Workbook wb) {
		try {
			System.out.println("Number of sheet: " + wb.getNumberOfSheets());
			if(wb.getNumberOfSheets() == 3) {
				System.out.println("do order");
				wb.setSheetOrder(wb.getSheetAt(0).getSheetName(), wb.getNumberOfSheets()-1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void modifyHeaderOf2ndSheet(Workbook wb) {
		try {
			Sheet sheet = wb.getSheetAt(1);
			
			Cell cell = sheet.getRow(0).getCell(0, Row.RETURN_NULL_AND_BLANK);
			if(null != cell && cell.getStringCellValue().equalsIgnoreCase("DailyPerformanceTrackingReport")) {
				sheet.removeRow(sheet.getRow(0));
				sheet.removeRow(sheet.getRow(1));
				sheet.shiftRows(2, sheet.getLastRowNum(), -2);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void save(File file, Workbook wb) {
		OutputStream out = null;
		try {
			System.out.println("do save");
			out = new BufferedOutputStream(new FileOutputStream(file));
			wb.write(out);
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}
}
