package com.report.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.billing.controller.BillingController;
import com.billing.model.CoverLetterModel;
import com.billing.model.InvoiceDetailModel;
import com.billing.model.InvoiceModel;
import com.billing.service.BillingService;
import com.common.service.MasterDataService;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.operation.service.BookingMasterService;
import com.operation.service.DutySlipService;
import com.report.model.InvoiceReportFactory;
import com.report.model.InvoiceReportModel;
import com.report.service.ReportService;
import com.report.validator.ReportValidator;
import com.util.JsonResponse;
import com.util.Message;

@Controller
public class ReportController {
	private static final Logger logger = Logger.getLogger(ReportController.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	List<DutySlipModel> dutySlipModelListPrint=null;
	String bookingFromDate=null;
	String bookingToDate=null;
	@Autowired
	private MasterDataService masterDataService;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private BillingService billingService;

	@Autowired
	private BookingMasterService bookingMasterService;
	
	@Autowired
	private DutySlipService dutySlipService;

	@Autowired 
	private BillingController billingController;
	
	private ReportValidator validator = null;

	public ReportValidator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(ReportValidator validator) {
		this.validator = validator;
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields(new String[] { "id" });
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping("/pageLoadReport/{pageNumber}")
	public ModelAndView pageLoadBookingReport(Map<String, Object> map, @PathVariable("pageNumber") int pageNumber,
			HttpSession session, @RequestParam("pageFor") String pageFor) {
		ModelAndView modelAndView = new ModelAndView("report");
		try {
			map.put("pageFor", pageFor);
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			map.put("pageNumber", pageNumber);
		}
		return modelAndView;
	}

	public static void exportreporttohtml(HttpServletRequest request, HttpServletResponse response, JasperPrint print,
			String image_dir_name) {
		System.out.println("request.getContextPath()" + request.getContextPath());
		try {
			File imagedir = new File(request.getContextPath() + "/" + "images/");
			request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, print);
			JRHtmlExporter exporter = new JRHtmlExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
			// exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR,imagedir);
			// exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,"imageServlet?image=");
			exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
			exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, 1.2F);
			// exporter.setParameter(JRHtmlExporterParameter.IGNORE_PAGE_MARGINS,
			// true);

			exporter.exportReport();
		} catch (JRException jre) {
			logger.error(jre);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void exportreporttopdf(HttpServletResponse response, JasperPrint print) {
		try {
			OutputStream outputStream = response.getOutputStream();
			byte[] bytes = null;

			bytes = JasperExportManager.exportReportToPdf(print);
			outputStream.write(bytes, 0, bytes.length);
			outputStream.flush();
			outputStream.close();
		} catch (JRException jre) {
			logger.error(jre);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = "/loadReportPDF")
	public void loadReportPDF(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {
		String sourceFileName = request.getSession().getServletContext()
				.getRealPath("/WEB-INF/jasperReport/InvoiceReport.jrxml");
		String compiledSourceFileName = request.getSession().getServletContext()
				.getRealPath("/WEB-INF/jasperReport/InvoiceReport.jasper");
		System.out.println(sourceFileName);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ReportTitle", "Jasper Demo");
		parameters.put("Author", "Prepared By jCombat");
		try {
			System.out.println("Start compiling!!! ...");
			File file = new File(compiledSourceFileName);
			if (!file.exists())
				JasperCompileManager.compileReportToFile(sourceFileName);
			System.out.println("Done compiling!!! ...");
			// List<BookingMasterModel> bookingMasterModelList =
			// reportService.getBookingData();
			List<InvoiceReportModel> bookingMasterModelList = (List<InvoiceReportModel>) InvoiceReportFactory
					.createBeanCollection();

			JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(compiledSourceFileName);

			JRBeanCollectionDataSource beanColDataSource = null;
			JasperPrint jasperPrint = new JasperPrint();
			JasperPrint jasperPrint1 = null;
			Iterator iterator = bookingMasterModelList.iterator();
			Collection<InvoiceReportModel> invoiceReportModel = null;
			invoiceReportModel = new ArrayList<InvoiceReportModel>();
			while (iterator.hasNext()) {
				invoiceReportModel.clear();
				invoiceReportModel.add((InvoiceReportModel) iterator.next());
				beanColDataSource = new JRBeanCollectionDataSource(invoiceReportModel);
				jasperPrint1 = JasperFillManager.fillReport(report, parameters, beanColDataSource);
				List pages = jasperPrint1.getPages();
				for (int j = 0; j < pages.size(); j++) {
					JRPrintPage object = (JRPrintPage) pages.get(j);
					jasperPrint.addPage(object);
				}
			}
			exportreporttohtml(request, response, jasperPrint, null);
			/*
			 * if (jasperPrint != null) { byte[] pdfReport =
			 * JasperExportManager.exportReportToPdf(jasperPrint);
			 * response.reset(); response.setContentType("application/pdf");
			 * response.setHeader("Cache-Control", "no-store");
			 * response.setHeader("Cache-Control", "private");
			 * response.setHeader("Pragma", "no-store");
			 * response.setContentLength(pdfReport.length); try {
			 * response.getOutputStream().write(pdfReport);
			 * response.getOutputStream().flush();
			 * response.getOutputStream().close(); } catch (Exception e) {
			 * 
			 * } }
			 */

			// JasperViewer.viewReport(jasperPrint,false);
		} catch (JRException e) {
			logger.error("",e);
		}
	}

	@RequestMapping("/loadReportXLS")
	public void loadReportXLS(ModelAndView modelAndView, HttpServletResponse response) {
		String sourceFileName = "F:/sample-report.jrxml";
		System.out.println(sourceFileName);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ReportTitle", "Jasper Demo");
		parameters.put("Author", "Prepared By jCombat");
		try {
			System.out.println("Start compiling!!! ...");
			JasperCompileManager.compileReportToFile(sourceFileName);
			System.out.println("Done compiling!!! ...");
			List<BookingMasterModel> bookingMasterModelList = reportService.getBookingData();
			sourceFileName = "F:/chathuranga-sample-report.jasper";
			JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(bookingMasterModelList);
			JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(sourceFileName);
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, beanColDataSource);
			if (jasperPrint != null) {
				byte[] pdfReport = JasperExportManager.exportReportToPdf(jasperPrint);
				response.reset();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Cache-Control", "no-store");
				response.setHeader("Cache-Control", "private");
				response.setHeader("Pragma", "no-store");
				response.setContentLength(pdfReport.length);
				try {
					response.getOutputStream().write(pdfReport);
					response.getOutputStream().flush();
					response.getOutputStream().close();
				} catch (Exception e) {

				}
			}
			JasperViewer.viewReport(jasperPrint, false);
		} catch (JRException e) {
			logger.error("",e);
		}
	}

	@RequestMapping("/loadReportCSV")
	public ModelAndView loadReportCSV(ModelAndView modelAndView) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<BookingMasterModel> bookingMasterModelList = reportService.getBookingData();
		JRDataSource JRdataSource = new JRBeanCollectionDataSource(bookingMasterModelList);
		parameterMap.put("datasource", JRdataSource);
		// xlsReport bean has ben declared in the jasper-views.xml file
		modelAndView = new ModelAndView("csvReport", parameterMap);
		return modelAndView;
	}

	@RequestMapping("/loadReportHTML")
	public ModelAndView loadReportHTML(ModelAndView modelAndView) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<BookingMasterModel> bookingMasterModelList = reportService.getBookingData();
		JRDataSource JRdataSource = new JRBeanCollectionDataSource(bookingMasterModelList);
		parameterMap.put("datasource", JRdataSource);
		// xlsReport bean has ben declared in the jasper-views.xml file
		modelAndView = new ModelAndView("htmlReport", parameterMap);
		return modelAndView;
	}
	
	@RequestMapping("/pageLoadBookingRegister")
	public ModelAndView pageLoadBookingRegister(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("bookingRegister");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:"+branchesAssigned)); // Type
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOB"));
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			map.put("bookingMasterModel", new BookingMasterModel());
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/searchReport", method = RequestMethod.POST)
	public @ResponseBody JsonResponse searchReport(
			@RequestParam("sCriteria") String sCriteria, 
			@RequestParam("sValue") String sValue, 
			@RequestParam("sPurpose") String sPurpose,
			HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			String[] sCriteriaList = sCriteria.split(",");
			String[] sValueList = sValue.split(",");
			String sDataGrid = "";
			if(sPurpose.equalsIgnoreCase("CoverLetter")){
				List<CoverLetterModel> coverLetterList = reportService.listCoverLetter(sCriteriaList, sValueList);
				sDataGrid = showGridJobs(coverLetterList);
			}else if(sPurpose.equalsIgnoreCase("Invoices")){
				List<DutySlipModel> dutySlipList = null;
				List<InvoiceModel> invoiceList = null;
				if(sValueList[0].equals("D")){
					dutySlipList = billingService.getDutySlipList(sCriteriaList, sValueList);
					invoiceList = billingService.getInvoiceList(sCriteriaList, sValueList);
				}else{
					invoiceList = billingService.getInvoiceList(sCriteriaList, sValueList);
				}
				sDataGrid = showGridDS(dutySlipList,invoiceList);
			}
			res.setDataGrid(sDataGrid);
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
	
	public String showGridJobs(List<CoverLetterModel> coverLetterList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat twoDigit = new DecimalFormat("0.00");

		String dataString = " <table style='table-layout:fixed; width:99%' class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>" 
				+ " <tr style='font-size:9pt;'> 									"
				+ " 	<th width='8%'>Action</th> 		  	"
				+ " 	<th width='10%' >Cover Note No</th>"
				+ " 	<th width='10%' >System Date</th>"
				+ " 	<th width='10%' >Submit Date</th>"
				+ " 	<th width='30%' >Corporate Name</th>" 
				+ " 	<th width='10%' >Amount</th>"
				+ " 	<th width='10%' >Branch</th>"
				+ " 	<th width='10%' >Hub</th>"
				+ " 	<th width='10%' >Remark</th>"
				+ " </tr>" 
				+ " </thead>"
				+ " <tbody>";
		if (coverLetterList == null) {
			return dataString + "</tbody></table>";
		}
		for (CoverLetterModel coverLetter : coverLetterList) {
			long lId = coverLetter.getId().longValue();
			
			dataString 	+="<tr> "
						+ " <td style='padding:5px'>"
						+ " 	<a href='javascript: getCoverLetter(\""+lId+"\",\"V\")' ><img src='./img/viewButton.png' border='0' title='View' width='30' height='20'/></a> "
						+ " 	<a href='javascript: getCoverLetter(\""+lId+"\",\"P\")' ><img src='./img/ic_print.png' border='0' title='Print' width='30' height='20'/></a> "
						+ " </td> "	
						+ " <td id='LetterNo_"+ lId+ "' style='padding:5px'>"+ coverLetter.getLetterNo() + "</td>"
						+ " <td id='SystemDt_"+ lId+ "' style='padding:5px'>"+ dateFormat.format(coverLetter.getCreationDate()) + "</td> "
						+ " <td id='LetterDt_"+ lId+ "' style='padding:5px'>"+ dateFormat.format(coverLetter.getLetterDate()) + "</td> "
					 	+ " <td id='corporateName_"+ lId+ "' style='padding:5px'>"+ coverLetter.getCorporate() + "</td>"
					 	+ " <td id='totalAmount_"+ lId+ "' style='padding:5px;' align=right>"+ twoDigit.format(coverLetter.getTotalLetterAmount()) + "</td>"	
					 	+ " <td id='branch_"+ lId+ "' style='padding:5px'>"+ coverLetter.getBranch() + "</td>"	
					 	+ " <td id='hub_"+ lId+ "' style='padding:5px'>"+ coverLetter.getHub() + "</td>"	
					 	+ " <td id='Remark_"+ lId+ "' style='padding:5px'>"+ coverLetter.getRemarks() + "</td>"	
					 	+ "</tr>";
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}
	@SuppressWarnings("unused")
	public String showGridDS(List<DutySlipModel> dutySlipList, List<InvoiceModel> invoiceList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat twoDigit = new DecimalFormat("0.00");
		DecimalFormat hh = new DecimalFormat("#");
		
		String dataString = " <table class='table table-bordered table-striped table-hover dataTable' id='userTable'> 	"
				+ " <thead style='background-color: #FED86F;'>" 
				+ " <tr style='font-size:9pt;'> 									"
				+ " 	<th>S.No.</th>"
				+ " 	<th>Corporate Name</th>"
				+ " 	<th>Bill No</th>"
				+ " 	<th>Bill Dt.</th>"
				+ " 	<th>Slip No</th>" 
				+ " 	<th>Slip Dt.</th>"
				+ " 	<th>Used By</th>"
				+ " 	<th>Booked By</th>"
				+ " 	<th>Emp ID</th>"
				+ " 	<th>Location</th>"
				+ " 	<th>Car Booked</th>"
				+ " 	<th>Referance No</th>"
				+ " 	<th>Tarrif</th>"
				+ " 	<th>Basic Rate</th>"
				+ " 	<th>Opening KM</th>"
				+ " 	<th>Closing KM</th>"
				+ " 	<th>Total Km</th>"
				+ " 	<th>Extra Kms</th>"
				+ " 	<th>Rate/ Km</th>"
				+ " 	<th>Total Km Amount</th>"
				+ " 	<th>Start Time</th>"
				+ " 	<th>Close Time</th>"
				+ " 	<th>Total Hrs</th>"
				+ " 	<th>Extra Hrs</th>"
				+ " 	<th>Rate/ Hrs</th>"
				+ " 	<th>Total Hr Amount</th>"
				+ " 	<th>Day Allowance</th>"
				+ " 	<th>Night Allowence</th>"
				+ " 	<th>Misc. Charges</th>"
				+ " 	<th>Guide Charges</th>"
				+ " 	<th>Discount</th>"
				+ " 	<th>Base Fare</th>"
				+ " 	<th>Parking Charges</th>"
				+ " 	<th>Toll Charges</th>"
				+ " 	<th>State Tax</th>"
				+ " 	<th>Gross Amount</th>"
				+ " 	<th>CGST</th>"
				+ " 	<th>SGST</th>"
				+ " 	<th>IGST</th>"
				+ " 	<th>Bill Amount</th>"
				+ " 	<th>Car No</th>"
				+ " 	<th>Remarks</th>"
				+ " </tr>" 
				+ " </thead>"
				+ " <tbody>";
		if ((dutySlipList == null && invoiceList == null)) {
			return dataString + "</tbody></table>";
		}
		
		int iSNo = 0;
		if(dutySlipList != null){
			for (DutySlipModel dutySlipModel : dutySlipList) {
				String sTariffName = (dutySlipModel.getTariff() != null ? dutySlipModel.getTariff().getName() : dutySlipModel.getBookingDetailModel().getTariff().getName());
				String[] sTotalHrsArr = (dutySlipModel.getTotalHrs() != null ? dutySlipModel.getTotalHrs() : "00:00").split(":");
				Double dHrsUsed =  Double.parseDouble(sTotalHrsArr[0] + "." + String.valueOf((Long.parseLong(sTotalHrsArr[1]) / 15) * 25));					
				
				Double lMinHrs = (dutySlipModel.getMinChgHrs() != null ? dutySlipModel.getMinChgHrs() : 0);
				String sExtraHrsCal = dHrsUsed > lMinHrs ? String.valueOf(Math.round(Long.parseLong(sTotalHrsArr[0]) - lMinHrs)) + "." + (Long.parseLong(sTotalHrsArr[1]) > 0 ? (Long.parseLong(sTotalHrsArr[1]) / 15) * 25 : "00") : "0.00";
				Double dExtraHrsCharge = 0.00;
				Double dExtraHrsRate = (dutySlipModel.getExtraChgHrs() != null ? dutySlipModel.getExtraChgHrs() : 0);
				if(!sExtraHrsCal.equals("0")) dExtraHrsCharge = Double.parseDouble(sExtraHrsCal) * dExtraHrsRate;
					
				Double dTotalBaseFare = 0.00;
				Double dBasicFare = (dutySlipModel.getBasicFare() != null ? dutySlipModel.getBasicFare() : 0.00);
				Double dTotalKm = (dutySlipModel.getTotalKms() != null ? dutySlipModel.getTotalKms() : 0.00);
				Double dMinKm   = (dutySlipModel.getMinChgKms() != null ? dutySlipModel.getMinChgKms() : 0.00);
				
				Long lDays      = (dutySlipModel.getTotalDay() != null ? dutySlipModel.getTotalDay() : 0);
				if(sTariffName.equalsIgnoreCase("Outstation") && lDays >= 1){
					dMinKm = dMinKm * lDays;
					dBasicFare = dBasicFare * lDays;
					dExtraHrsCharge = 0.00;
					dExtraHrsRate = 0.00;
					sExtraHrsCal = "0.00";
				}
				Double dExtraKm = (dTotalKm - dMinKm) <= 0 ? 0 : (dTotalKm - dMinKm);
				Double dExtraKmRate = (dutySlipModel.getExtraChgKms() != null ? dutySlipModel.getExtraChgKms() : 0.00);
				Double dTotalExtraKmCharge = dExtraKmRate * dExtraKm;

				if(sTariffName.contains("Drop") || sTariffName.contains("Transfer") || sTariffName.contains("A/T ") ){
					dExtraKm = 0.00;
					dExtraKmRate = 0.00;
					dTotalExtraKmCharge = 0.00;
					dExtraHrsCharge = 0.00;
					dExtraHrsRate = 0.00;
					sExtraHrsCal = "0.00";
				}
				
				dTotalBaseFare = dBasicFare + dTotalExtraKmCharge + dExtraHrsCharge + 
								(dutySlipModel.getDayAllow() != null ? dutySlipModel.getDayAllow() : 0) +
								(dutySlipModel.getNightAllow() != null ? dutySlipModel.getNightAllow() : 0) +
								(dutySlipModel.getMiscCharge() != null ? dutySlipModel.getMiscCharge() : 0) +
								(dutySlipModel.getGuideCharge() != null ? dutySlipModel.getGuideCharge() : 0);
				
				dTotalBaseFare = (double) Math.round(dTotalBaseFare);
				Double dTotalBill = (double) Math.round(dutySlipModel.getTotalFare() != null ? dutySlipModel.getTotalFare() : 0.00);
				
				dataString 	+=" <tr> 				  "
						+ "		<td>"+ ++iSNo + "</td>"	
						+ "		<td>"+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getCorporateId().getName() + "</td>"
						+ "		<td>"+ (dutySlipModel.getInvoiceNo() != null ? dutySlipModel.getInvoiceNo() : "") +"</td>" //BillNo
						+ "		<td>"+ (dutySlipModel.getInvoiceDate() != null ? dateFormat.format(dutySlipModel.getInvoiceDate()) : "")+"</td>" //BillDt
						+ "		<td>"+ dutySlipModel.getDutySlipNo() + "</td>"
						+ "		<td>"+ dateFormat.format(dutySlipModel.getDutySlipCreatedByDate()) + "</td>"
						+ "		<td>"+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedForName() + "</td>"
						+ "		<td>"+ dutySlipModel.getBookingDetailModel().getBookingMasterModel().getBookedBy().getName() + "</td>"
						+ "		<td></td>" //EmpNo
						+ "		<td>"+ dutySlipModel.getBookingDetailModel().getBranch().getName() + "</td>"
						+ "		<td>"+ dutySlipModel.getBookingDetailModel().getCarModel().getName() + "</td>"
						+ "		<td>"+ (dutySlipModel.getBookingDetailModel().getBookingMasterModel().getReferenceNo() != null ? dutySlipModel.getBookingDetailModel().getBookingMasterModel().getReferenceNo() : "") + "</td>"
						+ "		<td>"+ sTariffName + "</td>"
						+ "		<td>"+ twoDigit.format(dBasicFare) + "</td>"
						+ "		<td>"+ (dutySlipModel.getOpenKms() != null ? hh.format(dutySlipModel.getOpenKms()) : "0") + "</td>"
						+ "		<td>"+ (dutySlipModel.getCloseKms() != null ? hh.format(dutySlipModel.getCloseKms()) : "0") + "</td>"
						+ "		<td>"+ hh.format(dTotalKm) + "</td>" //TotalKm
						+ "		<td>"+ hh.format(dExtraKm) + "</td>" //ExtraKms
						+ "		<td>"+ twoDigit.format(dExtraKmRate) +"</td>" //RateKm
						+ "		<td>"+ twoDigit.format(dTotalExtraKmCharge) +"</td>" //TotalKmAmount
						+ "		<td>"+ (dutySlipModel.getTimeFrom() != null ? dutySlipModel.getTimeFrom() : "0.00") + "</td>"
						+ "		<td>"+ (dutySlipModel.getTimeTo() != null ? dutySlipModel.getTimeTo() : "0.00") + "</td>"
						+ "		<td>"+ twoDigit.format(dHrsUsed) + "</td>"
						+ "		<td>"+ sExtraHrsCal + "</td>" //ExtraHrs
						+ "		<td>"+ twoDigit.format(dExtraHrsRate) +"</td>" //RateHrs
						+ "		<td>"+ twoDigit.format(dExtraHrsCharge) + "</td>" //TotalHrAmount
						+ "		<td>"+ (dutySlipModel.getDayAllow() != null ? twoDigit.format(dutySlipModel.getDayAllow()) : "0.00") + "</td>"
						+ "		<td>"+ (dutySlipModel.getNightAllow() != null ? twoDigit.format(dutySlipModel.getNightAllow()) : "0.00") + "</td>"
						+ "		<td>"+ (dutySlipModel.getMiscCharge() != null ? twoDigit.format(dutySlipModel.getMiscCharge()) : "0.00") + "</td>"
						+ "		<td>"+ (dutySlipModel.getGuideCharge() != null ? twoDigit.format(dutySlipModel.getGuideCharge()) : "0.00") + "</td>"
						+ "		<td></td>" //Discount
						+ "		<td>"+ twoDigit.format(dTotalBaseFare) +"</td>" //BasicFare
						+ "		<td>"+ (dutySlipModel.getParkingAmount() != null ? twoDigit.format(dutySlipModel.getParkingAmount()) : "0.00") + "</td>"
						+ "		<td>"+ (dutySlipModel.getTollTaxAmount() != null ? twoDigit.format(dutySlipModel.getTollTaxAmount()) : "0.00") + "</td>"
						+ "		<td>"+ (dutySlipModel.getStateTax() != null ? twoDigit.format(dutySlipModel.getStateTax()) : "0.00") + "</td>"
						+ "		<td>"+ (dutySlipModel.getTotalFare() != null ? twoDigit.format(dutySlipModel.getTotalFare()) : "0.00")+"</td>" //GrossAmount
						+ "		<td></td>" //CGST
						+ "		<td></td>" //SGST
						+ "		<td></td>" //IGST
						+ "		<td>"+ twoDigit.format(dTotalBill) +"</td>" //GrossAmount
						+ "		<td>"+ dutySlipModel.getCarDetailModel().getRegistrationNo() + "</td>"
						+ "		<td>"+ (dutySlipModel.getRemarks() != null ? dutySlipModel.getRemarks() : "") + "</td>"
						+ " </tr>" ;
			}
		}
		
		if(invoiceList != null){
			for (InvoiceModel invoiceModel : invoiceList) {
				if(invoiceModel.getInvoiceType().equals("S")){
					String[] sTaxDedArr = extractTaxDed(invoiceModel.getTaxHeading(), invoiceModel.getTaxValues());
					Double dDiscount = 0.00;
					if(sTaxDedArr != null){
						if(!sTaxDedArr[3].equals("")) dDiscount = Double.parseDouble(sTaxDedArr[3]);
					}
					Double dTotalBaseFare =( (invoiceModel.getTotalBasicFair() != null ? invoiceModel.getTotalBasicFair() : 0) 	+
											 (invoiceModel.getExtraKmTotal() != null ? invoiceModel.getExtraKmTotal() : 0) 		+ 
											 (invoiceModel.getExtraHrTotal() != null ? invoiceModel.getExtraHrTotal() : 0) 		+
											 (invoiceModel.getDayAllowTotal() != null ? invoiceModel.getDayAllowTotal() : 0) 	+
											 (invoiceModel.getNightAllowTotal() != null ? invoiceModel.getNightAllowTotal() : 0)+
											 (invoiceModel.getMiscAllowTotal() != null ? invoiceModel.getMiscAllowTotal() : 0) 	+
											 (invoiceModel.getGuideCharge() != null ? invoiceModel.getGuideCharge() : 0)
											) - dDiscount;

					dTotalBaseFare = (double) Math.round(dTotalBaseFare);

					String[] sHrsPart = (invoiceModel.getHrsUsed() != null ? invoiceModel.getHrsUsed() : "0:00").split(":");
					Double dHrsUsed =  Double.parseDouble(sHrsPart[0] + "." + String.valueOf((Long.parseLong(sHrsPart[1]) / 15) * 25));					
					
					sHrsPart = (invoiceModel.getExtraHr() != null ? invoiceModel.getExtraHr() : "0:00").split(":");
					Double dExtraHrsHr =  Double.parseDouble(sHrsPart[0] + "." + String.valueOf((Long.parseLong(sHrsPart[1]) / 15) * 25));					
					
					dataString 	+=" <tr> 				  "
							+ "		<td>"+ ++iSNo + "</td>"	
							+ "		<td>"+ invoiceModel.getCorpName() + "</td>"
							+ "		<td>"+ invoiceModel.getInvoiceNo()+ "</td>" //BillNo
							+ "		<td>"+ dateFormat.format(invoiceModel.getInvoiceDt()) + "</td>" //BillDt
							+ "		<td>"+ invoiceModel.getDsNo() + "</td>"
							+ "		<td>"+ dateFormat.format(invoiceModel.getDsDt()) + "</td>"
							+ "		<td>"+ invoiceModel.getUsedBy() + "</td>"
							+ "		<td>"+ invoiceModel.getBookedBy() + "</td>"
							+ "		<td></td>" //EmpNo
							+ "		<td>"+ invoiceModel.getBrName() + "</td>"
							+ "		<td>"+ invoiceModel.getVehBooked() + "</td>"
							+ "		<td>"+ (invoiceModel.getRefNo() != null ? invoiceModel.getRefNo() : "") + "</td>"
							+ "		<td>"+ invoiceModel.getTariffName() + "</td>"
							+ "		<td>"+ (invoiceModel.getTotalBasicFair() != null ? twoDigit.format(invoiceModel.getTotalBasicFair()) : "0.00") + "</td>"
							+ "		<td>"+ (invoiceModel.getOpeningKm() != null ? hh.format(invoiceModel.getOpeningKm()) : "0") + "</td>"
							+ "		<td>"+ (invoiceModel.getClosingKm() != null ? hh.format(invoiceModel.getClosingKm()) : "0") + "</td>"
							+ "		<td>"+ (invoiceModel.getKmUsed() != null ? hh.format(invoiceModel.getKmUsed()) : "0") + "</td>"
							+ "		<td>"+ invoiceModel.getExtraKm() +"</td>" //ExtraKms
							+ "		<td>"+ (invoiceModel.getExtraKmRate() != null ? twoDigit.format(invoiceModel.getExtraKmRate()) : "0.00") +"</td>" //RateKm
							+ "		<td>"+ (invoiceModel.getExtraKmTotal() != null ? twoDigit.format(invoiceModel.getExtraKmTotal()) : "0") +"</td>" //TotalKmAmount
							+ "		<td>"+ invoiceModel.getStartTime() + "</td>"
							+ "		<td>"+ invoiceModel.getCloseTime() + "</td>"
							+ "		<td>"+ twoDigit.format(dHrsUsed) + "</td>"
							+ "		<td>"+ twoDigit.format(dExtraHrsHr) +"</td>" //ExtraHrs
							+ "		<td>"+ (invoiceModel.getExtraHrRate() != null ? twoDigit.format(invoiceModel.getExtraHrRate()) : "0.00")+"</td>" //RateHrs
							+ "		<td>"+ (invoiceModel.getExtraHrTotal() != null ? String.valueOf(invoiceModel.getExtraHrTotal()) : "0.00")+"</td>" //TotalHrAmount
							+ "		<td>"+ (invoiceModel.getDayAllowTotal() != null ? twoDigit.format(invoiceModel.getDayAllowTotal()) : "0.00") + "</td>"
							+ "		<td>"+ (invoiceModel.getNightAllowTotal() != null ? twoDigit.format(invoiceModel.getNightAllowTotal()) : "0.00") + "</td>"
							+ "		<td>"+ (invoiceModel.getMiscAllowTotal() != null ? twoDigit.format(invoiceModel.getMiscAllowTotal()) : "0.00") + "</td>"
							+ "		<td>"+ (invoiceModel.getGuideCharge() != null ? twoDigit.format(invoiceModel.getGuideCharge()) : "0.00") + "</td>"
							+ "		<td>"+ sTaxDedArr[3]+"</td>" //Discount
							+ "		<td>"+ twoDigit.format(dTotalBaseFare) +"</td>" //BasicFare
							+ "		<td>"+ (invoiceModel.getParking() != null ? twoDigit.format(invoiceModel.getParking()) : "0.00") + "</td>"
							+ "		<td>"+ (invoiceModel.getToll() != null ? twoDigit.format(invoiceModel.getToll()) : "0.00") + "</td>"
							+ "		<td>"+ (invoiceModel.getStateTax() != null ? twoDigit.format(invoiceModel.getStateTax()) : "0.00") + "</td>"
							+ "		<td>"+ (invoiceModel.getTotalAmt() != null ? twoDigit.format(invoiceModel.getTotalAmt()) : "0.00")+"</td>" //GrossAmount
							+ "		<td>"+ sTaxDedArr[0]+"</td>" //CGST
							+ "		<td>"+ sTaxDedArr[1]+"</td>" //SGST
							+ "		<td>"+ sTaxDedArr[2]+"</td>" //IGST
							+ "		<td>"+ (invoiceModel.getGrandTotal() != null ? twoDigit.format(invoiceModel.getGrandTotal()) : "0.00")+"</td>" //BillAmount
							+ "		<td>"+ invoiceModel.getVehNo() + "</td>"
							+ "		<td>"+ invoiceModel.getRemark() + "</td>"
							+ " </tr>" ;
				}else if(invoiceModel.getInvoiceType().equals("M")){
					for(InvoiceDetailModel invoiceDetailModel : invoiceModel.getInvoiceDetailModel()){
						String[] sTaxDedArr = extractTaxDed(invoiceDetailModel.getTaxHeading(), invoiceDetailModel.getTaxValues());
						Double dDiscount = 0.00;
						if(sTaxDedArr != null){
							if(!sTaxDedArr[3].equals("")) dDiscount = Double.parseDouble(sTaxDedArr[3]);
						}
						Double dTotalBaseFare =( (invoiceDetailModel.getTotalBasicFair() != null ? invoiceDetailModel.getTotalBasicFair() : 0) 	+
												 (invoiceDetailModel.getExtraKmTotal() != null ? invoiceDetailModel.getExtraKmTotal() : 0) 		+ 
												 (invoiceDetailModel.getExtraHrTotal() != null ? invoiceDetailModel.getExtraHrTotal() : 0) 		+
												 (invoiceDetailModel.getDayAllowTotal() != null ? invoiceDetailModel.getDayAllowTotal() : 0) 	+
												 (invoiceDetailModel.getNightAllowTotal() != null ? invoiceDetailModel.getNightAllowTotal() : 0)+
												 (invoiceDetailModel.getMiscAllowTotal() != null ? invoiceDetailModel.getMiscAllowTotal() : 0) 	+
												 (invoiceDetailModel.getGuideCharge() != null ? invoiceDetailModel.getGuideCharge() : 0)
												) - dDiscount;
						dTotalBaseFare = (double) Math.round(dTotalBaseFare);

						String[] sHrsPart = (invoiceDetailModel.getHrsUsed() != null ? invoiceDetailModel.getHrsUsed() : "0:00").split(":");
						Double dHrsUsed =  Double.parseDouble(sHrsPart[0] + "." + String.valueOf((Long.parseLong(sHrsPart[1]) / 15) * 25));					
						
						sHrsPart = (invoiceDetailModel.getExtraHr() != null ? invoiceDetailModel.getExtraHr() : "0:00").split(":");
						Double dExtraHrsHr =  Double.parseDouble(sHrsPart[0] + "." + String.valueOf((Long.parseLong(sHrsPart[1]) / 15) * 25));					
						
						dataString 	+=" <tr> 				  "
								+ "		<td>"+ ++iSNo + "</td>"	
								+ "		<td>"+ invoiceModel.getCorpName() + "</td>"
								+ "		<td>"+ invoiceModel.getInvoiceNo()+ "</td>" //BillNo
								+ "		<td>"+ dateFormat.format(invoiceModel.getInvoiceDt()) + "</td>" //BillDt
								+ "		<td>"+ invoiceDetailModel.getDsNo() + "</td>"
								+ "		<td>"+ dateFormat.format(invoiceDetailModel.getDsDt()) + "</td>"
								+ "		<td>"+ invoiceDetailModel.getUsedBy() + "</td>"
								+ "		<td>"+ invoiceDetailModel.getBookedBy() + "</td>"
								+ "		<td></td>" //EmpNo
								+ "		<td>"+ invoiceDetailModel.getBrName() + "</td>"
								+ "		<td>"+ invoiceDetailModel.getVehBooked() + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getRefNo() != null ? invoiceDetailModel.getRefNo() : "") + "</td>"
								+ "		<td>"+ invoiceDetailModel.getTariffName() + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getTotalBasicFair() != null ? twoDigit.format(invoiceDetailModel.getTotalBasicFair()) : "0.00") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getOpeningKm() != null ? hh.format(invoiceDetailModel.getOpeningKm()) : "0") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getClosingKm() != null ? hh.format(invoiceDetailModel.getClosingKm()) : "0") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getKmUsed() != null ? hh.format(invoiceDetailModel.getKmUsed()) : "0") + "</td>"
								+ "		<td>"+ invoiceDetailModel.getExtraKm() +"</td>" //ExtraKms
								+ "		<td>"+ (invoiceDetailModel.getExtraKmRate() != null ? twoDigit.format(invoiceDetailModel.getExtraKmRate()) : "0.00") +"</td>" //RateKm
								+ "		<td>"+ (invoiceDetailModel.getExtraKmTotal() != null ? twoDigit.format(invoiceDetailModel.getExtraKmTotal()) : "0") +"</td>" //TotalKmAmount
								+ "		<td>"+ invoiceDetailModel.getStartTime() + "</td>"
								+ "		<td>"+ invoiceDetailModel.getCloseTime() + "</td>"
								+ "		<td>"+ twoDigit.format(dHrsUsed) + "</td>"
								+ "		<td>"+ twoDigit.format(dExtraHrsHr) +"</td>" //ExtraHrs
								+ "		<td>"+ (invoiceDetailModel.getExtraHrRate() != null ? twoDigit.format(invoiceDetailModel.getExtraHrRate()) : "0.00")+"</td>" //RateHrs
								+ "		<td>"+ (invoiceDetailModel.getExtraHrTotal() != null ? twoDigit.format(invoiceDetailModel.getExtraHrTotal()) : "0.00")+"</td>" //TotalHrAmount
								+ "		<td>"+ (invoiceDetailModel.getDayAllowTotal() != null ? twoDigit.format(invoiceDetailModel.getDayAllowTotal()) : "0.00") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getNightAllowTotal() != null ? twoDigit.format(invoiceDetailModel.getNightAllowTotal()) : "0.00") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getMiscAllowTotal() != null ? twoDigit.format(invoiceDetailModel.getMiscAllowTotal()) : "0.00") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getGuideCharge() != null ? twoDigit.format(invoiceDetailModel.getGuideCharge()) : "0.00") + "</td>"
								+ "		<td>"+ sTaxDedArr[3]+"</td>" //Discount
								+ "		<td>"+ twoDigit.format(dTotalBaseFare) +"</td>" //BasicFare
								+ "		<td>"+ (invoiceDetailModel.getParking() != null ? twoDigit.format(invoiceDetailModel.getParking()) : "0.00") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getToll() != null ? twoDigit.format(invoiceDetailModel.getToll()) : "0.00") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getStateTax() != null ? twoDigit.format(invoiceDetailModel.getStateTax()) : "0.00") + "</td>"
								+ "		<td>"+ (invoiceDetailModel.getTotalAmt() != null ? twoDigit.format(invoiceDetailModel.getTotalAmt()) : "0.00")+"</td>" //GrossAmount
								+ "		<td>"+ sTaxDedArr[0]+"</td>" //CGST
								+ "		<td>"+ sTaxDedArr[1]+"</td>" //SGST
								+ "		<td>"+ sTaxDedArr[2]+"</td>" //IGST
								+ "		<td>"+ (invoiceDetailModel.getGrandTotal() != null ? twoDigit.format(invoiceDetailModel.getGrandTotal()) : "0.00")+"</td>" //BillAmount
								+ "		<td>"+ invoiceDetailModel.getVehNo() + "</td>"
								+ "		<td>"+ invoiceDetailModel.getRemark() + "</td>"
								+ " </tr>" ;
					}
				}
			}
		}
		dataString = dataString + "</tbody></table>";
		return dataString;
	}
	
	private String[] extractTaxDed(String sTaxHead, String sTaxVal){
		String[] sReturn = {"","","",""};
		DecimalFormat twoDigit = new DecimalFormat("0.00");

		if(!sTaxHead.equals("") && !sTaxHead.isEmpty()){
			String[] sTaxHeadArr = sTaxHead.split("\\##");
			String[] sTaxValArr = sTaxVal.split("\\##");
			for(int iCount = 0; iCount < sTaxHeadArr.length; iCount++){
				if(sTaxHeadArr[iCount].contains("CGST")) sReturn[0] = twoDigit.format(Double.parseDouble(sTaxValArr[iCount]));
				if(sTaxHeadArr[iCount].contains("SGST")) sReturn[1] = twoDigit.format(Double.parseDouble(sTaxValArr[iCount]));
				if(sTaxHeadArr[iCount].contains("IGST")) sReturn[2] = twoDigit.format(Double.parseDouble(sTaxValArr[iCount]));
				if(sTaxHeadArr[iCount].contains("Less")) sReturn[3] = twoDigit.format(Double.parseDouble(sTaxValArr[iCount].replace("( - ) ","")));
			}
		}
		return sReturn;
	}
	@RequestMapping("/pageLoadCovering")
	public ModelAndView pageLoadCovering(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("coveringLetter");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:"+branchesAssigned)); // Type
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOB"));
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			map.put("bookingMasterModel", new BookingMasterModel());
		}
		return modelAndView;
	}
	
	@RequestMapping("/pageLoadDataExport")
	public ModelAndView pageLoadDataExport(Map<String, Object> map, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("dataExport");
		String branchesAssigned = (String) session.getAttribute("branchesAssigned");
		try {
			session.setAttribute("bookingDetails_sessionData", null);
			map.put("customerOrCompanyNameIdList", masterDataService.getGeneralMasterData_UsingCode("CORP:" + branchesAssigned)); // Type
			map.put("branchList", masterDataService.getGeneralMasterData_UsingCode("VBM-A:"+branchesAssigned));
			map.put("usedByList", billingService.getUsedBy());
			map.put("closedByList", billingService.getClosedBy());
			map.put("invoiceNoList", billingService.getInvoiceNo());
			map.put("rentalTypeIdList", masterDataService.getGeneralMasterData_UsingCode("VRT"));
			map.put("MOBList", masterDataService.getGeneralMasterData_UsingCode("MOB"));
			map.put("bookedByList", billingService.getBookedBy());
		} catch (Exception e){
			logger.error("", e);
		} finally {}
		return modelAndView;
	}

	@RequestMapping(value = "/saveCoverDate", method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveCoverDate(@RequestParam("coverDate") String coverDate, HttpSession session,
			HttpServletRequest request) {
		JsonResponse res = new JsonResponse();

		try {
			for(DutySlipModel dutySlpModel:dutySlipModelListPrint){
				dutySlpModel.setId(dutySlpModel.getId());
				dutySlpModel.setCoverDate(coverDate);
				dutySlipService.update(dutySlpModel);
			}
			
			res.setStatus("Success");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus("Failure");
			res.setResult(Message.FATAL_ERROR_MESSAGE);
		}
		return res;
	}
}
