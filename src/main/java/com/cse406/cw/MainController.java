package com.cse406.cw;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cse406.cw.models.User;
import com.cse406.cw.models.dummyAccount;
import com.cse406.cw.models.Month;
import com.cse406.cw.models.Transaction;



@Controller
public class MainController {

	@GetMapping("/")
	public String index(HttpServletRequest request) {
		//model.addAttribute("name", name);
		try {
			User user = new User();
			HttpSession newSession = request.getSession();
			System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
			user.setUsername(newSession.getAttribute("username").toString());
			user.setToken(newSession.getAttribute("token").toString());

			if (user.checkToken()) {
				return "redirect:/home";
			}else{
				return "index";
			}
		}catch(Exception e){
				return "index";
			}

	}

	@GetMapping("/createaccount")
	public String login(Model model) {
		System.out.println("HAHAHHAHAH");
		model.addAttribute("dummyAccount", new dummyAccount());
		return "createacc";
	}
	@PostMapping("/createaccount")
	public String createaccount(Model model, @ModelAttribute dummyAccount dummyAccount) {
		System.out.println("HAHAHHAHAasdadadsH"+dummyAccount.getLast_name());
		if(dummyAccount.checkAccount() && dummyAccount.submit()) {
			System.out.println("Success");	 
			model.addAttribute("message", "new ACCOUNT CREATED success please registrate ONLINE BANKING!");  
			return "createacc";
		}else {
			model.addAttribute("dummyAccount", new dummyAccount());
			System.out.println("Failed");	 
			model.addAttribute("message", "Invalid information please try again");  
			return "createacc";
		}		
	}
	
	@GetMapping("/home")
	public String home(Model model, HttpServletRequest request) {
		try {
			User user = new User();
			HttpSession newSession = request.getSession();
			System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
			user.setUsername(newSession.getAttribute("username").toString());
			user.setToken(newSession.getAttribute("token").toString());

			if (user.checkToken()) {

				NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
				String userBalance = format.format(user.getSavings());
				model.addAttribute("user", user);
				model.addAttribute("userBalance", userBalance);
				return "home";

			} else {
				System.out.println("Failed");
				model.addAttribute("message", "Please Login!");
				model.addAttribute("user", new User());
				newSession.invalidate();
				return "login";
			}
		}catch(Exception e){
			return "redirect:/login";
		}
	}
	@GetMapping("/setting")
	public String setting(Model model, HttpServletRequest request) { 

		User user = new User();
		HttpSession newSession = request.getSession();
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());

		if(user.checkToken()) {

			model.addAttribute("user", user);
			return "setting";

		}else {
			System.out.println("Failed");
			model.addAttribute("message", "Please Login!");
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "login";
		}
	
	}


	@GetMapping("/personal") 
	public String personal(Model model, HttpServletRequest request) { 
		
		User user = new User();
		HttpSession newSession = request.getSession(); 
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());	
		
		if(user.checkToken()) {	
			
			model.addAttribute("user", user);
			return "personal";	
			
		}else {
			System.out.println("Failed");	 
			model.addAttribute("message", "Please Login!");  
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "login";
		}
	}
}
