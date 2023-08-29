package com.report.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.billing.model.CoverLetterModel;
import com.operation.model.BookingMasterModel;

@Repository("reportDao")
public class ReportDaoImpl implements ReportDao{
	private static final Logger logger = Logger.getLogger(ReportDaoImpl.class);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private SessionFactory sessionFactory;
	private static int pageSize = 5;
	
	@Override
	public List<BookingMasterModel> getBookingData() {
	     Query query =(Query) sessionFactory.getCurrentSession().createQuery("SELECT bmm.id as id ,bmm.bookingDate as bookingDate FROM BookingMasterModel as bmm").
	    		 setResultTransformer(Transformers.aliasToBean(BookingMasterModel.class));
	     return query.list();
	}	
	
	@SuppressWarnings("unchecked")
	public List<CoverLetterModel> listCoverLetter( String[] sCriteriaList, String[] sValueList) {
		List<CoverLetterModel> coverLetterModels = null;

		String sSearchCriteria = " WHERE ";
		for (int iCount = 0; iCount < sCriteriaList.length; iCount++) {
			String sCriteria = sCriteriaList[iCount];
			String sValue = sValueList[iCount];
			if (sCriteria.equals("invoiceFromDate") || sCriteria.equals("invoiceToDate")) {
				try {
					Date dDate = dateFormat.parse(sValue);
					sValue = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
				} catch (ParseException e) {
					logger.error("",e);
				}
			}
			if (sCriteria.equals("invoiceFromDate")) {
				sSearchCriteria += "(A.letterDate  BETWEEN '" + sValue + "'  And ";
			}else if (sCriteria.equals("invoiceToDate")) {
				sSearchCriteria += "'" + sValue + "') ";
			}else if(sCriteria.equals("corpName")){
				sSearchCriteria += " And (A.corporateId = '" + sValue + "' OR '" + sValue + "' = '0' )";
			}else if(sCriteria.equals("branch")){
				sSearchCriteria += " And (A.branchId = '" +sValue +"' OR '" + sValue + "' = '0') ";
			}else if(sCriteria.equals("hub")){
				sSearchCriteria += " And (A.hubId = '" +sValue +"' OR '" + sValue + "' = '0') ";
			}
		}
		String sSql = " FROM CoverLetterModel A " + sSearchCriteria + " ORDER By A.corporate asc, A.letterDate asc";
		logger.info("Query for CoverLetter List Searching : "+sSql);
		coverLetterModels = sessionFactory.getCurrentSession().createQuery(sSql).list();
		return coverLetterModels;
	}
}
