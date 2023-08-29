package com.report.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class InvoiceReportFactory {
	public InvoiceReportFactory(){
		
	}	
	
	@SuppressWarnings("deprecation")
	public static Collection<InvoiceReportModel> createBeanCollection() {
		Collection<InvoiceReportModel> dataBeanList = new ArrayList<InvoiceReportModel>();
		dataBeanList.add(new InvoiceReportModel("Gurgaon -Plot No- 64-65, Ist Floor, Udyog Vihar, Phase IV Gurgaon - 122001. Haryana (India) Telephone  : +91 124 4962000 , E-Mail : billing@viceroyindia.com","Plot No - 64-65, Ist Floor, Udyog Vihar, Phase IV Gurgaon - 122001. Haryana (India) Telephone  : +91 124 4962000 , Mobile No : +91 9818999999 Website: www.viceroyindia.com	","Dentsu Marcom Private Limited", "001-V-GGN-11-12", new Date(), "001-V-GGN-DS2-251011"
				,new Date(),"Mr. Chandan","Mr. Amitesh Kumar"," Swift Dzire"," Swift Dzire","5861 DL1YB "," Half / Full Days","4 Hrs / 40 Kms",2,50D,5,13D,
				200D,150D,50D,60D,100D,45,6,"Local Run"));
		dataBeanList.add(new InvoiceReportModel("Gurgaon -Plot No- 64-65, Ist Floor, Udyog Vihar, Phase IV Gurgaon - 122001. Haryana (India) Telephone  : +91 124 4962000 , E-Mail : billing@viceroyindia.com","Plot No - 64-65, Ist Floor, Udyog Vihar, Phase IV Gurgaon - 122001. Haryana (India) Telephone  : +91 124 4962000 , Mobile No : +91 9818999999 Website: www.viceroyindia.com	","Dentsu Marcom Private Limited", "001-V-GGN-11-12", new Date(), "001-V-GGN-DS2-251011"
				,new Date(),"Mr. Chandan","Mr. Amitesh Kumar"," Swift Dzire"," Swift Dzire","5861 DL1YB "," Half / Full Days","4 Hrs / 40 Kms",2,50D,5,13D,
				200D,150D,50D,60D,100D,45,6,"Local Run"));

		dataBeanList.add(new InvoiceReportModel("Gurgaon -Plot No- 64-65, Ist Floor, Udyog Vihar, Phase IV Gurgaon - 122001. Haryana (India) Telephone  : +91 124 4962000 , E-Mail : billing@viceroyindia.com","Plot No - 64-65, Ist Floor, Udyog Vihar, Phase IV Gurgaon - 122001. Haryana (India) Telephone  : +91 124 4962000 , Mobile No : +91 9818999999 Website: www.viceroyindia.com	","Dentsu Marcom Private Limited", "001-V-GGN-11-12", new Date(), "001-V-GGN-DS2-251011"
				,new Date(),"Mr. Chandan","Mr. Amitesh Kumar"," Swift Dzire"," Swift Dzire","5861 DL1YB "," Half / Full Days","4 Hrs / 40 Kms",2,50D,5,13D,
				200D,150D,50D,60D,100D,45,6,"Local Run"));

		return dataBeanList;
	}


}
