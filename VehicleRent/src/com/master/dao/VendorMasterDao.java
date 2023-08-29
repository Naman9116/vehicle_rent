package com.master.dao;

import java.util.List;

import com.master.model.TaxationModel;
import com.master.model.VendorModel;

public interface VendorMasterDao {
	public List<VendorModel> list() ;
	public VendorModel formFillForEdit(Long formFillForEditId);
	public String save(VendorModel vendorModel);
	public String update(VendorModel vendorModel);
	public String delete(Long idForDelete) ;
}
