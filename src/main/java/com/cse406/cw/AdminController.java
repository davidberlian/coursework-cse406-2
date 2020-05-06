package com.cse406.cw;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.cse406.cw.models.*;

@Controller
public class AdminController {

	@GetMapping("/admin")
	public String login(Model model) {
		model.addAttribute( "message" ,model.asMap().get("message"));
		model.addAttribute( "alert" ,model.asMap().get("alert"));
	    model.addAttribute("user", new User());
		return "loginadmin";
	}
	@PostMapping("/admin")
	public String login_check(Model model, HttpServletRequest request, @ModelAttribute User user) {		
		
	    HttpSession newSession = request.getSession(); // create session
		System.out.println(user.getUsername());
		System.out.println(user.getPassword());
		if(user.getPassword() != "" && user.getUsername() != "") {
			if(user.check_admin_password()) {
				System.out.println("Success");
				newSession.setAttribute("username", user.getUsername());
				newSession.setAttribute("token", user.getToken());
				return "redirect:/adminhome";
			}else {
				System.out.println("Failed");
				model.addAttribute("alert", "alert");
				model.addAttribute("message", "Incorrect Username or Password");
				return "redirect:/admin";
			}
		}else {
			System.out.println("Failed");	 
			model.addAttribute("message", "Please fill Username and Password");    
			return "redirect:/admin";
		}
	}

	@GetMapping("/adminhome")
	public String adminhome(Model model, HttpServletRequest request) {
		try {
			User user = new User();
			HttpSession newSession = request.getSession();
			System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
			user.setUsername(newSession.getAttribute("username").toString());
			user.setToken(newSession.getAttribute("token").toString());
			if (user.checkTokenAdmin()) {
				model.addAttribute("dummyAccount", new dummyAccount());
				model.addAttribute("user", user);
				return "createacc";
			} else {
				System.out.println("Failed");
				model.addAttribute("message", "Please Login!");
				model.addAttribute("user", new User());
				newSession.invalidate();
				return "redirect:/admin";
			}
		}catch(Exception e){
			return "redirect:/admin";
		}
	}
	@PostMapping("/adminhome")
	public String adminhome2(Model model, @ModelAttribute dummyAccount dummyAccount, HttpServletRequest request) {
		
		try {
			User user = new User();
			HttpSession newSession = request.getSession();
			System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
			user.setUsername(newSession.getAttribute("username").toString());
			user.setToken(newSession.getAttribute("token").toString());
			if (user.checkTokenAdmin()) {
				System.out.println("HAHAHHAHAasdadadsH"+dummyAccount.getLast_name());
				if(dummyAccount.checkAccount() && dummyAccount.submit()) {
					System.out.println("Success");	
					model.addAttribute("dummyAccount", new dummyAccount());
					model.addAttribute("message", "new ACCOUNT CREATED success please registrate ONLINE BANKING!");  
					return "createacc";
				}else {
					model.addAttribute("dummyAccount", new dummyAccount());
					System.out.println("Failed");	 
					model.addAttribute("message", "Invalid information please try again");  
					return "createacc";
				}	
			} else {
				newSession.invalidate();
				return "redirect:/admin";
			}
		}catch(Exception e){
			return "redirect:/admin";
		}
	}
	
}