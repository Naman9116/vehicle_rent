package com.billing.dao;
import java.util.List;

import com.billing.model.CoverLetterModel;
import com.billing.model.InvoiceModel;
import com.billing.model.TransactionDetailModel;
import com.corporate.model.AutorizedUserModel;
import com.corporate.model.CorporateModel;
import com.master.model.CarAllocationModel;
import com.master.model.CorporateTariffDetModel;
import com.master.model.CorporateTaxDetModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MasterModel;
import com.master.model.TariffSchemeParaModel;
import com.operation.model.BookingDetailModel;
import com.operation.model.BookingMasterModel;
import com.operation.model.CarDetailModel;
import com.operation.model.DutySlipModel;
import com.operation.model.HistoryDutySlipModel;
import com.user.model.UserModel;

public interface BillingDao {
	public List<DutySlipModel> list( String jobType,String[] sCriteriaList, String[] sValueList);
	public List<DutySlipModel> getInvoice( String[] sCriteriaList, String[] sValueList);
	public List<AutorizedUserModel> getBookedBy();
	public List<GeneralMasterModel> getCarMasterId(long id) ;
	public List<CorporateTariffDetModel> getTariffValue(long tId);
/*	public List<DutySlipModel> getDutySlipCount(long bookingDetailId);*/
	public List <DutySlipModel> getds(String invoiceNo, Long lCorpId);
	public List<CorporateTariffDetModel> getTariffValue(long tId,long corporateId,String sDate,long branchId);
	public BookingMasterModel getDutySlipClosingBookingMasterDetail(long dutySlipDetailsId);
	public List<BookingMasterModel> getUsedBy();
	public List<UserModel> getClosedBy();
	public List<DutySlipModel> getInvoiceNo();
	/*sahil*/
	
	public List<GeneralMasterModel> list(String corporateId);
	public List<CarDetailModel> getCarRegistrationNo();
	public List<BookingMasterModel> getBookedFor();
	public List<DutySlipModel> getDutySlipClosingDetail(String dutySlipRertrive) ;
	public List<BookingMasterModel> getDutySlipClosingBookingMasterDetail(String dutySlipRertrive) ;
	public String save(HistoryDutySlipModel historyDutySlipModel);
	public CarAllocationModel getCarOwnerType(Long carDetailId);
	public  CorporateModel getCompany(String corporateId);
	/*public BookingMasterModel save(BookingMasterModel bookingMasterModel);*/
	public List<CorporateTariffDetModel> getTariffValues(long tId,long corporateId) ;
	public BookingDetailModel getBookingDetailId(long id);
	public DutySlipModel save(DutySlipModel dutySlipModel,String sBookingNo);
	public List<CarDetailModel> getAlocationCarRegistrationNo() ;
	public CarAllocationModel getCarOwner(String id);
	public CarAllocationModel getSearchCarReg(long id);
	
	public TariffSchemeParaModel getTariffSchemePara(Long id);
	public List<CorporateTaxDetModel> getTaxs(long corporateId, String invoiceDate);
	
	public DutySlipModel getCombineFare(String invoiceNo);
	public List<CarAllocationModel> getAlocationCarNo(Long modelId);
	public List<TariffSchemeParaModel> getTariffSchemePara(Long[] id);
	public BookingDetailModel getBookingDetailModel(long id);
	public List<CorporateTaxDetModel> getAllTaxes(long corporateId, String invoiceDate);
	
	public InvoiceModel saveInvoice(InvoiceModel invoiceModel);
	public List<InvoiceModel> getInvoice(String invoiceNo,long lCorpId);
	public List<MasterModel> getAllInvoiceNo();
	public List<InvoiceModel> getInvoiceList( String[] sCriteriaList, String[] sValueList);
	public CoverLetterModel saveCoverLetter(CoverLetterModel coverLetterModel);
	
	public CoverLetterModel getCoverLetter(Long coverLetterId);
	public List<DutySlipModel> getDutySlipList( String[] sCriteriaList, String[] sValueList);
	
	public String savePaymentTransaction(TransactionDetailModel transactionDetailModel);
}
