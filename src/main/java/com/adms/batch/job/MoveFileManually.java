package com.adms.batch.job;

import java.io.File;
import java.io.FileFilter;

import com.adms.web.utils.FileUtils;

public class MoveFileManually {

	public static void main(String[] args) {
		System.out.println("START");

		String monthYear = "201409";
		
		String[] targetCampaigns = new String[]{
//				"POM_PA_Cash_Back"
//				, "Health Return Cash"
//				, "HIP_DDOP"
//				, "KBANK DDOP -PA Cash Back"
//				, "KBANK DDOP -POM PA Cash Back"
//				, "MTI-KBank"
//				, "MTLife Hip Broker"
//				, "MTLife POM 2nd Get"
//				, "MTLife POM PA"
//				, "MTLife WIN"
//				, "MSIG UOB"				
//				, "MSIG Happy Life BL"
//				, "FWD_TVD_Endowment 15_7"
//				"POM_PA_Cash_Back",
				"MSIG UOB"
		};
		String[] fileNames = new String[]{
				"DailyPerformanceTrackingReport"
				, "SalesReportByRecords"
				, "TsrTrackingReport"
				, "Daily_Performance_Tracking_ByLot"
				, "QC_Reconfirm"
				, "Sales_Report_By_Records"
				, "TSRTracking"
		};
		
		String origPath = "D:/Test/upload/Daily reprot from zip/report of " + monthYear + "/";
		String targetPath = "D:/Test/upload/kpi/" + monthYear + "/";
		
		String folderDate = "";
		
		File origDir = new File(origPath);
		
		for(File dir : origDir.listFiles()) {
			String fd = dir.getName();
			if(fd.startsWith(monthYear)) {
				folderDate = fd;
			} else {
				folderDate = fd.substring(4, fd.length()) + fd.substring(2,4) + fd.substring(0, 2);
			}
			
			for(File campaignFolder : dir.listFiles(new FolderFilter(targetCampaigns))) {
				String cf = campaignFolder.getName();
				if(cf.contains("POM_PA_Cash_Back")) {
					cf = cf.substring(0, cf.length() - 11);
				} else if(cf.contains("MTLife")) {
					cf = cf.substring(9, cf.length());
				} else if(cf.contains("_MSIG ")) {
					cf = cf.substring(9, cf.length());
				} else {
					cf = cf.substring(0, cf.length() - 9);
				}
				for(File xls : campaignFolder.listFiles(new XlsFilter(fileNames))) {
					File targetFile = new File(targetPath + cf + "/" + folderDate + "/");
					if(!targetFile.exists()) {
						targetFile.mkdirs();
					}
					try {
						File destFile = new File(targetFile.getAbsolutePath() + "/" + xls.getName());
						if(!destFile.exists()) {
							destFile.createNewFile();
//							System.out.println("moving file: " + xls.getAbsolutePath());
//							System.out.println("to " + destFile.getAbsolutePath());
							FileUtils.getInstance().copyFile(xls, destFile);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("FINISH");
	}
	
	private static class FolderFilter implements FileFilter  {

		private String[] folderNames;
		
		public FolderFilter(String... folderNames) {
			this.folderNames = folderNames;
		}
		
		@Override
		public boolean accept(File file) {
			if(file.isDirectory()) {
				for(String folder : folderNames) {
					if(file.getName().contains(folder)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	private static class XlsFilter implements FileFilter {

		private String[] fileNames;
		
		public XlsFilter(String...fileNames) {
			this.fileNames = fileNames;
		}
		
		@Override
		public boolean accept(File file) {
			if(file.isFile()) {
				for(String fileName : fileNames) {
					if(file.getName().toLowerCase().contains(fileName.toLowerCase()) && !file.getName().toLowerCase().contains("P'Sunan".toLowerCase())) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
