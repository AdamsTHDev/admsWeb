package com.adms.batch.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.adms.batch.enums.EFileFormat;
import com.adms.batch.service.KpiService;
import com.adms.domain.entities.Campaign;
import com.adms.domain.entities.CampaignKeyCode;
import com.adms.domain.entities.TsrContract;
import com.adms.domain.entities.TsrInfo;
import com.adms.domain.entities.TsrPosition;
import com.adms.domain.entities.TsrSite;
import com.adms.domain.entities.TsrStatus;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.utils.ExcelFileFilter;

@Component
public class ImportForKpiJob {
	
	public static ImportForKpiJob instance;
	
	public static ImportForKpiJob getInstance() {
		if(instance == null) {
			instance = new ImportForKpiJob();
		}
		return instance;
	}

	public void execute() {

		Date start = Calendar.getInstance().getTime();
		System.out.println("START Batch - " + start);
//		<!-- for import tsr -->
		importTsr();
//		importCampaignKeyCode();
//		importPosition();
		
//		startBatch();
		
		Date end = Calendar.getInstance().getTime();
		System.out.println("END - " + end);
		Long sec = end.getTime() - start.getTime();
		System.out.println("Process time: " + sec + "ms");
	}
	
	private void startBatch() {
		String dir = "D:/Test/upload/POM/20140701/";
		
		if(StringUtils.isBlank(dir)) {
			System.err.println("Directory is null");
			return;
		}
		
		File path = new File(dir);
		List<File> excels = Arrays.asList(path.listFiles(new ExcelFileFilter()));
		
		// process tsr tracking
		for(File excel : excels) {
			if(excel.getName().toLowerCase().contains("tsrtracking")) {
				try {
//					ImportTsrTracking.getInstance().importFromInputStream(new FileInputStream(excel));
				} catch(Exception e) {
					e.printStackTrace();
				}
				excels.remove(excel);
			}
		}
		
		//process daily perfomance
		for(File excel : excels) {
			if(excel.getName().toLowerCase().contains("daily_performance")) {
				try {
//					DailyPerformanceTracking.getInstance().importFromInpuStream(new FileInputStream(file));
				} catch(Exception e) {
					e.printStackTrace();
				}
				excels.remove(excel);
			}
		}

		//process sales report by record
		for(File excel : excels) {
			if(excel.getName().toLowerCase().contains("sales_report_by")) {
				try {
//					SalesByRecord.getInstance().importFromInpuStream(new FileInputStream(file));
				} catch(Exception e) {
					e.printStackTrace();
				}
				excels.remove(excel);
			}
		}

		//process qc reconfirm
		for(File excel : excels) {
			if(excel.getName().toLowerCase().contains("qc_reconfirm")) {
				try {
					QcReConfirm.getInstance().importFromInpuStream(new FileInputStream(excel));
				} catch(Exception e) {
					e.printStackTrace();
				}
				excels.remove(excel);
			}
		}
		
	}
	
