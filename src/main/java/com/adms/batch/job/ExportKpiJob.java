package com.adms.batch.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import com.adms.batch.service.KpiService;
import com.adms.domain.entities.Campaign;
import com.adms.domain.entities.KpiBean;
import com.adms.domain.entities.KpiCategorySetup;
import com.adms.domain.entities.KpiResult;
import com.adms.domain.entities.KpiRetention;
import com.adms.domain.entities.KpiScoreRate;

@Component
public class ExportKpiJob {
	
	private final Double WORK_DAYS = 22D;
	
	private final String NOT_AVILABLE = "N/A";
	private final String GRADE_A = "A";
	private final String GRADE_B = "B";
	
	private final String DSM = "DSM";
	private final String TSM = "SUP";
	private final String TSR = "TSR";
	
	private final int START_DSM_ROW = -1;
	private final int START_TSM_ROW = 1;
	private final int START_TSR_ROW = 7;
	
	private final int POSITION_COL = 0;
	private final int KPI_COL = 1;
	private final int WEIGHT_COL = 2;
	private final int TARGET_COL = 3;
	private final int ACTUAL_COL = 4;
	private final int VS_TARGET_COL = 5;
	private final int SCORE_COL = 6;
	private final int GRADE_COL = 7;
	
	private Map<String, Map<String, DsmKpiByCampaign>> dsmActualMaps = new HashMap<>();
	private Map<String, Map<String, TsmActualKpi>> tsmActualMaps = new HashMap<>();
	private List<KpiRetention> kpiRetentions;
	
	private List<KpiResult> kpiResults;
	
	private int currentRow = 0;
	private String campaignCode = "";
	private String dsmCode = "";
	private String tsmCode = "";
	private String tsrCode = "";
	
//	private Cell dsmActualSaleCell;
//	private Cell dsmActualConfCell;
//	private Cell dsmActualRetenCell;
//	private Cell dsmActualAbcCell;
	
	private Cell tsmActualTarpCell;
	private Cell tsmActualTotalTarpCell;
	private Cell tsmActualListConvCell;
	private Cell tsmActualConfCell;
	private Cell tsmActualRetenCell;
	
	private String processYearMonth = null;
	
	private KpiService kpiService() {
		return KpiService.getInstance();
	}

