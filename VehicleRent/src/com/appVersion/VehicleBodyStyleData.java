package com.appVersion;

import java.util.ArrayList;
import java.util.List;

import com.master.model.GeneralMasterModel;

public class VehicleBodyStyleData {
	
	List<Object>versionControlDataList=new ArrayList<Object>();
	Long parentId=null;

	public void vehicleBodyStyleData(){
		versionControlDataList.add(new GeneralMasterModel("VBS0001","Box","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0002","Box - Estate","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0003","Bus","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0004","Cab with Engine","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0005","Convertible","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0006","Coupe","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0007","Dumptruck","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0008","Estate","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0009","General Purpose Vehicle","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0010","Hatchback","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0011","MPV","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0012","Pickup","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0013","Platform-Chassis","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0014","Saloon","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0015","Special Design","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0016","SUV","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0017","SUV (Open)","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0018","Targa","",parentId));
		versionControlDataList.add(new GeneralMasterModel("VBS0019","Truck Tractor","",parentId));
	}

}
