package com.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Enabling CORS support  - Access-Control-Allow-Origin
 * @author zeroows@gmail.com
 * 
 * <code>
 	<!-- Add this to your web.xml to enable "CORS" -->
	<filter>
	  <filter-name>cors</filter-name>
	  <filter-class>com.elm.mb.rest.filters.CORSFilter</filter-class>
	</filter>
	  
	<filter-mapping>
	  <filter-name>cors</filter-name>
	  <url-pattern>/*</url-pattern>
	</filter-mapping>
 * </code>
 */
public class CORSFilter extends OncePerRequestFilter {
	private static final Logger logger = Logger.getLogger(CORSFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getHeader("Access-Control-Request-Method") != null ) {
			logger.trace("Sending Header....");
			// CORS "pre-flight" request
			response.setHeader("Access-Control-Allow-Origin", "*");
		    response.setHeader("Access-Control-Allow-Credentials", "true");
		    response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
		    response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers,Access-Control-Allow-Origin,ORIGINAPP,Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers");		
		    response.setHeader("Access-Control-Max-Age", "86400");
		}
		filterChain.doFilter(request, response);
	}
}

