package com.common.global;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import com.common.model.AddressDetailModel;
import com.common.model.ContactDetailModel;
import com.master.model.StateMasterModel;
import com.operation.model.MaintenanceDetailModel;
import com.util.Utilities;

public class CommonJspPage {
	private static final Logger logger = Logger.getLogger(CommonJspPage.class);

	public static String contactDetailForm(ContactDetailModel contactDetailModel){
		String formString ="<br>"+
			"<table style='width:100%;' border=0>"+
				"<tr>";
					if(contactDetailModel.getId()!=null)
						formString=formString+"<td style='width:8%;'><input type='hidden' name='contactDetailModel_id' id='contactDetailModel_id' value='"+contactDetailModel.getId()+"'></td>";
					else
						formString=formString+"<td style='width:8%;'><input type='hidden' name='contactDetailModel_id' id='contactDetailModel_id' value='' ></td>";
					formString=formString+"<td style='width:22%;' > Contact Person 1 </td>"+
					"<td style='width:25%;' > "+ 
						"<input type='text' name='contactPerson1' id='contactPerson1' maxlength='50' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getContactPerson1()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:15%;' > Contact Person 2</td>"+
					"<td style='width:25%;' > "	+ 
						"<input type='text' name='contactPerson2' id='contactPerson2' maxlength='50' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getContactPerson2()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:5%;'>&nbsp;</td>"+
				"<tr>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:22%;' > Residential Phone </td>"+
					"<td style='width:25%;' > "+ 
						"<input type='text' name='residentialPhone' id='residentialPhone' maxlength='15' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getResidentialPhone()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:15%;' > Official Phone</td>"+
					"<td style='width:25%;' > "	+ 
						"<input type='text' name='officialPhone' id='officialPhone' maxlength='15' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getOfficialPhone()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:5%;'>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:22%;' > Residential/Personal Mobile </td>"+
					"<td style='width:25%;' > "+ 
						"<input type='text' name='personalMobile' id='personalMobile' maxlength='13' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getPersonalMobile()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:15%;' > Official Mobile</td>"+
					"<td style='width:25%;' > "	+ 
						"<input type='text' name='officialMobile' id='officialMobile' maxlength='13' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getOfficialMobile()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:5%;'>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:22%;' > Personal Email Id</td>"+
					"<td style='width:25%;' > "+ 
						"<input type='text' name='personalEmailId' id='personalEmailId' maxlength='30' onkeypress='isValidEmail(event)' value='"+contactDetailModel.getPersonalEmailId()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:15%;' > Official Email Id</td>"+
					"<td style='width:10%;' > "	+ 
						"<input type='text' name='officialEmailId' id='officialEmailId' maxlength='30' onkeypress='isValidEmail(event)' value='"+contactDetailModel.getOfficialEmailId()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:5%;'>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:22%;' > Residential/Personal Fax No.</td>"+
					"<td style='width:25%;' > "+ 
						"<input type='text' name='residentialFaxNo' id='residentialFaxNo' maxlength='40' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getResidentialFaxNo()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:15%;' > Official Fax No.</td>"+
					"<td style='width:25%;' > "	+ 
						"<input type='text' name='officialFaxNo' id='officialFaxNo' maxlength='40' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getOfficialFaxNo()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:5%;'>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:22%;' > Personal Website</td>"+
					"<td style='width:25%;' > "+ 
						"<input type='text' name='personalWebsite' id='personalWebsite' maxlength='50' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getPersonalWebsite()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:15%;' > Official Website</td>"+
					"<td style='width:25%;' > "	+ 
						"<input type='text' name='officialWebsite' id='officialWebsite' maxlength='50' onkeypress='isAlphaNumeric(event)' value='"+contactDetailModel.getOfficialWebsite()+"' style='width:90%'>"+ 
					"</td>"+
					"<td style='width:5%;'>&nbsp;</td>"+
				"<tr>"+
			"</table>";
		return formString.trim();
	}
	
