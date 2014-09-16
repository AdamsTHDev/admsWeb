package com.adms.web.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * <p><b> File Utilities </b></p>
 * <li>Download file</li>
 * @author patawee.cha
 *
 */
public class FileUtils {
	
	private static FileUtils instatnce;
	
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	
	protected FileUtils() {
		
	}
	
	/**
	 * To get instance
	 * @return <code>new FileUtils()</code>
	 */
	public static FileUtils getInstance() {
		if(instatnce == null) {
			instatnce = new FileUtils();
		}
		return instatnce;
	}
	
	/**
	 * 
	 * @param fileName The file name with extension.
	 * @param contentType The content type of file.
	 * @param content The content by byte[].
	 * @throws IOException throws exception while do writing file out.
	 */
	public void downloadFile(String fileName, String contentType, byte[] content) throws IOException {
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext ec = facesContext.getExternalContext();

		ec.responseReset();
		ec.setResponseContentType(contentType);
		ec.setResponseHeader("Content-Disposition", "attachment); filename=\"" + fileName + "\"");
		
		OutputStream outputStream = null;
		InputStream in = null;
		try {
			// Open response output stream
			outputStream = ec.getResponseOutputStream();
			
			// Read contents
			in = new ByteArrayInputStream(content);
			
			// Read contents and write to output
			byte[] byteBuff = new byte[DEFAULT_BUFFER_SIZE];
			int byteRead;
			while ((byteRead = in.read(byteBuff)) > 0	) {
				outputStream.write(byteBuff, 0, byteRead);
			}
			
			facesContext.responseComplete();
		} catch (IOException e) {
			throw e;
		} finally {
			try { outputStream.flush(); } catch (IOException e) {}
			try { in.close(); } catch (IOException e) {}
			try { outputStream.close(); } catch (IOException e) {}
		}
	}

	/**
	 * 
	 * @param fileName The file name with extension.
	 * @param contentType The content type of file.
	 * @param file The file.
	 * @throws IOException throws exception while do writing file out.
	 */
	public void downloadFile(String fileName, String contentType, File file) throws IOException {
		byte[] bs = new byte[(int) file.length()];
		InputStream in = null;
		try {
			// convert file into byte[]
			in = new FileInputStream(file);
			in.read(bs);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				in.close();
			} catch(IOException e) {}
		}
		
		downloadFile(fileName, contentType, bs);
	}
}
