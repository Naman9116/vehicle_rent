package com.report.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestFactory {


	public static Collection<TestBean>  getBeanData(){
		Collection<TestBean> list=new ArrayList<TestBean>();
		list.add(new TestBean("Ankit","Jaipur"));
		list.add(new TestBean("Amit","Jaipur"));
		list.add(new TestBean("Kamal","Jaipur"));
		return list;
	}
}
