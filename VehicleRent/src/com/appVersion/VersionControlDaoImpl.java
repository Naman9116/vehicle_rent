package com.appVersion;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("versionControlDao")
public class VersionControlDaoImpl implements VersionControlDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Double getAppVersion() {
		Double appVersion = (Double)sessionFactory.getCurrentSession().createQuery("SELECT MAX(version) FROM VersionControlModel").uniqueResult();
		return appVersion ;
	}

	public String save(List versionControlDataList) {
		for(int i=0;i<versionControlDataList.size();i++){}
//			sessionFactory.getCurrentSession().save(versionControlDataList.get(i));	
		return "saveSuccess";
	}

	@Override
	public Long getMasterModelId_UsingCode(String code) {
		Long parentId = (Long)sessionFactory.getCurrentSession().createQuery("SELECT mm.id FROM MasterModel mm WHERE mm.code='"+code+"'").uniqueResult();
		return parentId;
	}

	
}
