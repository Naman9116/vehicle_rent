package com.common.dao;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.common.model.AddressDetailModel;
import com.corporate.model.CorporateModel;
import com.master.model.CityMasterModel;
import com.master.model.CorporateTariffModel;
import com.master.model.DistrictMasterModel;
import com.master.model.GeneralMasterModel;
import com.master.model.MappingMasterModel;
import com.master.model.MasterModel;
import com.master.model.StateMasterModel;
import com.operation.model.CarDetailModel;
import com.user.model.UserModel;

@Repository("masterDataDao")
public class MasterDataDaoImpl implements MasterDataDao {

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<MasterModel> getMasterData(String masterCode) {
		List<MasterModel> masterModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MasterModel.class,"mm").
		setProjection(Projections.projectionList().
			add(Projections.property("mm.id"),"id").
			add(Projections.property("mm.name"),"name").
			add(Projections.property("mm.code"),"code")).setResultTransformer(Transformers.aliasToBean(MasterModel.class));
		criteria.addOrder(Order.asc("mm.name"));
		if(!masterCode.equalsIgnoreCase("All"))
			criteria.add(Restrictions.eq("mm.code", masterCode));
		masterModelList = (List<MasterModel>)criteria.list();
		return masterModelList;
	}	

	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralMasterModel> getGeneralMasterData_UsingCode(String masterCode) {
		List<GeneralMasterModel> generalMasterModelList = null;
		String[] sMasterCode = masterCode.split(":");
		masterCode = sMasterCode[0];
		String sSql = "";
		if(masterCode.equals("RoleWithUser")){
			sSql = "SELECT A.id as id, A.name as name, B.id as masterId, B.userName as remark FROM GeneralMasterModel A LEFT JOIN A.userModel B , MasterModel C WHERE C.code = 'Role' And C.id = A.masterModel.id";
		}else if(masterCode.equals("MenuWithModule")){
			sSql = "SELECT A.id as id, A.name as name, C.id as masterId, C.name as remark, (SELECT COUNT(*) FROM UserAccessModel Z WHERE Z.menuId.id = A.id and Z.userId.id = "+sMasterCode[1]+") as extraId, A.sortId As sortId FROM GeneralMasterModel A LEFT JOIN A.masterValuesModel B LEFT JOIN B.subMasterValuesModel C WHERE A.masterModel.id = (SELECT id FROM MasterModel WHERE code = 'MODU') ORDER BY A.sortId";
		}else if(masterCode.equals("LinkedBranchCompany")){
			sSql = "SELECT A.id as id, A.name as name FROM GeneralMasterModel A,MappingMasterModel B, MasterModel C WHERE A.masterModel.id = C.id And C.code = 'VBM' And A.id = B.subMasterValuesModel.id And B.masterValuesModel.id =" + sMasterCode[1] + " And '" + sMasterCode[2] +"' LIKE concat('%,',A.id,',%') And A.itemCode not in ('PAN')";
		}else if(masterCode.equals("TaxationList")){
			sSql ="SELECT A.name as name, B.taxPer as doubleValue, B.id as id, B.efDate as efDate FROM GeneralMasterModel A,TaxationModel B " +
				  " 	WHERE A.id = B.parentId.id And B.efDate = (SELECT MAX(C.efDate) FROM TaxationModel C, GeneralMasterModel D " +
				  " 		WHERE D.id = C.parentId.id And C.efDate <= current_date() And C.parentId = B.parentId) And (B.endDate >= current_date() OR B.endDate is null)";
		}else if(masterCode.equals("CORP")){
			String sSubQurey = "";
			if(sMasterCode.length > 2){
				if (sMasterCode[2].equals("Invoice") || sMasterCode[2].equals("CoverLetter")){
					sSubQurey = "And A.id in (SELECT B.corporateId.id "
							  + "		  	  FROM BookingMasterModel B, BookingDetailModel C, DutySlipModel D "
							  + "		  	  WHERE B.id = C.bookingMasterModel.id And"
							  + "					C.id = D.bookingDetailModel.id And"
							  + " 					D.dutySlipStatus = 'I') ";	
				}
			}
			
			sSql = "SELECT A.id as id, A.name as name "
				 + "FROM CorporateModel A "
				 + "WHERE A.status = 'Y' And (A.penIndia= 'Y' OR '" + sMasterCode[1] +"' LIKE concat('%,',str(A.branId),',%')) "
				 + sSubQurey
				 + "ORDER BY A.name";
		}else if(masterCode.equals("CORPETS")){
			sSql = "SELECT A.id as id, A.name as name "
					 + "FROM CorporateModel A "
					 + "WHERE A.status = 'Y' And (A.penIndia= 'Y' OR '" + sMasterCode[1] +"' LIKE concat('%,',str(A.branId),',%')) And"
					 + "	  A.id in (SELECT DISTINCT corporateId.id FROM LocationMasterModel) "
					 + "ORDER BY A.name";
		}else if(masterCode.equals("CORPALL")){
			sSql = "SELECT A.id as id, A.name as name FROM CorporateModel A WHERE A.status = 'Y' ORDER BY A.name";
		}else if(masterCode.equals("LinkedBranchCorp")){
			sSql = "SELECT A.id as id, A.name as name FROM GeneralMasterModel A,CorporateModel B, MasterModel C WHERE A.masterModel.id = C.id And C.code = 'VBM' And B.id =" + sMasterCode[1] + " And ((B.penIndia= 'Y' And '" + sMasterCode[2] +"' LIKE concat('%,',A.id,',%')) OR (B.penIndia= 'N' And A.id = B.branId) )";
		}else if(masterCode.equals("CarList")){
			sSql = "SELECT A.masterValuesModel.id as id, B.id As sortId, B.name as name FROM MappingMasterModel A, GeneralMasterModel B " +
				   "	WHERE A.masterValuesModel.id IN (SELECT C.id FROM GeneralMasterModel C, MasterModel D WHERE C.masterModel.id = D.id And D.code = 'VCT') And " +
				   "		  B.id = A.subMasterValuesModel.id " +
				   "ORDER BY 1"	      ;
		}else if (masterCode.endsWith("LinkedAuthoriser")){
			sSql = "SELECT A.id as id, A.name as name, A.entityContact.personalEmailId as remark FROM AutorizedUserModel A, CorporateModel B Where A.corporateId.id = B.id And B.id = "+ sMasterCode[1] +" And A.status = 'Y' and A.authTypeAdmin = 'Y'";
		}else if (masterCode.endsWith("LinkedClient")){
			sSql = "SELECT A.id as id, A.name as name,(SELECT concat(personalMobile,'#',personalEmailId) FROM ContactDetailModel WHERE id = A.entityContact.id ) as remark FROM AutorizedUserModel A, CorporateModel B Where A.corporateId.id = B.id And B.id = "+ sMasterCode[1] +" And A.status = 'Y'";
		}else if(masterCode.equals("VENDOR")){
			sSql = "SELECT A.id as id, A.name as name FROM VendorModel A WHERE A.status = 'Y' ORDER BY A.name";
		}else if(masterCode.equals("CHAUFFEUR")){
			sSql = "SELECT A.id as id, concat(A.id,' - ',A.name) as name FROM ChauffeurModel A WHERE A.id not in(SELECT ca.chauffeurId from CarAllocationModel ca WHERE ca.carStatus = 'Y') ORDER BY A.name";
		}else if(masterCode.equals("TariffCorp")){
			sSql = "SELECT A.id as id, A.name as name, A.sortId as sortId, A.masterModel As masterModel FROM GeneralMasterModel A, MasterModel B WHERE A.masterModel.id = B.id And B.code IN ('VTF','VTR') And EXISTS (SELECT COALESCE(C.tariffs,' ') FROM CorporateModel C where C.id = "+ sMasterCode[1] +" and concat(',',COALESCE(C.tariffs,' '),',') LIKE concat('%,',str(A.id),',%')) ORDER BY A.masterModel, A.sortId";
		}else if(masterCode.equals("TariffScheCorp")){
			sSql = "SELECT A.id as id, A.name as name, A.sortId as sortId, "
				 + "	  (SELECT D.masterValuesModel.id "
				 + "	   FROM MappingMasterModel D, MasterModel E, GeneralMasterModel F "
				 + "       WHERE F.masterModel.id = E.id And E.code IN ('VRT') And D.subMasterValuesModel = A.id And F.id = D.masterValuesModel) As masterId "
				 + "FROM GeneralMasterModel A, MasterModel B "
				 + "WHERE A.id IN (select case when H.name like '%Outstation%' then 260 else F.tariffId.id end "
				 + "			   from CorporateTariffDetModel F, CorporateTariffModel G, GeneralMasterModel H "
				 + "			   where H.id = F.tariffId.id And F.corporateTariffModel.id = G.id And G.corporateId.id = "+ sMasterCode[1] +" And G.branchId.id = "+ sMasterCode[2] +" And "
				 + "				     G.fuelHikeDate = (SELECT max(I.fuelHikeDate) "
				 + "									   FROM CorporateTariffModel I "
				 + "									   WHERE I.corporateId.id = G.corporateId.id And I.branchId.id = G.branchId.id )) And "
				 + "	  A.masterModel.id = B.id And B.code IN ('VTF')"
				 + "ORDER BY sortId";
		}else if(masterCode.equals("HubAsBranch")){
			sSql = "SELECT A.id as id, A.name as name FROM GeneralMasterModel A,MappingMasterModel B, MasterModel C WHERE A.masterModel.id = C.id And C.code = 'VOM' And A.id = B.subMasterValuesModel.id And B.masterValuesModel.id =" + sMasterCode[1] + " ORDER BY A.name";
		}else if(masterCode.equals("TerminalAsBranch")){
			sSql = "SELECT A.id as id, A.name as name FROM GeneralMasterModel A WHERE (SELECT concat(',', airportTerminals , ',') FROM RelatedInfoModel WHERE parentid = '"+sMasterCode[1]+"') LIKE concat('%,',str(A.id),',%') ORDER BY A.name";
		}else if(masterCode.equals("CarModelAsType")){
			sSql = "SELECT A.id as id, A.name as name FROM GeneralMasterModel A,MappingMasterModel B, MasterModel C WHERE A.masterModel.id = C.id And C.code = 'VMD' And A.id = B.subMasterValuesModel.id And B.masterValuesModel.id =" + sMasterCode[1] + " ORDER BY A.name";
		}else if(masterCode.equals("LinkedCompBranch")){
			sSql = "SELECT A.id as id, A.name as name FROM GeneralMasterModel A,MappingMasterModel B, MasterModel C WHERE A.masterModel.id = C.id And C.code = 'VCM' And A.id = B.masterValuesModel.id And B.subMasterValuesModel.id =" + sMasterCode[1] + " ORDER BY A.name" ;
		}else if(masterCode.equals("BranchAsCompany")){
			sSql = "SELECT A.id as id, A.name as name FROM GeneralMasterModel A,MappingMasterModel B, MasterModel C,CorporateModel D WHERE A.masterModel.id = C.id And C.code = 'VBM' And A.id = B.subMasterValuesModel.id And B.masterValuesModel.id =" + sMasterCode[1] +" And D.id =" + sMasterCode[2] + " And (D.branId = A.id OR D.penIndia = 'Y')  ORDER BY A.name";
		}else if(masterCode.equals("carModelCorp")){
			if(sMasterCode.length == 5){
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				try{
					Date dDate = dateFormat.parse(sMasterCode[4]);
					sMasterCode[4] = new SimpleDateFormat("yyyy-MM-dd").format(dDate);
				}catch(Exception e){}	
				sSql = 	"SELECT distinct D.id as id, D.name as name, C.masterValuesModel.id as sortId " +
						"FROM CorporateTariffModel A, CorporateTariffDetModel B, MappingMasterModel C, GeneralMasterModel D " +
						"WHERE A.id = B.corporateTariffModel.id And " +
						"      C.masterValuesModel.id = B.carCatId.id And " +
						"	   D.id = C.subMasterValuesModel.id And " +
						"	   A.corporateId.id = " + sMasterCode[1] + " And " + 
						"      A.branchId.id    = " + sMasterCode[2] + " And " +
						"      B.tariffId.id    = " + sMasterCode[3] + " And " +
						"      A.fuelHikeDate <= '" + sMasterCode[4] + "' " +
						"ORDER BY D.name";
			}else{
				sSql = 	"SELECT distinct D.id as id, D.name as name, C.masterValuesModel.id as sortId " +
						"FROM CorporateTariffModel A, CorporateTariffDetModel B, MappingMasterModel C, GeneralMasterModel D " +
						"WHERE A.id = B.corporateTariffModel.id And " +
						"      C.masterValuesModel.id = B.carCatId.id And " +
						"	   D.id = C.subMasterValuesModel.id And " +
						"	   A.corporateId.id = " + sMasterCode[1] + " ORDER BY D.name";
			}
		}else if(masterCode.equals("RentalCorp")){
			sSql = " SELECT E.id As id, E.name As name FROM GeneralMasterModel E, MasterModel F "
				 + " WHERE E.masterModel.id = F.id And F.code IN ('VRT') And "
				 + "       E.id IN ("
			  	 + "		SELECT D.masterValuesModel.id "
				 + "		FROM GeneralMasterModel A, MasterModel B, MappingMasterModel D "
				 + "		WHERE A.masterModel.id = B.id And B.code IN ('VTF') And "
				 + "              A.id IN (select case when I.name like '%Outstation%' then 260 else G.tariffId.id end "
				 + "					   from CorporateTariffDetModel G, CorporateTariffModel H, GeneralMasterModel I "
				 + "					   where I.id = G.tariffId.id And G.corporateTariffModel.id = H.id And H.corporateId.id = "+ sMasterCode[1] +" And H.branchId.id = "+ sMasterCode[2] +" And " 
				 + "						     H.fuelHikeDate = (SELECT max(J.fuelHikeDate) FROM CorporateTariffModel J WHERE J.corporateId.id = H.corporateId.id And J.branchId.id = H.branchId.id)) And "
				 + "	  		  D.subMasterValuesModel = A.id )";
		}else if(masterCode.equals("VBM-A")){
			sSql = "SELECT A.id as id, A.name as name, A.sortId as sortId, A.masterModel.id as masterId FROM GeneralMasterModel A, MasterModel B "
				 + "WHERE A.masterModel.id = B.id And B.code = 'VBM' And '" + sMasterCode[1] +"' LIKE concat('%,',str(A.id),',%') ORDER BY A.sortId,A.name ";
		}else if(masterCode.equals("CoverLetterList")){
			sSql = "select A.id as id, cast(A.id as string) as name from CoverLetterModel A, InvoiceModel B "
				 + "where B.status = 'Y' And "
				 + "      A.corporateId = '"+ sMasterCode[1] +"' And "
				 + "	  B.invoiceNo = '"+ sMasterCode[2] +"' And "
				 + "      B.invoiceDt between A.invFromDate And A.invToDate";
		}else{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GeneralMasterModel.class).
					setProjection(Projections.projectionList().
					add(Projections.property("id"),"id").
					add(Projections.property("name"),"name").
					add(Projections.property("sortId"),"sortId").
					add(Projections.property("masterModel.id"),"masterId")).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
			criteria.add(Subqueries.propertyIn("masterModel.id",
						DetachedCriteria.forClass(MasterModel.class).
						add(Restrictions.eq("code",masterCode)).setProjection(Projections.distinct(Projections.property("id")))));
			if(masterCode.equals("ZONE")){
				criteria.addOrder(Order.asc("name"));
			}else{
				criteria.addOrder(Order.asc("sortId"));
				criteria.addOrder(Order.asc("name"));
			}
			generalMasterModelList = (List<GeneralMasterModel>) criteria.list();
			List<GeneralMasterModel> generalMasterModelListRet = new ArrayList<GeneralMasterModel>();
			for(GeneralMasterModel generalMasterModel : generalMasterModelList){
				if(!(generalMasterModel.getName().equals("Super Company") || generalMasterModel.getName().equals("Super Branch")))
					generalMasterModelListRet.add(generalMasterModel);
			}
			generalMasterModelList = generalMasterModelListRet;
		}
		if(!sSql.equals("")){
			Query query=sessionFactory.getCurrentSession().createQuery(sSql).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
			generalMasterModelList = query.list();
		}
		return generalMasterModelList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserModel> getUserDara_UsingCode(String parentCode,String childCode) {
		List<UserModel> userModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserModel.class).
			setProjection(Projections.projectionList().
				add(Projections.property("id"),"id").
				add(Projections.property("userName"),"userName")).setResultTransformer(Transformers.aliasToBean(UserModel.class));
		criteria.setFetchMode("userType", FetchMode.JOIN).createAlias("userType", "ut").add(Restrictions.eq("ut.code", childCode));
		criteria.setFetchMode("ut.masterModel", FetchMode.JOIN).createAlias("ut.masterModel", "mm").add(Restrictions.eq("mm.code", parentCode));
		userModelList = (List<UserModel>)criteria.list();
		return userModelList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralMasterModel> getGeneralMasterData_UsingId(String masterId) {
		List<GeneralMasterModel> generalMasterModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GeneralMasterModel.class).
				setProjection(Projections.projectionList().
				add(Projections.property("id"),"id").
				add(Projections.property("name"),"name").
				add(Projections.property("masterModel.id"),"masterId")).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		criteria.add(Restrictions.eq("masterModel.id",Long.parseLong(masterId)));
		criteria.addOrder(Order.asc("name"));
		generalMasterModelList = (List<GeneralMasterModel>)criteria.list();
		return generalMasterModelList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralMasterModel> getGeneralMasterData_WithMapping(String parentId) {
		List<GeneralMasterModel> generalMasterModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GeneralMasterModel.class).
				setProjection(Projections.projectionList().
				add(Projections.property("id"),"id").
				add(Projections.property("name"),"name").
				add(Projections.property("code"),"code").
				add(Projections.property("masterModel.id"),"masterId")).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		criteria.add(Subqueries.propertyIn("id",
				DetachedCriteria.forClass(MappingMasterModel.class).
				add(Restrictions.eq("masterValuesModel.id",Long.parseLong(parentId))).setProjection(Projections.distinct(Projections.property("subMasterValuesModel.id")))));
		criteria.addOrder(Order.asc("name"));
		generalMasterModelList = (List<GeneralMasterModel>)criteria.list();
		return generalMasterModelList;
	}
   
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> getCarModel_usingCarType(Long carTypeId) {
		List<CarDetailModel> carDetailModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CarDetailModel.class).
			setProjection(Projections.projectionList().
			add(Projections.distinct(Projections.property("model")),"model")).add(Restrictions.eq("carType.id", carTypeId)).
			setResultTransformer(Transformers.aliasToBean(CarDetailModel.class));
		//criteria.addOrder(Order.desc("id"));
		carDetailModel = (List<CarDetailModel>)criteria.list();
		return carDetailModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserModel> userLogin(String userCode, String password) {
		List<UserModel> userModel = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserModel.class,"um").
		setProjection(Projections.projectionList().
			add(Projections.property("um.id"),"id").
			add(Projections.property("um.userName"),"userName").
			add(Projections.property("um.userFirstName"),"userFirstName").
			add(Projections.property("um.userLastName"),"userLastName").
			add(Projections.property("um.userMobile"),"userMobile").
			add(Projections.property("um.userRole"),"userRole").
			add(Projections.property("um.userStatus"),"userStatus").
			add(Projections.property("um.company"),"company").
			add(Projections.property("um.branch"),"branch").
			add(Projections.property("um.assignBranches"),"assignBranches").
			add(Projections.property("um.userName"),"userName")).setResultTransformer(Transformers.aliasToBean(UserModel.class));
		criteria.add(Restrictions.eq("um.userName", userCode));
		criteria.add(Restrictions.eq("um.password", password));
		userModel = (List<UserModel>)criteria.list();
		return userModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StateMasterModel> getStateMasterData() {
		List<StateMasterModel> stateMasterModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateMasterModel.class).
		setProjection(Projections.projectionList().
			add(Projections.property("id"),"id").
			add(Projections.property("name"),"name")).setResultTransformer(Transformers.aliasToBean(StateMasterModel.class));
		criteria.addOrder(Order.asc("name"));
		stateMasterModelList = (List<StateMasterModel>)criteria.list();
		return stateMasterModelList;
	}	

	@SuppressWarnings("unchecked")
	@Override
	public List<DistrictMasterModel> getStateDistMasterData_usingPincode(Long pincode) {
		List<DistrictMasterModel> districtMasterModelList = null;
		String hqlQuery=" SELECT DISTINCT dm.id as id,dm.state as state "+ 
						" FROM StateMasterModel sm,DistrictMasterModel dm,CityMasterModel cm "+ 
						" WHERE dm.state=sm.id and cm.district=dm.id ";
		if(pincode!=null && pincode!=0L)
			hqlQuery=hqlQuery+"AND cm.pincode=:pincode";
		Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
		query.setResultTransformer(Transformers.aliasToBean(DistrictMasterModel.class));
		if(pincode!=null && pincode!=0L)
			query.setLong("pincode", pincode);
		districtMasterModelList=query.list();	
		return districtMasterModelList;
	}	
	@SuppressWarnings("unchecked")
	@Override
	public List<DistrictMasterModel> getDistrictMasterData(Long stateId,Long distId) {
		List<DistrictMasterModel> districtMasterModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DistrictMasterModel.class,"dmm").
		setProjection(Projections.projectionList().
			add(Projections.property("dmm.id"),"id").
			add(Projections.property("dmm.name"),"name")).setResultTransformer(Transformers.aliasToBean(DistrictMasterModel.class));
			criteria.add(Restrictions.eq("dmm.state", stateId));
		if(distId!=null && distId!=0L)
			criteria.add(Restrictions.eq("dmm.id", distId));
		criteria.addOrder(Order.asc("dmm.name"));
		districtMasterModelList = (List<DistrictMasterModel>)criteria.list();
		return districtMasterModelList;
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CityMasterModel> getCityMasterData(Long districtId,Long pincode) {
		List<CityMasterModel> cityMasterModelList = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CityMasterModel.class,"cdm").
		setProjection(Projections.projectionList().
			add(Projections.property("cdm.id"),"id").
			add(Projections.property("cdm.name"),"name")).setResultTransformer(Transformers.aliasToBean(CityMasterModel.class));
			criteria.add(Restrictions.eq("cdm.district", districtId));
		if(pincode!=null && pincode!=0L)
			criteria.add(Restrictions.eq("cdm.pincode", pincode));
		criteria.addOrder(Order.asc("cdm.name"));
		cityMasterModelList = (List<CityMasterModel>)criteria.list();
		return cityMasterModelList;
	}

	@Override
	public List<CorporateTariffModel> selectTariffOnTheBasisOfCustomer(String customerId,String bookingDate) {
		List<CorporateTariffModel> parameterMasterModels1=null;
		List<CorporateTariffModel> parameterMasterModels2=null;
		List<CorporateTariffModel> parameterMasterModels=new ArrayList<CorporateTariffModel>();
		String hql1 = " SELECT tpm1.id AS id, (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=tpm1.tariff.id ) AS tariffName "+ 
					  " FROM CorporateTariffModel tpm1 WHERE tpm1.customerNameId="+customerId+"								"+
					  " AND tpm1.effectiveDate =(SELECT MAX(tpm2.effectiveDate) FROM CorporateTariffModel tpm2 	"+
					  " WHERE tpm2.effectiveDate<='"+bookingDate+"' AND tpm2.tariff.id=tpm1.tariff.id AND tpm1.customerNameId=tpm2.customerNameId)"; 
		
		String hql2 = " SELECT tpm3.id AS id, (SELECT gmm.name FROM GeneralMasterModel gmm WHERE gmm.id=tpm3.tariff.id ) AS tariffName"+
					  " FROM CorporateTariffModel  tpm3	"+
					  " WHERE tpm3.tariff.id NOT IN (SELECT tpm4.tariff.id FROM CorporateTariffModel tpm4 WHERE tpm4.customerNameId="+customerId+") 		"+
					  " AND tpm3.customerNameId=0 AND tpm3.effectiveDate =(SELECT MAX(tpm5.effectiveDate) FROM CorporateTariffModel tpm5 	"+
					  " WHERE tpm5.effectiveDate<='"+bookingDate+"' AND tpm5.tariff.id=tpm3.tariff.id AND tpm3.customerNameId=tpm5.customerNameId) 	";
		
		Query query1=sessionFactory.getCurrentSession().createQuery(hql1).setResultTransformer(Transformers.aliasToBean(CorporateTariffModel.class));
		Query query2=sessionFactory.getCurrentSession().createQuery(hql2).setResultTransformer(Transformers.aliasToBean(CorporateTariffModel.class));
		
		parameterMasterModels1=query1.list();
		parameterMasterModels2=query2.list();
		parameterMasterModels1.addAll(parameterMasterModels2);
		HashSet uniqueCollection = new HashSet(parameterMasterModels1 );
		parameterMasterModels.addAll(uniqueCollection);
		return parameterMasterModels;
	}

	@Override
	public List<CorporateTariffModel> selectTariffOnTheBasisOfDutySlipId(String dutySlipId) {
		List<CorporateTariffModel> parameterMasterModels=new ArrayList<CorporateTariffModel>();
		String sql= " SELECT bmm.customerName.id,bmm.bookingDate "+
					" FROM DutySlipModel dsm,BookingMasterModel bmm "+
					" WHERE dsm.id="+dutySlipId+" AND bmm.id=dsm.bookingMasterModel.id ";  
		Query query = sessionFactory.getCurrentSession().createQuery(sql);
		List list=query.list();
		Object object[]=(Object[])list.get(0);
		parameterMasterModels=selectTariffOnTheBasisOfCustomer(object[0].toString(),object[1].toString());
		return parameterMasterModels;
	}	
	

	@Override
	public List<GeneralMasterModel> getBranchDataUsingCompany(String companyId) {
		List<GeneralMasterModel> generalMasterModels=null;
		String sql= " SELECT gmm.id as id, gmm.name as name FROM GeneralMasterModel gmm, MappingMasterModel mm "+ 
					" WHERE gmm.id=mm.subMasterValuesModel.id "+ 
					" AND mm.masterValuesModel.id="+companyId+" ";
		Query query = sessionFactory.getCurrentSession().createQuery(sql).setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		generalMasterModels=query.list();
		return generalMasterModels;
	}

	@Override
	public List<GeneralMasterModel> getGeneralMasterData_Menu(String userRole) {
		List<GeneralMasterModel> generalMasterModelList = null;
		Query query=sessionFactory.getCurrentSession().createQuery("SELECT B.id as id, B.name as name, B.remark as remark, C.masterValuesModel.id as masterId "
				+ " FROM MasterModel A,GeneralMasterModel B, MappingMasterModel C "
				+ " WHERE C.subMasterValuesModel.id = B.id AND B.masterModel.id = A.id AND A.code = 'MENU' AND B.id IN ( SELECT D.menuId.id FROM UserAccessModel D WHERE D.userId.id = '"+userRole+"') ORDER BY B.sortId").setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		generalMasterModelList = query.list();
		return generalMasterModelList;
	}

	@Override
	public Map<String,String> getUserAccess(String UserId, String sMenuPage) {
		List<GeneralMasterModel> generalMasterModelList = null;
		Map<String,String> userAccessMap = new HashMap<String,String>();
		String sAccess="", sRemark = "", sRemarkPrev="";
		Query query=sessionFactory.getCurrentSession().createQuery("SELECT B.remark as remark, A.menuAccess as name FROM UserAccessModel A, GeneralMasterModel B " +
						" WHERE A.menuId.id = B.id And (B.remark like '"+sMenuPage+"%' OR '"+sMenuPage+"' ='ALL') And length(COALESCE(B.remark,'')) > 0 And " + 
						"		A.userId = " + UserId + " ORDER By 1").setResultTransformer(Transformers.aliasToBean(GeneralMasterModel.class));
		generalMasterModelList = query.list();
		
		for(int iRow=0; iRow < generalMasterModelList.size(); iRow++){
			sRemark = generalMasterModelList.get(iRow).getRemark();
			if(sRemark.equals(sRemarkPrev) || iRow==0){
				sAccess += generalMasterModelList.get(iRow).getName();
			}else{
				try{
					sRemarkPrev = URLEncoder.encode(sRemarkPrev,"UTF-8");
				}catch(Exception e){}	
				userAccessMap.put(sRemarkPrev, sAccess);
				sAccess = generalMasterModelList.get(iRow).getName();
			}
			sRemarkPrev = sRemark;
		}
		userAccessMap.put(sRemark, sAccess);

		return userAccessMap;
	}	
	
	@Override
	public AddressDetailModel getAddressDataFromID(Long addressID, String sFor) {
		AddressDetailModel addressDetailModel = null;
		if(sFor.equals("Corporate")){
			CorporateModel corporateModel = (CorporateModel) sessionFactory.getCurrentSession().get(CorporateModel.class, addressID);
			addressDetailModel = (AddressDetailModel) sessionFactory.getCurrentSession().get(AddressDetailModel.class, corporateModel.getEntityAddress().getId());
		}
		return addressDetailModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public GeneralMasterModel getVehicleCategoryForVehicleModel(Long carModelId){
		List<GeneralMasterModel> vehicleTypeList = null;
		vehicleTypeList = sessionFactory.getCurrentSession().createQuery("SELECT C FROM GeneralMasterModel A, MappingMasterModel B, GeneralMasterModel C " +
										" WHERE A.id = B.subMasterValuesModel.id AND C.id = B.masterValuesModel.id AND A.masterModel.id = 2 AND C.masterModel.id = 7 AND A.id ='"+carModelId+"'").list();
		if(vehicleTypeList !=null && vehicleTypeList.size()>0)
			return vehicleTypeList.get(0);
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> getCarRegistrationNumberList() {
		List<CarDetailModel> carDetailModels = null;
		carDetailModels = sessionFactory.getCurrentSession().
				createQuery("SELECT cd.registrationNo as registrationNo FROM CarDetailModel cd WHERE cd.registrationNo not in (SELECT ca.carDetailModelId FROM CarAllocationModel ca WHERE cd.registrationNo = ca.carDetailModelId and ca.carStatus = 'Y')")
				.setResultTransformer(Transformers.aliasToBean(CarDetailModel.class)).list();
		return carDetailModels;
	}
	
	@SuppressWarnings("unchecked")
	public List<CarDetailModel> getCarRegistrationNoList(String sModelId) {
		List<CarDetailModel> carDetailModels = null;
		String sSql = "";
		if(!sModelId.equals("")){
			sSql = "SELECT cd.registrationNo as registrationNo "
					+ "	 FROM CarDetailModel cd, CarAllocationModel ca "
					+ "  WHERE cd.registrationNo  =  ca.carDetailModelId and ca.carStatus = 'Y' And "
					+ " 	(SELECT B.sortId FROM MappingMasterModel A, GeneralMasterModel B, MasterModel C "
					+ "		   	WHERE A.subMasterValuesModel.id = cd.model.id And A.masterValuesModel.id = B.id And C.code='VCT' and B.masterModel.id = C.id) >= "
					+ "		(SELECT B.sortId FROM MappingMasterModel A, GeneralMasterModel B, MasterModel C " 
					+ "   		WHERE A.subMasterValuesModel.id = '"+sModelId+"' And A.masterValuesModel.id = B.id And C.code='VCT' and B.masterModel.id = C.id)";
		}else{
			sSql = "SELECT cd.registrationNo as registrationNo "
				+  "FROM CarDetailModel cd "
				+  "WHERE cd.registrationNo  in (SELECT ca.carDetailModelId "
				+ "								 FROM CarAllocationModel ca "
				+ "								 WHERE cd.registrationNo = ca.carDetailModelId and ca.carStatus = 'Y')";
		}		
		carDetailModels = sessionFactory.getCurrentSession().createQuery(sSql)
				.setResultTransformer(Transformers.aliasToBean(CarDetailModel.class)).list();
		carDetailModels.addAll(sessionFactory.getCurrentSession().createQuery(""
				+ "SELECT cd.registrationNo as registrationNo FROM CarDetailModel cd where cd.ownType='A' And cd.status='Y'")
				.setResultTransformer(Transformers.aliasToBean(CarDetailModel.class)).list());
		return carDetailModels;
	}
}
