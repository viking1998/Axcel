package com.spring.hibernate.axcel.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileService {
	
	private String saveLocation;
	
	public String getSaveLocation() {
		return saveLocation;
	}

	public void setSaveLocation(String saveLocation) {
		this.saveLocation = saveLocation;
	}

	public void saveFileLocally(MultipartFile fileToSave) throws IOException {
		InputStream in = fileToSave.getInputStream();
	    String absolutePath = new File(saveLocation).getAbsolutePath();
	    FileOutputStream f = new FileOutputStream(absolutePath);
	    int ch = 0;
	    while ((ch = in.read()) != -1) {
	        f.write(ch);
	    }
	    f.flush();
	    f.close();
	}
	
	public void prepareFileForDownload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		File downloadFile = new File(ServletContextListener.class.getClassLoader().getResource("uploads/bla.xlsx").getPath());
		String mimeType = URLConnection.guessContentTypeFromName(downloadFile.getName());
		if(mimeType == null) {
			mimeType = "application/octet-stream";
		}
		resp.setContentType(mimeType);
		resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", 
															downloadFile.getName()));
		resp.setContentLength((int) downloadFile.length());
		
		InputStream inStream = new BufferedInputStream(new FileInputStream(downloadFile));
		FileCopyUtils.copy(inStream, resp.getOutputStream());
	}

}
