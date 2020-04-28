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
	public String index() {
		//model.addAttribute("name", name);
		return "index";
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
	public String transaction(Model model, HttpServletRequest request,@RequestParam(defaultValue = "") String month) {
				
		User user = new User();
		HttpSession newSession = request.getSession(); 
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());	
		
		if(user.checkToken()) {	
			
			model.addAttribute("user", user);


			
	        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");  
	        LocalDateTime picked = null;
	        if(month.equals("")) {
	        	picked = LocalDateTime.now();
	        }else {	        	
	        	picked = LocalDateTime.parse(month+"-01T00:00:01");
	        }
	        

			Double totalDebit = 0D;
			Double totalCredit = 0D;
			Double startingBalance = Transaction.loadTransactionBalance(user.getUsername(), picked.format(dtf));
			Double currentBalance = startingBalance;
			
	        System.out.println("CURRENT BALANCE" + currentBalance);
	        
	        ArrayList<String[]> list = null;
			
				list = Transaction.loadTransaction(user.getUsername(), picked.format(dtf));
			
			ArrayList<Transaction> listOfTransaction= new ArrayList<Transaction>(); 
	        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

	        ArrayList<Month> listOfMonth = new ArrayList<Month>();
        	LocalDateTime currentdate = LocalDateTime.now();
	        for(int i = 3; i > 0; i--) {
	        	listOfMonth.add(new Month(currentdate));
	        	currentdate = currentdate.minusMonths(1);
	        }
	        
	        model.addAttribute("options",listOfMonth);
	        
	        int maxDay = picked.getMonth().maxLength();
	        System.out.println("DAY OF MONTH + +"+maxDay);
	        dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy"); 
	        LocalDateTime begin = LocalDateTime.of(picked.getYear(), picked.getMonth(),  1,0,0,0);
	        LocalDateTime end = LocalDateTime.of(picked.getYear(), picked.getMonth(),  maxDay,0,0,0);
			model.addAttribute("period", begin.format(dtf)+" to "+end.format(dtf));
	        
			for(int i = 0; i<list.size(); i++) {
				System.out.println("Success+"+i);
				listOfTransaction.add(new Transaction(list.get(i)[2],list.get(i)[0],list.get(i)[1]));

		        System.out.println("CURRENT BALANCE BEFORE" + currentBalance);
				if(listOfTransaction.get(i).getCreditdebit().contentEquals("CREDIT")) {
					totalCredit += listOfTransaction.get(i).getAmount();
				}else {
					totalDebit += listOfTransaction.get(i).getAmount();
				}
				currentBalance += listOfTransaction.get(i).getAmount();
				listOfTransaction.get(i).setCurrentbalance(format.format(currentBalance));
		        System.out.println("CURRENT BALANCE" + currentBalance);
			}
			
			model.addAttribute("transactions",listOfTransaction);

			model.addAttribute("startingBalance",format.format(startingBalance));
			model.addAttribute("totalDebit",format.format(totalDebit));
			model.addAttribute("totalCredit",format.format(totalCredit));
			model.addAttribute("currentBalance",format.format(currentBalance));
			
			return "transaction";	
			
		}else {
			System.out.println("Failed");	 
			model.addAttribute("message", "Please Login!");  
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "login";
		}
	}
}
