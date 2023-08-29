package com.appVersion;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("versionControlService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class VersionControlServiceImpl implements VersionControlService {

	@Autowired
	private VersionControlDao versionControlDao;

	public VersionControlServiceImpl() {

	}

	@Override
	public Double getAppVersion() {
		return versionControlDao.getAppVersion();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String save(List versionControlDataList) {
		return versionControlDao.save(versionControlDataList);
	}

	@Override
	public Long getMasterModelId_UsingCode(String code) {
		return versionControlDao.getMasterModelId_UsingCode(code);
	}
	
}
