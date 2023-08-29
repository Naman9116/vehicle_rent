package com.appVersion;

import java.util.List;


public interface VersionControlDao {

	public Double getAppVersion();
	
	public Long getMasterModelId_UsingCode(String code);
	
	public String save(List versionControlDataList);
}
