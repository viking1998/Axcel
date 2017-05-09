package com.spring.hibernate.axcel.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.spring.hibernate.axcel.models.MyCell;

@Component
public class FileService {
	
	private String fileLocation;
	private String fileName;
	
	public String getFileName() {
		return fileName;
	}

	public String getSaveLocation() {
		return fileLocation;
	}

	public void setSaveLocation(String saveLocation) {
		this.fileLocation = saveLocation;
	}

	public void saveFileLocally(MultipartFile fileToSave) throws IOException {
		fileName = fileToSave.getOriginalFilename();
		String currDirAbsPath = new File(".").getAbsolutePath();
		String saveDirAbsPath = currDirAbsPath.substring(0, currDirAbsPath.length()-1) 
								+ "\\src\\main\\resources\\uploads\\";
		fileLocation = saveDirAbsPath + fileName;
		InputStream in = fileToSave.getInputStream();
	    FileOutputStream f = new FileOutputStream(fileLocation);
	    int ch = 0;
	    while ((ch = in.read()) != -1) {
	        f.write(ch);
	    }
	    f.flush();
	    f.close();
	}
	
	public void saveChanges() {
		
	}
	
	public void prepareFileForDownload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		File downloadFile = new File(fileLocation);
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
	
	public HashMap<Integer, List<MyCell>> readExcelFile() throws IOException, InvalidFormatException {
		if(fileName.endsWith(".xlsx")) {
			HashMap<Integer, List<MyCell>> mySheet = new HashMap<>();
			
			FileInputStream fInStream = new FileInputStream(new File(fileLocation));
			XSSFWorkbook workbook = new XSSFWorkbook(fInStream);
			
			Sheet sheet = workbook.getSheetAt(0);
			
			Iterator<Row> rowIterator = sheet.iterator();
			int currentRow = 0;
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				mySheet.put(currentRow, new ArrayList<MyCell>());
				while(cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					MyCell myCell = new MyCell();
					
					XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
					XSSFColor bgColor = cellStyle.getFillBackgroundColorColor();
					if(bgColor != null) {
						byte[] rgbColor = bgColor.getRGB();
						myCell.setBgColor("rgb(" 
											+ (rgbColor[0] < 0 ? (rgbColor[0]+0xff) : rgbColor[0]) + ","
											+ (rgbColor[1] < 0 ? (rgbColor[1]+0xff) : rgbColor[1]) + ","
											+ (rgbColor[2] < 0 ? (rgbColor[2]+0xff) : rgbColor[2]) + ")");
					}
					XSSFFont font = cellStyle.getFont();
					myCell.setTextSize(font.getFontHeightInPoints()+"");
					if(font.getBold()) {
						myCell.setTextWeight("bold");
					}
					else {
						myCell.setTextWeight("normal");
					}
					
					if(font.getItalic()) {
						myCell.setFontStyle("italic");
					}
					else {
						myCell.setFontStyle("normal");
					}
					
					 if(font.getUnderline() == Font.U_SINGLE) {
						myCell.setTextDecoration("underline");
					}
					 else {
						 myCell.setTextDecoration("none");
					 }
					
					switch(cell.getCellTypeEnum()) {
						case BOOLEAN: 
							myCell.setContent(cell.getBooleanCellValue()+"");
							break;
						case STRING:
							myCell.setContent(cell.getRichStringCellValue().getString());
							break;
						case NUMERIC:
							if(DateUtil.isCellDateFormatted(cell)) {
								myCell.setContent(cell.getDateCellValue()+"");
							}
							else {
								myCell.setContent(cell.getNumericCellValue()+"");
							}
							break;
						case FORMULA:
							myCell.setContent(cell.getCellFormula());
							break;
						default: 
							myCell.setContent("");
							break;
					}
					mySheet.get(currentRow).add(myCell);
				}
				currentRow++;
			}
			fInStream.close();
			workbook.close();
			return mySheet;
		}
		return null;
	}

}