	public static String addressDetailForm(AddressDetailModel addressDetailModel,List<StateMasterModel> stateMasterModels){
		String formString ="<br>"+
			"<table style='width:100%;' border=0>"+
				"<tr>";
					if(addressDetailModel.getId()!=null)
						formString=formString+"<td style='width:8%;'><input type='hidden' name='addressDetailModel_id' id='addressDetailModel_id' value='"+addressDetailModel.getId()+"'></td>";
					else
						formString=formString+"<td style='width:8%;'><input type='hidden' name='addressDetailModel_id' id='addressDetailModel_id' value=''></td>";
				formString=formString+"<td style='width:17%;' > Address 1 </td>"+
					"<td style='width:35%;' > "+ 
						"<input type='text' name='address1' id='address1' maxlength='200' onkeypress='isAlphaNumeric(event)' value='"+addressDetailModel.getAddress1()+"' style='width:100%'>"+ 
					"</td>"+
					"<td style='width:40%;'colspan=3>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:17%;' > Address 2 </td>"+
					"<td style='width:35%;' > "+ 
						"<input type='text' name='address2' id='address2' maxlength='200' onkeypress='isAlphaNumeric(event)' value='"+addressDetailModel.getAddress2()+"' style='width:100%'>"+ 
					"</td>"+
					"<td style='width:40%;'colspan=3>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:17%;' > LandMark </td>"+
					"<td style='width:35%;' > "+ 
						"<input type='text' name='landMark' id='landMark' maxlength='200' onkeypress='isAlphaNumeric(event)' value='"+addressDetailModel.getLandMark()+"' style='width:100%'>"+ 
					"</td>"+
					"<td style='width:40%;'colspan=3>&nbsp;</td>"+
				"</tr>"+
					"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:17%;' > Pincode </td>"+
					"<td style='width:35%;' ><table><tr><td> "; 
					if(addressDetailModel.getPincode()!=null)
						formString=formString+"<input type='text' name='pincode' id='pincode' maxlength='6' onkeypress='isNumeric(event)' value='"+addressDetailModel.getPincode()+"' onblur='getStateDistMasterData_usingPincode(this.value)' style='width:50%'>";
					else
						formString=formString+"<input type='text' name='pincode' id='pincode' maxlength='6' onkeypress='isNumeric(event)' onblur='getStateDistMasterData_usingPincode(this.value)' value='' style='width:50%'>";
					formString=formString+"</td><td><div id='pincodeMsgDiv' style='color:red;display:none;'>Invalid Pincode</div></td></tr></table></td>"+
					"<td style='width:40%;'colspan=3></td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:17%;' > State </td>"+
					"<td style='width:35%;' > "+ 
					    "<Select NAME='state'  id='state' style=\"width:100% \" onchange='getDistrictData(this.value,0)' >"+
							"<option value = '0'>---Select---</option>";
							for(StateMasterModel masterModel:stateMasterModels){
								String selectedIndex="";
								if(addressDetailModel.getState().getId()!=null && addressDetailModel.getState().getId().equals(masterModel.getId()))
									selectedIndex="selected";
								else 
									selectedIndex="";
								formString=formString+"<option value = '"+masterModel.getId()+"' "+selectedIndex+">"+masterModel.getName()+"</option>";
							}
							formString=formString+"</select>"+
					"</td>"+
					"<td style='width:40%;'colspan=3>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:17%;' > District </td>"+
					"<td style='width:35%;' > 	"+ 
						"<div id='distictDiv' name='distictDiv'>"+ 
							"<Select NAME='district'  id='district' style=\"width:100% \" onchange='getCityData(this.value,0)'>"+
								"<option value = '0'>---Select---</option>"+
							"</select>"+ 
						"</div>"+
					"</td>"+
					"<td style='width:40%;'colspan=3>&nbsp;</td>"+
				"</tr>"+
				"<tr>"+
					"<td style='width:8%;'>&nbsp;</td>"+
					"<td style='width:17%;' > City </td>"+
					"<td style='width:35%;' > 	"+ 
						"<div id='cityDiv' name='cityDiv'>"+ 
							"<Select NAME='city'  id='city' style=\"width:100% \" >"+
								"<option value = '0'>---Select---</option>"+
							"</select>"+ 
						"</div>"+
					"</td>"+
					"<td style='width:40%;'colspan=3>&nbsp;</td>"+
				"</tr>"+
			"</table>";
		return formString.trim();
	}
	
