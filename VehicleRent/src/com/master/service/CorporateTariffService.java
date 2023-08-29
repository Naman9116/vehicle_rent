package com.master.service;

import java.util.List;

import com.master.model.CorporateTariffModel;

public interface CorporateTariffService {
	public List<CorporateTariffModel> list() ;
	public List<CorporateTariffModel> listMasterWise(String searchCriteria) ;
	public String save(CorporateTariffModel tariffParameterMasterModel, String controlsWithValue);
	public CorporateTariffModel formFillForEdit(Long formFillForEditId);
	public String delete(Long idForDelete) ;
	public String update(CorporateTariffModel corporateTariffModel,String controlsWithValue) ;
	public List<CorporateTariffModel> getCommonTariffParameter_TariffParameterMaster(CorporateTariffModel corporateTariffModel);
	public List<CorporateTariffModel >list(String corporateId,String branchID);
	public List<CorporateTariffModel >getCorporateTariff(Long corporateId);
	public List<CorporateTariffModel >getCorporateFuelHikeDate(Long corporateId, Long branchId);
	public List<CorporateTariffModel >listAsPerDate(Long id);
}
