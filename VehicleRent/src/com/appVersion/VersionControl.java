package com.appVersion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.context.WebApplicationContext;

import com.master.model.GeneralMasterModel;
import com.master.model.MasterModel;
import com.user.model.UserModel;

public class VersionControl {

	private VersionControlService versionControlService=null;
	
	public void syncVersion(WebApplicationContext webApplicationContext){
		versionControlService = (VersionControlService) webApplicationContext.getBean("versionControlService");
		Double appVersion= versionControlService.getAppVersion();
		appVersion=appVersion==null?0.00:appVersion;
		List<Object>versionControlDataList=new ArrayList<Object>();
		Long parentId=null;
		
		if(appVersion==0.0){
			versionControlDataList.add(new UserModel("","Administrator","ntplupl","Male"));
			versionControlDataList.add(new MasterModel("Make","VMK","Vehicle Manufacturer Brand Name",null));
			versionControlService.save(versionControlDataList);
			parentId=versionControlService.getMasterModelId_UsingCode("VMK");
			versionControlDataList.clear();
			
			versionControlDataList.add(new MasterModel("Model","VMD","Vehicle Model by Manufacturer",parentId));
			versionControlService.save(versionControlDataList);
			parentId=versionControlService.getMasterModelId_UsingCode("VMD");
			versionControlDataList.clear();
			
			versionControlDataList.add(new MasterModel("Body Style","VBS","Vehicle Design Body Style",parentId));
			versionControlDataList.add(new MasterModel("Varient","VVT","Varient in Model",parentId));
			versionControlDataList.add(new MasterModel("Type","VTP","Vehicle Capacity or Type",parentId));
			versionControlDataList.add(new MasterModel("Body Color","VBC","Vehicle Body Color",null));
			versionControlDataList.add(new MasterModel("Car Category","VCT","Vehicle Car Type",null));
			
			versionControlDataList.add(new MasterModel("Tariff","VTF","Vehicle Tariff Master",null));
			versionControlDataList.add(new MasterModel("Rental Type","VRT","Vehicle Rental Type",null));
			versionControlDataList.add(new MasterModel("User Type","VUT","Vehicle User Type",null));
			versionControlDataList.add(new MasterModel("Duty Type","VDT","Vehicle Duty Type",null));
			versionControlDataList.add(new MasterModel("Owner Type","VOT","Vehicle Owner Type",null));
			
			versionControlDataList.add(new MasterModel("Company","VCM","Vehicle Company Master",null));
			versionControlService.save(versionControlDataList);
			parentId=versionControlService.getMasterModelId_UsingCode("VCM");
			versionControlDataList.clear();
			
			versionControlDataList.add(new MasterModel("Branch","VBM","Vehicle Branch Master",parentId));
			versionControlService.save(versionControlDataList);
			parentId=versionControlService.getMasterModelId_UsingCode("VBM");
			versionControlDataList.clear();
			
			versionControlDataList.add(new MasterModel("Outlet","VOM","Vehicle Outlet Master",parentId));
			versionControlDataList.add(new VersionControlModel(0.1));
		}
		versionControlService.save(versionControlDataList);
		
		appVersion= versionControlService.getAppVersion();
		webApplicationContext.getServletContext().setAttribute("appVersion",appVersion);
	}
	
	
	
	
	
}
