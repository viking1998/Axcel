package com.spring.hibernate.axcel.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.hibernate.axcel.services.FileService;

@Controller
@EnableAutoConfiguration
public class FileController {

	@Autowired
	private FileService fileService;
	
	@PostMapping("/upload")
	public String upload(MultipartFile file, RedirectAttributes redirAttr) throws IOException, InvalidFormatException {
		if(!file.isEmpty()) {
			fileService.saveFileLocally(file);
			redirAttr.addFlashAttribute("excelSheet", fileService.readExcelFile());
		    redirAttr.addFlashAttribute("message", "File: " + file.getOriginalFilename() 
		      + " has been uploaded successfully!");
		}
		else {
			redirAttr.addFlashAttribute("message", "Please choose an excel file to upload!");
		}
		return "redirect:/home";
	}
	
	@GetMapping("/download")
	public String download(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		fileService.saveChanges();
		fileService.prepareFileForDownload(req, resp);
		return "redirect:/home";
	}
	
}