	private void importTsr() {
		String dir = "D:/Test/upload/TSRUpdate/";
		String fileName = "TSR_update200914.xlsx";
		File file = new File(dir + fileName);
		
		try {
			InputStream excel = new FileInputStream(file);
			InputStream fileFormat = Thread.currentThread().getContextClassLoader().getResourceAsStream(EFileFormat.TSR_IMPORT.getValue());
			
			ExcelFormat ef = new ExcelFormat(fileFormat);
			
			DataHolder wbHolder = ef.readExcel(excel);
			DataHolder sheetHolder = wbHolder.get(wbHolder.getKeyList().get(0));
			List<DataHolder> list = sheetHolder.getDataList("tsrInfoList");
			
			/*
			 * <DataCell row="1" column="B" dataType="TEXT" fieldName="tsrCode" />
				
				<DataCell row="1" column="D" dataType="TEXT" fieldName="empCode" />
				<DataCell row="1" column="E" dataType="TEXT" fieldName="site" />
				<DataCell row="1" column="F" dataType="TEXT" fieldName="accNo" />
				<DataCell row="1" column="G" dataType="TEXT" fieldName="citizenCode" />
				<DataCell row="1" column="H" dataType="DATE" dataFormat="d-MMM-yy" fieldName="startDate" />
				<DataCell row="1" column="I" dataType="DATE" dataFormat="d-MMM-yy" fieldName="startDateMtb" />
				<DataCell row="1" column="J" dataType="TEXT" fieldName="campaignA" />
				<DataCell row="1" column="K" dataType="TEXT" fieldName="campaignB" />
				<DataCell row="1" column="L" dataType="TEXT" fieldName="position" />
				<DataCell row="1" column="M" dataType="TEXT" fieldName="title" />
				<DataCell row="1" column="N" dataType="TEXT" fieldName="firstName" />
				<DataCell row="1" column="O" dataType="TEXT" fieldName="lastName" />
				<DataCell row="1" column="P" dataType="TEXT" fieldName="nickName" />
				<DataCell row="1" column="Q" dataType="TEXT" fieldName="firstNameEn" />
				<DataCell row="1" column="R" dataType="TEXT" fieldName="lastNameEn" />
				<DataCell row="1" column="S" dataType="TEXT" fieldName="status" />
				<DataCell row="1" column="T" dataType="DATE" dataFormat="d-MMM-yy" fieldName="lastDateOfWork" />
				<DataCell row="1" column="U" dataType="DATE" dataFormat="d-MMM-yy" fieldName="resignEffectiveDate" />
				<DataCell row="1" column="V" dataType="DATE" dataFormat="d-MMM-yy" fieldName="completeProbation" />
				<DataCell row="1" column="W" dataType="TEXT" fieldName="remark" />
				<DataCell row="1" column="X" dataType="DATE" dataFormat="d MMM yy" fieldName="birthDate" />
				<DataCell row="1" column="Y" dataType="TEXT" fieldName="highestGrad" />
				<DataCell row="1" column="Z" dataType="TEXT" fieldName="addressOnIdCard" />
				<DataCell row="1" column="AA" dataType="TEXT" fieldName="addressCurrent" />
				<DataCell row="1" column="AB" dataType="TEXT" fieldName="mobileNo" />
				<DataCell row="1" column="AC" dataType="TEXT" fieldName="suggestedBy" />
				<DataCell row="1" column="AD" dataType="TEXT" fieldName="emergencyContact" />
				<DataCell row="1" column="AE" dataType="TEXT" fieldName="emergencyCall" />
				<DataCell row="1" column="AG" dataType="TEXT" fieldName="brokerLicense" />
			 */
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
				String firstNameEn = data.get("firstNameEn").getStringValue();
				String lastNameEn = data.get("lastNameEn").getStringValue();
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
				if(title != null) {
					title = title.replaceAll(" ", "");
				}
				tsrInfo.setTitle(title);
				tsrInfo.setFirstName(firstName);
				tsrInfo.setLastName(lastName);
				tsrInfo.setFirstNameEn(firstNameEn);
				tsrInfo.setLastNameEn(lastNameEn);
				tsrInfo.setMobileNo(mobileNo);
				tsrInfo.setTsrPosition(tsrPosition);
//				tsrInfo.setBirthDate(birthDate);
				
				tsrInfo = service.addOrUpdateTsrInfo(tsrInfo);
				
//				tsrContract = new TsrContract();
//				tsrContract.setTsrInfo(tsrInfo);
//				tsrContract.setTsrSite(tsrSite);
//				tsrContract.setTsrStatus(tsrStatus);
//				tsrContract.setStartDate(startDate);
//				tsrContract.setResignDate(lastDateOfWork);
////				tsrContract.setResignEffectiveDate(resignEffectiveDate);
//				tsrContract.setTsrCampaign(campaignA);
//				
//				service.addTsrContract(tsrContract);
					
				
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
	
}
