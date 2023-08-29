package com.report.service;

import java.util.List;

import com.billing.model.CoverLetterModel;
import com.operation.model.BookingMasterModel;


public interface ReportService {
	public List<BookingMasterModel> getBookingData();
	public List<CoverLetterModel> listCoverLetter( String[] sCriteriaList, String[] sValueList);
}
