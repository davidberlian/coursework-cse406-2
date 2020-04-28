package com.cse406.cw;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cse406.cw.models.User;
import com.cse406.cw.models.Transaction;

@Controller
public class MainController {

	@GetMapping("/")
	public String index() {
		//model.addAttribute("name", name);
		return "index";
	}
	@GetMapping("/home")
	public String home(Model model, HttpServletRequest request) {
		
		User user = new User();
		HttpSession newSession = request.getSession(); 
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());	
		
		if(user.checkToken()) {	
			
			model.addAttribute("user", user);
			return "home";	
			
		}else {
			System.out.println("Failed");	 
			model.addAttribute("message", "Please Login!");  
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "login";
		}
	}
	@GetMapping("/transaction")
	public String transaction(Model model, HttpServletRequest request) {
		
		User user = new User();
		HttpSession newSession = request.getSession(); 
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());	
		
		if(user.checkToken()) {	
			
			model.addAttribute("user", user);
			
			ArrayList<String[]> list = Transaction.loadTransaction(user.getUsername());
			ArrayList<Transaction> listOfTransaction= new ArrayList<Transaction>(); 
			for(int i = 0; i<list.size(); i++) {
				listOfTransaction.add(new Transaction(list.get(i)[0],list.get(i)[1],list.get(i)[2]));
			}
			model.addAttribute("transactions",listOfTransaction);
			return "transaction";	
			
		}else {
			System.out.println("Failed");	 
			model.addAttribute("message", "Please Login!");  
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "login";
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
