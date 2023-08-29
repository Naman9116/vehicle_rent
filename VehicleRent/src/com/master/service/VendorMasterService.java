package com.master.service;

import java.util.List;

import com.master.model.VendorModel;

public interface VendorMasterService {
	public List<VendorModel> list() ;
	public VendorModel formFillForEdit(Long formFillForEditId);
	public String save(VendorModel vendorModel);
	public String update(VendorModel vendorModel);
	public String delete(Long idForDelete) ;
}
