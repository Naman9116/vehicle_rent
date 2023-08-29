package com.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.appVersion.VersionControl;

@Component
public class VehicleContextListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("ServletContextListener destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("ServletContextListener started");	
		WebApplicationContext servletContext =  WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		new VersionControl().syncVersion(servletContext); 
	}
	
	
}
