package com.menu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MenuNavigationController {
	
	@RequestMapping(value="/loadMenuTraininig/{menuNext}/{pageNumber}",method=RequestMethod.GET)
	public ModelAndView loadMenuTraininig(@PathVariable("menuNext") String menuNext,@PathVariable("pageNumber") String pageNumber){
		int menuNextNumber=Integer.parseInt(menuNext);
		ModelAndView modelAndView = null;
		switch(menuNextNumber){
			case 1:
				modelAndView=	new ModelAndView("mainFrame");
				break;
			case 2:
				modelAndView=	new ModelAndView("schedule");
				break;
			case 3:
				modelAndView=	new ModelAndView("projectNameMaster");
				break;
			case 11:
				modelAndView=	new ModelAndView("javaIntroduction");
				break;
			default:
				break;
		}
		
		return modelAndView;
	}

	
	
}
