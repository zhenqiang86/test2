package com.iflashbuy.baseservice.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Test {
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	
	@RequestMapping(value="t1.do")
	public ModelAndView getCardList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("Hello, this is a debug info");
		return null;
	}
}