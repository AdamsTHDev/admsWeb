package com.adms.batch.app;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.adms.web.utils.FileUtils;

public class MoveFileManually {

	public static void main(String[] args) {
		System.out.println("START");
		
		String monthYear = "201502";
		
//		String[] targetCampaigns = new String[]{
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
//				"MSIG UOB"
//		};
		
		String[] fileNames = new String[]{
				"SalesReportByRecord"
				, "TsrTrackingReport"
				, "QC_Reconfirm"
				, "Sales_Report_By_Record"
				, "TSRTracking"
		};
		
		String origPath = "T:/Business Solution/Share/N_Mos/Daily Sales Report/" + monthYear + "/";
		String targetPath = "D:/Test/upload/kpi/" + monthYear + "/";
		
		String folderDate = "";
		
		File origDir = new File(origPath);
		
		for(File root : origDir.listFiles()) {
//			OTO & TELE
			System.out.println("root: " + root.getName());
			for(File cPath : root.listFiles()) {

				System.out.println("cPath: " + cPath.getName());
				if(cPath.isFile()) {
					continue;
				}
				for(File inPath : cPath.listFiles(new FolderFilter())) {
					System.out.println("inPath: " + inPath.getName());
					if(inPath.isFile() || inPath.getName().equals("zipfiles")) {
						continue;
					}
					folderDate = inPath.getName();
					String cfn = inPath.getName();
					
					try {
						if(folderDate.contains(monthYear)) {
							folderDate = folderDate.substring(folderDate.indexOf(monthYear), folderDate.indexOf(monthYear) + 8);
						} else if(folderDate.contains("POM_PA_Cash_Back")) {
							folderDate = folderDate.substring(17, folderDate.length());
							folderDate = folderDate.substring(6, folderDate.length()) + folderDate.substring(3, 5) + folderDate.substring(0, 2);;
						} else {
							String rmy = monthYear.substring(4, monthYear.length()) + monthYear.substring(0, 4);
							folderDate = folderDate.substring(folderDate.indexOf(rmy) - 2, folderDate.indexOf(rmy) + 6);
						}
					} catch(Exception e) {
						e.printStackTrace();
						continue;
					}
					
					if(cfn.contains("POM_PA_Cash_Back")) {
						cfn = cfn.substring(0, cfn.length() - 11);
					} else if(cfn.contains("MTLife")) {
						cfn = cfn.substring(9, cfn.length());
					} else if(cfn.contains("_MSIG ")) {
						cfn = cfn.substring(9, cfn.length());
					} else {
						cfn = cfn.substring(0, cfn.length() - 9);
					}
					
					if(StringUtils.isNotBlank(cfn)) {
						System.out.println("cfn: " + cfn);
						for(File xls : inPath.listFiles(new XlsFilter(fileNames))) {
							File target = new File(targetPath + cfn.trim() + "/" + folderDate);
							if(!target.exists()) {
								target.mkdirs();
							}
							
							try {
								File file = new File(target.getAbsolutePath() + "/" + xls.getName());
								System.out.println("file: " + file.getAbsolutePath());
								if(!file.exists()) {
									file.createNewFile();
								}
								FileUtils.getInstance().copyFile(xls, file);
							} catch(IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		/*
		 * below this is old process
		 */
		
//		for(File dir : origDir.listFiles()) {
//			String fd = dir.getName();
//			if(fd.startsWith(monthYear)) {
//				folderDate = fd;
//			} else {
//				folderDate = fd.substring(4, fd.length()) + fd.substring(2,4) + fd.substring(0, 2);
//			}
//			
//			for(File campaignFolder : dir.listFiles(new FolderFilter(targetCampaigns))) {
//				String cf = campaignFolder.getName();
//				if(cf.contains("POM_PA_Cash_Back")) {
//					cf = cf.substring(0, cf.length() - 11);
//				} else if(cf.contains("MTLife")) {
//					cf = cf.substring(9, cf.length());
//				} else if(cf.contains("_MSIG ")) {
//					cf = cf.substring(9, cf.length());
//				} else {
//					cf = cf.substring(0, cf.length() - 9);
//				}
//				for(File xls : campaignFolder.listFiles(new XlsFilter(fileNames))) {
//					File targetFile = new File(targetPath + cf + "/" + folderDate + "/");
//					if(!targetFile.exists()) {
//						targetFile.mkdirs();
//					}
//					try {
//						File destFile = new File(targetFile.getAbsolutePath() + "/" + xls.getName());
//						if(!destFile.exists()) {
//							destFile.createNewFile();
////							System.out.println("moving file: " + xls.getAbsolutePath());
////							System.out.println("to " + destFile.getAbsolutePath());
//							FileUtils.getInstance().copyFile(xls, destFile);
//						}
//					} catch(Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
		System.out.println("FINISH");
	}
	
	private static class FolderFilter implements FileFilter  {

		@Override
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
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
