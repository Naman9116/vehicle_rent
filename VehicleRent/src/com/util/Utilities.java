package com.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.billing.model.CoverLetterModel;
import com.billing.model.InvoiceDetailModel;
import com.billing.model.InvoiceModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.master.model.CorporateTaxDetModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.DutySlipModel;
import com.toursandtravels.model.BookingModel;
import com.user.model.UserModel;

public class Utilities {
	private static final Logger logger = Logger.getLogger(Utilities.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();

	private static Utilities ut = new Utilities();
	public static void main(String[] args) {
//		sentEmail("nknama@artifactsolutions.com", "Test Mail 587", "TestMail", "");
	}
	public String userAccessString(HttpSession session, HttpServletRequest request, boolean bQueryString){
		String sAccess = "";
		HashMap userPageAccess = new HashMap();
		Object objUsers = session.getAttribute("userPageAccess")==null?null:session.getAttribute("userPageAccess");
		if((objUsers!=null) && (objUsers instanceof HashMap)){
			userPageAccess= (HashMap)objUsers;
		}
		String contextPath = (String) request.getContextPath();
		String requestURI = (String) request.getAttribute("javax.servlet.forward.request_uri");
		requestURI = requestURI.replace(contextPath + "/","");

		if(bQueryString) requestURI += "?" + request.getQueryString();

		try{
			requestURI = URLEncoder.encode(requestURI,"UTF-8");
			sAccess = userPageAccess.get(requestURI) == null?"":userPageAccess.get(requestURI).toString();
		}catch(Exception e){}
		return sAccess;
	}
	public String  generatePdfForBooking(BookingDetailModel bookingDetailModel, HttpServletRequest request, String realPath,UserModel userModel){
		String bookingHtmlTemplate = null;
		try {
			Document document = new Document();
			new File(realPath+"/pdf").mkdirs();
			File pdfFile = new File(realPath+"/pdf/Booking"+bookingDetailModel.getBookingMasterModel().getBookingNo()+".pdf");
			logger.info("pdf file path :"+realPath+"/pdf/Booking"+bookingDetailModel.getBookingMasterModel().getBookingNo()+".pdf");
			// if file doesn't exists, then create it
			// if file doesn't exists, then create it
			if (!pdfFile.exists()) {
				pdfFile.createNewFile();
/*				pdfFile.setExecutable(true,false);
				pdfFile.setWritable(true,false);
				pdfFile.setReadable(true,false);
	            logger.info("set the everybody execute permission: "+ pdfFile.canExecute());
	            logger.info("set the everybody write permission: "+ pdfFile.canWrite());
	            logger.info("set the everybody read permission: "+ pdfFile.canRead());
*/			}
			OutputStream file = new FileOutputStream(pdfFile);

			//BufferedReader br = new BufferedReader(new FileReader("E:\\BookingConfirmE-mail.html"));
			bookingHtmlTemplate = IOUtils.toString(new FileReader(realPath+"/html_Formats/BookingConfirmE-mail.html"));
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##BookingID##", bookingDetailModel.getBookingMasterModel().getBookingNo());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##LogoImage##","http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/img/VCR_Logo.png"); 
			SimpleDateFormat ipFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat opFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
			Date ipPickUpDateTime = null;
			String opPickUpDateTime = null;
			try {
				ipPickUpDateTime = ipFormat.parse(new SimpleDateFormat("yyyy-MM-dd").format(bookingDetailModel.getPickUpDate()) +" "+bookingDetailModel.getPickUpTime());
				opPickUpDateTime = opFormat.format(ipPickUpDateTime);
			}catch(Exception e){
				logger.error("",e);
			}
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##PickUpDateTime##", opPickUpDateTime+ " Hrs");
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##GuestName##", bookingDetailModel.getBookingMasterModel().getBookedForName());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##ContactNumber##", bookingDetailModel.getBookingMasterModel().getMobileNo());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##CorporateName##", bookingDetailModel.getBookingMasterModel().getCorporateId().getName());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##BookedBy##", bookingDetailModel.getBookingMasterModel().getBookedBy().getName());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##ModeOfPayment##", bookingDetailModel.getMob().getName());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##VehicleType##", bookingDetailModel.getCarModel().getName());
			if(bookingDetailModel.getCarType()!=null)
				bookingHtmlTemplate = bookingHtmlTemplate.replace("##VehicleCategory##", bookingDetailModel.getCarType().getName());
			else
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##VehicleCategory##", "");
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##PickUpCity##", bookingDetailModel.getOutlet().getName());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##PickUpPoint##", bookingDetailModel.getReportingAddress());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##DropCity##", bookingDetailModel.getToBeRealeseAt());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##RentalType##", bookingDetailModel.getRentalType().getName());
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##SpecialInstruction##", bookingDetailModel.getInstruction());
			String firstName =" ", lastName = " ";
			if(userModel.getUserFirstName() != null || userModel.getUserFirstName() != ""){
				firstName = userModel.getUserFirstName();
			}
			if(userModel.getUserLastName() != null || userModel.getUserLastName() != ""){
				lastName = userModel.getUserLastName();
			}
			bookingHtmlTemplate = bookingHtmlTemplate.replace("##ReservationsTeam##", firstName == "" && lastName == "" ?"ReservationsTeam":(firstName+" "+lastName));
			/*String content="",str;
	        while ((str = br.readLine()) != null) {
	            content +=str;
	        }
		    System.out.println(content);
		    br.close();*/

			PdfWriter writer = PdfWriter.getInstance(document, file);
			document.open();
			//InputStream is = new ByteArrayInputStream(content.getBytes());
			InputStream is = new ByteArrayInputStream(bookingHtmlTemplate.getBytes());
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
			document.close();
			file.close();
		}catch (Exception e) {
			logger.error("",e);
		}finally{		
		}
		return bookingHtmlTemplate;
	}
	public String generateDutySlip(BookingDetailModel bookingDetailModel, HttpServletRequest request,
			String realPath){
		String dutyHtmlTemplate = null;
		String noData="";
		try{
			dutyHtmlTemplate = IOUtils.toString(new FileReader(realPath+"/html_Formats/DutySlip.html"));
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##Company##", bookingDetailModel.getCompany().getName());

			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##BranchOffice##", bookingDetailModel.getAddress().getAddress1());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##PinCode##", bookingDetailModel.getAddress().getPincode().toString());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##State##", bookingDetailModel.getAddress().getState().getName());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##CompanyMobileNo##", bookingDetailModel.getRelatedInfo().getHelpLine());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##Email##", bookingDetailModel.getRelatedInfo().getEmail());
			SimpleDateFormat ipBookingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat opBookingFormat = new SimpleDateFormat("dd MMM yyyy");
			Date ipBookingDateOut = null;
			String opBookingDateOut = null;
			try {
				ipBookingDateOut = ipBookingFormat.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bookingDetailModel.getDutySlipModel().getDispatchDateTime()));

				opBookingDateOut=opBookingFormat.format(ipBookingDateOut);
			}catch(Exception e){
				logger.error("",e);
			}
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##BookingDate##",opBookingDateOut);  
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##DutySlipNo##",bookingDetailModel.getDutySlipModel().getDutySlipNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##BookingNo##",bookingDetailModel.getBookingMasterModel().getBookingNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##ReportingAddress##",bookingDetailModel.getReportingAddress());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##Drop##",bookingDetailModel.getToBeRealeseAt());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##Corporate##",bookingDetailModel.getBookingMasterModel().getCorporateId().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##UserName##",bookingDetailModel.getBookingMasterModel().getBookedForName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##MobileNo##",bookingDetailModel.getBookingMasterModel().getMobileNo() == null ? "" :bookingDetailModel.getBookingMasterModel().getMobileNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##BookedBy##",bookingDetailModel.getBookingMasterModel().getBookedBy().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##RentalType##",bookingDetailModel.getRentalType().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##FlightNo##",bookingDetailModel.getFlightNo()==null?"":bookingDetailModel.getFlightNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##Terminal##",bookingDetailModel.getTerminal()==null ?"":bookingDetailModel.getTerminal().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##Mob##",bookingDetailModel.getMob() == null ? "" : bookingDetailModel.getMob().getName());

			SimpleDateFormat ipFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat opFormat = new SimpleDateFormat("dd MMM yyyy");
			SimpleDateFormat opTime = new SimpleDateFormat("HH:mm");
			Date ipDateOut = null;
			String opDateOut = null;
			String opTimeOut=null;
			if(bookingDetailModel.getDutySlipModel().getDispatchDateTime()!=null)
			{
				try {
					ipDateOut = ipFormat.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bookingDetailModel.getDutySlipModel().getDispatchDateTime()));

					opDateOut = opFormat.format(ipDateOut);
					opTimeOut = opTime.format(ipDateOut);
				}catch(Exception e){
					logger.error("",e);
				}

				dutyHtmlTemplate = dutyHtmlTemplate.replace("##TimeOut##",bookingDetailModel.getStartTime() == null ? "" : bookingDetailModel.getStartTime());
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##DateOut##",opDateOut);
			}
			else{
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##TimeOut##","");
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##DateOut##","");
			}
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##KmOut##",bookingDetailModel.getDutySlipModel().getOpenKms().toString());

			Long lTotalKms = bookingDetailModel.getDutySlipModel().getCloseKms() - bookingDetailModel.getDutySlipModel().getOpenKms();
			String sTotalKms="";
			if(lTotalKms > 0) sTotalKms = String.valueOf(lTotalKms);
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TotalKms##",sTotalKms);

			dutyHtmlTemplate = dutyHtmlTemplate.replace("##ReportingTime##",bookingDetailModel.getPickUpTime());
			if(bookingDetailModel.getDutySlipModel().getIsUpdatedChauffeur()!=null){
				if(bookingDetailModel.getDutySlipModel().getIsUpdatedChauffeur().equals("Y")){ 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",bookingDetailModel.getDutySlipModel().getChauffeurName());
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",bookingDetailModel.getDutySlipModel().getChauffeurMobile()); 
				}else{
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",""); 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",""); 
				}			
			}else{
				if(bookingDetailModel.getDutySlipModel().getChauffeurModel() != null){
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",bookingDetailModel.getDutySlipModel().getChauffeurModel().getName()); 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",bookingDetailModel.getDutySlipModel().getChauffeurModel().getMobileNo());
				}else{
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",""); 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",""); 
				}
			}
