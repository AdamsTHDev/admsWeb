package com.adms.batch.job;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MoveFileManually {

	public static void main(String[] args) {
		System.out.println("START");
//		POM_PA_CASH_BACK, Health Return Cash, HIP_DDOP, KBANK DDOP -PA Cash Back, KBANK DDOP -POM PA Cash Back, MTI-KBank
		String targetCampaign = "MTI-KBank";
		
		String origPath = "D:/Test/upload/report of 201407/";
		String targetPath = "D:/Test/upload/kpi/";
		
		String folderDate = "";
		
		File origDir = new File(origPath);
		
		
		for(File dir : origDir.listFiles()) {
			String fd = dir.getName();
			folderDate = fd.substring(4, fd.length()) + fd.substring(2,4) + fd.substring(0, 2);
			for(File campaignFolder : dir.listFiles(new FolderFilter(targetCampaign))) {

				for(File xls : campaignFolder.listFiles(new XlsFilter("Daily_Performance_Tracking_ByLot.xls", "QC_Reconfirm.xls", "Sales_Report_By_Records.xls", "TSRTracking.xls"))) {
					File targetFile = new File(targetPath + targetCampaign + "/" + folderDate + "/");
					if(!targetFile.exists()) {
						targetFile.mkdirs();
					}
					try {
						File destFile = new File(targetFile.getAbsolutePath() + "/" + xls.getName());
						if(!destFile.exists()) {
							destFile.createNewFile();
							copyFile(xls, destFile);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("FINISH");
	}
	
	private static void copyFile(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buff = new byte[1024];
			int read;
			while((read = is.read(buff)) > 0) {
				os.write(buff, 0, read);
			}
		} catch(Exception e) {
			throw e;
		} finally {
			os.close();
			is.close();
		}
	}
	
	private static class FolderFilter implements FileFilter  {

		private String folderName;
		
		public FolderFilter(String folderName) {
			this.folderName = folderName;
		}
		
		@Override
		public boolean accept(File file) {
			if(file.isDirectory()) {
				if(file.getName().toLowerCase().contains(folderName.toLowerCase())) {
					return true;
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
					if(file.getName().toLowerCase().contains(fileName.toLowerCase())) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
