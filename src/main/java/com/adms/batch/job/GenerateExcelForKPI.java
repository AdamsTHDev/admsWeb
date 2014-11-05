package com.adms.batch.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.adms.batch.enums.EFileFormat;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class GenerateExcelForKPI {

	public void start() {
		InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.SALES_BY_RECORD.getValue());
		String fileDir = "D:/Test/upload/kpiresult/";
		String fileName = "forkpi201409.xlsx";
		
		File xls = new File(fileDir + fileName);
		
		ExcelFormat ef = new ExcelFormat(fileFormat);
		try {
			DataHolder dataHolderWb = ef.readExcel(new FileInputStream(xls));
			
			DataHolder dataHolderSheet = dataHolderWb.get("Sheet1");
			
			List<DataHolder> dataList = dataHolderSheet.getDataList("resultList");
			
			Workbook wb = null;
			
			String cn = null;
			
			for(DataHolder data : dataList) {
				try {
					String campaignName = data.get("campaignName").getStringValue();
					if(null == cn) {
						File target = new File(fileDir + campaignName + ".xlsx");
						target.createNewFile();
						wb = new XSSFWorkbook(target);
					}
					process(wb);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void process(Workbook wb) throws Exception {
		if(null != wb && null != wb.getSheetAt(0)) {
			
		}
	}
	
	/*
	 * <DataCell row="1" column="A" dataType="TEXT" fieldName="campaignName"/>
				<DataCell row="1" column="B" dataType="TEXT" fieldName="tsmFullName" />
				<DataCell row="1" column="C" dataType="TEXT" fieldName="tsrCode" />
				<DataCell row="1" column="D" dataType="TEXT" fieldName="tsrFullName" />
				<DataCell row="1" column="E" dataType="TEXT" fieldName="premium"/>
				<DataCell row="1" column="F" dataType="TEXT" fieldName="attend"/>
				<DataCell row="1" column="G" dataType="NUMBER" fieldName="totalaTalk"/>
				<DataCell row="1" column="H" dataType="TEXT" fieldName="firstConfirmSale" />
				<DataCell row="1" column="I" dataType="TEXT" fieldName="totalSale" />
	 */
	
}
