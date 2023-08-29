package com.common.global;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.util.Utilities;

public class SMSSender {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String[] values = {"Nama NK","RJ14-CC-5746"};
//		System.out.println(MyValueFirstSend("carReport",values,"919001798489"));
	}
    public static String MyValueFirstSend(String smsName, String[] sVariable,String recipient) {
    	Utilities ut = new Utilities();
    	String requestUrl = "",sReturnedValue="", message = "";
        String username = ut.getProp("SMS_USERNAME"), password = ut.getProp("SMS_PASSWORD"), senderid=ut.getProp("SMS_SENDERID");
        if(smsName.equals("BookingConfirmation")){
        	message = bookingConfirmation(sVariable);
        }else if(smsName.equals("carDetailSMS")){
        	message = carDetail(sVariable);
        }else if(smsName.equals("carReport")){
        	message = carReporting(sVariable);
        }
        try {
            requestUrl  ="http://bulkpush.mytoday.com/BulkSms/SingleMsgApi?feedid=332530"         +
                            "&username=" + URLEncoder.encode(username, "UTF-8")  +
                            "&password=" + URLEncoder.encode(password, "UTF-8") + 
                            "&to=" + URLEncoder.encode(recipient, "UTF-8")      + 
                            "&messagetype=SMS:TEXT"                             +
                            "&text=" + URLEncoder.encode(message, "UTF-8")      +
                            "&from=" + URLEncoder.encode(senderid, "UTF-8")   +";";
             URL url = new URL(requestUrl);
             System.out.println(requestUrl);
             HttpURLConnection uc = (HttpURLConnection)url.openConnection();
             sReturnedValue  =uc.getResponseMessage();
             uc.disconnect();
        } catch (Exception ex) {
            sReturnedValue = ex.getMessage();
        }
        return sReturnedValue;
    }

    /* String[] sVariable where:  index 0 : Name	 1 : BookingNo		2 : Date	 3 : Time	 4 : CarType */
    private static String bookingConfirmation(String[] sVariable){
    	return "Dear "+sVariable[0]
		+ ",\nYour Booking Details:-"
		+ "\nBkg No- "+sVariable[1]
		+ "\nDate - "+sVariable[2]
		+ "\nTime - "+sVariable[3]
		+ "\nCar Type : "+sVariable[4]
		+ "\nHelpline-9818999999"
		+ "\nThank you for choosing Viceroy.";
    }

    /* String[] sVariable where:  index 0 : Name	 1 : Car No.		2 : Chauffeur Name	 3 : Chauffeur Mobile */
    private static String carDetail(String[] sVariable){
    	return "Dear "+sVariable[0]
				+ ",\nYour Travel Details:-"
				+ "\nCar No- "+sVariable[1]
				+ "\nChauffeur- "+sVariable[2]
				+ "\nMobile No- "+sVariable[3]
				+ "\nHelpline- 9818999999"
				+ "\nThanks, Team Viceroy.";
    }

    /* String[] sVariable where:  index 0 : Name	 1 : Car No.		2 : Chauffeur Name	 3 : Chauffeur Mobile */
    private static String carReporting(String[] sVariable){
    	return "Dear "+sVariable[0]
				+ ",\nYour Car No- "+sVariable[1]
				+ "has reached the pickup address"
				+ "\nHave a Nice Journey!!!";
    }
    /* String[] sVariable where:  index 0 : Name 1: Otp */
    private static String sendOtp(String[] sVariable){
    	return "Dear " +sVariable[0]
				+ ",\nForgot password require an OTP "+sVariable[1] + "."
				+ "\nHelpline- 9818999999"
				+ "\nThanks, Team Viceroy.";
    }
}
