package com.master.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.corporate.model.CorporateModel;
import com.master.model.CorporateTaxDetModel;
import com.master.model.TaxationModel;

@Repository("taxationMasterDao")
public class TaxationMasterDaoImpl implements TaxationMasterDao{
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<TaxationModel> list() {
		List<TaxationModel> taxationModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(TaxationModel.class);
		taxationModel = (List<TaxationModel>)criteria.list();
		return taxationModel;
	}
	
	public TaxationModel formFillForEdit(Long formFillForEditId) {
		TaxationModel taxationModel=(TaxationModel) sessionFactory.getCurrentSession().get(TaxationModel.class, new Long(formFillForEditId));
		return taxationModel;
	}
	
	public String save(TaxationModel taxationModel) {
		sessionFactory.getCurrentSession().save(taxationModel);
		return "saveSuccess";
	}
	
	public String update(TaxationModel taxationModel) {
		sessionFactory.getCurrentSession().update(taxationModel);
		return "updateSuccess";
	}

	public String delete(Long idForDelete) {
		TaxationModel taxationModel =(TaxationModel) sessionFactory.getCurrentSession().get(TaxationModel.class, new Long(idForDelete));
		sessionFactory.getCurrentSession().delete(taxationModel);
		return "deleteSuccess";
	}
	
	@SuppressWarnings("unchecked")
	public String saveCorpTaxDetail(CorporateTaxDetModel corporateTaxDetModel) {
		String sInsDate = dateFormat.format(corporateTaxDetModel.getInsDate());
		@SuppressWarnings("unused")
		List<CorporateModel> corporateModel=null;
		String ssSql = "SELECT id as id  FROM CorporateModel WHERE id  NOT IN"
				+ "(SELECT A.corporateModelId FROM CorporateTaxDetModel A WHERE A.insDate ='"+sInsDate+"' AND A.taxationModelId ='"+corporateTaxDetModel.getTaxationModelId()+"') ";
	
		corporateModel= sessionFactory.getCurrentSession().createQuery(ssSql).setResultTransformer(Transformers.aliasToBean(CorporateModel.class)).list();
		for(int i=0;i<corporateModel.size();i++){
			CorporateTaxDetModel corporateTaxDetTemp = new CorporateTaxDetModel();
			corporateTaxDetTemp.setCorporateModelId(corporateModel.get(i).getId());
			corporateTaxDetTemp.setInsDate(corporateTaxDetModel.getInsDate());
			corporateTaxDetTemp.setTaxationModelId(corporateTaxDetModel.getTaxationModelId());
			corporateTaxDetTemp.setTaxVal(corporateTaxDetModel.getTaxVal());
			corporateTaxDetTemp.setUserId(corporateTaxDetModel.getUserId());
			sessionFactory.getCurrentSession().save(corporateTaxDetTemp);
		}
/*		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
*/		return "SUCCESS" ;
	}
	

	public String updateCorpTaxDetail(CorporateTaxDetModel corporateTaxDetModel) {
		String sInsDate = dateFormat.format(corporateTaxDetModel.getInsDate());
		String sSql = "UPDATE CorporateTaxDetModel SET VALUE='"+corporateTaxDetModel.getTaxVal()+"' WHERE insDate='"+sInsDate+"' AND taxationModelId='"+corporateTaxDetModel.getTaxationModelId()+"'  ";
		 sessionFactory.getCurrentSession().createQuery(sSql);
		return "Success" ;
	}
}