	public static String maintenanceDetailForm(MaintenanceDetailModel maintenanceDetailModel) {
		String formString = "<br>" +
		 "<table width='100%' align='left' border=0>"+
		 "<tr> "+
		 "<td width='40%' valign='top'>" +
			 "<table width='100%' align='left' border=0>" +
			 "<tr> ";
			 if(maintenanceDetailModel.getId()!=null)
				 formString=formString+"<td style='width:8%;'><input type='hidden' name='maintenanceDetailModel_id' id='maintenanceDetailModel_id' value='"+maintenanceDetailModel.getId()+"'></td>";
			 else
				 formString=formString+"<td style='width:8%;'><input type='hidden' name='maintenanceDetailModel_id' id='maintenanceDetailModel_id' value=''></td>";
			 formString=formString+ "<td width='40%'>Fitness Certificate No.</td> " +
				 "<td width='45%'> "+
				 "<input type='text' value='"+maintenanceDetailModel.getFitnessCertificateNo()+"' name='fitnessCertificateNo' id='fitnessCertificateNo' onkeypress='isAlphaNumeric(event)' style='width:100%' maxlength='30'>"+
				 "</td> "+
				 "<td width='5%'>&nbsp;</td> "+
			 "</tr> "+
			 "<tr> "+
				"<td width='10%'>&nbsp;</td> "+
				 "<td width='40%'>Fitness UpTo</td> "+
				 "<td width='45%'> "+
				 "<input type='text' value='"+getGMTDateTo_DDMMYYYY(maintenanceDetailModel.getFitnessUpTo()!=null?maintenanceDetailModel.getFitnessUpTo().toString():null)+"' name='maintenanceDetailModel.fitnessUpTo' id='fitnessUpTo' readonly='true' style='width:100%' size='10'  class='datetimepicker' >"+
				 "</td> "+
				 "<td width='5%'>&nbsp;</td> "+
			 "</tr> "+
			 "<tr> "+
				 "<td width='10%'>&nbsp;</td> "+
				 "<td width='40%'>PUC No.</td> "+
				 "<td width='45%'> "+
				 "<input type='text' value='"+maintenanceDetailModel.getPucNo()+"' name='maintenanceDetailModel.pucNo' id='pucNo' onkeypress='isAlphaNumeric(event)' style='width:100%' maxlength='30'>"+
				 "</td> "+
				 "<td width='5%'>&nbsp;</td> "+
			 "</tr> "+
			 "<tr> "+
				 "<td width='10%'>&nbsp;</td> "+
				 "<td width='40%'>PUC UpTo</td> "+
				 "<td width='45%'> "+
				 "<input type='text' value='"+getGMTDateTo_DDMMYYYY(maintenanceDetailModel.getPucUpTo()!=null?maintenanceDetailModel.getPucUpTo().toString():null)+"' name='maintenanceDetailModel.pucUpTo' id='pucUpTo' readonly='true' style='width:100%' size='10' class='datetimepicker' >"+
				 "</td> "+
				 "<td width='5%'>&nbsp;</td> "+
			 "</tr> "+
			 "</table> "+
		 "</td>"+
		 "<td width='60%' valign='top'> "+
			 "<table width='100%' align='left' border=0> "+
			 "<tr>"+
				 "<td align='left'>"+
				 "<input type='button' id='mainTenanceDetailAddButton'	value='+' onclick='addRowTable_MaintenanceDetail(\"mainTenanceDetailTable\")'/>&nbsp; "+
				 "<input type='button' id='mainTenanceDetailDeleteButton' value='-' onclick='deleteRowTable_maintenanceDetail(\"mainTenanceDetailTable\")'/>"+
				 "</td> "+
			 "</tr> "+
			 "<tr> "+
				 "<td> "+
				 "<TABLE Width='90%' Align='left' Border=0 Id='' style='border:#000000 1px solid;' > "+
					 "<TR> "+
						 "<TH Width='10%' >Select</TH> "+
						 "<TH Width='20%' >Service Date</TH> "+
						 "<TH Width='30%' >Garadge</TH> "+
						 "<TH Width='40%' >Description</TH> "+
					 "</TR> "+
				 "</TABLE> "+
				 "</td> "+
			 "</tr> "+
			 "<tr> "+
				 "<td> "+
				 "<Div Style='width:90%;height:112px;overflow-Y:scroll;overflow-X:hide; border:#000000 1px solid;' > "+
				 	"<TABLE Width='100%' Align='left' Border=0 style='border:#000000 0px solid;'  id='mainTenanceDetailTable'></TABLE> "+ 
				 "</Div> " + 
				 "</td> " + 
			"</tr> " +
			"</table> "+
		 "</td>" +
		 "</tr>" +
		 "</table>";
		return formString;
	}	
	
	public static String getGMTDateTo_DDMMYYYY(String dateString) {
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		Date date=null;
		String formatedDate ="";
		if(dateString==null)
			return "";
		try{
			date = (Date)formatter.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			formatedDate = cal.get(Calendar.DATE) + "/" +(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
		}
		catch(Exception e){
		}
		return formatedDate;
	}

	public static String[] dateTimeDifferenceWithJoda(String startDate,String endDate) {
		String dateData[]=new String[2]; 
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			DateTime dt1 = new DateTime(format.parse(startDate));
			DateTime dt2 = new DateTime(format.parse(endDate));
			String day=((Days.daysBetween(dt1, dt2).getDays())+"").length()==1?"0"+(Days.daysBetween(dt1, dt2).getDays()):(Days.daysBetween(dt1, dt2).getDays())+"";
			String hours=((Hours.hoursBetween(dt1, dt2).getHours() % 24)+"").length()==1?"0"+(Hours.hoursBetween(dt1, dt2).getHours() % 24):(Hours.hoursBetween(dt1, dt2).getHours() % 24)+"";
			String minutes=((Minutes.minutesBetween(dt1, dt2).getMinutes() % 60)+"").length()==1?"0"+(Minutes.minutesBetween(dt1, dt2).getMinutes() % 60):(Minutes.minutesBetween(dt1, dt2).getMinutes() % 60)+"";
			String seconds=((Seconds.secondsBetween(dt1, dt2).getSeconds() % 60)+"").length()==1?"0"+(Seconds.secondsBetween(dt1, dt2).getSeconds() % 60):(Seconds.secondsBetween(dt1, dt2).getSeconds() % 60)+"";
			dateData[0]=day;
			dateData[1]=(hours+":"+minutes+":"+seconds);
		} 
		catch (Exception e) {
			logger.error("",e);
		}
		return dateData;
	}

	
}
