package com.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test test = new Test();
		String  requestUrl = "http://psms.parthsolutions.net/sendsms.jsp";
		String sSMS = "Dear Customer, Your A/c XXXX0763 has been debited with Rs.3000.00 on 07/10/2017 Ref. CASH PAID TO DHEERAJ SHARMA. Now your A/c balance is Rs. 1491.10 Unclear funds Rs. 0.00";
		 try {
			 String sResponse = test.ParthSmsSend("chamba","demo1234","CHAMBA", "9001798489", sSMS); 
			 System.out.println(sResponse);
			 try{
				 System.out.println(test.getTagValue(sResponse, "messageid"));
			 }catch(Exception e){
		        System.out.println(test.getTagValue(sResponse, "error-code"));
		        System.out.println(test.getTagValue(sResponse, "error-description"));
			 }
		 } catch (Exception e) {
	            e.printStackTrace();
        }
	}

    public String getTagValue(String xml, String tagName){
        return xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
    }

    public static String ParthSmsSend(String username,String password,String originator,String recipient,String message) {
        String requestUrl = "",sReturnedValue="";
        try {
            requestUrl  =   "http://psms.parthsolutions.net/sendsms.jsp?" +
                            "user=" + URLEncoder.encode(username, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") + 
                            "&mobiles=" + URLEncoder.encode(recipient, "UTF-8") + 
                            "&sms=" + URLEncoder.encode(message, "UTF-8")  +
                            "&unicode=0" +
                            "&senderid=" + URLEncoder.encode(originator, "UTF-8") ;  
             System.out.println(requestUrl);
             URL url = new URL(requestUrl);
             URLConnection con = url.openConnection();
             InputStream in = con.getInputStream();
             String encoding = con.getContentEncoding();
             encoding = encoding == null ? "UTF-8" : encoding;
             sReturnedValue = IOUtils.toString(in, encoding);
        } catch (Exception ex) {
            sReturnedValue = ex.getMessage();
        }
        return sReturnedValue;
    }

}
