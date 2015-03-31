package com.adms.batch.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.job.sub.DailyPerformanceOTO;
import com.adms.batch.job.sub.DailyPerformanceTracking;
import com.adms.batch.job.sub.IExcelData;
import com.adms.batch.job.sub.ImportTsrTracking;
import com.adms.batch.job.sub.ImportTsrTrackingOTO;
import com.adms.batch.job.sub.QcReConfirm;
import com.adms.batch.job.sub.QcReConfirmMSIGUOB;
import com.adms.batch.job.sub.QcReConfirmOTO;
import com.adms.batch.job.sub.SalesByRecord;
import com.adms.batch.job.sub.SalesByRecordMSIGUOB;
import com.adms.batch.job.sub.SalesByRecordOTO;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.Campaign;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.KpiCategorySetup;
import com.adms.domain.entities.TsrHierarchical;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrPosition;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.ExcelFileFilter;
import com.adms.utils.FileFilterByName;

@Component
public class ImportForKpiJob {
	
	public static ImportForKpiJob instance;
	private String yyyyMMprocess = "";
	
//	private List<String> dirs = new ArrayList<>();
	
	public static ImportForKpiJob getInstance(final String processDate) {
		if(instance == null) {
			instance = new ImportForKpiJob();
			instance.yyyyMMprocess = processDate.substring(0, 4);
		}
		return instance;
	}
	
