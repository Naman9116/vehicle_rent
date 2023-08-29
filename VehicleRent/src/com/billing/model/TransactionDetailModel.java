package com.billing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name="transactionDetail")
public class TransactionDetailModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id=null;
	
	@Column(name="userId",updatable=true,nullable=true,length=16,unique=false)
	private Long userId=null;
	
	@Column(name="order_id",updatable=true,nullable=false,length=50,unique=false)
	private String order_id=null;

	@Column(name="tracking_id",updatable=true,nullable=true,length=100,unique=false)
	private String tracking_id=null;

	@Column(name="bank_ref_no",updatable=true,nullable=true,length=100,unique=false)
	private String bank_ref_no=null;

	@Column(name="order_status",updatable=true,nullable=false,length=50,unique=false)
	private String order_status=null;

	@Column(name="failure_message",updatable=true,nullable=true,length=500,unique=false)
	private String failure_message=null;

	@Column(name="payment_mode",updatable=true,nullable=true,length=100,unique=false)
	private String payment_mode=null;

	@Column(name="card_name",updatable=true,nullable=true,length=100,unique=false)
	private String card_name=null;
	
	@Column(name="status_code",updatable=true,nullable=true,length=30,unique=false)
	private String status_code=null;
	
	@Column(name="status_message",updatable=true,nullable=true,length=30,unique=false)
	private String status_message=null;
	
	@Column(name="currency",updatable=true,nullable=true,length=10,unique=false)
	private String currency=null;
	
	@Column(name="amount",updatable=true,nullable=false,length=30,unique=false)
	private String amount=null;
	
	@Column(name="billing_name",updatable=true,nullable=true,length=80,unique=false)
	private String billing_name=null;
	
	@Column(name="billing_address",updatable=true,nullable=true,length=250,unique=false)
	private String billing_address=null;
	
	@Column(name="billing_city",updatable=true,nullable=true,length=50,unique=false)
	private String billing_city=null;
	
	@Column(name="billing_state",updatable=true,nullable=true,length=50,unique=false)
	private String billing_state=null;
	
	@Column(name="billing_zip",updatable=true,nullable=true,length=6,unique=false)
	private String billing_zip=null;
	
	@Column(name="billing_country",updatable=true,nullable=true,length=50,unique=false)
	private String billing_country=null;
	
	@Column(name="billing_tel",updatable=true,nullable=true,length=15,unique=false)
	private String billing_tel=null;
	
	@Column(name="billing_email",updatable=true,nullable=true,length=100,unique=false)
	private String billing_email=null;
	
	/*@Column(name="delivery_name",updatable=true,nullable=true,length=50,unique=false)
	private String delivery_name=null;
	@Column(name="delivery_address",updatable=true,nullable=true,length=250,unique=false)
	private String delivery_address=null;
	@Column(name="delivery_city",updatable=true,nullable=true,length=50,unique=false)
	private String delivery_city=null;
	@Column(name="delivery_state",updatable=true,nullable=true,length=50,unique=false)
	private String delivery_state=null;
	@Column(name="delivery_zip",updatable=true,nullable=true,length=6,unique=false)
	private String delivery_zip=null;
	@Column(name="delivery_country",updatable=true,nullable=true,length=50,unique=false)
	private String delivery_country=null;
	@Column(name="delivery_tel",updatable=true,nullable=true,length=15,unique=false)
	private String delivery_tel=null;*/
	
	/*@Column(name="merchant_param1",updatable=true,nullable=true,length=50,unique=false)
	private String merchant_param1=null;
	@Column(name="merchant_param2",updatable=true,nullable=true,length=50,unique=false)
	private String merchant_param2=null;
	@Column(name="merchant_param3",updatable=true,nullable=true,length=50,unique=false)
	private String merchant_param3=null;
	@Column(name="merchant_param4",updatable=true,nullable=true,length=50,unique=false)
	private String merchant_param4=null;
	@Column(name="merchant_param5",updatable=true,nullable=true,length=50,unique=false)
	private String merchant_param5=null;*/
	
	@Column(name="vault",updatable=true,nullable=true,length=2,unique=false)
	private String vault=null;
	
	@Column(name="offer_type",updatable=true,nullable=true,length=50,unique=false)
	private String offer_type=null;
	
	@Column(name="offer_code",updatable=true,nullable=true,length=50,unique=false)
	private String offer_code=null;
	
	@Column(name="discount_value",updatable=true,nullable=true,length=30,unique=false)
	private String discount_value=null;
	
	@Column(name="mer_amount",updatable=true,nullable=true,length=30,unique=false)
	private String mer_amount=null;
	
	@Column(name="eci_value",updatable=true,nullable=true,length=30,unique=false)
	private String eci_value;
	
	@Column(name="retry",updatable=true,nullable=true,length=1,unique=false)
	private String retry=null;
	
	@Column(name="response_code",updatable=true,nullable=true,length=5,unique=false)
	private String response_code=null;
	
	@Column(name="billing_notes",updatable=true,nullable=true,length=100,unique=false)
	private String billing_notes=null;
	
	@Column(name="trans_date",updatable=true,nullable=true,unique=false)
	private Date trans_date;
	
	@Column(name="bin_country",updatable=true,nullable=true,length=5,unique=false)
	private String bin_country=null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getTracking_id() {
		return tracking_id;
	}

	public void setTracking_id(String tracking_id) {
		this.tracking_id = tracking_id;
	}

	public String getBank_ref_no() {
		return bank_ref_no;
	}

	public void setBank_ref_no(String bank_ref_no) {
		this.bank_ref_no = bank_ref_no;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getFailure_message() {
		return failure_message;
	}

	public void setFailure_message(String failure_message) {
		this.failure_message = failure_message;
	}

	public String getPayment_mode() {
		return payment_mode;
	}

	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}

	public String getCard_name() {
		return card_name;
	}

	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getStatus_message() {
		return status_message;
	}

	public void setStatus_message(String status_message) {
		this.status_message = status_message;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBilling_name() {
		return billing_name;
	}

	public void setBilling_name(String billing_name) {
		this.billing_name = billing_name;
	}

	public String getBilling_address() {
		return billing_address;
	}

	public void setBilling_address(String billing_address) {
		this.billing_address = billing_address;
	}

	public String getBilling_city() {
		return billing_city;
	}

	public void setBilling_city(String billing_city) {
		this.billing_city = billing_city;
	}

	public String getBilling_state() {
		return billing_state;
	}

	public void setBilling_state(String billing_state) {
		this.billing_state = billing_state;
	}

	public String getBilling_zip() {
		return billing_zip;
	}

	public void setBilling_zip(String billing_zip) {
		this.billing_zip = billing_zip;
	}

	public String getBilling_country() {
		return billing_country;
	}

	public void setBilling_country(String billing_country) {
		this.billing_country = billing_country;
	}

	public String getBilling_tel() {
		return billing_tel;
	}

	public void setBilling_tel(String billing_tel) {
		this.billing_tel = billing_tel;
	}

	public String getBilling_email() {
		return billing_email;
	}

	public void setBilling_email(String billing_email) {
		this.billing_email = billing_email;
	}

	public String getVault() {
		return vault;
	}

	public void setVault(String vault) {
		this.vault = vault;
	}

	public String getOffer_type() {
		return offer_type;
	}

	public void setOffer_type(String offer_type) {
		this.offer_type = offer_type;
	}

	public String getOffer_code() {
		return offer_code;
	}

	public void setOffer_code(String offer_code) {
		this.offer_code = offer_code;
	}

	public String getDiscount_value() {
		return discount_value;
	}

	public void setDiscount_value(String discount_value) {
		this.discount_value = discount_value;
	}

	public String getMer_amount() {
		return mer_amount;
	}

	public void setMer_amount(String mer_amount) {
		this.mer_amount = mer_amount;
	}

	public String getEci_value() {
		return eci_value;
	}

	public void setEci_value(String eci_value) {
		this.eci_value = eci_value;
	}

	public String getRetry() {
		return retry;
	}

	public void setRetry(String retry) {
		this.retry = retry;
	}

	public String getResponse_code() {
		return response_code;
	}

	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}

	public String getBilling_notes() {
		return billing_notes;
	}

	public void setBilling_notes(String billing_notes) {
		this.billing_notes = billing_notes;
	}

	public Date getTrans_date() {
		return trans_date;
	}

	public void setTrans_date(Date trans_date) {
		this.trans_date = trans_date;
	}

	public String getBin_country() {
		return bin_country;
	}

	public void setBin_country(String bin_country) {
		this.bin_country = bin_country;
	}
}
