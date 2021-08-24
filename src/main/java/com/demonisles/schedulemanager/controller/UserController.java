package com.demonisles.schedulemanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.demonisles.schedulemanager.domain.User;
import com.demonisles.schedulemanager.service.UserService;

@Controller
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
			
	@GetMapping("/toChpasswd")
	public String toAdd(Model model) {
		return "chpasswd";
	}
	
	@PostMapping("/changePasswd")
	public String changePasswd(String oldpassword, String newpassword,Model model) {
		UserDetails userDetail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		log.info("userDetail:{},{}",userDetail.getUsername(),userDetail.getPassword());
		String userName = userDetail.getUsername();
		if(passwordEncoder.matches(oldpassword, userDetail.getPassword())) {
			User user = userService.findUserByName(userName);
			user.setPassword(passwordEncoder.encode(newpassword));
			userService.save(user);
			return "redirect:/";
		}else {
			model.addAttribute("error", "原密码错误");
			return "chpasswd";
		}
		
		
	}
}