/*			if(bookingDetailModel.getDutySlipModel().getIsUpdatedChauffeur()!=null && bookingDetailModel.getDutySlipModel().getIsUpdatedChauffeur().equals("Y")){ 
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",bookingDetailModel.getDutySlipModel().getChauffeurName());
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",bookingDetailModel.getDutySlipModel().getChauffeurMobile()); 
			}else if(bookingDetailModel.getDutySlipModel().getChauffeurModel()!= null){
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",bookingDetailModel.getDutySlipModel().getChauffeurModel().getName()); 
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",bookingDetailModel.getDutySlipModel().getChauffeurModel().getMobileNo());
			}else{
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",""); 
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",""); 
			}
*/		
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##CarBooked##",bookingDetailModel.getCarModel().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##CarAllocated##",bookingDetailModel.getDutySlipModel().getCarDetailModel().getModel().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##CarNo##",bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##SplInstruction##",bookingDetailModel.getInstruction() == null ? "" : bookingDetailModel.getInstruction());

			if(bookingDetailModel.getDutySlipModel().getTollTaxAmount()!=null && bookingDetailModel.getDutySlipModel().getTollTaxAmount() > 0){
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ParkingToll##",bookingDetailModel.getDutySlipModel().getTollTaxAmount().toString());
			}else{
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ParkingToll##","");
			}

			if(bookingDetailModel.getDutySlipModel().getStateTax()!=null && bookingDetailModel.getDutySlipModel().getStateTax() > 0){
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##InterstateTaxes##",bookingDetailModel.getDutySlipModel().getStateTax().toString());
			}else{
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##InterstateTaxes##","");
			}
			//NO Data
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##DateIn##",bookingDetailModel.getDutySlipModel().getDateTo() == null ? "" : opFormat.format(bookingDetailModel.getDutySlipModel().getDateTo()));
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TimeIn##",bookingDetailModel.getDutySlipModel().getTimeTo() == null ? "" : bookingDetailModel.getDutySlipModel().getTimeTo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TotalDays##",bookingDetailModel.getDutySlipModel().getTotalDay() == null ? "" : String.valueOf(bookingDetailModel.getDutySlipModel().getTotalDay()));
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TotaHrs##",bookingDetailModel.getDutySlipModel().getTotalHrs() == null ? "": bookingDetailModel.getDutySlipModel().getTotalHrs());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##KmIn##",bookingDetailModel.getDutySlipModel().getCloseKms() == null || bookingDetailModel.getDutySlipModel().getCloseKms() == 0 ? "" : bookingDetailModel.getDutySlipModel().getCloseKms().toString());
		}
		catch (Exception e) {
			logger.error("",e);
		}finally{		
		}
		return dutyHtmlTemplate;
	}
	public HashMap<String, Object> generateInvoice(BookingDetailModel bookingDetailModel){
		String traiffName = "";
		String extraKmUsedString="",extraHrsUsedString="";
		HashMap<String, Object> JSONRESPONSE = new HashMap<String, Object>();

		InvoiceModel invoiceModel = new InvoiceModel();
		try{
			/*	StyleChange*/
			DecimalFormat hh = new DecimalFormat("#");
			DecimalFormat tariff = new DecimalFormat("0.00");
			/*Invoice Heading Info*/
			String sCompany = bookingDetailModel.getCompany().getName();
			String sBranchOffice = bookingDetailModel.getBranch().getName() + " - " + bookingDetailModel.getAddress().getAddress1() + " " + bookingDetailModel.getAddress().getAddress2() + "-" +bookingDetailModel.getAddress().getPincode() + ", " + bookingDetailModel.getAddress().getState().getName() + " (India)";
			String sPinCode = bookingDetailModel.getAddress().getPincode().toString();
			String sState = bookingDetailModel.getAddress().getState().getName();
			String sCompanyMobileNo = bookingDetailModel.getRelatedInfo().getHelpLine();
			String sEmail = bookingDetailModel.getRelatedInfo().getEmail();
			String sWebsite = bookingDetailModel.getRelatedInfo().getWebSite();
			String sCorporateName = bookingDetailModel.getBookingMasterModel().getCorporateId().getName();
			String sCorporateAddress = bookingDetailModel.getRelatedInfo().getEntityAddress().getAddress1() + " " + bookingDetailModel.getRelatedInfo().getEntityAddress().getAddress2() + "-" + bookingDetailModel.getRelatedInfo().getEntityAddress().getPincode() + ", " + bookingDetailModel.getRelatedInfo().getEntityAddress().getState().getName() + " (India)";
			String sClientAddress = bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress1()+" "+bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress2();
			
			if(bookingDetailModel.getCorporateTariffDetModels().get(0).getAddressDetailModel() != null){
				sClientAddress = bookingDetailModel.getCorporateTariffDetModels().get(0).getAddressDetailModel().getAddress1()+" " + bookingDetailModel.getCorporateTariffDetModels().get(0).getAddressDetailModel().getAddress2();			
			}else{
				sClientAddress = bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress1()+" "+bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress2();			
			}
			String sClientName = bookingDetailModel.getBookingMasterModel().getBookedForName();
			String sInvoiceNo = bookingDetailModel.getDutySlipModel().getInvoiceNo();
			String sBookedBy = bookingDetailModel.getBookingMasterModel().getBookedBy().getName();
			String sGSTINCorp = bookingDetailModel.getCorporateTariffDetModels() != null ? bookingDetailModel.getCorporateTariffDetModels().get(0).getGstin() : "";
			String sDutySlipNo = bookingDetailModel.getDutySlipModel().getDutySlipNo();
			String sCarBooked = bookingDetailModel.getCarModel().getName();
			String sCarAlocated = bookingDetailModel.getDutySlipModel().getCarDetailModel().getModel().getName();
			String sCarNo = bookingDetailModel.getDutySlipModel().getCarDetailModel().getRegistrationNo();
					
			double outStationAllowence=0,nightAllowence=0,miscCharges=0,stateTax=0;
			double total=0, totalWithoutParking = 0,extraKmUsed=0,totalExtraKmUsed =0,extraHrsUsed = 0,totalExtraHrsUsed = 0;
			
			double dNightCharge = bookingDetailModel.getDutySlipModel().getNightAllowanceRate() == null ? 0 : bookingDetailModel.getDutySlipModel().getNightAllowanceRate();
			double dDayCharges = bookingDetailModel.getDutySlipModel().getOutStationAllowRate() == null ? 0 : bookingDetailModel.getDutySlipModel().getOutStationAllowRate();
			double dBasicFare = bookingDetailModel.getDutySlipModel().getBasicFare() == null ? 0.00 : bookingDetailModel.getDutySlipModel().getBasicFare();
			
			Long iTotalDays = bookingDetailModel.getDutySlipModel().getTotalDay() != null ? bookingDetailModel.getDutySlipModel().getTotalDay() : 1;

			if(bookingDetailModel.getBookingMasterModel().getCorporateId().getsTaxCalPark().equals("Y")){
				invoiceModel.setIsParkTaxable("Y");
			}else{
				invoiceModel.setIsParkTaxable("N");
			}
			invoiceModel.setCompName(sCompany);
			invoiceModel.setBrOffice(sBranchOffice);
			invoiceModel.setCompPIN(Integer.parseInt(sPinCode));
			invoiceModel.setCompState(sState);
			invoiceModel.setHeadCont(sCompanyMobileNo);
			invoiceModel.setHeadEmail(sEmail);
			invoiceModel.setHeadWebsite(sWebsite);
			invoiceModel.setCorpId(bookingDetailModel.getBookingMasterModel().getCorporateId().getId().longValue());
			invoiceModel.setCorpName(sCorporateName);
			invoiceModel.setHeadOffice(sCorporateAddress);
			invoiceModel.setCorpAddr(sClientAddress);
			invoiceModel.setUsedBy(sClientName);
			invoiceModel.setInvoiceNo(sInvoiceNo);
			invoiceModel.setInvoiceDt(bookingDetailModel.getDutySlipModel().getInvoiceDate());
			invoiceModel.setBookedById(bookingDetailModel.getBookingMasterModel().getBookedBy().getId().longValue());
			invoiceModel.setBookedBy(sBookedBy);
			invoiceModel.setRefNo(bookingDetailModel.getBookingMasterModel().getReferenceNo());
			invoiceModel.setCorpGSTIN(sGSTINCorp);
			invoiceModel.setDsNo(sDutySlipNo);
			invoiceModel.setVehBooked(sCarBooked);
			invoiceModel.setVehUsed(sCarAlocated);
			invoiceModel.setVehNo(sCarNo);
			invoiceModel.setNightAllow(dNightCharge);
			invoiceModel.setDayAllow(dDayCharges);
			invoiceModel.setBasicFair(dBasicFare);
			/*Mode of Payment*/
			invoiceModel.setPaymentMode(bookingDetailModel.getMob() != null ? bookingDetailModel.getMob().getName() : "");
			/*Dispatched DateTime*/
			if(bookingDetailModel.getDutySlipModel().getDispatchDateTime() != null){
				try {
				}catch(Exception e){
					logger.error("",e);
				}
			}else{
			}
			invoiceModel.setVehUsedDt(bookingDetailModel.getDutySlipModel().getDispatchDateTime());
			/*Replace*/
			double finalTotal = 0.0, finalTotalWithoutParking  = 0.00;
			/*Extra Hours Charges*/
			String sExtraChgHour="";
			if(bookingDetailModel.getDutySlipModel().getExtraChgHrs() == null ){ 
				sExtraChgHour="0";
			}else{
				sExtraChgHour= tariff.format(bookingDetailModel.getDutySlipModel().getExtraChgHrs());
			}
			invoiceModel.setExtraHrRate(Double.parseDouble(sExtraChgHour));
			
			/*Extra KM Charges*/
			String sExtraChgKM="";
			if(bookingDetailModel.getDutySlipModel().getExtraChgKms() == null || bookingDetailModel.getDutySlipModel().getExtraChgKms().toString().trim().length() == 0) {
				sExtraChgKM = "0";
				bookingDetailModel.getDutySlipModel().setExtraChgKms(0.00);
			}else{
				sExtraChgKM = tariff.format(bookingDetailModel.getDutySlipModel().getExtraChgKms());
			}
			invoiceModel.setOpeningKm(bookingDetailModel.getDutySlipModel().getOpenKms());
			invoiceModel.setClosingKm(bookingDetailModel.getDutySlipModel().getCloseKms());
			invoiceModel.setExtraKmRate(Double.parseDouble(sExtraChgKM));
			
			traiffName = bookingDetailModel.getDutySlipModel().getTariff() == null ?"":bookingDetailModel.getDutySlipModel().getTariff().getName();

			if(traiffName.equalsIgnoreCase("Outstation")){
				dBasicFare = dBasicFare * iTotalDays;
			}
			dBasicFare = Math.round(dBasicFare);
			invoiceModel.setTotalBasicFair(dBasicFare);
			
			String Totalhrs="",interSateTax="";
			
			double dMinChg = bookingDetailModel.getDutySlipModel().getMinChgKms() == null || bookingDetailModel.getDutySlipModel().getMinChgKms().toString().equals("") || bookingDetailModel.getDutySlipModel().getMinChgKms().toString().length() == 0 ? 0:bookingDetailModel.getDutySlipModel().getMinChgKms();
			double dtotalKms = bookingDetailModel.getDutySlipModel().getTotalKms() == null || bookingDetailModel.getDutySlipModel().getTotalKms().toString().equals("") || bookingDetailModel.getDutySlipModel().getTotalKms().toString().length() == 0 ? 0:bookingDetailModel.getDutySlipModel().getTotalKms();
			if(dtotalKms > dMinChg){
				extraKmUsed=dtotalKms - dMinChg;
				extraKmUsedString= hh.format(extraKmUsed);
			}
			else{
				extraKmUsedString= "0";
			}
			double getTotalHour=0;
			if(bookingDetailModel.getDutySlipModel().getTotalHrs() ==null || bookingDetailModel.getDutySlipModel().getTotalHrs().toString().length() == 0 || bookingDetailModel.getDutySlipModel().getTotalHrs().toString().equals("")) 
				bookingDetailModel.getDutySlipModel().setTotalHrs("0");
			
			String totalhrs = bookingDetailModel.getDutySlipModel().getTotalHrs();
			String[] ttlhrs = totalhrs.split(":");
			long lTlhrs = 0l;
			if(ttlhrs.length == 2) lTlhrs = Long.parseLong(ttlhrs[1]);

			getTotalHour=Double.parseDouble(ttlhrs[0]);
			String sMinHrsChg = bookingDetailModel.getDutySlipModel().getMinChgHrs() == null ? "0.00": String.valueOf(bookingDetailModel.getDutySlipModel().getMinChgHrs());
			
			if(getTotalHour >= Double.parseDouble(sMinHrsChg)){
				extraHrsUsed = getTotalHour - Double.parseDouble(sMinHrsChg);
				if(lTlhrs > 0) 
					extraHrsUsedString=  hh.format(extraHrsUsed) + ":" + ttlhrs[1];
				else
					extraHrsUsedString=  hh.format(extraHrsUsed) + ":" + "00";
			}
			else{
				extraHrsUsedString= "0:00";
			}
			if(bookingDetailModel.getDutySlipModel().getExtraChgKms() == null) bookingDetailModel.getDutySlipModel().setExtraChgKms(0.00);
			totalExtraKmUsed=extraKmUsed * bookingDetailModel.getDutySlipModel().getExtraChgKms();
			String [] extraHrsArr = extraHrsUsedString.split(":"); 
			int coficent =Integer.parseInt(extraHrsArr[1]) / 15;
			coficent = coficent * 25;
			String extraChgHr = extraHrsArr[0] + "." + coficent;
			totalExtraHrsUsed=Double.parseDouble(extraChgHr) * (bookingDetailModel.getDutySlipModel().getExtraChgHrs() == null ? 0:bookingDetailModel.getDutySlipModel().getExtraChgHrs());
			DecimalFormat sr = new DecimalFormat("0.00");
			
			totalExtraKmUsed = Math.round(totalExtraKmUsed);
			totalExtraHrsUsed = Math.round(totalExtraHrsUsed);
			
			invoiceModel.setExtraKmTotal(totalExtraKmUsed);
			
			invoiceModel.setStartTime(bookingDetailModel.getDutySlipModel().getTimeFrom());
			invoiceModel.setCloseTime(bookingDetailModel.getDutySlipModel().getTimeTo());
			
			invoiceModel.setExtraHrTotal(totalExtraHrsUsed);
			invoiceModel.setExtraKm(extraKmUsedString);
			invoiceModel.setExtraHr(extraHrsUsedString);
			
			invoiceModel.setRentalTypeId(bookingDetailModel.getRentalType().getId().longValue());
			invoiceModel.setRentalType(bookingDetailModel.getRentalType().getName());

			if(bookingDetailModel.getDutySlipModel().getParkingAmount() == null)  bookingDetailModel.getDutySlipModel().setParkingAmount(0.00);
			if(bookingDetailModel.getDutySlipModel().getTollTaxAmount() == null)   bookingDetailModel.getDutySlipModel().setTollTaxAmount(0.00);
			double parking = bookingDetailModel.getDutySlipModel().getParkingAmount();
			double toll =  bookingDetailModel.getDutySlipModel().getTollTaxAmount();
			if(bookingDetailModel.getDutySlipModel().getNightAllow() == null ) bookingDetailModel.getDutySlipModel().setNightAllow(0.00);
			double nightAllowenceVARIABLE= Math.round(bookingDetailModel.getDutySlipModel().getNightAllow() == null ? 0.00 : bookingDetailModel.getDutySlipModel().getNightAllow());
			double dayAllowenceVARIABLE= Math.round(bookingDetailModel.getDutySlipModel().getDayAllow() == null ? 0.00 :bookingDetailModel.getDutySlipModel().getDayAllow());
			
			if(bookingDetailModel.getDutySlipModel().getMiscCharge() == null) bookingDetailModel.getDutySlipModel().setMiscCharge(0.00);
			if(bookingDetailModel.getDutySlipModel().getFuelCharge() == null) bookingDetailModel.getDutySlipModel().setFuelCharge(0.00);
			if(bookingDetailModel.getDutySlipModel().getGuideCharge()== null) bookingDetailModel.getDutySlipModel().setGuideCharge(0.00);
			double miscChargesVariable=Math.round(((bookingDetailModel.getDutySlipModel().getMiscCharge() != null ? bookingDetailModel.getDutySlipModel().getMiscCharge() : 0) + 
										(bookingDetailModel.getDutySlipModel().getFuelCharge() != null ? bookingDetailModel.getDutySlipModel().getFuelCharge() : 0) + 
										(bookingDetailModel.getDutySlipModel().getGuideCharge()!= null ? bookingDetailModel.getDutySlipModel().getGuideCharge(): 0)));

			invoiceModel.setDays(bookingDetailModel.getDutySlipModel().getTotalDay() == null ? 1 : bookingDetailModel.getDutySlipModel().getTotalDay());
			invoiceModel.setDayAllowTotal(dayAllowenceVARIABLE);
			
			invoiceModel.setNightAllowTotal(nightAllowenceVARIABLE);
			invoiceModel.setNight(Integer.parseInt(bookingDetailModel.getDutySlipModel().getTotalNight() == null || bookingDetailModel.getDutySlipModel().getTotalNight().equals("") ? "0" : bookingDetailModel.getDutySlipModel().getTotalNight()));
			
			invoiceModel.setParking(parking);
			invoiceModel.setToll(toll);
			invoiceModel.setMiscAllowTotal(miscChargesVariable);
			
			if(bookingDetailModel.getDutySlipModel().getTotalKms() == null ) bookingDetailModel.getDutySlipModel().setTotalKms(0.00);
			invoiceModel.setKmUsed(bookingDetailModel.getDutySlipModel().getTotalKms());
			invoiceModel.setTariffId(bookingDetailModel.getDutySlipModel().getTariff() == null ?0:bookingDetailModel.getDutySlipModel().getTariff().getId().longValue());
			invoiceModel.setTariffName(bookingDetailModel.getDutySlipModel().getTariff() == null ?"":bookingDetailModel.getDutySlipModel().getTariff().getName());
			
			if(bookingDetailModel.getDutySlipModel().getTotalHrs()!=null) 	Totalhrs = bookingDetailModel.getDutySlipModel().getTotalHrs();
			if(bookingDetailModel.getDutySlipModel().getStateTax() ==null) bookingDetailModel.getDutySlipModel().setStateTax(0.00);
				double statetax=bookingDetailModel.getDutySlipModel().getStateTax();
				interSateTax = sr.format(statetax);
				
			invoiceModel.setHrsUsed(Totalhrs);
			invoiceModel.setStateTax(statetax);
			
			String vehicleUsedAt="";
			if(bookingDetailModel.getRentalType().getName().contains("Outstation")){
				vehicleUsedAt = bookingDetailModel.getToBeRealeseAt();
			}else{
				vehicleUsedAt = bookingDetailModel.getOutlet().getName();
			}
			invoiceModel.setVehUsedAt(vehicleUsedAt);
			invoiceModel.setCompId(bookingDetailModel.getCompany().getId().longValue());
			invoiceModel.setBranchId(bookingDetailModel.getBranch().getId().longValue());
			invoiceModel.setHubId(bookingDetailModel.getOutlet().getId().longValue());
			invoiceModel.setBrName(bookingDetailModel.getBranch().getName());
			invoiceModel.setHubName(bookingDetailModel.getOutlet().getName());
			/*	 Addition*/
			nightAllowence=bookingDetailModel.getDutySlipModel().getNightAllow() == null ? 0.00 : bookingDetailModel.getDutySlipModel().getNightAllow();
			outStationAllowence=bookingDetailModel.getDutySlipModel().getDayAllow() == null ? 0.00 : bookingDetailModel.getDutySlipModel().getDayAllow();
			
			if(bookingDetailModel.getDutySlipModel().getMiscCharge() == null) bookingDetailModel.getDutySlipModel().setMiscCharge(0.00);
			if(bookingDetailModel.getDutySlipModel().getFuelCharge() == null) bookingDetailModel.getDutySlipModel().setFuelCharge(0.00);
			if(bookingDetailModel.getDutySlipModel().getGuideCharge() == null) bookingDetailModel.getDutySlipModel().setGuideCharge(0.00);
			miscCharges=((bookingDetailModel.getDutySlipModel().getMiscCharge())+(bookingDetailModel.getDutySlipModel().getFuelCharge())+(bookingDetailModel.getDutySlipModel().getGuideCharge()));
			stateTax = Double.parseDouble(interSateTax);
			total=dBasicFare+totalExtraHrsUsed+totalExtraKmUsed+outStationAllowence+nightAllowence+miscCharges+parking+stateTax+toll;
			total = Math.round(total);
			finalTotal = finalTotal + total;

			totalWithoutParking = dBasicFare+totalExtraHrsUsed+totalExtraKmUsed+outStationAllowence+nightAllowence+miscCharges;
			totalWithoutParking = Math.round(totalWithoutParking);
			finalTotalWithoutParking = finalTotalWithoutParking + totalWithoutParking;
			
		//	totalTariff= hr.format(Math.round(total));
		//	totalTariff=Double.toString( total);
			invoiceModel.setTotalAmt(total);
			/*	Taxes*/ 
			String tax = "tax";
			String taxName = "taxName";
			String ssTax =null; 
			String ssTaxName = null;
			String ssTaxVal = null;
			double finalCalculatedTax = 0.00;
			String sTaxHeading="",sTaxValue="";
			if(bookingDetailModel.getDutySlipModel().getTaxName() !=null && !bookingDetailModel.getDutySlipModel().getTaxName().isEmpty()){
				String[] sTaxName = (bookingDetailModel.getDutySlipModel().getTaxName()).split(",");
				String[] sTaxPer = (bookingDetailModel.getDutySlipModel().getTaxPercentage()).split(",");
				String[] sTaxVal = (bookingDetailModel.getDutySlipModel().getTaxValues()).split(",");
				String addLess,sign = "";
				for(int i = 0;  i<sTaxName.length; i++){
					 ssTax = tax.concat(String.valueOf(i));
					 ssTaxName = taxName.concat(String.valueOf(i));	
					 ssTax = sTaxPer[i];
					 ssTaxName = sTaxName[i];
					 ssTaxVal = sTaxVal[i];
					/*Calculate Percent	*/ 
					double calculatedTax = 0.00;
//					String sRoundOff = bookingDetailModel.getBookingMasterModel().getCorporateId().getRoundedOff()==null?"N":bookingDetailModel.getBookingMasterModel().getCorporateId().getRoundedOff();
//					calculatedTax = (Double.parseDouble(ssTax)/100)*finalTotalWithoutParking;
					if(bookingDetailModel.getBookingMasterModel().getCorporateId().getsTaxCalPark().equals("Y")){
						calculatedTax = Math.round((Double.parseDouble(ssTax)/100) * finalTotal);
					}else{
						calculatedTax = Math.round((Double.parseDouble(ssTax)/100) * totalWithoutParking);
					}
//					if(sRoundOff.equals("Y")) calculatedTax = Math.round(calculatedTax);
					if(ssTaxName.contains("Discount")){
						if(calculatedTax > 0 && Double.parseDouble(ssTaxVal) > 0){
							calculatedTax = calculatedTax > Double.parseDouble(ssTaxVal) ? Double.parseDouble(ssTaxVal): calculatedTax ;
						}else{
							calculatedTax = calculatedTax > 0 ? calculatedTax : Double.parseDouble(ssTaxVal);
						}	
						finalCalculatedTax = finalCalculatedTax - calculatedTax;
						addLess = "Less";sign="( - )";	
					}else{
						calculatedTax = calculatedTax > Double.parseDouble(ssTaxVal) ? calculatedTax : Double.parseDouble(ssTaxVal);
						finalCalculatedTax += calculatedTax;
						addLess = "Add";sign="";
					}
					if(calculatedTax > 0){
						sTaxHeading += addLess+": "+ssTaxName+"@"+ssTax+"% ##";
						sTaxValue   += sign+" " + sr.format(calculatedTax) + " ##" ;
					}
				}
			}
			invoiceModel.setTaxHeading(sTaxHeading);
			invoiceModel.setTaxValues(sTaxValue);
			
			double grandTotal=finalTotal + finalCalculatedTax;
			float finalGrandTotal = (float)Math.round(grandTotal);
			invoiceModel.setGrandTotal(Double.parseDouble(String.valueOf(finalGrandTotal)));
			
			invoiceModel.setBrGSTIN(bookingDetailModel.getRelatedInfo().getGstinBr() != null ?bookingDetailModel.getRelatedInfo().getGstinBr():"");
			invoiceModel.setBrPAN(bookingDetailModel.getRelatedInfo().getPanNo());
			/*Bank Details */
			invoiceModel.setBankName(bookingDetailModel.getRelatedInfo().getBankName() != null ?bookingDetailModel.getRelatedInfo().getBankName():"");
			invoiceModel.setBankAccNo(bookingDetailModel.getRelatedInfo().getBankAccNo() != null ?bookingDetailModel.getRelatedInfo().getBankAccNo():"");
			invoiceModel.setBankIFSC(bookingDetailModel.getRelatedInfo().getBankIFSC() != null ?bookingDetailModel.getRelatedInfo().getBankIFSC():"");
			invoiceModel.setRemark(bookingDetailModel.getDutySlipModel().getRemarks());
			invoiceModel.setInvoiceType("S");
			invoiceModel.setDsDt(bookingDetailModel.getDutySlipModel().getDateFrom());
			JSONRESPONSE.put("InvoiceModel", invoiceModel);
		}
		catch (Exception e) {
			logger.error("",e);
		}finally{		
		}
		return JSONRESPONSE;
	}
	public HashMap<String, Object> generateMultipleInvoice(BookingDetailModel bookingDetailModel,List<DutySlipModel> dutySlipModellist){
		String extraKmUsedString="",extraHrsUsedString="";
		double basicFare=0,extraHours=0,outStationAllowence=0,nightAllowence=0,nightCharge=0,dayCharges=0,miscCharges=0,stateTax=0;
		double total=0, totalWithoutParking = 0,totalExtraKmUsed =0,extraHrsUsed = 0,totalExtraHrsUsed = 0;

		HashMap<String, Object> JSONRESPONSE = new HashMap<String, Object>();
		InvoiceModel invoiceModel = new InvoiceModel();
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat hh = new DecimalFormat("#");

		try{
			invoiceModel.setCompName(bookingDetailModel.getCompany().getName());
			invoiceModel.setBrOffice(bookingDetailModel.getBranch().getName() +" - "+  bookingDetailModel.getAddress().getAddress1()+" "+bookingDetailModel.getAddress().getAddress2() + "-" +bookingDetailModel.getAddress().getPincode() + ", " + bookingDetailModel.getAddress().getState().getName() + " (India)");
			invoiceModel.setCompPIN(Integer.parseInt(bookingDetailModel.getAddress().getPincode().toString()));
			invoiceModel.setCompState(bookingDetailModel.getAddress().getState().getName());
			invoiceModel.setHeadCont(bookingDetailModel.getRelatedInfo().getHelpLine());
			invoiceModel.setHeadEmail(bookingDetailModel.getRelatedInfo().getEmail());
			invoiceModel.setHeadWebsite(bookingDetailModel.getRelatedInfo().getWebSite());
			invoiceModel.setCorpId(bookingDetailModel.getBookingMasterModel().getCorporateId().getId().longValue());
			invoiceModel.setCorpName(bookingDetailModel.getBookingMasterModel().getCorporateId().getName());
			invoiceModel.setHeadOffice(bookingDetailModel.getRelatedInfo().getEntityAddress().getAddress1()+" "+bookingDetailModel.getRelatedInfo().getEntityAddress().getAddress2() + "-" +bookingDetailModel.getRelatedInfo().getEntityAddress().getPincode() + ", " + bookingDetailModel.getRelatedInfo().getEntityAddress().getState().getName() + " (India)");

			if(bookingDetailModel.getCorporateTariffDetModels().get(0).getAddressDetailModel() != null){
				invoiceModel.setCorpAddr(bookingDetailModel.getCorporateTariffDetModels().get(0).getAddressDetailModel().getAddress1()+" " + bookingDetailModel.getCorporateTariffDetModels().get(0).getAddressDetailModel().getAddress2());
			}else{
				invoiceModel.setCorpAddr(bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress1()+" "+bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress2());
			}
			invoiceModel.setInvoiceNo(dutySlipModellist.get(0).getInvoiceNo());
			invoiceModel.setInvoiceDt(dutySlipModellist.get(0).getInvoiceDate());
			invoiceModel.setCorpGSTIN(bookingDetailModel.getCorporateTariffDetModels().get(0).getGstin() != null ? bookingDetailModel.getCorporateTariffDetModels().get(0).getGstin() : "");
			invoiceModel.setPaymentMode(bookingDetailModel.getMob() != null ? bookingDetailModel.getMob().getName() : "");

			if(bookingDetailModel.getBookingMasterModel().getCorporateId().getsTaxCalPark().equals("Y")){
				invoiceModel.setIsParkTaxable("Y");
			}else{
				invoiceModel.setIsParkTaxable("N");
			}
			/*Replace*/
			double finalTotal = 0.0, finalTotalWithoutParking  = 0.00,  tariffSchemVariable=0.00;
			/* for( i=1;i<=dutySlipModellist.size();i++ )*/
			List<InvoiceDetailModel> invoiceDetailModelList = new ArrayList<InvoiceDetailModel>();
			
			int iLoop = 0;
			double finalCalculatedTax = 0.00, finalCalculatedDiscount = 0.00;

			List<String> sTaxLine = new ArrayList<String>();
			List<String> sSign = new ArrayList<String>();
			List<Long> lTaxValue  = new ArrayList<Long>();

			String[] sTaxName = null;
			String[] sTaxPer = null;
			String[] sTaxVal = null;
			
			for(DutySlipModel dutySlipModel : bookingDetailModel.getDutyslip()) {
				/*	StyleChange*/
				InvoiceDetailModel invoiceDetailModel = new InvoiceDetailModel();
				invoiceDetailModel.setInvoiceModel(invoiceModel);
				String traiffName = "";
				DecimalFormat tariff = new DecimalFormat("0.00");
				/* StyleChange*/
				invoiceDetailModel.setVehUsedDt(dutySlipModel.getDispatchDateTime());
				if(iLoop == 0) invoiceModel.setVehUsedDt(dutySlipModel.getDispatchDateTime());

				invoiceDetailModel.setVehNo(dutySlipModel.getCarDetailModel().getRegistrationNo());
				if(iLoop == 0) invoiceModel.setVehNo(dutySlipModel.getCarDetailModel().getRegistrationNo());
				invoiceDetailModel.setDsNo(dutySlipModel.getDutySlipNo());
				invoiceDetailModel.setDsDt(dutySlipModel.getDateFrom());
				if(iLoop == 0){
					invoiceModel.setDsNo(dutySlipModel.getDutySlipNo());
					invoiceModel.setDsDt(dutySlipModel.getDateFrom());
				}
				invoiceDetailModel.setVehBooked(dutySlipModel.getBookingDetailModel().getCarModel().getName());
				if(iLoop == 0) invoiceModel.setVehBooked(dutySlipModel.getBookingDetailModel().getCarModel().getName());
				invoiceDetailModel.setVehUsed(dutySlipModel.getCarDetailModel().getModel().getName());
				if(iLoop == 0) invoiceModel.setVehUsed(dutySlipModel.getCarDetailModel().getModel().getName());
				
				nightCharge = dutySlipModel.getNightAllowanceRate() == null ? 0 : dutySlipModel.getNightAllowanceRate();
				invoiceDetailModel.setNightAllow(nightCharge);
				if(iLoop == 0) invoiceModel.setNightAllow(nightCharge);
					
				dayCharges = bookingDetailModel.getDutySlipModel().getOutStationAllowRate() == null ? 0 : bookingDetailModel.getDutySlipModel().getOutStationAllowRate();
				invoiceDetailModel.setDayAllow(dayCharges);
				if(iLoop == 0) invoiceModel.setDayAllow(dayCharges);
				
				String extraChangeHour="";
				if(dutySlipModel.getExtraChgHrs() == null){ 
					 extraChangeHour="0";
				}
				else
				{
					extraChangeHour=tariff.format(dutySlipModel.getExtraChgHrs());
				}
				extraHours=Double.parseDouble(extraChangeHour);
				invoiceDetailModel.setExtraHrRate(extraHours);
				if(iLoop == 0) invoiceModel.setExtraHrRate(extraHours);
				
				if(dutySlipModel.getExtraChgKms() == null || dutySlipModel.getExtraChgKms().toString().equals(" ") || dutySlipModel.getExtraChgKms().toString().length() == 0) {
					dutySlipModel.setExtraChgKms(0.00);
				}
				invoiceDetailModel.setExtraKmRate(dutySlipModel.getExtraChgKms());
				if(iLoop == 0) invoiceModel.setExtraKmRate(dutySlipModel.getExtraChgKms());
				
				tariffSchemVariable=dutySlipModel.getBasicFare();
				invoiceDetailModel.setBasicFair(tariffSchemVariable);	
				if(iLoop == 0) invoiceModel.setBasicFair(tariffSchemVariable);	
				
				basicFare = dutySlipModel.getBasicFare();
				
				traiffName = dutySlipModel.getTariff() == null ?"":dutySlipModel.getTariff().getName();
				basicFare =dutySlipModel.getBasicFare();
				if(traiffName.equalsIgnoreCase("Outstation")){
					basicFare = basicFare * Double.valueOf(dutySlipModel.getTotalDay() == null ? 1 : dutySlipModel.getTotalDay());
				}
				invoiceDetailModel.setTotalBasicFair(basicFare);
				invoiceModel.setTotalBasicFair(invoiceModel.getTotalBasicFair() + basicFare);
				
				String Totalhrs="",interSateTax="";
				double extraKmUsed = 0.00;
				double dMinChg = dutySlipModel.getMinChgKms() == null || dutySlipModel.getMinChgKms().toString().equals("") || dutySlipModel.getMinChgKms().toString().length() == 0 ? 0:dutySlipModel.getMinChgKms();
				double dtotalKms = dutySlipModel.getTotalKms() == null || dutySlipModel.getTotalKms().toString().equals("") || dutySlipModel.getTotalKms().toString().length() == 0 ? 0:dutySlipModel.getTotalKms();
				if(dtotalKms > dMinChg){
					extraKmUsed = dtotalKms - dMinChg;
					extraKmUsedString= hh.format(extraKmUsed);
				}
				else{
					extraKmUsedString= "0";
				}
				
				double getTotalHour=0;
				if(dutySlipModel.getTotalHrs() ==null || dutySlipModel.getTotalHrs().toString().length() == 0 || dutySlipModel.getTotalHrs().toString().equals("")) 
					dutySlipModel.setTotalHrs("0");
				
				String totalhrs = dutySlipModel.getTotalHrs();
				String[] ttlhrs = totalhrs.split(":");
				long lTlhrs = 0l;
				if(ttlhrs.length == 2) lTlhrs = Long.parseLong(ttlhrs[1]);
					
				getTotalHour=Double.parseDouble(ttlhrs[0]);
				
				invoiceDetailModel.setRentalTypeId(bookingDetailModel.getRentalType().getId().longValue());
				invoiceDetailModel.setRentalType(bookingDetailModel.getRentalType().getName());
				if(iLoop == 0){
					invoiceModel.setRentalTypeId(invoiceDetailModel.getRentalTypeId());
					invoiceModel.setRentalType(invoiceDetailModel.getRentalType());				
				}
				
				String sMinHrsChg = dutySlipModel.getMinChgHrs() == null ? "0.00": String.valueOf(dutySlipModel.getMinChgHrs());
				extraHrsUsedString= "0:00";
				if(getTotalHour >= Double.parseDouble(sMinHrsChg)){
					extraHrsUsed = getTotalHour - Double.parseDouble(sMinHrsChg);
					if(lTlhrs > 0) 
						extraHrsUsedString=  hh.format(extraHrsUsed) + ":" + ttlhrs[1];
					else
						extraHrsUsedString=  hh.format(extraHrsUsed) + ":" + "00";
				}
				else{
					extraHrsUsedString= "0:00";
				}
				if(dutySlipModel.getExtraChgKms() == null) dutySlipModel.setExtraChgKms(0.00);
				totalExtraKmUsed=extraKmUsed* dutySlipModel.getExtraChgKms();
				String [] extraHrsArr = extraHrsUsedString.split(":"); 
				int coficent =Integer.parseInt(extraHrsArr[1]) / 15;
				coficent = coficent * 25;
				String extraChgHr = extraHrsArr[0] + "." + coficent;
				totalExtraHrsUsed=Double.parseDouble(extraChgHr) * (dutySlipModel.getExtraChgHrs() == null ? 0 : dutySlipModel.getExtraChgHrs());
				DecimalFormat sr = new DecimalFormat("0.00");
				invoiceDetailModel.setExtraKmTotal(totalExtraKmUsed);
				invoiceModel.setExtraKmTotal(invoiceModel.getExtraKmTotal() + totalExtraKmUsed);
				invoiceDetailModel.setExtraHrTotal(totalExtraHrsUsed);
				invoiceModel.setExtraHrTotal(invoiceModel.getExtraHrTotal() + totalExtraHrsUsed);
				invoiceDetailModel.setExtraKm(extraKmUsedString);
				if(iLoop == 0) invoiceModel.setExtraKm(extraKmUsedString);
				invoiceDetailModel.setExtraHr(extraHrsUsedString);
				if(iLoop == 0) invoiceModel.setExtraHr(extraHrsUsedString);
				
				if(dutySlipModel.getParkingAmount() == null)  dutySlipModel.setParkingAmount(0.00);
				if(dutySlipModel.getTollTaxAmount() == null)   dutySlipModel.setTollTaxAmount(0.00);
				double parking=dutySlipModel.getParkingAmount();
				double toll = dutySlipModel.getTollTaxAmount();
				if(dutySlipModel.getNightAllow() == null ) dutySlipModel.setNightAllow(0.00);
				double nightAllowenceVARIABLE= dutySlipModel.getNightAllow() ==null ?0.00:dutySlipModel.getNightAllow();
				double dayAllowenceVARIABLE= dutySlipModel.getDayAllow() == null ? 0.00 : dutySlipModel.getDayAllow();
				
				if(dutySlipModel.getMiscCharge() == null) dutySlipModel.setMiscCharge(0.00);
				if(dutySlipModel.getFuelCharge() == null) dutySlipModel.setFuelCharge(0.00);
				if(dutySlipModel.getGuideCharge()== null) dutySlipModel.setGuideCharge(0.00);
				
				double miscChargesVariable=((dutySlipModel.getMiscCharge())+(dutySlipModel.getFuelCharge())+(dutySlipModel.getGuideCharge()));

				if(dutySlipModel.getOutStationAllowRate() == null) dutySlipModel.setOutStationAllowRate(0.00); 
				
				invoiceDetailModel.setDays(dutySlipModel.getTotalDay() == null ? 1 :dutySlipModel.getTotalDay());
				if(iLoop == 0) invoiceModel.setDays(dutySlipModel.getTotalDay() == null ? 1 : dutySlipModel.getTotalDay());
				invoiceDetailModel.setDayAllowTotal(dayAllowenceVARIABLE);
				invoiceModel.setDayAllowTotal(invoiceModel.getDayAllowTotal() + dayAllowenceVARIABLE);
				invoiceDetailModel.setNightAllowTotal(nightAllowenceVARIABLE);
				invoiceModel.setNightAllowTotal(invoiceModel.getNightAllowTotal() + nightAllowenceVARIABLE);
				invoiceDetailModel.setNight(Integer.parseInt( dutySlipModel.getTotalNight() == null || dutySlipModel.getTotalNight().equals("") ? "0" : dutySlipModel.getTotalNight()));
				if(iLoop == 0) invoiceModel.setNight(Integer.parseInt( dutySlipModel.getTotalNight() == null || dutySlipModel.getTotalNight().equals("") ? "0" : dutySlipModel.getTotalNight()));
				invoiceDetailModel.setParking(parking);
				invoiceModel.setParking(invoiceModel.getParking() + parking);
				invoiceDetailModel.setToll(toll);
				invoiceModel.setToll(invoiceModel.getToll() + toll);
				invoiceDetailModel.setMiscAllowTotal(miscChargesVariable);
				invoiceModel.setMiscAllowTotal(invoiceModel.getMiscAllowTotal() + miscChargesVariable);
				
				if(dutySlipModel.getTotalKms() == null ) dutySlipModel.setTotalKms(0.00);

				invoiceDetailModel.setKmUsed(dutySlipModel.getTotalKms());
				invoiceDetailModel.setOpeningKm(dutySlipModel.getOpenKms());
				invoiceDetailModel.setClosingKm(dutySlipModel.getCloseKms());
				if(iLoop == 0){
					invoiceModel.setKmUsed(invoiceDetailModel.getKmUsed());
					invoiceModel.setOpeningKm(invoiceDetailModel.getOpeningKm());
					invoiceModel.setClosingKm(invoiceDetailModel.getClosingKm());
				}
				
				invoiceDetailModel.setTariffId(dutySlipModel.getTariff().getId().longValue());
				invoiceDetailModel.setTariffName(dutySlipModel.getTariff().getName());
				if(iLoop == 0){
					invoiceModel.setTariffId(invoiceDetailModel.getTariffId());
					invoiceModel.setTariffName(invoiceDetailModel.getTariffName());
				}
				
				if(dutySlipModel.getTotalHrs()!=null) 	Totalhrs = dutySlipModel.getTotalHrs();
				if(dutySlipModel.getStateTax() ==null) dutySlipModel.setStateTax(0.00);
					double statetax=dutySlipModel.getStateTax();
					interSateTax = sr.format(statetax);

				invoiceDetailModel.setStartTime(dutySlipModel.getTimeFrom());
				invoiceDetailModel.setCloseTime(dutySlipModel.getTimeTo());
				invoiceDetailModel.setHrsUsed(Totalhrs);
				if(iLoop == 0){
					invoiceModel.setHrsUsed(Totalhrs);
					invoiceModel.setStartTime(invoiceDetailModel.getStartTime());
					invoiceModel.setCloseTime(invoiceDetailModel.getCloseTime());
				}
				invoiceDetailModel.setStateTax(statetax);
				invoiceModel.setStateTax(invoiceModel.getStateTax() + statetax);
				invoiceDetailModel.setRentalTypeId(dutySlipModel.getBookingDetailModel().getRentalType().getId().longValue());
				invoiceDetailModel.setRentalType(dutySlipModel.getBookingDetailModel().getRentalType().getName());

				if(iLoop == 0){
					invoiceModel.setRentalTypeId(invoiceDetailModel.getRentalTypeId());
					invoiceModel.setRentalType(invoiceDetailModel.getRentalType());
				}
				invoiceDetailModel.setBookedById(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedBy().getId().longValue());
				invoiceDetailModel.setBookedBy(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedBy().getName());
				invoiceDetailModel.setRefNo(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getReferenceNo());
				if(iLoop == 0){
					invoiceModel.setBookedById(invoiceDetailModel.getBookedById());
					invoiceModel.setBookedBy(invoiceDetailModel.getBookedBy());
					invoiceModel.setRefNo(invoiceDetailModel.getRefNo());
				}
				
				String vehicleUsedAt="";
				if(dutySlipModel.getBookingDetailModel().getRentalType().getName().contains("Outstation")){
					vehicleUsedAt = dutySlipModel.getBookingDetailModel().getToBeRealeseAt();
				}else{
					vehicleUsedAt = dutySlipModel.getBookingDetailModel().getOutlet().getName();
				}
				invoiceDetailModel.setVehUsedAt(vehicleUsedAt);
				invoiceDetailModel.setBranchId(dutySlipModel.getBookingDetailModel().getBranch().getId().longValue());
				invoiceDetailModel.setBrName(dutySlipModel.getBookingDetailModel().getBranch().getName());
				invoiceDetailModel.setHubId(dutySlipModel.getBookingDetailModel().getOutlet().getId().longValue());
				invoiceDetailModel.setHubName(dutySlipModel.getBookingDetailModel().getOutlet().getName());
				if(iLoop == 0){
					invoiceModel.setCompId(dutySlipModel.getBookingDetailModel().getCompany().getId().longValue());
					invoiceModel.setVehUsedAt(invoiceDetailModel.getVehUsedAt());
					invoiceModel.setBranchId(invoiceDetailModel.getBranchId());
					invoiceModel.setBrName(invoiceDetailModel.getBrName());
					invoiceModel.setHubId(invoiceDetailModel.getHubId());
					invoiceModel.setHubName(invoiceDetailModel.getHubName());
				}
				
				/*	 Addition*/
				nightAllowence=dutySlipModel.getNightAllow() == null ? 0.00 : dutySlipModel.getNightAllow();
				outStationAllowence=dutySlipModel.getDayAllow() == null ? 0.00 : dutySlipModel.getDayAllow();
				
				if(dutySlipModel.getMiscCharge() == null) dutySlipModel.setMiscCharge(0.00);
				if(dutySlipModel.getFuelCharge() == null) dutySlipModel.setFuelCharge(0.00);
				if(dutySlipModel.getGuideCharge() == null) dutySlipModel.setGuideCharge(0.00);
				miscCharges=((dutySlipModel.getMiscCharge())+(dutySlipModel.getFuelCharge())+(dutySlipModel.getGuideCharge()));
				stateTax = Double.parseDouble(interSateTax);
				total=basicFare+totalExtraHrsUsed+totalExtraKmUsed+outStationAllowence+nightAllowence+miscCharges+parking+stateTax+toll;
				total = Math.round(total);
				
				finalTotal = finalTotal + total;

				totalWithoutParking = basicFare+totalExtraHrsUsed+totalExtraKmUsed+outStationAllowence+nightAllowence+miscCharges;
				totalWithoutParking = Math.round(totalWithoutParking);
				finalTotalWithoutParking = finalTotalWithoutParking + totalWithoutParking;
				
				invoiceDetailModel.setTotalAmt(total);
				invoiceModel.setTotalAmt(invoiceModel.getTotalAmt() +  total);
				invoiceDetailModel.setUsedBy(dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName());
				if(iLoop == 0) invoiceModel.setUsedBy(invoiceDetailModel.getUsedBy());
				
				invoiceDetailModel.setRemark(dutySlipModel.getRemarks());
				if(iLoop == 0) invoiceModel.setRemark(invoiceDetailModel.getRemark());
				/*	Taxes*/ 
				String ssTax =null,ssTaxName = null,ssTaxVal = null,sTaxHeading="",sTaxValue="";
				Double dTaxDedAmt = 0.00;
				
				if(iLoop == 0 && dutySlipModel.getTaxName() != null && !dutySlipModel.getTaxName().isEmpty()){
					int iTaxCount = (dutySlipModel.getTaxName()).split(",").length;
					sTaxName = new String[iTaxCount];
					sTaxPer = new String[iTaxCount];
					sTaxVal = new String[iTaxCount];

					sTaxName = (dutySlipModel.getTaxName()).split(",");
					sTaxPer = (dutySlipModel.getTaxPercentage()).split(",");
					sTaxVal = (dutySlipModel.getTaxValues()).split(",");
				}	

				for(int i = 0;  i < sTaxName.length; i++){
					ssTax = sTaxPer[i];
					ssTaxName = sTaxName[i];
					ssTaxVal = sTaxVal[i];
					if(!ssTaxName.contains("Discount")) ssTaxVal = "0";
					String addLess,sign = "";
					if(ssTax.equals("0") && ssTaxVal.equals("0")) continue;
					/*Calculate Percent	*/ 
					double calculatedTax = 0.00;
//						String sRoundOff = bookingDetailModel.getBookingMasterModel().getCorporateId().getRoundedOff()==null?"N":bookingDetailModel.getBookingMasterModel().getCorporateId().getRoundedOff();
//						calculatedTax = (Double.parseDouble(ssTax)/100)*finalTotalWithoutParking;
					if(bookingDetailModel.getBookingMasterModel().getCorporateId().getsTaxCalPark().equals("Y")){
						calculatedTax = (Double.parseDouble(ssTax)/100) * total;
					}else{
						calculatedTax = (Double.parseDouble(ssTax)/100) * totalWithoutParking;
					}
//						if(sRoundOff.equals("Y")) calculatedTax = Math.round(calculatedTax);
					if(ssTaxName.contains("Discount")){
						if(calculatedTax > 0 && Double.parseDouble(ssTaxVal) > 0){
							calculatedTax = calculatedTax > Double.parseDouble(ssTaxVal) ? Double.parseDouble(ssTaxVal): calculatedTax ;
						}else{
							calculatedTax = calculatedTax > 0 ? calculatedTax : Double.parseDouble(ssTaxVal);
						}	
						calculatedTax = Math.round(calculatedTax);
						finalCalculatedDiscount += calculatedTax;
						finalCalculatedTax = finalCalculatedTax - calculatedTax;
						dTaxDedAmt = dTaxDedAmt - calculatedTax;
						addLess = "Less";sign="( - )";	
					}else{
						calculatedTax = calculatedTax > Double.parseDouble(ssTaxVal) ? calculatedTax : Double.parseDouble(ssTaxVal);
						finalCalculatedTax += calculatedTax;
						dTaxDedAmt = dTaxDedAmt + calculatedTax;
						addLess = "Add";sign="";
					}
					if(calculatedTax > 0){
						calculatedTax = (double) Math.round(calculatedTax * 100.0) / 100.0;
						
						sTaxHeading += addLess+": "+ssTaxName+"@"+ssTax+"% ##";
						sTaxValue   += sign+" " + df.format(calculatedTax) + " ##" ;
						
						String sTaxHeadLine = addLess +": " + ssTaxName + "@" + ssTax + "%";
						boolean bFound = false;
						for(int iCount = 0; iCount < sTaxLine.size(); iCount++){
							if(sTaxLine.get(iCount).equals(sTaxHeadLine)){
								long lSetTax = lTaxValue.get(iCount) + (long) calculatedTax;
								lTaxValue.set(iCount, lSetTax);
								sSign.set(iCount,sign);
								bFound = true;
							}
						}
						if(!bFound){
							sTaxLine.add(sTaxHeadLine);
							lTaxValue.add((long) calculatedTax);
							sSign.add(sign);
						}
					}
				}
				invoiceDetailModel.setTaxHeading(sTaxHeading);
				invoiceDetailModel.setTaxValues(sTaxValue);
				Double grandTotal = total + dTaxDedAmt;
				invoiceDetailModel.setGrandTotal(grandTotal);

				invoiceDetailModelList.add(invoiceDetailModel);
				iLoop++;
			}

			/*	Taxes*/ 
			String ssTax =null,ssTaxName = null,ssTaxVal = null,sTaxHeading="",sTaxValue="";
			finalCalculatedTax = 0;
			for(int i = 0;  i < sTaxName.length; i++){
				ssTax = sTaxPer[i];
				ssTaxName = sTaxName[i];
				ssTaxVal = "0";
				String addLess,sign = "";
				if(ssTax.equals("0") && ssTaxVal.equals("0")) continue;
				/*Calculate Percent	*/ 
				double calculatedTax = 0.00;
//					String sRoundOff = bookingDetailModel.getBookingMasterModel().getCorporateId().getRoundedOff()==null?"N":bookingDetailModel.getBookingMasterModel().getCorporateId().getRoundedOff();
//					calculatedTax = (Double.parseDouble(ssTax)/100)*finalTotalWithoutParking;
				if(bookingDetailModel.getBookingMasterModel().getCorporateId().getsTaxCalPark().equals("Y")){
					calculatedTax = Math.round((Double.parseDouble(ssTax)/100) * finalTotal);
				}else{
					calculatedTax = Math.round((Double.parseDouble(ssTax)/100) * finalTotalWithoutParking);
				}
//					if(sRoundOff.equals("Y")) calculatedTax = Math.round(calculatedTax);
				if(ssTaxName.contains("Discount")){
					calculatedTax = finalCalculatedDiscount;
					finalCalculatedTax -= Math.round(calculatedTax);
					addLess = "Less";sign="( - )";	
				}else{
					calculatedTax = calculatedTax > Double.parseDouble(ssTaxVal) ? calculatedTax : Double.parseDouble(ssTaxVal);
					finalCalculatedTax += Math.round(calculatedTax);
					addLess = "Add";sign="";
				}
				if(calculatedTax > 0){
					calculatedTax = Math.round(calculatedTax);
					sTaxHeading += addLess+": "+ssTaxName+"@"+ssTax+"% ##";
					sTaxValue   += sign+" " + df.format(calculatedTax) + " ##" ;
				}
			}
			invoiceModel.setTaxHeading(sTaxHeading);
			invoiceModel.setTaxValues(sTaxValue);

			double grandTotal=finalTotal + Math.round(finalCalculatedTax);
			float finalGrandTotal = (float)Math.round(grandTotal);
			invoiceModel.setGrandTotal(Double.parseDouble(String.valueOf(finalGrandTotal)));
			invoiceModel.setBrGSTIN(bookingDetailModel.getRelatedInfo().getGstinBr() != null ?bookingDetailModel.getRelatedInfo().getGstinBr():"");
			
			/*Bank Details */
			invoiceModel.setBankName(bookingDetailModel.getRelatedInfo().getBankName() != null ?bookingDetailModel.getRelatedInfo().getBankName():"");
			invoiceModel.setBankAccNo(bookingDetailModel.getRelatedInfo().getBankAccNo() != null ?bookingDetailModel.getRelatedInfo().getBankAccNo():"");
			invoiceModel.setBankIFSC(bookingDetailModel.getRelatedInfo().getBankIFSC() != null ?bookingDetailModel.getRelatedInfo().getBankIFSC():"");
			invoiceModel.setBrPAN(bookingDetailModel.getRelatedInfo().getPanNo());
			
			/* StyleChangeLowerPart*/
			invoiceModel.setInvoiceDetailModel(invoiceDetailModelList);
			invoiceModel.setInvoiceType("M");
			JSONRESPONSE.put("InvoiceModel", invoiceModel);
		}
		catch (Exception e) {
			logger.error("",e);
		}
		return JSONRESPONSE;
	}
	
	public String getInvoice(InvoiceModel invoiceModel,String realPath){
		String invHtmlTemp = null;
		try{
			invHtmlTemp = IOUtils.toString(new FileReader(realPath+"/html_Formats/printInvoice.html"));
			/*	StyleChange*/
			DecimalFormat hh = new DecimalFormat("#");
			DecimalFormat tariff = new DecimalFormat("0.00");
			SimpleDateFormat opFormat = new SimpleDateFormat("dd MMM yyyy");
			
			String sExtraHrs = invoiceModel.getExtraHr()== null ? "" : invoiceModel.getExtraHr();
			if(!sExtraHrs.equals("")){
				String[] sHrsPart = sExtraHrs.split("\\:");
				int second = Integer.parseInt(sHrsPart[1]);
				second = (second / 15) * 25;
				sExtraHrs = sHrsPart[0] + "." + String.valueOf(second);
			}
			
			invHtmlTemp = invHtmlTemp.replace("##Company##", invoiceModel.getCompName() == null ? "":invoiceModel.getCompName()); 
			invHtmlTemp = invHtmlTemp.replace("##BranchOffice##",invoiceModel.getBrOffice() == null ? "" : invoiceModel.getBrOffice());
			invHtmlTemp = invHtmlTemp.replace("##PinCode##",String.valueOf(invoiceModel.getCompPIN()) );
			invHtmlTemp = invHtmlTemp.replace("##State##",invoiceModel.getCompState() == null ? "" :invoiceModel.getCompState());
			invHtmlTemp = invHtmlTemp.replace("##CompanyMobileNo##", invoiceModel.getHeadCont() == null ? "" : invoiceModel.getHeadCont());
			invHtmlTemp = invHtmlTemp.replace("##Email##", invoiceModel.getHeadEmail() == null ? "" : invoiceModel.getHeadEmail());
			invHtmlTemp = invHtmlTemp.replace("##Website##", invoiceModel.getHeadWebsite() == null ? "" : invoiceModel.getHeadWebsite());
			invHtmlTemp = invHtmlTemp.replace("##CorporateName##", invoiceModel.getCorpName() == null ? "" : invoiceModel.getCorpName());
			invHtmlTemp = invHtmlTemp.replace("##CorporateAddress##", invoiceModel.getHeadOffice() == null ? "" : invoiceModel.getHeadOffice());
			invHtmlTemp = invHtmlTemp.replace("##ClientAddress##", invoiceModel.getCorpAddr());
			invHtmlTemp = invHtmlTemp.replace("##ClientName##", invoiceModel.getUsedBy() == null ? "" : invoiceModel.getUsedBy());
			invHtmlTemp = invHtmlTemp.replace("##InvoiceNo##", invoiceModel.getInvoiceNo() == null ? "" : invoiceModel.getInvoiceNo());
			invHtmlTemp = invHtmlTemp.replace("##InvoiceDate##", invoiceModel.getInvoiceDt() == null ? "" : opFormat.format(invoiceModel.getInvoiceDt()));
			invHtmlTemp = invHtmlTemp.replace("##BookedBy##", invoiceModel.getBookedBy() == null ? "" : invoiceModel.getBookedBy());	
			invHtmlTemp = invHtmlTemp.replace("##GSTINCorp##", invoiceModel.getCorpGSTIN() == null ? "" : invoiceModel.getCorpGSTIN());	
			invHtmlTemp = invHtmlTemp.replace("##DutySlipNo##", invoiceModel.getDsNo() == null ? "" : invoiceModel.getDsNo());
			invHtmlTemp = invHtmlTemp.replace("##CarBooked##", invoiceModel.getVehBooked() == null ? "" : invoiceModel.getVehBooked());
			invHtmlTemp = invHtmlTemp.replace("##CarAlocated##", invoiceModel.getVehUsed() == null ? "" : invoiceModel.getVehUsed());
			invHtmlTemp = invHtmlTemp.replace("##CarUsedDate##",invoiceModel.getVehUsedDt() == null ? "" : opFormat.format(invoiceModel.getVehUsedDt()));
			invHtmlTemp = invHtmlTemp.replace("##CarNo##", invoiceModel.getVehNo() == null ? "" : invoiceModel.getVehNo());
			invHtmlTemp = invHtmlTemp.replace("##Night Allowence Rate##",tariff.format(invoiceModel.getNightAllow()));
			invHtmlTemp = invHtmlTemp.replace("##Day Allowence Rate##",tariff.format(invoiceModel.getDayAllow()));
			invHtmlTemp = invHtmlTemp.replace("##Tariff Scheme Rate##",tariff.format(invoiceModel.getBasicFair()));
			invHtmlTemp = invHtmlTemp.replace("##Extra Hrs##",tariff.format(invoiceModel.getExtraHrRate()));
			invHtmlTemp = invHtmlTemp.replace("##ExtraKms##",tariff.format(invoiceModel.getExtraKmRate()));
			invHtmlTemp = invHtmlTemp.replace("##Tariff Scheme##",tariff.format(invoiceModel.getTotalBasicFair()));
			invHtmlTemp = invHtmlTemp.replace("##ChargesFor##", invoiceModel.getRentalType());
			invHtmlTemp = invHtmlTemp.replace("##totalExtraKms##", tariff.format(invoiceModel.getExtraKmTotal()));
			invHtmlTemp = invHtmlTemp.replace("##totalExtraHrs##", tariff.format(invoiceModel.getExtraHrTotal()));
			invHtmlTemp = invHtmlTemp.replace("##ExtraKmNo##",invoiceModel.getExtraKm() == null ? "" : invoiceModel.getExtraKm());
			invHtmlTemp = invHtmlTemp.replace("##ExtraHrsNo##",sExtraHrs);
			invHtmlTemp = invHtmlTemp.replace("##TotalDays##",String.valueOf(invoiceModel.getDays()));
			invHtmlTemp = invHtmlTemp.replace("##Day Allowence##",tariff.format(invoiceModel.getDayAllowTotal()));
			invHtmlTemp = invHtmlTemp.replace("##Night Allowence##",tariff.format(invoiceModel.getNightAllowTotal()));
			invHtmlTemp = invHtmlTemp.replace("##TotalNight##",String.valueOf(invoiceModel.getNight()));
			invHtmlTemp = invHtmlTemp.replace("##Parking##",tariff.format(invoiceModel.getParking()));
			invHtmlTemp = invHtmlTemp.replace("##Toll##",tariff.format(invoiceModel.getToll()));
			invHtmlTemp = invHtmlTemp.replace("##Misc Charges##",tariff.format(invoiceModel.getMiscAllowTotal()));
			invHtmlTemp = invHtmlTemp.replace("##KM Used##",hh.format(invoiceModel.getKmUsed()));
			invHtmlTemp = invHtmlTemp.replace("##Tariff Scheme Name##",invoiceModel.getTariffName() == null ? "" : invoiceModel.getTariffName());
			invHtmlTemp = invHtmlTemp.replace("##Hrs Used##", invoiceModel.getHrsUsed() == null ? "" : invoiceModel.getHrsUsed());
			invHtmlTemp = invHtmlTemp.replace("##Interstate Taxes##",tariff.format(invoiceModel.getStateTax()));
			invHtmlTemp = invHtmlTemp.replace("##VehicleUsedAt##", invoiceModel.getVehUsedAt() == null ? "" : invoiceModel.getVehUsedAt());
			invHtmlTemp = invHtmlTemp.replace("##total##", tariff.format(invoiceModel.getTotalAmt()));
			if(invoiceModel.getTariffName().equalsIgnoreCase("Outstation")){
				invHtmlTemp = invHtmlTemp.replace("##TariffSchemeUnit##",String.valueOf(invoiceModel.getDays()));
			}else{
				invHtmlTemp = invHtmlTemp.replace("##TariffSchemeUnit##","");
			}

			String sTaxDetDiv="";
			if(!invoiceModel.getTaxHeading().equals("")){
				String sTaxHeading[] = invoiceModel.getTaxHeading().split("\\##");
				String sTaxValue[]   = invoiceModel.getTaxValues().split("\\##");
				for(int iLoop = 0 ; iLoop < sTaxHeading.length; iLoop++){
					sTaxDetDiv += "<div style='left:100'>" +
							  "		<span class='ft5'>"+sTaxHeading[iLoop]+"</span>" +
							  "		<span class='ft4'>           </span>" +
							  "		<span class='ft4'>           </span>" +
							  "		<span class='ft4' style='float:right; margin-right:15px'>"+sTaxValue[iLoop] +"</span>" +
							  "</div>";
				}
			}
			invHtmlTemp = invHtmlTemp.replace("##taxDeiv##",sTaxDetDiv);
			invHtmlTemp = invHtmlTemp.replace("##grandTotal##", tariff.format(invoiceModel.getGrandTotal()));
			invHtmlTemp = invHtmlTemp.replace("##GSTINBr##",invoiceModel.getBrGSTIN() == null ? "" : invoiceModel.getBrGSTIN());
			
			invHtmlTemp = invHtmlTemp.replace("##bankName##",invoiceModel.getBankName() == null ? "" : invoiceModel.getBankName());
			invHtmlTemp = invHtmlTemp.replace("##bankAccNo##",invoiceModel.getBankAccNo() == null ? "" : invoiceModel.getBankAccNo());
			invHtmlTemp = invHtmlTemp.replace("##bankIFSC##",invoiceModel.getBankIFSC() == null ? "" : invoiceModel.getBankIFSC());
			
			invHtmlTemp = invHtmlTemp.replace("##PanNo##",invoiceModel.getBrPAN() == null ? "" : invoiceModel.getBrPAN());
		}
		catch (Exception e) {
			logger.error("",e);
		}finally{		
		}
		return invHtmlTemp;
	}
	public String getInvoiceMultiple(InvoiceModel invoiceModel,String realPath){
		int fixHeight = 183;
		String multipleInvoiceHtmlSubTemplateFixedUpper = null;
		String multipleInvoiceHtmlSubTemplateVariable = null,multipleInvoiceHtmlSubTemplateFixedLower = null,finalSting=null;
		SimpleDateFormat opFormat = new SimpleDateFormat("dd MMM yyyy");
		DecimalFormat hh = new DecimalFormat("#");
		DecimalFormat tariff = new DecimalFormat("0.00");
		
		try{
			String multipleinvHtmlTemp = IOUtils.toString(new FileReader(realPath+"/html_Formats/multipleinvoice.html"));
			multipleInvoiceHtmlSubTemplateFixedUpper=multipleinvHtmlTemp.substring(0,multipleinvHtmlTemp.indexOf("<span style='display:none;'>span for splitting index 0</span>"));
			multipleInvoiceHtmlSubTemplateVariable=multipleinvHtmlTemp.substring(multipleinvHtmlTemp.indexOf("<span style='display:none;'>span for splitting index 0</span>"),multipleinvHtmlTemp.indexOf("<span style='display:none;'>span for splitting index 1</span>"));
			multipleInvoiceHtmlSubTemplateFixedLower=multipleinvHtmlTemp.substring(multipleinvHtmlTemp.indexOf("<span style='display:none;'>span for splitting index 1</span>"),multipleinvHtmlTemp.indexOf("</html>")+7);

			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##Company##", invoiceModel.getCompName());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##BranchOffice##",invoiceModel.getBrOffice());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##PinCode##", String.valueOf(invoiceModel.getCompPIN()));
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##State##", invoiceModel.getCompState());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CompanyMobileNo##", invoiceModel.getHeadCont());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Email##", invoiceModel.getHeadEmail());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Website##", invoiceModel.getHeadWebsite());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CorporateName##", invoiceModel.getCorpName());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CorporateAddress1##", invoiceModel.getHeadOffice());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##ClientAddress1##", invoiceModel.getCorpAddr());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##InvoiceNo##", invoiceModel.getInvoiceNo());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##InvoiceDate##", opFormat.format(invoiceModel.getInvoiceDt()));
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##GSTINCorp##", invoiceModel.getCorpGSTIN());	
			/*Replace*/
			String finalVariablePart="";
			long height_550=535,height_dutySlipNo = 363,height_carUsedDate=379,height_carBooked = 394,height_carAlloted = 446;
			long height_kmUsed = 479,height_hrsUsed = 497,height_vehicle = 513,height_nightAlowence=462,height_total=513;

			List<InvoiceDetailModel> invoiceDetailModelList = invoiceModel.getInvoiceDetailModel();

			for(InvoiceDetailModel invoiceDetailModel : invoiceDetailModelList) {
				/*	StyleChange*/
				String height_dutySlipNoStyleString=hh.format(height_dutySlipNo);
				String height_carBookedStyleString=hh.format( height_carBooked);
				String height_carUsedDateStyleString=hh.format( height_carUsedDate);
				String height_carAlocatedStyleString=hh.format( height_carAlloted);
				String height_hrsUsedStyleString=hh.format( height_hrsUsed);
				String height_kmUsedString=hh.format( height_kmUsed);
				String height_vehicleUsedString=hh.format( height_vehicle);
				String height_nightAllowenceString=hh.format( height_nightAlowence);
				String height_totalString=hh.format( height_total);
				String tempVariableString = multipleInvoiceHtmlSubTemplateVariable;
				String height_550Styles=hh.format(height_550);
				
				tempVariableString = tempVariableString.replace("##height_550Styles##", height_550Styles);
				tempVariableString = tempVariableString.replace("##dutySlipNoStyle##", height_dutySlipNoStyleString);
				tempVariableString = tempVariableString.replace("##carBookedStyle##", height_carBookedStyleString);
				tempVariableString = tempVariableString.replace("##carUsedDateStyle##",height_carUsedDateStyleString);
				tempVariableString = tempVariableString.replace("##carAlocatedStyle##",height_carAlocatedStyleString);
				tempVariableString = tempVariableString.replace("##kmUsedStyle##",height_kmUsedString);
				tempVariableString = tempVariableString.replace("##hrsUsedStyle##",height_hrsUsedStyleString);
				tempVariableString = tempVariableString.replace("##vehicleUsedStyle##",height_vehicleUsedString);
				tempVariableString = tempVariableString.replace("##nightAllowenceStyle##",height_nightAllowenceString);
				tempVariableString = tempVariableString.replace("##totalStyle##",height_totalString);

				/* StyleChange*/
				tempVariableString = tempVariableString.replace("##Car used Date##",opFormat.format(invoiceDetailModel.getVehUsedDt()));
				tempVariableString = tempVariableString.replace("##DutySlipNo##",invoiceDetailModel.getDsNo());

				tempVariableString = tempVariableString.replace("##CarBooked##",invoiceDetailModel.getVehBooked());
				tempVariableString = tempVariableString.replace("##CarAlocated##",invoiceDetailModel.getVehUsed());
				tempVariableString = tempVariableString.replace("##CarNo##", invoiceDetailModel.getVehNo() == null ? "" : invoiceDetailModel.getVehNo());

				tempVariableString = tempVariableString.replace("##ChargesFor##", invoiceDetailModel.getRentalType());

				tempVariableString = tempVariableString.replace("##Tariff Scheme Rate##",tariff.format(invoiceDetailModel.getBasicFair()));
				tempVariableString = tempVariableString.replace("##Tariff Scheme##",tariff.format(invoiceDetailModel.getTotalBasicFair()));

				tempVariableString = tempVariableString.replace("##ExtraKmNo##",invoiceDetailModel.getExtraKm());
				tempVariableString = tempVariableString.replace("##ExtraKms##",tariff.format(invoiceDetailModel.getExtraKmRate()));
				tempVariableString = tempVariableString.replace("##totalExtraKms##", tariff.format(invoiceDetailModel.getExtraKmTotal()));

				String sExtraHrs = invoiceDetailModel.getExtraHr()== null ? "" : invoiceDetailModel.getExtraHr();
				if(!sExtraHrs.equals("")){
					String[] sHrsPart = sExtraHrs.split("\\:");
					int second = Integer.parseInt(sHrsPart[1]);
					second = (second / 15) * 25;
					sExtraHrs = sHrsPart[0] + "." + String.valueOf(second);
				}
				
				tempVariableString = tempVariableString.replace("##ExtraHrsNo##",sExtraHrs);
				tempVariableString = tempVariableString.replace("##Extra Hrs##",tariff.format(invoiceDetailModel.getExtraHrRate()));
				tempVariableString = tempVariableString.replace("##totalExtraHrs##", tariff.format(invoiceDetailModel.getExtraHrTotal()));
				
				tempVariableString = tempVariableString.replace("##TotalNight##",String.valueOf(invoiceDetailModel.getNight()));
				tempVariableString = tempVariableString.replace("##Night Allowence Rate##",tariff.format(invoiceDetailModel.getNightAllow()));
				tempVariableString = tempVariableString.replace("##Night Allowence##",tariff.format(invoiceDetailModel.getNightAllowTotal()));

				tempVariableString = tempVariableString.replace("##TotalDays##",String.valueOf(invoiceDetailModel.getDays()));
				tempVariableString = tempVariableString.replace("##Day Allowence Rate##",tariff.format(invoiceDetailModel.getDayAllow()));
				tempVariableString = tempVariableString.replace("##Day Allowence##",tariff.format(invoiceDetailModel.getDayAllowTotal()));

				tempVariableString = tempVariableString.replace("##Parking##",tariff.format(invoiceDetailModel.getParking()));
				tempVariableString = tempVariableString.replace("##Toll##",tariff.format(invoiceDetailModel.getToll()));
				tempVariableString = tempVariableString.replace("##Misc Charges##",tariff.format(invoiceDetailModel.getMiscAllowTotal()));
				tempVariableString = tempVariableString.replace("##Interstate Taxes##",tariff.format(invoiceDetailModel.getStateTax()));

				tempVariableString = tempVariableString.replace("##KM Used##",hh.format(invoiceDetailModel.getKmUsed()));
				tempVariableString = tempVariableString.replace("##Tariff Scheme Name##",invoiceDetailModel.getTariffName());
				tempVariableString = tempVariableString.replace("##Hrs Used##", invoiceDetailModel.getHrsUsed());
				tempVariableString = tempVariableString.replace("##RentalType##", invoiceDetailModel.getRentalType());
				tempVariableString = tempVariableString.replace("##BookedBy##",invoiceDetailModel.getBookedBy());
				tempVariableString = tempVariableString.replace("##ClientName##", invoiceDetailModel.getUsedBy());
				tempVariableString = tempVariableString.replace("##VehicleUsedAt##", invoiceDetailModel.getBrName());

				tempVariableString = tempVariableString.replace("##total##", tariff.format(invoiceDetailModel.getTotalAmt()));

				finalVariablePart+=tempVariableString;

				height_dutySlipNo 	+= fixHeight;
				height_carUsedDate 	+= fixHeight;
				height_carBooked 	+= fixHeight;
				height_carAlloted 	+= fixHeight;
				height_kmUsed 		+= fixHeight;
				height_hrsUsed 		+= fixHeight;
				height_vehicle 		+= fixHeight;
				height_nightAlowence+= fixHeight;
				height_total		+= fixHeight;
				height_550 			+= fixHeight;
			}
			long heightAfterInvoices = (height_550 - fixHeight) + 15;
			
			multipleInvoiceHtmlSubTemplateVariable = finalVariablePart;
			String sTaxDetDiv="";
			if(!invoiceModel.getTaxHeading().equals("")){
				String sTaxHeading[] = invoiceModel.getTaxHeading().split("\\##");
				String sTaxValue[]   = invoiceModel.getTaxValues().split("\\##");
				for(int iLoop = 0 ; iLoop < sTaxHeading.length; iLoop++){
					sTaxDetDiv += "<div style='left:100'>" +
							  "		<span class='ft5'>"+sTaxHeading[iLoop]+"</span>" +
							  "		<span class='ft4'>           </span>" +
							  "		<span class='ft4'>           </span>" +
							  "		<span class='ft4' style='float:right; margin-right:15px'>"+ tariff.format(Double.parseDouble(sTaxValue[iLoop])) +"</span>" +
							  "</div>";
				}
			}
			
			if(invoiceModel.getIsParkTaxable().equals("Y")){
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##parkDiv##","");
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grosstotal##",tariff.format(invoiceModel.getTotalAmt()));
			}else{
				double dParkToll = invoiceModel.getParking() + invoiceModel.getToll() + invoiceModel.getStateTax();
				String sparkString = "<div style='position:absolute;top:##tollparkingTop##;left:98'><span class='ft4'>Toll,Taxes & Parking</span></div>" +
						 "<div style='position:absolute;top:##tollparkingTop##;right:39'><span class='ft4'> ##tollparking##</span></div>";
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##parkDiv##",sparkString);
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##tollparking##",tariff.format(dParkToll));
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grosstotal##",tariff.format(invoiceModel.getTotalAmt() - dParkToll));
			}
			
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##taxDiv##",sTaxDetDiv);
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grandTotal##",hh.format(invoiceModel.getGrandTotal()));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##GSTINBr##",invoiceModel.getBrGSTIN());

			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##bankName##",invoiceModel.getBankName() == null ? "" : invoiceModel.getBankName()); 
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##bankAccNo##",invoiceModel.getBankAccNo() == null ? "" : invoiceModel.getBankAccNo());
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##bankIFSC##",invoiceModel.getBankIFSC() == null ? "" : invoiceModel.getBankIFSC());
			
			
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##PanNo##",invoiceModel.getBrPAN());

			long lpreLineHeight = heightAfterInvoices;
			
			if(invoiceModel.getIsParkTaxable().equals("Y")){
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grossTotalTop##", hh.format(lpreLineHeight));
			}else{
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##tollparkingTop##", hh.format(lpreLineHeight));
				multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grossTotalTop##", hh.format(lpreLineHeight = lpreLineHeight + 20));
			}			
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grossTotalTopLine##", hh.format(lpreLineHeight = lpreLineHeight + 10));

			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##taxDivTop##", hh.format(lpreLineHeight = lpreLineHeight + 15));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grandTotalWordStylesTopDiv##", hh.format(lpreLineHeight = lpreLineHeight + 65));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grandTotalWordStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 5));
			
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##EOEStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 25));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##termsStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 15));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##judrificationStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 15));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##ourResponsiplitiesStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##noObjectionStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##interestStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##timeKilometerStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##GSTINStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 15));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##panNoStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##SACNoStylesTop##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##bankAcDetailStyleTop##", hh.format(lpreLineHeight = lpreLineHeight + 15));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##bankAcDetailStyleTop2##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##bankAcDetailStyleTop3##", hh.format(lpreLineHeight = lpreLineHeight + 10));
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("$", "");
			/* StyleChangeLowerPart*/
			finalSting=multipleInvoiceHtmlSubTemplateFixedUpper+multipleInvoiceHtmlSubTemplateVariable+multipleInvoiceHtmlSubTemplateFixedLower;
		}
		catch (Exception e) {
			logger.error("",e);
		}
		return finalSting;
	}
	public String getProp(String sKey) {
		Properties prop = new Properties();
		InputStream input = null;
		String sPropValue = "";
		try {
			String filename = "jdbc.properties";
			input = getClass().getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				return "";
			}
			// load a properties file
			prop.load(input);
			sPropValue = prop.getProperty(sKey);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sPropValue;
	}
	public static void ImageWrite(String sBase64String,String sId, String sType){
		
		String[] parts = sBase64String.split("~");
		if(parts.length <= 1) return;
		String sFileType = parts[0];
		String imageString = parts[1];
		sFileType = sFileType.replace("data:image/", "");
		sFileType = sFileType.replace(";base64", "");
		// create a buffered image
		BufferedImage image = null;
		byte[] imageByte;
		try{
			logger.info("imageString Size: " + imageString.length());
			BASE64Decoder decoder = new BASE64Decoder();
			imageByte = decoder.decodeBuffer(imageString);
			logger.info("imageString Size: " + imageByte.length);

			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();
			// write the image to a file
			String fileName =  ut.getProp("imageStoragePath") + "/" +sType+"/" + sId;
			File outputfile = new File(fileName);
			ImageIO.write(image, sFileType, outputfile);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	public static boolean deleteFile(String sId, String sType){
		String fileName = ut.getProp("imageStoragePath") + "/" +sType+"/" + sId;
		boolean bFindFile = false;
		try{
			File file = new File(fileName);
			bFindFile = file.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
        return bFindFile;
	}
	public static void renameFile(String sOldName, String sNewName, String sType){
		String sPrefix = ut.getProp("imageStoragePath") + "/" +sType+"/";
		sOldName = sPrefix + sOldName;
		sNewName = sPrefix + sNewName;
		try{
			File fileOld = new File(sOldName);
			// File (or directory) with new name
			File fileNew = new File(sNewName);
			if (fileNew.exists())
			   throw new java.io.IOException("file exists");
			// Rename file (or directory)
			boolean success = fileOld.renameTo(fileNew);
			if (!success) {
			   // File was not successfully renamed
			}
		}catch(Exception e){}
	}
	public static void copyFile(String sSrc, String sDest, String sType){
		String sPrefix = ut.getProp("imageStoragePath") + "/" +sType+"/";
		sSrc = sPrefix + sSrc;
		sDest = sPrefix + sDest;
		try{
			Files.copy((new File(sSrc)).toPath(), (new File(sDest)).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}catch(Exception e){}
	}
	public static boolean findFile(String sId, String sType){
		String fileName = ut.getProp("imageStoragePath") + "/" +sType;
		File file = new File(fileName);
		File[] list = file.listFiles();
        boolean bFileFound = false;
        if(list!=null){
		    for (File fil : list){
		    	if (sId.equalsIgnoreCase(fil.getName())){
		        	bFileFound = true;
		        }
		    }
        }
        return bFileFound;
    }
	public static String randomString( int len ){
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
	public static boolean findFile(String sFileName) {
		File f = new File(sFileName);
		if(f.exists() && !f.isDirectory()) { 
			return true;
		}		
		return false;
	}
	public static String sentEmail(String sRecepent, String Header, String sText, String sAttachFilePath){
		Utilities ut = new Utilities();  
		String result = "";
		final String SMTP_HOST_NAME = ut.getProp("SMTP_HOST_NAME");
		final int SMTP_HOST_PORT = Integer.parseInt(ut.getProp("SMTP_HOST_PORT"));
		final String SMTP_AUTH_USER = ut.getProp("SMTP_AUTH_USER"); 
		final String SMTP_AUTH_PWD = ut.getProp("SMTP_AUTH_PWD");
		
		try {
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			props.put("mail.smtp.auth", true);
			props.put("mail.smtp.ssl.enable", true);
			props.put("mail.smtp.starttls.enable",true); 
			
			Session mailSession = Session.getDefaultInstance(props);
			mailSession.setDebug(true);
			final Transport transport = mailSession.getTransport("smtp");
			final MimeMessage message = new MimeMessage(mailSession);

			message.setSubject(Header);
			// Create the message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			// Now set the actual message
			messageBodyPart.setText(sText,"US-ASCII", "html");
			// Create a multipar message
			Multipart multipart = new MimeMultipart();
			// Set text message part
			multipart.addBodyPart(messageBodyPart);
			// Part two is attachment
			logger.info(sAttachFilePath);
			if(findFile(sAttachFilePath)){
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(sAttachFilePath);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(new File(sAttachFilePath).getName());
				multipart.addBodyPart(messageBodyPart);
			}
			// Send the complete message parts
			message.setContent(multipart);
			Address[] from = InternetAddress.parse(SMTP_AUTH_USER);
			message.addFrom(from);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(sRecepent));
			
			new Thread(new Runnable() {
			    public void run() {
			    	try{
					transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
					transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
					transport.close();
			    	}catch(Exception e){
						logger.error("",e);
			    	}
			    }
			}).start();
			result = "email sent";
		} catch (Exception e) {
			logger.error("",e);
			result = "email not sent [Error]";
		}
		return result;
	}
	public static String dateDeshSqlFormat(String sDate){
		String sReturnDate = "Invalid Date";
		if(sDate.length() == 10){
			String[] sToDate = sDate.split("/");
			sReturnDate = sToDate[2]+"-"+sToDate[1]+"-"+sToDate[0];
		}
		return sReturnDate;
	}
	public String generateTDutySlip(BookingModel bookingModel, HttpServletRequest request,
			String realPath){
		String dutyHtmlTemplate = null;
		String noData="";
		try{
			dutyHtmlTemplate = IOUtils.toString(new FileReader(realPath+"/html_Formats/tDutySlip.html"));
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##Company##", bookingModel.getCompany().getName());

			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##BranchOffice##", bookingModel.getAddress().getAddress1());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##PinCode##", bookingModel.getAddress().getPincode().toString());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##State##", bookingModel.getAddress().getState().getName());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##CompanyMobileNo##", bookingModel.getRelatedInfo().getHelpLine());
			dutyHtmlTemplate =  dutyHtmlTemplate.replace("##Email##", bookingModel.getRelatedInfo().getEmail());
			SimpleDateFormat ipBookingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat opBookingFormat = new SimpleDateFormat("dd MMM yyyy");
			Date ipBookingDateOut = null;
			String opBookingDateOut = null;
			try {
				ipBookingDateOut = ipBookingFormat.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bookingModel.gettDutySlipModel().getDispatchDateTime()));

				opBookingDateOut=opBookingFormat.format(ipBookingDateOut);
			}catch(Exception e){
				logger.error("",e);
			}
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##BookingDate##",opBookingDateOut);  
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##DutySlipNo##",bookingModel.gettDutySlipModel().getDutySlipNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##BookingNo##",bookingModel.getBookingNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##ReportingAddress##",bookingModel.getReportingAddress());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##Corporate##",bookingModel.getCompany().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##UserName##",bookingModel.getCustomerName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##MobileNo##",bookingModel.getMobileNo() == null ? "" :bookingModel.getMobileNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##BookedBy##",bookingModel.getBookingTakenBy().getUserName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##RentalType##",bookingModel.getRentalType().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##Mob##",bookingModel.getMob() == null ? "" : bookingModel.getMob().getName());
			

			SimpleDateFormat ipFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat opFormat = new SimpleDateFormat("dd MMM yyyy");
			SimpleDateFormat opTime = new SimpleDateFormat("HH:mm");
			Date ipDateOut = null;
			String opDateOut = null;
			String opTimeOut=null;
			if(bookingModel.gettDutySlipModel().getDispatchDateTime()!=null)
			{
				try {
					ipDateOut = ipFormat.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bookingModel.gettDutySlipModel().getDispatchDateTime()));

					opDateOut = opFormat.format(ipDateOut);
					opTimeOut = opTime.format(ipDateOut);
				}catch(Exception e){
					logger.error("",e);
				}

				dutyHtmlTemplate = dutyHtmlTemplate.replace("##TimeOut##",bookingModel.getStartTime() == null ? "" : bookingModel.getStartTime());
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##DateOut##",opDateOut);
			}
			else{
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##TimeOut##","");
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##DateOut##","");
			}
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##KmOut##",bookingModel.gettDutySlipModel().getOpenKms().toString());

			Long lTotalKms = bookingModel.gettDutySlipModel().getCloseKms() - bookingModel.gettDutySlipModel().getOpenKms();
			String sTotalKms="";
			if(lTotalKms > 0) sTotalKms = String.valueOf(lTotalKms);
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TotalKms##",sTotalKms);

			dutyHtmlTemplate = dutyHtmlTemplate.replace("##ReportingTime##",bookingModel.getPickUpTime());
			if(bookingModel.gettDutySlipModel().getIsUpdatedChauffeur()!=null){
				if(bookingModel.gettDutySlipModel().getIsUpdatedChauffeur().equals("Y")){ 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",bookingModel.gettDutySlipModel().getChauffeurName());
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",bookingModel.gettDutySlipModel().getChauffeurMobile()); 
				}else{
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",""); 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",""); 
				}			
			}else{
				if(bookingModel.gettDutySlipModel().getChauffeurModel() != null){
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",bookingModel.gettDutySlipModel().getChauffeurModel().getName()); 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",bookingModel.gettDutySlipModel().getChauffeurModel().getMobileNo());
				}else{
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurName##",""); 
					dutyHtmlTemplate = dutyHtmlTemplate.replace("##ChaufeurMobile##",""); 
				}
			}

			dutyHtmlTemplate = dutyHtmlTemplate.replace("##CarBooked##",bookingModel.getCarModel().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##CarAllocated##",bookingModel.gettDutySlipModel().getCarDetailModel().getModel().getName());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##CarNo##",bookingModel.gettDutySlipModel().getCarDetailModel().getRegistrationNo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##SplInstruction##",bookingModel.getInstruction() == null ? "" : bookingModel.getInstruction());

			if(bookingModel.gettDutySlipModel().getTollTaxAmount()!=null && bookingModel.gettDutySlipModel().getTollTaxAmount() > 0){
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ParkingToll##",bookingModel.gettDutySlipModel().getTollTaxAmount().toString());
			}else{
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##ParkingToll##","");
			}

			if(bookingModel.gettDutySlipModel().getStateTax()!=null && bookingModel.gettDutySlipModel().getStateTax() > 0){
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##InterstateTaxes##",bookingModel.gettDutySlipModel().getStateTax().toString());
			}else{
				dutyHtmlTemplate = dutyHtmlTemplate.replace("##InterstateTaxes##","");
			}

			//NO Data
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##DateIn##",bookingModel.gettDutySlipModel().getDateTo() == null ? "" : opFormat.format(bookingModel.gettDutySlipModel().getDateTo()));
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TimeIn##",bookingModel.gettDutySlipModel().getTimeTo() == null ? "" : bookingModel.gettDutySlipModel().getTimeTo());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TotalDays##",bookingModel.gettDutySlipModel().getTotalDay() == null ? "" : bookingModel.gettDutySlipModel().getTotalDay());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##TotaHrs##",bookingModel.gettDutySlipModel().getTotalHrs() == null ? "": bookingModel.gettDutySlipModel().getTotalHrs());
			dutyHtmlTemplate = dutyHtmlTemplate.replace("##KmIn##",bookingModel.gettDutySlipModel().getCloseKms() == null ? "" : bookingModel.gettDutySlipModel().getCloseKms().toString());
		}
		catch (Exception e) {
			logger.error("",e);
		}finally{		
		}
		return dutyHtmlTemplate;
	}
	public static String sendNotification(String fcmRegId,String sTitle, String sMessage) {
		Map<String,Object> data = new HashMap<String,Object>();
		Map<String,String> notification = new HashMap<String,String>();
		notification.put("title", sTitle);
		notification.put("message", sMessage);
		data.put("data",notification);
		data.put("to", fcmRegId);
		
		Gson gson=new Gson();
		JsonObject jsonObject = (new JSONParser()).makeHttpRequest("https://fcm.googleapis.com/fcm/send", "jsonPOST", gson.toJson(data));
		return jsonObject.toString();
	}
	public String generateCoveringLetterPrint(BookingDetailModel bookingDetailModel, HttpServletRequest request, String realPath,HashMap<String,Object> mapReturn,String bookingFromDate,String bookingToDate){
		double grandtotalall=0;
		SimpleDateFormat opFormats = new SimpleDateFormat("dd MMM yyyy");
		String multipleinvHtmlTemp = null,multipleInvoiceHtmlSubTemplateFixedUpper = null,multipleInvoiceHtmlSubTemplateFixedMiddle=null,multipleInvoiceHtmlSubTemplateFixedLower=null,opDateOut = null,finalSting=null;
		try{
			multipleinvHtmlTemp = IOUtils.toString(new FileReader(realPath+"/html_Formats/CoveringLetterPrint.htm"));
			multipleInvoiceHtmlSubTemplateFixedUpper=multipleinvHtmlTemp.substring(0,multipleinvHtmlTemp.indexOf("<tr class='border_bottom '>"));
			multipleInvoiceHtmlSubTemplateFixedMiddle=multipleinvHtmlTemp.substring(multipleinvHtmlTemp.indexOf("<tr class='border_bottom '>"),multipleinvHtmlTemp.indexOf("</tbody >"));
			multipleInvoiceHtmlSubTemplateFixedLower=multipleinvHtmlTemp.substring(multipleinvHtmlTemp.indexOf("</tbody >"),multipleinvHtmlTemp.indexOf("yes"));
		
			/*	UPPER PART OF PAGE*/
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Company##", bookingDetailModel.getCompany().getName());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##BranchOffice##",bookingDetailModel.getBranch().getName() +" - "+  bookingDetailModel.getAddress().getAddress1()+" "+bookingDetailModel.getAddress().getAddress2());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##PinCode##", bookingDetailModel.getAddress().getPincode().toString());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##State##", bookingDetailModel.getAddress().getState().getName());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##City##", bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getCity().getName());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CompanyMobileNo##", bookingDetailModel.getRelatedInfo().getHelpLine());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Email##", bookingDetailModel.getRelatedInfo().getEmail());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Website##", bookingDetailModel.getRelatedInfo().getWebSite());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CorporateName##", bookingDetailModel.getBookingMasterModel().getCorporateId().getName());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CorporateAddress1##", bookingDetailModel.getRelatedInfo().getEntityAddress().getAddress1()+" "+bookingDetailModel.getRelatedInfo().getEntityAddress().getAddress2());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##ClientAddress1##", bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress1()+" "+bookingDetailModel.getBookingMasterModel().getCorporateId().getEntityAddress().getAddress2());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##InvoiceNo##",  bookingFromDate);
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##InvoiceDate##", bookingToDate);
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Date##", opFormats.format(new Date()));
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##grandTotal##", "");
			/*	UPPER PART OF PAGE END*/
			/*	MIDDLE PART OF PAGE */
			String coverFinalVariable=null;
			DecimalFormat as= new DecimalFormat("0.00");
			
			int iSlNo = 1;
			String sInvoiceNo = "", sInvoiceDate="",sOutlet="",sBookedByName="",sBookedForName="",sFinalGrandTotal = "",sDispatchDateTime="",sDutyslipNo="";
			Object[] mapKeys = mapReturn.keySet().toArray();
			Arrays.sort(mapKeys);
			
			for(Object key : mapKeys) {
				String coverTempVariable="";
				coverTempVariable=multipleInvoiceHtmlSubTemplateFixedMiddle;
				@SuppressWarnings("unchecked")
				HashMap<String,String> mapReturnIn = (HashMap<String,String>) mapReturn.get(key);
				long lId = Long.parseLong(mapReturnIn.get("lId"));
				sInvoiceNo = mapReturnIn.get("invoiceNo").split("-")[0];
				sInvoiceDate = mapReturnIn.get("invoiceDate");
				sOutlet = mapReturnIn.get("outletName");
				sBookedByName = mapReturnIn.get("bookedByName");
				sBookedForName = mapReturnIn.get("bookedForName");
				sFinalGrandTotal = mapReturnIn.get("finalGrandTotal");
				sDutyslipNo = mapReturnIn.get("dutyslipNo").split("-")[0];
				sDispatchDateTime = mapReturnIn.get("dispatchDateTime");
				
				coverTempVariable = coverTempVariable.replace("##Sl.No##", String.valueOf(iSlNo));
				coverTempVariable = coverTempVariable.replace("##InvoiceNo##",sInvoiceNo);
				coverTempVariable = coverTempVariable.replace("##InvoiceDate##",sInvoiceDate);
				coverTempVariable = coverTempVariable.replace("##SlipNo##",sDutyslipNo);
				coverTempVariable = coverTempVariable.replace("##SlipDate##", sDispatchDateTime);
				coverTempVariable = coverTempVariable.replace("##UsedBy##",sBookedForName);
				coverTempVariable = coverTempVariable.replace("##BookedBy##",sBookedByName);
				coverTempVariable = coverTempVariable.replace("##Amount##",as.format(Double.parseDouble(sFinalGrandTotal)));
				coverTempVariable = coverTempVariable.replace("##Location##",sOutlet);
				coverFinalVariable+=coverTempVariable;
				grandtotalall += Double.parseDouble(sFinalGrandTotal);
				iSlNo++;
			}
			multipleInvoiceHtmlSubTemplateFixedMiddle=coverFinalVariable;
			Float totalgrandTotal=(float)Math.round(grandtotalall);
			String totalGrandTotalformat=as.format(totalgrandTotal);
			/*	MIDDLE PART OF PAGE END */
			/*	LOWER PART OF PAGE */
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grandtotals##", totalGrandTotalformat);
			/*	 LOWERPART OF END */
			multipleInvoiceHtmlSubTemplateFixedMiddle=multipleInvoiceHtmlSubTemplateFixedMiddle.replace("null", "");
			finalSting=multipleInvoiceHtmlSubTemplateFixedUpper+multipleInvoiceHtmlSubTemplateFixedMiddle+multipleInvoiceHtmlSubTemplateFixedLower;
		}
		catch (Exception e) {
			logger.error("",e);
		}
		return finalSting;
	}
	public String generateCoveringLetter(List<InvoiceModel> invoiceModelList, CoverLetterModel coverLetterModel, String realPath){
		double grandtotalall=0;
		SimpleDateFormat opFormats = new SimpleDateFormat("dd MMM yyyy");
		String multipleinvHtmlTemp = null,multipleInvoiceHtmlSubTemplateFixedUpper = null,multipleInvoiceHtmlSubTemplateFixedMiddle=null,multipleInvoiceHtmlSubTemplateFixedLower=null,opDateOut = null,finalSting=null;
		InvoiceModel invoiceModel = invoiceModelList.get(0);
		try{
			multipleinvHtmlTemp = IOUtils.toString(new FileReader(realPath+"/html_Formats/CoveringLetterPrint.htm"));
			multipleInvoiceHtmlSubTemplateFixedUpper=multipleinvHtmlTemp.substring(0,multipleinvHtmlTemp.indexOf("<tr class='border_bottom '>"));
			multipleInvoiceHtmlSubTemplateFixedMiddle=multipleinvHtmlTemp.substring(multipleinvHtmlTemp.indexOf("<tr class='border_bottom '>"),multipleinvHtmlTemp.indexOf("</tbody >"));
			multipleInvoiceHtmlSubTemplateFixedLower=multipleinvHtmlTemp.substring(multipleinvHtmlTemp.indexOf("</tbody >"),multipleinvHtmlTemp.indexOf("yes"));
		
			/*	UPPER PART OF PAGE*/
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Company##", invoiceModel.getCompName());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##BranchOffice##",invoiceModel.getBrOffice());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##PinCode##", String.valueOf(invoiceModel.getCompPIN()));
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##State##", invoiceModel.getCompState());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##City##", "");
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CompanyMobileNo##", invoiceModel.getHeadCont());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Email##", invoiceModel.getHeadEmail());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Website##", invoiceModel.getHeadWebsite());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CorporateName##", coverLetterModel.getCorporate());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##CorporateAddress1##", invoiceModel.getHeadOffice());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##ClientAddress1##", invoiceModel.getCorpAddr());
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##DateFrom##",  coverLetterModel.getInvFromDate() != null ? opFormats.format(coverLetterModel.getInvFromDate()) : "");
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##DateTo##", coverLetterModel.getInvToDate() != null ? opFormats.format(coverLetterModel.getInvToDate()) : "");
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##Date##", opFormats.format(new Date()));
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##LetterNo##", coverLetterModel.getLetterNo() != null ? coverLetterModel.getLetterNo() : "");
			multipleInvoiceHtmlSubTemplateFixedUpper = multipleInvoiceHtmlSubTemplateFixedUpper.replace("##grandTotal##", "");
			/*	UPPER PART OF PAGE END*/
			/*	MIDDLE PART OF PAGE */
			String coverFinalVariable=null;
			DecimalFormat as= new DecimalFormat("0.00");
			
			int iSlNo = 1;
			for(InvoiceModel invoiceModelInd : invoiceModelList) {
				String coverTempVariable = multipleInvoiceHtmlSubTemplateFixedMiddle;
				coverTempVariable = coverTempVariable.replace("##Sl.No##", String.valueOf(iSlNo));
				coverTempVariable = coverTempVariable.replace("##InvoiceNo##",invoiceModelInd.getInvoiceNo().split("-")[0]);
				coverTempVariable = coverTempVariable.replace("##InvoiceDate##",opFormats.format(invoiceModelInd.getInvoiceDt()));
				coverTempVariable = coverTempVariable.replace("##SlipNo##",invoiceModelInd.getDsNo() == null ? "" : invoiceModelInd.getDsNo().split("-")[0]);
				coverTempVariable = coverTempVariable.replace("##SlipDate##", invoiceModelInd.getDsDt() == null ? "" : opFormats.format(invoiceModelInd.getDsDt()));
				coverTempVariable = coverTempVariable.replace("##UsedBy##",invoiceModelInd.getUsedBy());
				coverTempVariable = coverTempVariable.replace("##BookedBy##",invoiceModelInd.getBookedBy());
				coverTempVariable = coverTempVariable.replace("##Amount##",as.format(invoiceModelInd.getGrandTotal()));
				coverTempVariable = coverTempVariable.replace("##Location##",invoiceModelInd.getHubName());
				coverFinalVariable+=coverTempVariable;
				grandtotalall += invoiceModelInd.getGrandTotal();
				iSlNo++;
			}
			multipleInvoiceHtmlSubTemplateFixedMiddle=coverFinalVariable;
			Float totalgrandTotal=(float)Math.round(grandtotalall);
			String totalGrandTotalformat=as.format(totalgrandTotal);
			/*	MIDDLE PART OF PAGE END */
			/*	LOWER PART OF PAGE */
			multipleInvoiceHtmlSubTemplateFixedLower = multipleInvoiceHtmlSubTemplateFixedLower.replace("##grandtotals##", totalGrandTotalformat);
			/*	 LOWERPART OF END */
			multipleInvoiceHtmlSubTemplateFixedMiddle=multipleInvoiceHtmlSubTemplateFixedMiddle.replace("null", "");
			finalSting=multipleInvoiceHtmlSubTemplateFixedUpper+multipleInvoiceHtmlSubTemplateFixedMiddle+multipleInvoiceHtmlSubTemplateFixedLower;
		}
		catch (Exception e) {
			logger.error("",e);
		}
		return finalSting;
	}
}