	public void execute() {
			
			Date start = Calendar.getInstance().getTime();
			System.out.println("=======================================================================");
			System.out.println("START ExportKpiJob Batch - " + start);
			System.out.println("=======================================================================");
			
			String mDate = "201501";
			try {
				processYearMonth = new String(mDate);
//				processToDB(processYearMonth);
				processData();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			Date end = Calendar.getInstance().getTime();
			System.out.println("=======================================================================");
			System.out.println("END ExportKpiJob Batch - " + end);
			System.out.println("Batch processing time: " + getProcessTime(start, end));
			System.out.println("=======================================================================");
		}

	private void processToDB(String yyyyMM) {
		List<KpiBean> list = kpiService().getKpi(yyyyMM);
		for(KpiBean kpi : list) {
			kpiService().addOrUpdateKpiResult(kpi, yyyyMM);
		}
	}

	private void processData() throws Exception {
			kpiResults = kpiService().getKpiResults(processYearMonth);
			if(null == kpiResults) throw new Exception("No data has been found: " + processYearMonth);
			
			kpiRetentions = kpiService().findKpiRetentionByYearMonth(processYearMonth);
			
			Workbook wb = getWbTemplate();
			Sheet tSheet = null;
			Sheet toSheet = null;
			
			for(KpiResult data : kpiResults) {
				
//				if(!data.getCampaign().getCode().equals("021DP1714L04")) {
//					continue;
//				}
				
				if(!campaignCode.equals(data.getCampaign().getCode())) {
					
					if(toSheet != null && !StringUtils.isBlank(campaignCode)) {
						System.out.println("campaignCode: " + campaignCode);
						setTSMActual();
						doCalculate(toSheet, false);
//						doDsmGroup(toSheet);
						doWriteWorkbook(wb, kpiService().getCampaignInMap(campaignCode).getDisplayName());
						wb = null;
					}
					
					campaignCode = data.getCampaign().getCode();
					Campaign campaign = kpiService().getCampaignInMap(campaignCode);
					
					if(wb == null) {
						wb = getWbTemplate();
					}
					
					tSheet = wb.getSheetAt(0);
					toSheet = wb.createSheet(campaign.getDisplayName());
					doHead(wb, campaign.getDisplayName());
					
	//				<!-- set width -->
					for(int n = 0; n < 7; n++) {
						copyColumnWidth(tSheet, n, toSheet, n);
					}
					
	//				campaignCode = "";
					dsmCode = "";
					tsmCode = "";
					tsrCode = "";
					currentRow = 1;
					
				}
				
	//			<!-- DSM rows -->
				if(!dsmCode.equals(data.getDsmInfo().getTsrCode())) {
					
					dsmCode = data.getDsmInfo().getTsrCode();
	
	//				<!-- DSM have no campaignCode -->
//					List<KpiCategorySetup> kpiCats = kpiService().getKpiCategorySetups(DSM, null, dsmCode, processYearMonth);
					try {
//						if(kpiCats == null || kpiCats.isEmpty()) {
//							System.err.println("Not found KPIs Category-> dsmCode:" + dsmCode + " | campaignCode: " + campaignCode);
//							kpiCats = null;
//						}
						
						Map<String, DsmKpiByCampaign> campaignMap = null;
						if(dsmActualMaps.get(dsmCode) == null) {
							campaignMap = new HashMap<>();
							dsmActualMaps.put(dsmCode, campaignMap);
						} else {
							campaignMap = dsmActualMaps.get(dsmCode);
						}
						
						if(campaignMap.get(campaignCode) == null) {
							campaignMap.put(campaignCode, new DsmKpiByCampaign());
						}
						
//						generateRowData(tSheet, toSheet, kpiCats, dsmCode, DSM, data);
						generateRowData(tSheet, toSheet, null, dsmCode, DSM, data);
						
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
	//			</!-- End DSM row -->
	//			<!-- TSM row -->
				if(!tsmCode.equals(data.getTsmInfo().getTsrCode())) {
					
					setTSMActual();
					
					tsmCode = data.getTsmInfo().getTsrCode();
	
					List<KpiCategorySetup> kpiCats = kpiService().getKpiCategorySetups(TSM, campaignCode, tsmCode, processYearMonth);
					try {
						if(kpiCats == null || kpiCats.isEmpty()) {
							System.err.println("!!!!!!!!!!!!!!!!!!!!!| Not found KPIs Category-> tsmCode:" + tsmCode + " | campaignCode: " + campaignCode + " |!!!!!!!!!!!!!!!!!!!!!");
							kpiCats = null;
						}
						
						Map<String, TsmActualKpi> campaignMap = null;
						if(tsmActualMaps.get(tsmCode) == null) {
							campaignMap = new HashMap<>();
							tsmActualMaps.put(tsmCode, campaignMap);
						} else {
							campaignMap = tsmActualMaps.get(tsmCode);
						}
						
						if(campaignMap.get(campaignCode) == null) {
							campaignMap.put(campaignCode, new TsmActualKpi());
						}
						
						generateRowData(tSheet, toSheet, kpiCats, tsmCode, TSM, data);
						
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
	//			</!-- End TSM row -->
	//			<!-- TSR row -->
				if(!tsrCode.equals(data.getTsrInfo().getTsrCode())) {
					tsrCode = data.getTsrInfo().getTsrCode();
					
					List<KpiCategorySetup> kpiCats = kpiService().getKpiCategorySetups(TSR, campaignCode, null, processYearMonth);
					try {
						if(kpiCats == null || kpiCats.isEmpty()) {
							throw new Exception("Not found KPIs Category-> tsrCode:" + tsrCode + " | campaignCode: " + campaignCode);
						}
						
						generateRowData(tSheet, toSheet, kpiCats, tsrCode, TSR, data);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
	//			</!-- End TSR row -->
				
			}
	//		<!-- for last campaign -->
			setTSMActual();
			doCalculate(toSheet, false);
//			doDsmGroup(toSheet);
			doWriteWorkbook(wb, kpiService().getCampaignInMap(campaignCode).getDisplayName());
			wb.close();
	//		<!-- end KPIs -->
	
			doDsmKpi();
			doTsmKpi();
		}

	private String calculateGrade(Double score, boolean isNewbie) {
		List<KpiScoreRate> kpiScoreRates = kpiService().getKpiScoreRateAll();
		for(KpiScoreRate rate : kpiScoreRates) {
			if(isNewbie) {
				if(rate.getCondition() == null) {
					return rate.getGrade();
				}
			} else {
				if(score.doubleValue() > rate.getMin().doubleValue() 
						&& score.doubleValue() <= rate.getMax().doubleValue()) {
					return rate.getGrade();
				}
			}
		}
		return null;
	}
	
	private Double calculateScore(Sheet sheet, int currRowNum, String level, int groupStartRow, int totalRowNum, Double sumScore, boolean isDSMGroup) {
		Cell scoreCell = sheet.getRow(currRowNum).getCell(SCORE_COL, Row.CREATE_NULL_AS_BLANK);
		
		DsmKpiByCampaign dsmKpi = dsmActualMaps.get(dsmCode).get(campaignCode);
		
		Double vsTargetValue = sheet.getRow(currRowNum).getCell(VS_TARGET_COL, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
		Double weightValue = sheet.getRow(currRowNum).getCell(WEIGHT_COL, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
		Double scoreValue = 0D;
		String grade = "";

		scoreValue = vsTargetValue * weightValue;
		if(vsTargetValue >= 1) {
			scoreValue = weightValue;
		}
		
		if(isDSMGroup) {
			if(level.equals(DSM) && groupStartRow != totalRowNum) {
				scoreCell.setCellValue(scoreValue);
			} else if(level.equals(DSM) && groupStartRow == totalRowNum) {
				scoreCell.setCellValue(sumScore);
				grade = calculateGrade(sumScore * 100, false);
				doGrade(sheet, currRowNum, level, grade == null ? NOT_AVILABLE : grade);
			}
		} else {
			if(!level.equals(DSM)) {
				if(level.toUpperCase().contains(TSM) && groupStartRow == 2){
					sheet.addMergedRegion(new CellRangeAddress(currRowNum - 1, currRowNum, SCORE_COL, SCORE_COL));
				} else if(groupStartRow != totalRowNum) {
					scoreCell.setCellValue(scoreValue);
				} else if(groupStartRow == totalRowNum) {
					scoreCell.setCellValue(sumScore);
					grade = calculateGrade(sumScore * 100, false);
					doGrade(sheet, currRowNum, level, grade == null ? NOT_AVILABLE : grade);
					dsmKpi.countAllForAB++;
					if(grade != null && (grade.equals(GRADE_A) || grade.equals(GRADE_B))) {
						dsmKpi.countOnlyAB++;
					}
					
//					<!-- for using in TSM Kpi(Sup Kpi) -->
					if(level.toUpperCase().contains(TSM)) {
						String tsmCode = sheet.getRow(currRowNum).getCell(POSITION_COL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						if(!StringUtils.isBlank(tsmCode)) {
							TsmActualKpi tsmKpi = tsmActualMaps.get(tsmCode).get(campaignCode);
							tsmKpi.totalScore = sumScore;
							tsmKpi.isGradeNull = grade == null ? true : false;
						} else {
							System.err.println("TSM IS NULL -> currRowNum: " + currRowNum + " | POLISITION COL: " + POSITION_COL + " | Campaign: " + campaignCode);
						}
					}
				}
			}
		}
		return scoreValue;
	}
	
	private void calculateVsTarget(Sheet sheet, int curRowNum, String level, int groupStartRow, int totalRowNum, boolean isDSMGroup) {
		try {
			Cell vsTargetCell = sheet.getRow(curRowNum).getCell(VS_TARGET_COL, Row.CREATE_NULL_AS_BLANK);
			try {
				sheet.getRow(curRowNum).getCell(ACTUAL_COL, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
			} catch(Exception e) {
				System.out.println("sheet: " + sheet.getSheetName() + " | curRownum: " + curRowNum + " | cell: " + ACTUAL_COL);
				throw e;
			}
			Double actualValue = sheet.getRow(curRowNum).getCell(ACTUAL_COL, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
			Double targetValue = sheet.getRow(curRowNum).getCell(TARGET_COL, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
			Double vsTarget = 0D;
			
			if(targetValue != null && targetValue != 0) {
				vsTarget = actualValue / targetValue;
			}
			
			if(isDSMGroup) {
				if(level.equals(DSM) && groupStartRow != totalRowNum) {
					vsTargetCell.setCellValue(vsTarget);
				}
			} else {
				if(!level.equals(DSM)) {
					if(level.toUpperCase().contains(TSM) && groupStartRow == 2){
						Double combineTarget = sheet.getRow(curRowNum - 1).getCell(VS_TARGET_COL, Row.CREATE_NULL_AS_BLANK).getNumericCellValue() * vsTarget;
						vsTargetCell.setCellValue(combineTarget);
						sheet.addMergedRegion(new CellRangeAddress(curRowNum - 1, curRowNum, VS_TARGET_COL, VS_TARGET_COL));
					} else if(groupStartRow != totalRowNum) {
						vsTargetCell.setCellValue(vsTarget);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void copyCellType(Cell origCell, Cell toCell) {
		toCell.setCellType(origCell.getCellType());
	}
	
	private void copyCellTypeAndStyle(Cell origCell, Cell toCell) {
		this.copyCellType(origCell, toCell);
		this.copyStyle(origCell, toCell);
	}

	private void copyCellValue(Cell origCell, Cell toCell) {
		switch(origCell.getCellType()) {
		case Cell.CELL_TYPE_BLANK :
			toCell.setCellValue(origCell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN :
			toCell.setCellValue(origCell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR :
			toCell.setCellValue(origCell.getErrorCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA :
			toCell.setCellValue(origCell.getCellFormula());
			break;
		case Cell.CELL_TYPE_NUMERIC :
			toCell.setCellValue(origCell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING :
			toCell.setCellValue(origCell.getRichStringCellValue());
			break;
		}
	}
	
	private void copyColumnWidth(Sheet origSheet, int origColumnNum, Sheet toSheet, int toColumnNum) {
		toSheet.setColumnWidth(toColumnNum, origSheet.getColumnWidth(origColumnNum));
	}
	
	private void copyRow(Sheet origSheet, Sheet toSheet, int origRowNum, int toRowNum, int startCellNum, int endCellNum) {
		Row toRow = toSheet.getRow(toRowNum);
		Row origRow = origSheet.getRow(origRowNum);
		
		if(toRow == null) {
			toRow = toSheet.createRow(toRowNum);
		}
		
		for(int i = startCellNum; i <= endCellNum; i++) {
			Cell origCell = origRow.getCell(i);
			Cell toCell = toRow.createCell(i, origCell.getCellType());
			copyCellValue(origCell, toCell);
			copyStyle(origCell, toCell);
		}
	}

	private void copyStyle(Cell origCell, Cell toCell) {
		toCell.setCellStyle(origCell.getCellStyle());
	}
	
	private void doActualColumn(Sheet tempSheet, int tempRowNum, Sheet toSheet, int toRowNum, int colNum, KpiResult kpi) {
//		<!-- Actual Column -->
//		<!-- for sum value -->
		DsmKpiByCampaign dsmKpi = dsmActualMaps.get(dsmCode).get(campaignCode);
		TsmActualKpi tsmKpi = null;
		if(!StringUtils.isBlank(tsmCode)) {
			tsmKpi = tsmActualMaps.get(tsmCode).get(campaignCode);
		}
		
//		<!-- keep cell info for add value later -->
		Cell tempCell = tempSheet.getRow(tempRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
		Cell toCell = null;
		switch(tempRowNum) {
//		case 1 : 
//			dsmActualSaleCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
//			copyCellTypeAndStyle(tempCell, dsmActualSaleCell); 
//			break;
//		case 2 : 
//			dsmActualConfCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
//			copyCellTypeAndStyle(tempCell, dsmActualConfCell); 
//			break;
//		case 3 : 
//			dsmActualRetenCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
//			copyCellTypeAndStyle(tempCell, dsmActualRetenCell); 
//			break;
//		case 4 : 
//			dsmActualAbcCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
//			copyCellTypeAndStyle(tempCell, dsmActualAbcCell); 
//			break;
		case 1 : 
			tsmActualTarpCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, tsmActualTarpCell); 
			break;
		case 2 : 
			tsmActualTotalTarpCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, tsmActualTotalTarpCell); 
			break;
		case 3 : 
			tsmActualListConvCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, tsmActualListConvCell); 
			break;
		case 4 : 
			tsmActualConfCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, tsmActualConfCell); 
			break;
		case 5 : 
			tsmActualRetenCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, tsmActualRetenCell); 
			Double retention = getRetentionForTsm();
			tsmActualRetenCell.setCellValue(retention);
			break;
		case 7 : 
			Double sale = kpi.getSumOfAfyp().doubleValue();
			
			if(dsmKpi != null) {
				dsmKpi.sales += sale;
			}
			
			if(tsmKpi != null) {
				tsmKpi.totalTarp += sale;
				tsmKpi.countTsr += 1;
			}
			
			toCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, toCell);
			toCell.setCellValue(sale);
			break;
		case 8 : 
			Double attend = kpi.getCountTalkDate() == null ? 0 : (kpi.getCountTalkDate().doubleValue() / WORK_DAYS);
			toCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, toCell);
			toCell.setCellValue(attend);
			break;
		case 9 : 
			toCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, toCell);
			toCell.setCellValue(kpi.getTotalTalkHrs() == null ? 0D : (kpi.getTotalTalkHrs().doubleValue()));
			break;
		case 10 : 
			Double firstConf = kpi.getFirstConfirmSale().doubleValue() / kpi.getAllSale().doubleValue();
			
			if(dsmKpi != null) {
				dsmKpi.firstSale += kpi.getFirstConfirmSale().doubleValue();
				dsmKpi.allSale += kpi.getAllSale().doubleValue();
			}
			
			if(tsmKpi != null) {
				tsmKpi.firstSale += kpi.getFirstConfirmSale().doubleValue();
				tsmKpi.allSale += kpi.getAllSale().doubleValue();
				
				tsmKpi.eocSuccess += kpi.getSuccessEoc() == null ? 0 : kpi.getSuccessEoc();
				tsmKpi.allEoc += kpi.getAllEoc() == null ? 0 : kpi.getAllEoc();
			}
			
			toCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, toCell);
			toCell.setCellValue(firstConf);
			break;
		default: 
			toCell = toSheet.getRow(toRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
			copyCellTypeAndStyle(tempCell, toCell); 
			break;
		}
	}
	
	private void doCalculate(Sheet toSheet, boolean isDsmGroup) {
//		<!-- vs Target -->
		if(toSheet != null) {
			int groupStartRow = 1;
			String level = "";
			Double sumScore = 0D;
			int totalRowNum = 0;
			for(int n = 1; n <= toSheet.getLastRowNum(); n++) {
				String cellPos = toSheet.getRow(n).getCell(POSITION_COL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				if(!StringUtils.isBlank(cellPos)) {
					if(cellPos.equals(DSM) || cellPos.toUpperCase().contains(TSM) || cellPos.equals(TSR)) {
						level = toSheet.getRow(n).getCell(POSITION_COL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						groupStartRow = 1;
						sumScore = 0D;
						totalRowNum = level.toUpperCase().contains(TSM) ? 6 : 5 ;
					}
				}
				calculateVsTarget(toSheet, n, level, groupStartRow, totalRowNum, isDsmGroup);
				sumScore += calculateScore(toSheet, n, level, groupStartRow, totalRowNum, sumScore, isDsmGroup);
				
				groupStartRow++;
			}
		}
	}
	
	private void doColumn(Sheet tempSheet, Sheet toSheet, final int colNum, int tempRow, int currentRowNum) {
		Row tRow = tempSheet.getRow(tempRow);
		Cell tCell = tRow.getCell(colNum, Row.CREATE_NULL_AS_BLANK);
		
		Row toRow = toSheet.getRow(currentRowNum);
		if(toRow == null) {
			toRow = toSheet.createRow(currentRowNum);
		}
		Cell toCell = toRow.createCell(colNum, tCell.getCellType());
		
		copyStyle(tCell, toCell);
	}
	
	private void doColumn(Sheet tempSheet, Sheet toSheet, final int colNum, int tempRow, int currentRowNum, Object value) {
		
		this.doColumn(tempSheet, toSheet, colNum, tempRow, currentRowNum);
		
		Cell cell = toSheet.getRow(currentRowNum).getCell(colNum, Row.CREATE_NULL_AS_BLANK);
		if(null != value) {
			try {
				if(value instanceof String) {
					cell.setCellValue(String.valueOf(value));
				} else if(value instanceof RichTextString) {
					cell.setCellValue((RichTextString) value);
				} else if(value instanceof Double) {
					cell.setCellValue(((Double) value).doubleValue());
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void doDsmGroup(Sheet toSheet) {
//		<!-- do All for DSM -->
//		if(toSheet != null && dsmCode != null) {
//			DsmKpiByCampaign dsmKpiByCampaign = dsmActualMaps.get(dsmCode).get(campaignCode);
//			double firstConfirmSale = 0;
//			if(dsmKpiByCampaign.allSale != null && dsmKpiByCampaign.allSale > 0) {
//				firstConfirmSale = dsmKpiByCampaign.firstSale / dsmKpiByCampaign.allSale;
//			}
//			double abCat = Integer.valueOf(dsmKpiByCampaign.countOnlyAB).doubleValue() / Integer.valueOf(dsmKpiByCampaign.countAllForAB).doubleValue();
//			
//			dsmActualSaleCell.setCellValue(dsmKpiByCampaign.sales);
//			dsmActualConfCell.setCellValue(firstConfirmSale);
//			dsmActualAbcCell.setCellValue(abCat);
//			
//			Double retentionValue = 0D;
//			Double base = dsmKpiByCampaign.openHc + dsmKpiByCampaign.addDuringHc;
//			if(base != null && base != 0) {
//				retentionValue = dsmKpiByCampaign.endHc / base;
//			}
//			dsmActualRetenCell.setCellValue(retentionValue.doubleValue());
//			
//			doCalculate(toSheet, true);
//		}
	}
	
	private void doDsmKpi() {
		System.out.println("START do DSM Workbook");
		Workbook wb = getWbTemplate();
		Sheet tempSheet = wb.getSheetAt(1);
		Sheet toSheet = wb.createSheet("DSMs Kpi");
		int groupRow = 0;
		int currentRow = 0;
		
		double sumWeight = 0d;
		double sumScore = 0d;
		
		if(dsmActualMaps != null) {
//			<!-- Header -->
			for(int i = 0; i < 8; i++) {
				copyColumnWidth(tempSheet, i, toSheet, i);
			}
			
			copyRow(tempSheet, toSheet, 0, 0, 0, 7);
			currentRow++;
			
			for(String dsmCode : dsmActualMaps.keySet()) {
				Map<String, DsmKpiByCampaign> mapByDsm = dsmActualMaps.get(dsmCode);
//				<!-- Sales Performance Achievement -->
				Double sales = 0D;
				
//				<!-- 1st Confirm Sales -->
				Double firstSale = 0D;
				Double allSaleNum = 0D;
				
//				<!-- Retention Rate -->
				Double openHC = 0D;
				Double addDuring = 0D;
				Double endHc = 0D;
				
//				<!-- A & B Cat -->
				Double onlyABNum = 0D;
				Double allForABCat = 0D;
				
				for(String campaignCode : mapByDsm.keySet()) {
					DsmKpiByCampaign dsmKpiSum = mapByDsm.get(campaignCode);
					
					sales += dsmKpiSum.sales;
					
					firstSale += dsmKpiSum.firstSale;
					allSaleNum += dsmKpiSum.allSale;
					
					openHC += dsmKpiSum.openHc;
					addDuring += dsmKpiSum.addDuringHc;
					endHc += dsmKpiSum.endHc;
					
					onlyABNum += dsmKpiSum.countOnlyAB;
					allForABCat += dsmKpiSum.countAllForAB;
					
				}
				
				List<KpiCategorySetup> kpiCats = kpiService().getKpiCategorySetups(DSM, null, dsmCode, processYearMonth);
				groupRow = 1;
				
				if(kpiCats != null && !kpiCats.isEmpty()) {
					sumWeight = 0d;
					sumScore = 0d;
					
					for(KpiCategorySetup kpiCat : kpiCats) {
						Row toRow = toSheet.getRow(currentRow);
						if(toRow == null) {
							toRow = toSheet.createRow(currentRow);
						}
						
						for(int c = 0; c < 7; c++) {
							Cell tCell = tempSheet.getRow(groupRow).getCell(c, Row.CREATE_NULL_AS_BLANK);
							Cell toCell = toRow.getCell(c, Row.CREATE_NULL_AS_BLANK);
							
							copyCellTypeAndStyle(tCell, toCell);
							switch(c) {
							case POSITION_COL : 
								if(groupRow == 2) {
									toCell.setCellValue(kpiCat.getPersonal().getFullName());
								} else {
									copyRow(tempSheet, toSheet, groupRow, currentRow, c, c);
								}
								break;
							case KPI_COL : 
								toCell.setCellValue(kpiCat.getCategory());
								break;
							case WEIGHT_COL : 
								toCell.setCellValue(kpiCat.getWeight().doubleValue());
								sumWeight += kpiCat.getWeight().doubleValue();
								break;
							case TARGET_COL :
								toCell.setCellValue(kpiCat.getTarget().doubleValue());
								break;
							case ACTUAL_COL :
								double actualValue = 0d;
								if(groupRow == 1) {
									actualValue = sales;
								} else if(groupRow == 2) {
									actualValue = firstSale / allSaleNum;
								} else if(groupRow == 3) {
									Double base = openHC + addDuring;
									if(base != null && base != 0) {
										actualValue = endHc / base;
									}
								} else if(groupRow == 4) {
									if(allForABCat != 0) {
										actualValue = onlyABNum.doubleValue() / allForABCat.doubleValue();
									}
								}
								toCell.setCellValue(actualValue);
								break;
							case VS_TARGET_COL :
								copyCellTypeAndStyle(tCell, toCell);
								calculateVsTarget(toSheet, currentRow, DSM, groupRow, 5, true);
								break;
							case SCORE_COL :
								sumScore += calculateScore(toSheet, currentRow, DSM, groupRow, 5, sumScore, true);
								break;
							default : break;
							}
						}
						
						groupRow++;
						currentRow++;
					}
					
					copyRow(tempSheet, toSheet, groupRow, currentRow, 0, 7);
					toSheet.getRow(currentRow).getCell(WEIGHT_COL, Row.CREATE_NULL_AS_BLANK).setCellValue(sumWeight);
					toSheet.getRow(currentRow).getCell(SCORE_COL, Row.CREATE_NULL_AS_BLANK).setCellValue(sumScore);
					doGrade(toSheet, currentRow, DSM, calculateGrade(sumScore * 100, false));
					currentRow++;
				}
			}
			doWriteWorkbook(wb, "DSM KPI");
		}
	}
	
	private void doTsmKpi() {
		System.out.println("START do TSM Workbook");
		Workbook wb = getWbTemplate();
		Sheet tempSheet = wb.getSheetAt(2);
		Sheet toSheet = wb.createSheet("TSMs Kpi");
		int currentRow = 0;
		
//		<!-- fix column number -->
		final int positionCol = 0;
		final int campaignCodeCol = 1;
		final int campaignNameCol = 2;
		final int gradeCol = 3;
		
		if(tsmActualMaps != null) {
//			<!-- Header -->
			for(int i = 0; i < 4; i++) {
//				<!-- set width -->
				copyColumnWidth(tempSheet, i, toSheet, i);
			}
			copyRow(tempSheet, toSheet, 0, 0, 0, 3);
			currentRow++;
			
			for(String tsmCode : tsmActualMaps.keySet()) {
				double sumGrade = 0d;
				int count = 0;
				int groupRow = 1;
				int loopBlank = 0;
				
				Map<String, TsmActualKpi> campaignMaps = tsmActualMaps.get(tsmCode);
				
				for(String campaignCode : campaignMaps.keySet()) {
					
//					System.out.println("tsm: " + kpiService().getTsrInfoInMap(tsmCode).getFullName() + " | campaignCode: "
//							+ campaignCode + " | campaignName: " + kpiService().getCampaignInMap(campaignCode).getDisplayName()
//							+ " | score: " + campaignMaps.get(campaignCode).totalScore);
					
					TsmActualKpi tsmKpi = campaignMaps.get(campaignCode);
					
					if(groupRow == 1) {
						copyRow(tempSheet, toSheet, groupRow, currentRow, positionCol, positionCol);
					} else if(groupRow == 2) {
						doColumn(tempSheet, toSheet, positionCol, groupRow, currentRow, kpiService().getTsrInfoInMap(tsmCode).getFullName());
					} else if(groupRow == 4) {
						copyRow(tempSheet, toSheet, groupRow - 1, currentRow, positionCol, positionCol);
					}
					
					if(groupRow == 4) {
						doColumn(tempSheet, toSheet, campaignCodeCol, groupRow - 1, currentRow, campaignCode);
						doColumn(tempSheet, toSheet, campaignNameCol, groupRow - 1, currentRow, kpiService().getCampaignInMap(campaignCode).getDisplayName());
					} else {
						doColumn(tempSheet, toSheet, campaignCodeCol, groupRow, currentRow, campaignCode);
						doColumn(tempSheet, toSheet, campaignNameCol, groupRow, currentRow, kpiService().getCampaignInMap(campaignCode).getDisplayName());
					}
					
					if(tsmKpi.isGradeNull) {
						doColumn(tempSheet, toSheet, gradeCol, groupRow, currentRow, NOT_AVILABLE);
					} else {
						if(groupRow == 4) {
							doColumn(tempSheet, toSheet, gradeCol, groupRow - 1, currentRow, calculateGrade(tsmKpi.totalScore * 100, false));
						} else {
							doColumn(tempSheet, toSheet, gradeCol, groupRow, currentRow, calculateGrade(tsmKpi.totalScore * 100, false));
						}
						sumGrade += tsmKpi.totalScore;
						count++;
					}
					
					if(groupRow < 4) {
						groupRow++;
					}
					currentRow++;
				}
				if(groupRow == 2) {
					loopBlank = 2;
				} else if(groupRow == 3) {
					loopBlank = 1;
				}
				
				for(int i = 0; i < loopBlank; i++) {
					if(groupRow == 2) { 
						doColumn(tempSheet, toSheet, positionCol, groupRow, currentRow, kpiService().getTsrInfoInMap(tsmCode).getFullName());
					} else {
						doColumn(tempSheet, toSheet, positionCol, groupRow, currentRow);
					}
					copyRow(tempSheet, toSheet, groupRow, currentRow, campaignCodeCol, gradeCol);
					groupRow++;
					currentRow++;
				}
				
				if(groupRow == 4) {
					double grade = 0;
					if(count != 0) {
						grade = sumGrade / count;
					}
					copyRow(tempSheet, toSheet, groupRow, currentRow, positionCol, gradeCol);
					doColumn(tempSheet, toSheet, gradeCol, groupRow, currentRow, grade == 0 && count == 0 ? NOT_AVILABLE : calculateGrade(grade * 100, false));
					currentRow++;
				}
				
			}
			doWriteWorkbook(wb, "SUP KPI");
		}
	}
	
	private void doGrade(Sheet sheet, int currRowNum, String level, String grade) {
		int row = 0;
		if(level.toUpperCase().contains(TSM)) {
			row = currRowNum - 5;
		} else {
			row = currRowNum - 4;
		}
		sheet.getRow(row).getCell(GRADE_COL, Row.CREATE_NULL_AS_BLANK).setCellValue(grade);
	}

	private void doHead(Workbook wb, String newSheetName) {
		Sheet sheet = wb.getSheetAt(0);
		Sheet toSheet = wb.getSheet(newSheetName);
		copyRow(sheet, toSheet, 0, 0, 0, 6);
	}

	private void doWriteWorkbook(Workbook wb, String name) {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		String path = "D:/Test/export/" + processYearMonth + "/";
		String fileName = name + "_" + processYearMonth + ".xlsx";
		
		try {
			File fp = new File(path);
			
			if(!fp.exists()) {
				fp.mkdirs();
			}
			
			File outpath = new File(path + fileName);
			if(!outpath.exists()) {
				outpath.createNewFile();
			}
			fos = new FileOutputStream(outpath);
			wb.write(fos);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
			try {
				wb.close();
			} catch (IOException e) {
			}
		}
		
		try {
//			<!-- open again to remove sheets -->
			fis = new FileInputStream(new File(path + fileName));
			Workbook temp = WorkbookFactory.create(fis);
			temp.removeSheetAt(0);
			temp.removeSheetAt(0);
			temp.removeSheetAt(0);
			fos = new FileOutputStream(new File(path + fileName));
			temp.write(fos);
			temp.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
			try {
				wb.close();
			} catch (IOException e) {
			}
		}
	}
	
	private void generateRowData(Sheet tempSheet, Sheet toSheet, List<KpiCategorySetup> kpiCats, String tsrCode, String level, KpiResult kpi) {

		int groupRow = 1;
		int groupSize = 0;
		Integer startRowNum = new Integer(currentRow);
		
		int tempRow;
//		<!-- set startTempRow -->
		if(level.equals(DSM)) {
			tempRow = START_DSM_ROW;
			groupSize = 4;
		} else if(level.equals(TSM)) {
			tempRow = START_TSM_ROW;
			groupSize = 5;
		} else if(level.equals(TSR)) {
			tempRow = START_TSR_ROW;
			groupSize = 4;
		} else {
			tempRow = 17;
		}
		
		if(tempRow < 0) {
			return;
		}
		
		int endRow = kpiCats == null ? groupSize : kpiCats.size();
		int i = 0;
		Double weightSum = 0D;
		
		for(int n = startRowNum; n <= (startRowNum + endRow); n++) {
			
//			<!-- Position Column -->
			if(groupRow == 1) {
//				<!-- copy Position name -->\
				copyRow(tempSheet, toSheet, tempRow, n, POSITION_COL, POSITION_COL);
			} else {
				doColumn(tempSheet, toSheet, POSITION_COL, tempRow, n);
				if(groupRow == 2) {
					toSheet.getRow(n).getCell(POSITION_COL, Row.CREATE_NULL_AS_BLANK).setCellValue(kpiService().getTsrInfoInMap(tsrCode).getFullName());
				}
			}
			
			if(kpiCats != null && i < kpiCats.size()) {
				KpiCategorySetup kpiCat = kpiCats.get(i);
//				<!-- Kpis Column -->
				if(level.equals(TSM)) {
					String cat = groupRow == 1 ? kpiCat.getCategory() : tempSheet.getRow(tempRow)
												.getCell(KPI_COL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					doColumn(tempSheet, toSheet, KPI_COL, tempRow, n, cat);
				} else {
					doColumn(tempSheet, toSheet, KPI_COL, tempRow, n, kpiCat.getCategory());
				}
				
//				<!-- Weight Column-->
				if(level.equals(TSM) && groupRow == 1) {
					Double weight = kpiCat.getWeight().doubleValue();
					weightSum += weight;
					doColumn(tempSheet, toSheet, WEIGHT_COL, tempRow, n, weight);
				} else if(level.equals(TSM) && groupRow == 2) {
					doColumn(tempSheet, toSheet, WEIGHT_COL, tempRow, n);
					toSheet.addMergedRegion(new CellRangeAddress(n - 1, n, WEIGHT_COL, WEIGHT_COL));
				} else {
					Double weight = kpiCat.getWeight().doubleValue();
					doColumn(tempSheet, toSheet, WEIGHT_COL, tempRow, n, weight);
					weightSum += weight;
				}
				
//				<!-- Target Column -->
				Double target = kpiCat.getTarget().doubleValue();
				doColumn(tempSheet, toSheet, TARGET_COL, tempRow, n, target);
				
//				<!-- Actual Column -->
				doActualColumn(tempSheet, tempRow, toSheet, n, ACTUAL_COL, kpi);
				
//				<!-- vs Target -->
				doColumn(tempSheet, toSheet, VS_TARGET_COL, tempRow, n);
				
//				<!-- score -->
				doColumn(tempSheet, toSheet, SCORE_COL, tempRow, n);
				
//				<!-- grade -->
				doColumn(tempSheet, toSheet, GRADE_COL, tempRow, n);
				
			} else {
//				<!-- total row -->
//				<!-- Position Column (hidden tsrCode)>
				if(level.equals(TSM) && tempRow == 6) {
					doColumn(tempSheet, toSheet, POSITION_COL, tempRow, n, tsrCode);
				}
				
//				<!-- Kpis Column -->
				RichTextString value = tempSheet.getRow(tempRow).getCell(1, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue();
				doColumn(tempSheet, toSheet, KPI_COL, tempRow, n, value);
				
//				<!-- set weight total -->
				if(kpiCats == null) {
					if(groupRow == 1) {
						doColumn(tempSheet, toSheet, WEIGHT_COL, tempRow, n);
					} else if(groupRow == 2) {
						doColumn(tempSheet, toSheet, WEIGHT_COL, tempRow, n);
						toSheet.addMergedRegion(new CellRangeAddress(n - 1, n, WEIGHT_COL, WEIGHT_COL));
					} else {
						doColumn(tempSheet, toSheet, WEIGHT_COL, tempRow, n);
					}
				} else {
					doColumn(tempSheet, toSheet, WEIGHT_COL, tempRow, n, weightSum);
				}
				
//				<!-- target -->
				doColumn(tempSheet, toSheet, TARGET_COL, tempRow, n);
				
//				<!-- actual -->
				if(kpiCats == null) {
					if(groupRow == 5) {
						getRetentionForTsm();
					}
					doColumn(tempSheet, toSheet, ACTUAL_COL, tempRow, n);
				} else {
					doColumn(tempSheet, toSheet, ACTUAL_COL, tempRow, n);
				}
				
//				<!-- vs target -->
				doColumn(tempSheet, toSheet, VS_TARGET_COL, tempRow, n);
				
//				<!-- score -->
				doColumn(tempSheet, toSheet, SCORE_COL, tempRow, n);
				
//				<!-- grade -->
				doColumn(tempSheet, toSheet, GRADE_COL, tempRow, n);
			}
			tempRow++;
			i++;
			groupRow++;
			currentRow++;
		}
	}
	
	private String getProcessTime(Date start, Date end) {
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

	private double getRetentionForTsm() {
		
		DsmKpiByCampaign dsmKpi = dsmActualMaps.get(dsmCode).get(campaignCode);
		
		double retentionValue = 0d;
		
		if(!StringUtils.isBlank(tsmCode) && !StringUtils.isBlank(campaignCode)) {
			for(KpiRetention retention : kpiRetentions) {
				if(retention.getTsmCode().equals(tsmCode) && retention.getCampaignCode().equals(campaignCode)) {
					Double openHc = retention.getCountStartHc() != null ? retention.getCountStartHc().doubleValue() : 0D;
					Double addDuringHc = retention.getTsrAddDuringMonth() != null ? retention.getTsrAddDuringMonth().doubleValue() : 0D;
					Double base = openHc + addDuringHc;
					Double endHc = retention.getEndHc() != null ? retention.getEndHc().doubleValue() : 0D;
					
					if(dsmKpi != null) {
						dsmKpi.openHc += openHc;
						dsmKpi.addDuringHc += addDuringHc;
						dsmKpi.endHc += endHc;
					}
					
					if(base != null && base != 0) {
						retentionValue = endHc / base;
					}
				}
			}
		}
		return retentionValue;
	}
	
	private Workbook getWbTemplate() {
		try {
			return WorkbookFactory.create(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/template/exportKpiTemplate.xlsx"));
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void setTSMActual() {
		
		if(!StringUtils.isBlank(tsmCode)) {
			TsmActualKpi tsmKpi = tsmActualMaps.get(tsmCode).get(campaignCode);
			if(tsmKpi != null) {
				Double tarpPerTsr = tsmKpi.totalTarp / tsmKpi.countTsr;
				Double firstConfirmSale = tsmKpi.firstSale / tsmKpi.allSale;
				Double listConv = 0D;
				
				if(tsmKpi.allEoc != null && tsmKpi.allEoc != 0D) {
					listConv = (tsmKpi.eocSuccess == null ? 0 : tsmKpi.eocSuccess) / tsmKpi.allEoc;
				}
				
				if(tsmActualConfCell != null) {
					tsmActualConfCell.setCellValue(firstConfirmSale);
				}
				if(tsmActualTotalTarpCell != null) {
					tsmActualTotalTarpCell.setCellValue(tsmKpi.totalTarp);
				}
				if(tsmActualTarpCell != null) {
					tsmActualTarpCell.setCellValue(tarpPerTsr);
				}
				if(tsmActualListConvCell != null) {
					tsmActualListConvCell.setCellValue(listConv);
				}
			}
		}
	}

	private class DsmKpiByCampaign {
		
		private Double sales = 0D;
		
		private Double firstSale = 0D;
		private Double allSale = 0D;
		
		private int countOnlyAB = 0;
		private int countAllForAB = 0;
		
		private Double openHc = 0D;
		private Double addDuringHc = 0D;
		private Double endHc = 0D;
		
	}

	private class TsmActualKpi {
	
		private Double totalTarp = 0D;
		private int countTsr = 0;
		
		private Double firstSale = 0D;
		private Double allSale = 0D;
		
		private Double eocSuccess = 0D;
		private Double allEoc = 0D;
		
		private boolean isGradeNull = false;
		private Double totalScore = 0D;
	}
}
