package com.cse406.cw;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.cse406.cw.models.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Properties;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Controller
public class LoginController {

	User user_data= null;
	HttpSession session = null;

	@GetMapping("/login")
	public String login(Model model) {
	    model.addAttribute("user", new User());
		return "login";
	}
	@PostMapping("/login")
	public String login_check(Model model, HttpServletRequest request, @ModelAttribute User user) {		
		
	    HttpSession newSession = request.getSession(); // create session

		System.out.println(user.getUsername());
		System.out.println(user.getPassword());
		if(user.getPassword() != "" && user.getUsername() != "") {
			if(user.check_password()) {
				System.out.println("Success");
				user_data = user;
				newSession.setAttribute("username", user.getUsername());
				newSession.setAttribute("token", user.getToken());
				return "redirect:/home";
			}else {
				System.out.println("Failed");	 
				model.addAttribute("message", "Incorrect Username or Password");    
				return "login";
			}
		}else {
			System.out.println("Failed");	 
			model.addAttribute("message", "Please fill Username and Password");    
			return "login";
		}
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {	
		model.addAttribute("user", new Signup());
		return "signup";
	}

	@GetMapping("/logout")
	public String logout(Model model, HttpServletRequest request) {
		HttpSession newSession = request.getSession(); // create session
		newSession.invalidate();
		return "index";
	}


	@GetMapping("/forgot")
	public String forgot(Model model, HttpServletRequest request) {
		model.addAttribute("user", new Signup());
		model.addAttribute( "message" ,model.asMap().get("message"));
		return "forgot";
	}

	@PostMapping("/forgot")
	public String forgot_check(RedirectAttributes redirectAttrs, Model model, HttpServletRequest request, @ModelAttribute Signup user) {
		try{
			if(user.forgotCheck()){
				try{
					sendMail();
				}catch (Exception e){

				}finally {

				}
			}
			redirectAttrs.addAttribute("message","if your email and username match the system data, an email will be sent");
			return "redirect:/forgot";
		}catch (Exception E){
			redirectAttrs.addAttribute("message","Something went wrong plase try again");
			return "redirect:/forgot";
		}
	}

	public Boolean sendMail() {
		      //provide recipient's email ID
		      String to = "david.berlian@gmail.com";

		      //provide sender's email ID
		      String from = "Bank EE CS";
		      //provide Mailtrap's username
		      final String username = "eebankcse406@gmail.com";
		      //provide Mailtrap's password
		      final String password = "D.berlian19@";

		      //provide Mailtrap's host address 
		      String host = "smtp.gmail.com";
		      //configure Mailtrap's SMTP server details 
		      Properties props = new Properties();
		      props.put("mail.smtp.auth", "true");
		      props.put("mail.smtp.starttls.enable", "true");  
		      props.put("mail.smtp.host", host);
		      props.put("mail.smtp.port", "587");

		      //create the Session object
		      Session session = Session.getInstance(props,
		         new javax.mail.Authenticator() {
		            protected PasswordAuthentication getPasswordAuthentication() {
		               return new PasswordAuthentication(username, password);
		    }
		         });

		      try {
		    //create a MimeMessage object
		    Message message = new MimeMessage(session);
		 
		    //set From email field 
		    message.setFrom(new InternetAddress("18.163.170.111"));
		 
		    //set To email field
		    message.setRecipients(Message.RecipientType.TO,
		               InternetAddress.parse(to));
		 
		    //set email subject field
		    message.setSubject("Here comes Jakarta Mail!");
		 
		    //set the content of the email message
		    message.setContent("Just discovered that Jakarta Mail is fun and easy to use", "text/html");

		    //send the email message
		    Transport.send(message);

		    System.out.println("Email Message Sent Successfully");
		    	return true;
		      } catch (MessagingException e) {
		        return false;
		      }
		   }
	

	@PostMapping("/signup")
	public String signup_check(Model model, @ModelAttribute Signup user) {	
		System.out.println(user.getPassword()+"POST MAPPING");
		if(user.checkfield()) {
			if(user.validate()) {
				model.addAttribute("user", new Signup());
				System.out.println("Success");	 
				model.addAttribute("message", "Registration success please login!");  
				return "login";
			}else {
				model.addAttribute("user", new Signup());
				System.out.println("Failed");	 
				model.addAttribute("message", "Invalid information please try again n/b: ensure your DOB, Savings Number is correct, minimum password length is 8, or choose new username");
				return "signup";				
			}
		}else {		
			model.addAttribute("user", new Signup());
			System.out.println("Failed 2");
			model.addAttribute("message", "Please fill all the field correctly");
			return "signup";
		}
	}
}
