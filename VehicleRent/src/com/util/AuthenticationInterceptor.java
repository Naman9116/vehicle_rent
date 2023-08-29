package com.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.operation.controller.BookingMasterController;
import com.user.model.UserModel;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = Logger.getLogger(AuthenticationInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String uri = request.getRequestURI();
		logger.debug("RequestHeader:" + request.getHeader("ORIGINAPP"));
		if(request.getHeader("ORIGINAPP") == null && request.getHeader("X-Requested-With") == null){
			if (!uri.endsWith("userLogin.html") && !uri.endsWith("login.html") && 
				!uri.endsWith("logout.html") && !uri.endsWith("getBranchDataUsingCompany.html")) {
				UserModel userData = (UserModel) request.getSession().getAttribute("loginUserData");
				if (userData == null) {
					response.sendRedirect(request.getContextPath()+"/login.html");
					return false;
				}
			}
		}
		return true;
	}
}
