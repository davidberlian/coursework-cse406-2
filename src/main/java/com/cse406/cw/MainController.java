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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cse406.cw.models.User;
import com.cse406.cw.models.*;

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

	@GetMapping("/personal") 
	public String personal(Model model, HttpServletRequest request) { 
		
		User user = new User();
		HttpSession newSession = request.getSession(); 
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());	
		
		if(user.checkToken()) {	

			NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
			String userBalance = format.format(user.getSavings());
			model.addAttribute("user", user);
			model.addAttribute("userBalance", userBalance);
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
