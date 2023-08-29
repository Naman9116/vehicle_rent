package com;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class HtmlToPdf {
	private static final Logger logger = Logger.getLogger(HtmlToPdf.class);

	public static void main(String[] args) {
	    try {
		    Document document = new Document();
		    OutputStream file = new FileOutputStream(new File("E:\\MemberReceipt.pdf"));
		    BufferedReader br = new BufferedReader(new FileReader("E:\\MemberReceipt.htm"));
		    String content="",str;
	        while ((str = br.readLine()) != null) {
	            content +=str;
	        }
		    System.out.println(content);
		    br.close();
			
		    PdfWriter writer = PdfWriter.getInstance(document, file);
		    document.open();
		    InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
		    XMLWorkerHelper.getInstance().parseXHtml( writer, document, is);
		    document.close();
		    file.close();
		} catch (Exception e) {
		    logger.error("",e);
		}finally{

		}
	}
}
