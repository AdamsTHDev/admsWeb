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
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.Campaign;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.KpiCategorySetup;
import com.adms.domain.entities.KpiScoreRate;
import com.adms.domain.entities.TsrContract;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrPosition;
import com.adms.domain.entities.TsrSite;
import com.adms.domain.entities.TsrStatus;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.ExcelFileFilter;
import com.adms.utils.FileFilterByName;

@Component
public class ImportForKpiJob {
	
	public static ImportForKpiJob instance;
	
	public static ImportForKpiJob getInstance() {
		if(instance == null) {
			instance = new ImportForKpiJob();
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
//		<!-- for import tsr -->
//		importTsr();
//		importCampaignKeyCode();
//		importPosition();
//		test();
//		importKpiTargetSetup();
		try {
			startBatch();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Date end = Calendar.getInstance().getTime();
		System.out.println("=======================================================================");
		System.out.println("END Import Batch- " + end);
		System.out.println("Batch processing time: " + getProcessTime(start, end));
		System.out.println("=======================================================================");
	}
	
	private void startBatch() {
		List<Exception> exceptionList = new ArrayList<Exception>();
		String dir = "D:/Test/upload/kpi/201409";
		
		if(StringUtils.isBlank(dir)) {
			System.err.println("Directory is null");
			return;
		}
		
		File path = new File(dir);
		
		for(File fd : path.listFiles()) {
			if(fd.isDirectory()) {
				for(File byDate: fd.listFiles()) {
					System.out.println("=========================================================");
					System.out.println("folder: " + fd.getName() + " >>> " + byDate.getName());
					List<File> excels = Arrays.asList(byDate.listFiles(new ExcelFileFilter()));
					//specific importing order

					//process sales report by record
					for(File excel : excels) {
						if(excel.getName().contains("Sales_Report_By_Records") || excel.getName().contains("SalesReportByRecords")) {
							try {
								IExcelData xd = factory(excel.getName());
								Date start = Calendar.getInstance().getTime();
								xd.importFromInputStream(new FileInputStream(excel), exceptionList);
								Date end = Calendar.getInstance().getTime();
								System.out.println("Import Sales Report by Records processing time: " + getProcessTime(start, end));
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					//process tsr tracking
					for(File excel : excels) {
						if(excel.getName().contains("TSRTracking") || excel.getName().contains("TsrTrackingReport")) {
							try {
								IExcelData xd = factory(excel.getName());
								Date start = Calendar.getInstance().getTime();
								xd.importFromInputStream(new FileInputStream(excel), exceptionList);
								Date end = Calendar.getInstance().getTime();
								System.out.println("Import TsrTracking processing time: " + getProcessTime(start, end));
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					//process daily perfomance
					for(File excel : excels) {
						if(excel.getName().contains("Daily_Performance_Tracking_ByLot") || excel.getName().contains("DailyPerformanceTrackingReport")) {
							try {
								IExcelData xd = factory(excel.getName());
								Date start = Calendar.getInstance().getTime();
								xd.importFromInputStream(new FileInputStream(excel), exceptionList);
								Date end = Calendar.getInstance().getTime();
								System.out.println("Import Daily Performance Tracking processing time: " + getProcessTime(start, end));
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}

					//process qc reconfirm
					for(File excel : excels) {
						if(excel.getName().contains("QC_Reconfirm")) {
							try {
								IExcelData xd = factory(excel.getName());
								Date start = Calendar.getInstance().getTime();
								xd.importFromInputStream(new FileInputStream(excel), exceptionList);
								Date end = Calendar.getInstance().getTime();
								System.out.println("Import Qc Reconfirm processing time: " + getProcessTime(start, end));
							} catch(Exception e) {
								e.printStackTrace();
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
	
	private IExcelData factory(String excelName) {
		System.out.println("Excel Name: " + excelName);
		
		if(excelName.contains("TSRTracking")) {
			return new ImportTsrTracking();
		} else if(excelName.contains("Daily_Performance_Tracking")) {
			return new DailyPerformanceTracking();
		} else if(excelName.contains("Sales_Report_By_Records")) {
			return new SalesByRecord();
		} else if(excelName.contains("QC_Reconfirm")) {
			return new QcReConfirm();
			
		} else if(excelName.contains("TsrTrackingReport")) {
			return new ImportTsrTrackingOTO();
		} else if(excelName.contains("DailyPerformanceTrackingReport")) {
			return new DailyPerformanceOTO();
		} else if(excelName.contains("SalesReportByRecords")) {
			return new SalesByRecordOTO();
			
		} else {
			return null;
		}
	}
	
	private void importTsr() {
		String dir = "D:/Test/upload/TSRUpdate/";
		String fileName = "TSR_update041114.xlsx";
		File file = new File(dir + fileName);
		
		try {
			InputStream excel = new FileInputStream(file);
			InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.TSR_IMPORT.getValue());
			
			ExcelFormat ef = new ExcelFormat(fileFormat);
			
			DataHolder wbHolder = ef.readExcel(excel);
			DataHolder sheetHolder = wbHolder.get(wbHolder.getKeyList().get(0));
			List<DataHolder> list = sheetHolder.getDataList("tsrInfoList");
			
			TsrContract tsrContract = null;
			TsrInfo tsrInfo = null;
			Campaign campaign = null;
			TsrSite tsrSite = null;
			TsrStatus tsrStatus = null;
			TsrPosition tsrPosition = null;
			
			KpiService service = KpiService.getInstance();
			for(DataHolder data : list) {
				
//				<!-- value from cell -->
				String tsrCode = data.get("tsrCode").getStringValue();
				String siteName = data.get("site").getStringValue();
				
//				String accNo = data.get("accNo").getStringValue();
				String citizenCode = data.get("citizenCode").getStringValue();
				Date startDate = (Date) data.get("startDate").getValue();
//				Date startDateMtb = (Date) data.get("startDateMtb").getValue();
				String campaignA = data.get("campaignA").getStringValue();
//				String campaignB = data.get("campaignB").getStringValue();
				String position = data.get("position").getStringValue();
				String title = data.get("title").getStringValue();
				String firstName = data.get("firstName").getStringValue();
				String lastName = data.get("lastName").getStringValue();
//				String nickName = data.get("nickName").getStringValue().trim();
//				String firstNameEn = null != data.get("firstNameEn").getValue() ? data.get("firstNameEn").getStringValue().trim() : null;
//				String lastNameEn = null != data.get("lastNameEn").getValue() ? data.get("lastNameEn").getStringValue().trim() : null;
				String status = data.get("status").getStringValue();
				Date lastDateOfWork = (Date) data.get("lastDateOfWork").getValue();
//				Date resignEffectiveDate = (Date) data.get("resignEffectiveDate").getValue();
//				Date completePro = (Date) data.get("completeProbation").getValue();
//				String remark = data.get("remark").getStringValue();
//				Date birthDate = (Date) data.get("birthDate").getValue();
//				String highestGrad = data.get("highestGrad").getStringValue();
//				String addressOnIdCard = data.get("addressOnIdCard").getStringValue();
//				String addressCurrent = data.get("addressCurrent").getStringValue();
				String mobileNo = data.get("mobileNo").getStringValue();
//				String suggestedBy = data.get("suggestedBy").getStringValue();
				
				tsrInfo = service.getTsrInfo(tsrCode);
				tsrSite = service.getTsrSite(siteName);
				tsrPosition = service.getTsrPosition(position);
				
				tsrStatus = service.getTsrStatus(status);
				
				tsrInfo = new TsrInfo();
				tsrInfo.setTsrCode(tsrCode);
				tsrInfo.setCitizenCode(citizenCode);
				tsrInfo.setFullName(firstName + " " + lastName);
//				tsrInfo.setFullNameEn(firstNameEn + " " + lastNameEn);
				tsrInfo.setTitle(title);
				tsrInfo.setFirstName(firstName);
				tsrInfo.setLastName(lastName);
//				tsrInfo.setFirstNameEn(firstNameEn);
//				tsrInfo.setLastNameEn(lastNameEn);
				tsrInfo.setMobileNo(mobileNo);
				tsrInfo.setTsrPosition(tsrPosition);
//				tsrInfo.setBirthDate(birthDate);
				
				tsrInfo = service.addOrUpdateTsrInfo(tsrInfo);
				
				tsrContract = new TsrContract();
				tsrContract.setTsrInfo(tsrInfo);
				tsrContract.setTsrSite(tsrSite);
				tsrContract.setTsrStatus(tsrStatus);
				tsrContract.setStartDate(startDate);
				tsrContract.setResignDate(lastDateOfWork);
//				tsrContract.setResignEffectiveDate(resignEffectiveDate);
				tsrContract.setTsrCampaign(campaignA);
				
				service.addTsrContract(tsrContract);
				
			}
			
			fileFormat.close();
			excel.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void importCampaignKeyCode() {
		String dir = "D:/Test/upload/CampaignAndKeycode/";

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
				
				List<String> sheetNames = wb.getKeyList();
				
				DataHolder sheet = wb.get(sheetNames.get(0));
				
				List<DataHolder> datas = sheet.getDataList("campaignKeyCodeList");
				
				for(DataHolder data : datas) {
					
					/*
					 * 
					 * <DataCell row="1" column="A" dataType="TEXT" fieldName="campaignCode"/>
						<DataCell row="1" column="B" dataType="TEXT" fieldName="keyCode" />
						<DataCell row="1" column="C" dataType="TEXT" fieldName="site" />
						<DataCell row="1" column="D" dataType="TEXT" fieldName="partner" />
						<DataCell row="1" column="E" dataType="TEXT" fieldName="insurer"/>
						<DataCell row="1" column="F" dataType="TEXT" fieldName="campaignName"/>
						<DataCell row="1" column="G" dataType="TEXT" fieldName="product"/>
						<DataCell row="1" column="H" dataType="TEXT" fieldName="productCode" />
						<DataCell row="1" column="I" dataType="TEXT" fieldName="scriptCode" />
						<DataCell row="1" column="J" dataType="TEXT" fieldName="desc" />
						<DataCell row="1" column="K" dataType="DATE" dataFormat="dd/MM/yyyy" fieldName="launchDate" />
					 */
					String campaignCode = data.get("campaignCode").getStringValue();
					
					String keyCode = data.get("keyCode").getStringValue();
					String site = data.get("site").getStringValue();
					
					String partner = data.get("partner").getStringValue();
					String insurer = data.get("insurer").getStringValue();
					String campaignName = data.get("campaignName").getStringValue();
					String product = data.get("product").getStringValue();
					String productCode = data.get("productCode").getStringValue();
					String scriptCode = data.get("scriptCode").getStringValue();
					String desc = data.get("desc").getStringValue();
					
					CampaignKeyCode listLot = new CampaignKeyCode();
					listLot.setKeyCode(keyCode);
					listLot = KpiService.getInstance().getCampaignKeyCode(listLot);
					
//					if(listLot != null) {
//						System.err.println("Key Code existing");
//						continue;
//					}
					
					Campaign campaign = KpiService.getInstance().isCampaignExist(data.get("campaignCode").getStringValue());
					
					if(campaign != null) {
						campaign.setCode(campaignCode);
						campaign.setPartner(partner);
						campaign.setInsurer(insurer);
						campaign.setFullName(campaignName);
						campaign.setProduct(product);
						campaign.setProductCode(productCode);
						campaign.setScript(scriptCode);
						campaign.setDescription(desc);
						
						campaign = KpiService.getInstance().updateCampaign(campaign);
					} else {
						campaign = new Campaign();
						campaign.setCode(campaignCode);
						campaign.setPartner(partner);
						campaign.setInsurer(insurer);
						campaign.setFullName(campaignName);
						campaign.setProduct(product);
						campaign.setProductCode(productCode);
						campaign.setScript(scriptCode);
						campaign.setDescription(desc);
						campaign = KpiService.getInstance().addCampaign(campaign);
					}

					if(listLot == null) {
						listLot = new CampaignKeyCode();
						listLot.setKeyCode(keyCode);
						listLot.setCampaign(campaign);
						listLot.setDescription(desc);

						listLot = KpiService.getInstance().addCampaignKeyCode(listLot);
					} else {

						listLot.setKeyCode(keyCode);
						listLot.setCampaign(campaign);
						listLot.setDescription(desc);
						listLot = KpiService.getInstance().updateCampaignKeyCode(listLot);
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
	
	private void importPosition() {
		String dir = "D:/Test/upload/TSRUpdate/";
		String fileName = "TSR_Position_update200914.xlsx";
		
		try {
			File file = new File(dir + fileName);
			
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
	
	private void importKpiTargetSetup() {
		String dir = "D:/Test/upload/KpiTarget/20140901";
		final String DSM = "DSM";
		final String SUP = "SUP";
		final String TSR = "TSR";
		try {
			ExcelFormat ef = new ExcelFormat(Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.KPI_SETUP_IMPORT.getValue()));
			File fdir = new File(dir);
			for(File xls : fdir.listFiles(new FileFilterByName("Sales KPIs Target Setup - 201409-2"))) {
				DataHolder wb = ef.readExcel(new FileInputStream(xls));
				
				for(String sheetName : wb.getKeyList()) {
					try {
						System.out.println("SheetName: " + sheetName);
						DataHolder sheet = wb.get(sheetName);

						Date effectiveDate = (Date) sheet.get("effectiveDate").getValue();
						Date endDate = (Date) sheet.get("endDate").getValue();
						
//						String rateA = sheet.get("rateA").getStringValue();
//						String rateB = sheet.get("rateB").getStringValue();
//						String rateC = sheet.get("rateC").getStringValue();
//						String rateD = sheet.get("rateD").getStringValue();
//						String rateE = sheet.get("rateE").getStringValue();
//						
//						String criRateA = sheet.get("criRateA").getStringValue();
//						String criRateB = sheet.get("criRateB").getStringValue();
//						String criRateC = sheet.get("criRateC").getStringValue();
//						String criRateD = sheet.get("criRateD").getStringValue();
//						String criRateE = sheet.get("criRateE").getStringValue();
						
						if(sheetName.equalsIgnoreCase(DSM)) {
						System.out.println("+++ DSM +++");
							for(DataHolder data : sheet.getDataList("dsmTargetList")) {
								
								String dsmCode = data.get("dsmCode").getStringValue();
								String dsmName = data.get("dsmName").getStringValue();
//								
//								BigDecimal targetCat1 = data.get("targetCat1").getDecimalValue();
//								BigDecimal weightCat1 = data.get("weightCat1").getDecimalValue();
//								
//								BigDecimal targetCat2 = data.get("targetCat2").getDecimalValue();
//								BigDecimal weightCat2 = data.get("weightCat2").getDecimalValue();
//								
//								BigDecimal targetCat3 = data.get("targetCat3").getDecimalValue();
//								BigDecimal weightCat3 = data.get("weightCat3").getDecimalValue();
	//
//								BigDecimal targetCat4 = data.get("targetCat4").getDecimalValue();
//								BigDecimal weightCat4 = data.get("weightCat4").getDecimalValue();
								
//								BigDecimal totalWeight = data.get("totalWeight").getDecimalValue();
								
								List<KpiCategorySetup> kpiSetups = KpiService.getInstance().findKpiCategorySetup(effectiveDate, endDate, null, DSM, dsmCode);
								
								if(kpiSetups == null) {
									TsrInfo dsmInfo = KpiService.getInstance().getTsrInfoInMap(dsmCode);
									if(dsmInfo == null) throw new Exception("Dsm not found: " + dsmCode);
									
									for(int i = 1; i <= 4; i++) {
										KpiCategorySetup kpiSetup = new KpiCategorySetup();
										
										kpiSetup.setTsrLevel(DSM);
										kpiSetup.setPersonal(dsmInfo);
										
										kpiSetup.setEffectiveDate(effectiveDate);
										kpiSetup.setEndDate(endDate);
										
										String category = sheet.get("cat" + i).getStringValue();
										BigDecimal targetCat = data.get("targetCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP);
										BigDecimal weightCat = data.get("weightCat" + i).getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP);
										
//										System.out.println("cat: " + category);
//										System.out.println("target: " + targetCat);
//										System.out.println("weight: " + weightCat);
										
										kpiSetup.setCategory(category);
										kpiSetup.setTarget(targetCat);
										kpiSetup.setWeight(weightCat);
										
										KpiService.getInstance().addKpiCategorySetup(kpiSetup);
									}
								} else {
									/**
									 * don't know what to do yet.
									 */
								}
								
							}
							
						} else {
							
							String campaignCode = sheet.get("campaignCode").getStringValue();
							
//							<!-- Sup List -->
							System.out.println("+++ SUP +++");
							for(DataHolder data : sheet.getDataList("supTargetList")) {
								String supCode = data.get("supCode").getStringValue();
								List<KpiCategorySetup> kpiSetups = KpiService.getInstance().findKpiCategorySetup(effectiveDate, endDate, campaignCode, SUP, supCode);
								
								if(kpiSetups == null) {
									TsrInfo supInfo = KpiService.getInstance().getTsrInfoInMap(supCode);
									if(supInfo == null) throw new Exception("SUP not found: " + supCode);
									
									Campaign campaign = KpiService.getInstance().getCampaignInMap(campaignCode);
									if(campaign == null) throw new Exception("Camapaign not found: " + campaignCode);
									
									for(int i = 1; i <= 5; i++) {
										KpiCategorySetup kpiSetup = new KpiCategorySetup();
										
										kpiSetup.setCampaign(campaign);
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
									}
								}
							}
//							<!-- end Sup List -->
							
//							<!-- Tsr List -->
							System.out.println("+++ TSR +++");
							for(DataHolder data : sheet.getDataList("tsrTargetList")) {
								List<KpiCategorySetup> kpiSetups = KpiService.getInstance().findKpiCategorySetup(effectiveDate, endDate, campaignCode, TSR, null);
								
								if(kpiSetups == null) {
									
									Campaign campaign = KpiService.getInstance().getCampaignInMap(campaignCode);
									if(campaign == null) throw new Exception("Camapaign not found: " + campaignCode);
									
									for(int i = 1; i <= 4; i++) {
										KpiCategorySetup kpiSetup = new KpiCategorySetup();
										
										kpiSetup.setCampaign(campaign);
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
		}
	}
	
}