package com.demonisles.schedulemanager.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demonisles.schedulemanager.domain.Customer;
import com.demonisles.schedulemanager.service.CustomerService;

@Controller
public class GreetingController {

	private static final Logger log = LoggerFactory.getLogger(GreetingController.class);
			 
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		List<Customer> list = customerService.queryAll();
		log.info("Customers :{}",list);
		return "greeting";
	}
}