	private static String getProcessTime(Date start, Date end) {
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
	
	public void execute() {

		Date start = Calendar.getInstance().getTime();
		System.out.println("=======================================================================");
		System.out.println("START Import Batch - " + start);
		System.out.println("=======================================================================");
		try {
//			yyyyMMprocess = processDate.substring(0, 4);
//			importCampaignKeyCode();
//			importPosition();

//			<-- Below here are need to Import Monthly -->
//			importTsr("D:/Test/upload/TSRUpdate/Update New Staff by month for comm_March-2015.xlsx");
			
//			Sup DSM Hierarchy
//			importSupDsmHierarchy("D:/Test/upload/TSRUpdate/", "UPDATE_SUP_DSM.xlsx");
			
//			KPI Target
//			importKpiTargetSetup();

//			<-- not use EOC anymore -->
//			importEoc();
			
//			importDataForKPI();
//			</-- Import Monothly -->
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Date end = Calendar.getInstance().getTime();
		System.out.println("=======================================================================");
		System.out.println("END Import Batch- " + end);
		System.out.println("Batch processing time: " + getProcessTime(start, end));
		System.out.println("=======================================================================");
	}
	
	public void importDataForKPI(String dir) {
		System.out.println("=======================================================================");
		System.out.println("========================== START IMPORT DATA ==========================");
		System.out.println("=======================================================================");
		List<Exception> exceptionList = new ArrayList<Exception>();
		dir = dir + "/" + yyyyMMprocess;
		
		if(StringUtils.isBlank(dir)) {
			System.err.println("Directory is null");
			return;
		}
		
		File path = new File(dir);
		
		for(File fd : path.listFiles(
//				new FileFilterByName(
//						"MSIG Happy Life BL"
//						)
				)) {
			if(fd.isDirectory()) {
				for(File byDate: fd.listFiles(
//						new FileFilterByName(
//						"15112014"
//						)
						)) {
					System.out.println("=========================================================");
					System.out.println("folder: " + fd.getName() + " >>> " + byDate.getName());
					List<File> excels = Arrays.asList(byDate.listFiles(new ExcelFileFilter()));
					//specific importing order

					//process sales report by record
					for(File excel : excels) {
						if(excel.getName().contains("Sales_Report_By_Record") || excel.getName().contains("SalesReportByRecord")) {
							FileInputStream fis = null;
							try {
								fis = new FileInputStream(excel);
								IExcelData xd = factory(excel.getName(), fd.getName());
								Date start = Calendar.getInstance().getTime();
								xd.importFromInputStream(fis, exceptionList);
								Date end = Calendar.getInstance().getTime();
								System.out.println("Import Sales Report by Records processing time: " + getProcessTime(start, end));
							} catch(Exception e) {
								e.printStackTrace();
							} finally {
								try { fis.close(); } catch (IOException e) {}
							}
						}
					}
					
					//process tsr tracking
					for(File excel : excels) {
						if(excel.getName().contains("TSRTracking") || excel.getName().contains("TsrTrackingReport")) {
							FileInputStream fis = null;
							try {
								fis = new FileInputStream(excel);
								IExcelData xd = factory(excel.getName(), fd.getName());
								Date start = Calendar.getInstance().getTime();
								xd.importFromInputStream(fis, exceptionList);
								Date end = Calendar.getInstance().getTime();
								System.out.println("Import TsrTracking processing time: " + getProcessTime(start, end));
							} catch(Exception e) {
								e.printStackTrace();
							} finally {
								try { fis.close(); } catch (IOException e) {}
							}
						}
					}

					//process qc reconfirm
					for(File excel : excels) {
						if(excel.getName().contains("QC_Reconfirm")) {
							FileInputStream fis = null;
							try {
								fis = new FileInputStream(excel);
								IExcelData xd = factory(excel.getName(), fd.getName());
								Date start = Calendar.getInstance().getTime();
								xd.importFromInputStream(fis, exceptionList);
								Date end = Calendar.getInstance().getTime();
								System.out.println("Import Qc Reconfirm processing time: " + getProcessTime(start, end));
							} catch(Exception e) {
								e.printStackTrace();
							} finally {
								try { fis.close(); } catch (IOException e) {}
							}
						}
					}
					
				}
			}
		}

		if(exceptionList.size() > 0) {
			System.err.println("Found error during batch:");
			for(Exception e : exceptionList) {
				System.err.println("ERROR: " + e.getMessage());
			}
		}
		
	}
	
	private IExcelData factory(String excelName, String campaignFolderName) {
		System.out.println("Excel Name: " + excelName);
		
		if(excelName.contains("TSRTracking")) {
			return new ImportTsrTracking();
		} else if(excelName.contains("Daily_Performance_Tracking")) {
			return new DailyPerformanceTracking();
		} else if(excelName.contains("Sales_Report_By_Records_Pending")) {
			return new SalesByRecordMSIGUOB();
		} else if(excelName.contains("Sales_Report_By_Record")) {
			return new SalesByRecord();
			
		} else if(excelName.toLowerCase().contains("qc_reconfirm") && campaignFolderName.equalsIgnoreCase("MSIG UOB")) {
			return new QcReConfirmMSIGUOB();
		} else if(excelName.contains("QC_Reconfirm_")) {
			return new QcReConfirmOTO();
		} else if(excelName.contains("QC_Reconfirm")) {
			return new QcReConfirm();
			
		} else if(excelName.contains("TsrTrackingReport")) {
			return new ImportTsrTrackingOTO();
		} else if(excelName.contains("DailyPerformanceTrackingReport")) {
			return new DailyPerformanceOTO();
		} else if(excelName.contains("SalesReportByRecord")) {
			return new SalesByRecordOTO();
			
		} else {
			return null;
		}
	}
	
	public void importSupDsmHierarchy(String dir, String fileName) {
		System.out.println("======================================================================================");
		System.out.println("================================ START Import SUP DSM ================================");
		System.out.println("======================================================================================");
		File file = new File(dir + "/" + fileName);
		
		try {
			InputStream xls = new FileInputStream(file);
			InputStream format = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.SUP_DSM_IMPORT.getValue());
			
			ExcelFormat ef = new ExcelFormat(format);
			DataHolder wbHolder = ef.readExcel(xls);
			DataHolder sheetHolder = wbHolder.get(wbHolder.getKeyList().get(0));

			KpiService service = KpiService.getInstance();
			List<DataHolder> list = sheetHolder.getDataList("dataList");
			for(DataHolder data : list) {
				String tsrCode = data.get("tsrCode").getStringValue();
				String uplineCode = data.get("uplineCode").getStringValue();
				Date startDate = (Date) data.get("startDate").getValue();
				Date endDate = (Date) data.get("endDate").getValue();
				
				TsrHierarchical h = service.getTsrHierarchical(tsrCode, uplineCode, null, startDate, null);
				if(h == null) {
					service.addTsrHierarchical(service.getTsrInfoInMap(tsrCode), service.getTsrInfoInMap(uplineCode), null, startDate, endDate);
					System.out.println("ADD ==> " + tsrCode + " | uplineCode: " + uplineCode + " | startDate: " + startDate + " | endDate: " + endDate);
				} else {
					
				}
			}
			service.refreshDsmMap();
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("======================================================================================");
		System.out.println("================================ FINISH Import SUP DSM ================================");
		System.out.println("======================================================================================");
	}
	
	public void importTsr(String filePath) {
		System.out.println("======================================================================================");
		System.out.println("================================ START Import TSR ================================");
		System.out.println("======================================================================================");
		
		File file = new File(filePath);
		
		try {
			InputStream excel = new FileInputStream(file);
			InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.TSR_IMPORT.getValue());
			
			ExcelFormat ef = new ExcelFormat(fileFormat);
			
			DataHolder wbHolder = ef.readExcel(excel);
			DataHolder sheetHolder = wbHolder.get(wbHolder.getKeyList().get(0));
			List<DataHolder> list = sheetHolder.getDataList("tsrInfoList");
			
			TsrInfo tsrInfo = null;
//			TsrSite tsrSite = null;
//			TsrStatus tsrStatus = null;
			TsrPosition tsrPosition = null;
			
			List<TsrInfo> tsrInfoList = new ArrayList<>();
			
			KpiService service = KpiService.getInstance();
			for(DataHolder data : list) {
				
//				<!-- value from cell -->
				String tsrCode = data.get("tsrCode").getStringValue();
//				String siteName = data.get("site").getStringValue();
				
				Date startDate = (Date) data.get("startDate").getValue();
//				String campaignA = data.get("campaignA").getStringValue();
				String position = data.get("position").getStringValue();
				String firstName = data.get("firstName").getStringValue();
				String lastName = data.get("lastName").getStringValue();
//				String status = data.get("status").getStringValue();
				Date lastDateOfWork = (Date) data.get("lastDateOfWork").getValue();
				
//				tsrSite = service.getTsrSite(siteName);
				tsrPosition = service.getTsrPosition(position);
//				tsrStatus = service.getTsrStatus(status);
				
				tsrInfo = new TsrInfo();
				tsrInfo.setTsrCode(tsrCode);
				tsrInfo.setFullName(firstName + " " + lastName);
				tsrInfo.setFirstName(firstName);
				tsrInfo.setLastName(lastName);
				tsrInfo.setTsrPosition(tsrPosition);
				
//				service.saveTsrInfoAndContract(tsrInfo, tsrSite, tsrStatus, startDate, campaignA, lastDateOfWork);
				
				tsrInfo.setStartDate(startDate);
				tsrInfo.setResignDate(lastDateOfWork);
				
				tsrInfoList.add(tsrInfo);
				
			}
			for(TsrInfo t : tsrInfoList) {
				service.saveTsrInfo(t);
			}
			service.refreshTsrInfoMap();
			
			fileFormat.close();
			excel.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("======================================================================================");
		System.out.println("================================ FINISH Import TSR ================================");
		System.out.println("======================================================================================");
	}
	
	public void importCampaignKeyCode(String dir) {
//		String dir = "D:/Test/upload/CampaignAndKeycode/import";

		File path = new File(dir);
		File[] excels = path.listFiles(new ExcelFileFilter());
		InputStream xlsStream;
		InputStream fileFormat;
		for(File excel : excels) {
			xlsStream = null;
			fileFormat = null;
			try {
				System.out.println("FileName: " + excel.getName());
				xlsStream = new FileInputStream(excel);
				fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.CAMPAIGN_KEY_CODE.getValue());
				
				ExcelFormat ef = new ExcelFormat(fileFormat);
				DataHolder wb = ef.readExcel(xlsStream);
				
				DataHolder sheetHolder = wb.get(wb.getKeyList().get(1));
				List<DataHolder> dataHolders = sheetHolder.getDataList("dataList");
				
				for(DataHolder data : dataHolders) {
					String campaignCode = data.get("campaignCode").getStringValue();
					String campaignName = data.get("campaignName").getStringValue();
					String site = data.get("site").getStringValue();
					String partner = data.get("partner").getStringValue();
					String insurer = data.get("insurer").getStringValue();
					
					Campaign campaign = KpiService.getInstance().getCampaignInMap(campaignCode);
					
					if(campaign == null) {
						campaign = new Campaign();
						campaign.setCode(campaignCode);
						campaign.setDisplayName(campaignName);
						campaign.setFullName(campaignName);
						campaign.setPartner(partner);
						campaign.setInsurer(insurer);
						campaign.setSite(site);
						
						KpiService.getInstance().addCampaign(campaign);
					}
				}
//				
//				
				sheetHolder = wb.get(wb.getKeyList().get(0));
				dataHolders = sheetHolder.getDataList("dataList");
				
				for(DataHolder data : dataHolders) {
					String campaignCode = data.get("campaignCode").getStringValue();
					String keyCode = data.get("keyCode").getStringValue();
					String listLotName = data.get("listLotName").getStringValue();
					Date effectiveDate = (Date) data.get("lotEffectiveDate").getValue();
					
					CampaignKeyCode listLot = KpiService.getInstance().getCampaignKeyCode(keyCode);
					
					if(listLot == null) {
						listLot = new CampaignKeyCode();
						listLot.setKeyCode(keyCode);
						listLot.setCampaign(KpiService.getInstance().getCampaignInMap(campaignCode));
						listLot.setDescription(listLotName);
						listLot.setStartDate(effectiveDate);
						KpiService.getInstance().addCampaignKeyCode(listLot);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(xlsStream != null) {
					try {
						xlsStream.close();
					} catch (IOException e) {
					}
				}
				if(fileFormat != null) {
					try {
						fileFormat.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
	
	public void importPosition(String dir) {
//		String dir = "D:/Test/upload/TSRUpdate/TSR_Position_update200914.xlsx";
		
		try {
			File file = new File(dir);
			
			InputStream excel = new FileInputStream(file);
			InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.POSITION_IMPORT.getValue());
			ExcelFormat ef = new ExcelFormat(fileFormat);
			
			DataHolder wb = ef.readExcel(excel);
			
			DataHolder sheet = wb.get(wb.getKeyList().get(0));
			
			List<DataHolder> datas = sheet.getDataList("positionList");
			TsrPosition tsrPosition = null;
			for(DataHolder data : datas) {
				String positionName = data.get("positionName").getStringValue();
				tsrPosition = KpiService.getInstance().getTsrPosition(positionName);
				
				if(tsrPosition == null) {
					tsrPosition = new TsrPosition(positionName);
					String positionParam = positionName.toUpperCase().replaceAll(" ", "_");
					tsrPosition.setParam(positionParam);
					tsrPosition = KpiService.getInstance().addTsrPosition(tsrPosition);
					System.out.println("NEW: " + tsrPosition.toString());
				}
				
			}
			
			excel.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void importKpiTargetSetup(String dir, String fileName) {
		dir = dir + "/" + yyyyMMprocess;
		fileName = fileName.replaceAll("#yyyyMM", yyyyMMprocess);
		
		final String DSM = "DSM";
		final String SUP = "SUP";
		final String TSR = "TSR";
		InputStream is = null;
		try {
//			delete old kpi category
			KpiService.getInstance().deleteKpiCategorySetupByEffectiveMonth(yyyyMMprocess);
			
			ExcelFormat ef = new ExcelFormat(Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.KPI_SETUP_IMPORT.getValue()));
			File fdir = new File(dir);
			for(File xls : fdir.listFiles(new FileFilterByName(fileName))) {
				is = new FileInputStream(xls);
				DataHolder wb = ef.readExcel(is);
				
				for(String sheetName : wb.getKeyList()) {
					try {
						System.out.println("SheetName: " + sheetName);
						DataHolder sheet = wb.get(sheetName);

						Date effectiveDate = (Date) sheet.get("effectiveDate").getValue();
						Date endDate = (Date) sheet.get("endDate").getValue();
						
						if(sheetName.equalsIgnoreCase(DSM)) {
							System.out.println("+++ DSM +++");
							for(DataHolder data : sheet.getDataList("dsmTargetList")) {
								
								String dsmCode = data.get("dsmCode").getStringValue();
								
								List<KpiCategorySetup> kpiSetups = KpiService.getInstance().findKpiCategorySetup(effectiveDate, endDate, null, DSM, dsmCode);
								
								TsrInfo dsmInfo = KpiService.getInstance().getTsrInfoInMap(dsmCode);
								if(dsmInfo == null) throw new Exception("Dsm not found: " + dsmCode);
								
								System.out.println("DSM => " + dsmInfo.getTsrCode() + " | name: " + dsmInfo.getFullName());
								
								for(int i = 1; i <= 4; i++) {
									KpiCategorySetup kpiSetup = new KpiCategorySetup();
									
									kpiSetup.setTsrLevel(DSM);
									kpiSetup.setPersonal(dsmInfo);
									
									kpiSetup.setEffectiveDate(effectiveDate);
									kpiSetup.setEndDate(endDate);
									
									String category = sheet.get("cat" + i).getStringValue();
									BigDecimal targetCat = data.get("targetCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP);
									BigDecimal weightCat = data.get("weightCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP);
									
									kpiSetup.setCategory(category);
									kpiSetup.setTarget(targetCat);
									kpiSetup.setWeight(weightCat);
									if(kpiSetups == null) {
										KpiService.getInstance().addKpiCategorySetup(kpiSetup);
									}
									
								}								
							}
							
						} else {
							
							String campaignCode = sheet.get("campaignCode").getStringValue();
							String keyCode = sheet.get("keyCode").getStringValue();
							
//							<!-- Sup List -->
							System.out.println("+++ SUP +++");
							for(DataHolder data : sheet.getDataList("supTargetList")) {
								String supCode = data.get("supCode").getStringValue();
//								List<KpiCategorySetup> kpiSetups = KpiService.getInstance().findKpiCategorySetup(effectiveDate, endDate, campaignCode, SUP, supCode);
								

								TsrInfo supInfo = KpiService.getInstance().getTsrInfoInMap(supCode);
								if(supInfo == null) throw new Exception("SUP not found: " + supCode);
								
								Campaign campaign = KpiService.getInstance().getCampaignInMap(campaignCode);
								if(campaign == null) throw new Exception("Camapaign not found: " + campaignCode);
								
								for(int i = 1; i <= 5; i++) {
									KpiCategorySetup kpiSetup = new KpiCategorySetup();
									
									kpiSetup.setCampaign(campaign);
									kpiSetup.setKeyCode(keyCode);
									
									kpiSetup.setTsrLevel(SUP);
									kpiSetup.setPersonal(supInfo);
									
									kpiSetup.setEffectiveDate(effectiveDate);
									kpiSetup.setEndDate(endDate);
									
									String category = sheet.get("cat" + i).getStringValue();
									BigDecimal targetCat = data.get("targetCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP);
									BigDecimal weightCat = data.get("weightCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP);
									
									kpiSetup.setCategory(category);
									kpiSetup.setTarget(targetCat);
									kpiSetup.setWeight(weightCat);
									
									KpiService.getInstance().addKpiCategorySetup(kpiSetup);
									
//									if(kpiSetups == null) {
//										KpiService.getInstance().addKpiCategorySetup(kpiSetup);
//									} else {
//										kpiSetup.setId(kpiSetups.get(0).getId());
//										KpiService.getInstance().updateKpiCategorySetup(kpiSetup);
//									}
								}
							}
//							<!-- end Sup List -->
							
//							<!-- Tsr List -->
							System.out.println("+++ TSR +++");
							for(DataHolder data : sheet.getDataList("tsrTargetList")) {
//								List<KpiCategorySetup> kpiSetups = KpiService.getInstance().findKpiCategorySetup(effectiveDate, endDate, campaignCode, TSR, null);

								Campaign campaign = KpiService.getInstance().getCampaignInMap(campaignCode);
								if(campaign == null) throw new Exception("Camapaign not found: " + campaignCode);
								
								for(int i = 1; i <= 4; i++) {
									KpiCategorySetup kpiSetup = new KpiCategorySetup();
									
									kpiSetup.setCampaign(campaign);
									kpiSetup.setKeyCode(keyCode);
									
									kpiSetup.setTsrLevel(TSR);
									
									kpiSetup.setEffectiveDate(effectiveDate);
									kpiSetup.setEndDate(endDate);
									
									DataHolder tsrCat = sheet.getDataList("tsrCat").get(0);
									
									kpiSetup.setCategory(tsrCat.get("tsrCat" + i).getStringValue());
									kpiSetup.setTarget(data.get("targetCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP));
									kpiSetup.setWeight(data.get("weightCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP));
									
									KpiService.getInstance().addKpiCategorySetup(kpiSetup);
								}
							}
//							<!-- end Tsr List -->
						}
						
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}
	
//	private void importEoc() {
//		String fileName = "EOC_20150219.xlsx";
//		String dir = "D:/Test/upload/EOC/";
//		try {
//			ExcelFormat ef = new ExcelFormat(Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.KPI_EOC_IMPORT.getValue()));
//			File file = new File(dir + fileName);
//			
//			if(!file.isFile()) return;
//			
//			DataHolder wb = ef.readExcel(new FileInputStream(file));
//			DataHolder sheet = wb.get(wb.getKeyList().get(0));
//			
//			for(DataHolder data : sheet.getDataList("eocList")) {
//				String keyCode = data.get("keyCode").getStringValue();
//				String tsrCode = data.get("tsrCode").getStringValue();
//				String countSuccess = data.get("countSuccess").getStringValue();
//				String countAllEoc = data.get("countAllEoc").getStringValue();
//				
//				KpiService.getInstance().addUpdateKpiEoc(keyCode, tsrCode, countSuccess, countAllEoc);
//			}
//			
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
	
}