package com.report.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.billing.model.CoverLetterModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.DutySlipModel;
import com.report.dao.ReportDao;

@Service("reportService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ReportServiceImpl implements ReportService{
	
	@Autowired
	private ReportDao reportDao;

	public ReportServiceImpl() {
		
	}

	@Override
	public List<BookingMasterModel> getBookingData() {
		return reportDao.getBookingData();
	}

	@Override
	public List<CoverLetterModel> listCoverLetter(String[] sCriteriaList, String[] sValueList) {
		return reportDao.listCoverLetter(sCriteriaList, sValueList);
	}
}
