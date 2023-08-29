package com.billing.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.billing.dao.BillingDao;
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

@Service("billingService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class BillingServiceImpl implements BillingService {

	@Autowired
	private BillingDao  billingDao;

	public BillingServiceImpl() {

	}

	@Override
	public List<DutySlipModel> list( String jobType, String[] sCriteriaList, String[] sValueList){
		return billingDao.list(jobType,sCriteriaList,sValueList);
	}
	@Override
	public List<DutySlipModel> getInvoice( String[] sCriteriaList, String[] sValueList){
		return billingDao.getInvoice(   sCriteriaList,  sValueList);
	}
	
	@Override
	public List<AutorizedUserModel> getBookedBy() {
		return billingDao.getBookedBy();
	}
	
	@Override
	public List<GeneralMasterModel> getCarMasterId(long id) {
		return billingDao.getCarMasterId(id);
	}
	@Override
	public List<CorporateTariffDetModel> getTariffValue(long tId){
		return billingDao.getTariffValue(tId);
	}
	/*@Override
	public List<DutySlipModel> getDutySlipCount(long bookingDetailId){
		return billingDao.getDutySlipCount(bookingDetailId);
	}*/
	@Override
	public List <DutySlipModel> getds(String invoiceNo, Long lCorpId){
		return billingDao.getds(invoiceNo,lCorpId);
	}
	
	@Override
	public List<CorporateTariffDetModel> getTariffValue(long tId,long corporateId,String sDate, long branchId){
		return billingDao.getTariffValue(tId,corporateId,sDate, branchId);
	}

	@Override
	public BookingMasterModel getDutySlipClosingBookingMasterDetail(long dutySlipDetailsId){
		return billingDao.getDutySlipClosingBookingMasterDetail(dutySlipDetailsId);
	}
	
	@Override
	public List<BookingMasterModel> getUsedBy() {
		return billingDao.getUsedBy();
	}
	
	@Override
	public List<UserModel> getClosedBy() {
		return billingDao.getClosedBy();
	}
	
	@Override
	public List<DutySlipModel> getInvoiceNo(){
		return billingDao.getInvoiceNo();
	}
	
	/*sahil*/
	
	@Override
	public List<GeneralMasterModel> list(String corporateId){
		return billingDao.list(corporateId);
	}
	@Override
	public List<CarDetailModel> getCarRegistrationNo(){
	return billingDao.getCarRegistrationNo();
	}
	@Override
	public List<BookingMasterModel> getBookedFor(){
		return billingDao.getBookedFor();
	}
	@Override
	public List<DutySlipModel> getDutySlipClosingDetail(String dutySlipRertrive) {
		return billingDao.getDutySlipClosingDetail(dutySlipRertrive) ;	
	}
	@Override
	public List<BookingMasterModel> getDutySlipClosingBookingMasterDetail(String dutySlipRertrive) {
		return billingDao.getDutySlipClosingBookingMasterDetail(dutySlipRertrive);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(HistoryDutySlipModel historyDutySlipModel) {
		return billingDao.save(historyDutySlipModel);
	}
	@Override
	public CarAllocationModel getCarOwnerType(Long carDetailId){
		return billingDao.getCarOwnerType(carDetailId);
	}
	
	@Override
	public  CorporateModel getCompany(String corporateId){
		return billingDao.getCompany(corporateId);
	}
	/*@Override
	public BookingMasterModel save(BookingMasterModel bookingMasterModel){
		return billingDao.save(bookingMasterModel);
	}*/
	@Override
	 public List<CorporateTariffDetModel> getTariffValues(long tId,long corporateId) {
			return billingDao.getTariffValues( tId, corporateId);
	 }
	@Override
	public  BookingDetailModel getBookingDetailId(long id){
			return billingDao.getBookingDetailId( id);
		}
	@Override
	public DutySlipModel save(DutySlipModel dutySlipModel,String sBookingNo){
		return billingDao.save(dutySlipModel, sBookingNo);
	}
	@Override
	public List<CarDetailModel> getAlocationCarRegistrationNo() {
		return billingDao.getAlocationCarRegistrationNo();
	}
	@Override
	public CarAllocationModel getCarOwner(String id){
		return billingDao.getCarOwner(id);
	}
	@Override
	public CarAllocationModel getSearchCarReg(long id){
		return billingDao.getSearchCarReg(id);
	}
	
	@Override
	public TariffSchemeParaModel getTariffSchemePara(Long id) {
		return billingDao.getTariffSchemePara(id);
	}
	
	@Override
	public List<CorporateTaxDetModel> getTaxs(long corporateId, String invoiceDate){
		return billingDao.getTaxs(corporateId,invoiceDate);
	}
	
	@Override
	public DutySlipModel getCombineFare(String invoiceNo){
		return billingDao.getCombineFare(invoiceNo);
	}
	
	@Override
	public List<CarAllocationModel> getAlocationCarNo(Long modelId){
		return billingDao.getAlocationCarNo(modelId);
	}
	
	@Override
	public List<TariffSchemeParaModel> getTariffSchemePara(Long[] id) {
		return billingDao.getTariffSchemePara(id);
	}
	
	@Override
	public  BookingDetailModel getBookingDetailModel(long id){
		return billingDao.getBookingDetailModel(id);
		
	}
	@Override
	public List<CorporateTaxDetModel> getAllTaxes(long corporateId, String invoiceDate){
		return billingDao.getAllTaxes(corporateId,invoiceDate);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public InvoiceModel saveInvoice(InvoiceModel invoiceModel) {
		return billingDao.saveInvoice(invoiceModel);
	}

	@Override
	public List<InvoiceModel> getInvoice(String invoiceNo,long lCorpId) {
		return billingDao.getInvoice(invoiceNo,lCorpId);
	}

	@Override
	public List<MasterModel> getAllInvoiceNo() {
		return billingDao.getAllInvoiceNo();
	}

	@Override
	public List<InvoiceModel> getInvoiceList(String[] sCriteriaList, String[] sValueList) {
		return billingDao.getInvoiceList(sCriteriaList, sValueList);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public CoverLetterModel saveCoverLetter(CoverLetterModel coverLetterModel) {
		return billingDao.saveCoverLetter(coverLetterModel);
	}

	@Override
	public CoverLetterModel getCoverLetter(Long coverLetterId) {
		return billingDao.getCoverLetter(coverLetterId);
	}

	@Override
	public List<DutySlipModel> getDutySlipList(String[] sCriteriaList, String[] sValueList) {
		return billingDao.getDutySlipList(sCriteriaList,sValueList);
	}
	
	@Override
	public String savePaymentTransaction(TransactionDetailModel transactionDetailModel) {
		billingDao.savePaymentTransaction(transactionDetailModel);
		return null;
	}
}
