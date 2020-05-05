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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;


@Controller
public class LoginController {

	User user_data= null;
	HttpSession session = null;

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute( "message" ,model.asMap().get("message"));
		model.addAttribute( "alert" ,model.asMap().get("alert"));
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

				model.addAttribute("alert", "alert");
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
	public String forgot(RedirectAttributes redirectAttrs, Model model, HttpServletRequest request) {
		model.addAttribute("user", new Signup());
		model.addAttribute( "message" ,model.asMap().get("message"));
		return "forgot";
	}

	protected String generateToken() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 7) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}
	@GetMapping("/resetpassword")
	public String reset_password(RedirectAttributes redirectAttrs, Model model, HttpServletRequest request) {
		model.addAttribute("user", new Signup());
		model.addAttribute( "message" ,model.asMap().get("message"));
		model.addAttribute( "alert" ,model.asMap().get("alert"));
		return "resetpassword";
	}
	@PostMapping("/resetpassword")
	public String reset_password2(RedirectAttributes redirectAttrs, Model model, HttpServletRequest request,@ModelAttribute Signup user) {
		// check token
		// update password
		try{
			if(user.checkForgotToken() && user.getPassword().equals(user.getPassword2())){
				user.disolveToken();
				user.updatePassword();
				redirectAttrs.addFlashAttribute("alert","alert2");
				redirectAttrs.addFlashAttribute("message","your password has been successfully updated");
				return "redirect:/login";
			}else{redirectAttrs.addFlashAttribute("alert","alert");
				redirectAttrs.addFlashAttribute("message","Invalid Data");
				return "redirect:/resetpassword";
			}
		}catch (Exception e){
			redirectAttrs.addFlashAttribute("alert","alert");
			redirectAttrs.addFlashAttribute("message","Something went wrong please try again");
			return "redirect:/resetpassword";
		}
	}

	@PostMapping("/forgot")
	public String forgot_check(RedirectAttributes redirectAttrs, HttpServletRequest request, @ModelAttribute Signup user) {
		try{
			System.out.println("93 check Strart");
			if(user.forgotCheck()){
				try{
					String token = generateToken();
					System.out.println("93 check success"+user.getEmail());
					if(sendMail(user.getEmail(),token)){
						user.insertForgotToken(token);
						redirectAttrs.addFlashAttribute("message","if. your email and username match the system data, an email will be sent");
						redirectAttrs.addFlashAttribute("alert","alert2");
						return "redirect:/forgot";
					}else{
						redirectAttrs.addFlashAttribute("message","Please Contact Admin");
						redirectAttrs.addFlashAttribute("alert","alert");
						return "redirect:/forgot";
					}
				}catch (Exception e){
					
				}finally {

				}
			}
			redirectAttrs.addFlashAttribute("message","if your email and username match the system data, an email will be sent");
			redirectAttrs.addFlashAttribute("alert","alert2");
			return "redirect:/forgot";
		}catch (Exception E){
			redirectAttrs.addFlashAttribute("message","Something went wrong please try again");
			redirectAttrs.addFlashAttribute("alert","alert");
			return "redirect:/forgot";
		}
	}

	public Boolean sendMail(String email, String token) throws UnknownHostException, SocketException {
		      //provide recipient's email ID
		      String to = email;

		      //provide sender's email ID
				String from = "davidberlian.com";
				try(final DatagramSocket socket = new DatagramSocket()){
				  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				  from = socket.getLocalAddress().getHostAddress();
				}

		//provide Mailtrap's username
		// final String username = "4d805ec5e82105";
		//provide Mailtrap's password
		//final String password = "0d4a8b2d3939e8";
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
		    message.setFrom(new InternetAddress(from));
		 
		    //set To email field
		    message.setRecipients(Message.RecipientType.TO,
		               InternetAddress.parse(to));
		 
		    //set email subject field
		    message.setSubject("Reset Account");
		 
		    //set the content of the email message
		    message.setContent("click here <a href=\"http://davidberlian.com:8080/cw/resetpassword\" >here</a> Your token is "+token, "text/html");

		    //send the email message
		    Transport.send(message);

		    System.out.println("Email Message Sent Successfully");
		    	return true;
		      } catch (MessagingException e) {
		    	  e.printStackTrace();
		        return false;
		      }
		   }
	

	@PostMapping("/signup")
	public String signup_check(Model model, @ModelAttribute Signup user, RedirectAttributes redirectAttributes) {
		System.out.println(user.getPassword()+"POST MAPPING");
		if(user.checkfield()) {
			if(user.validate()) {
				model.addAttribute("user", new Signup());
				System.out.println("Success");
				redirectAttributes.addFlashAttribute("message", "Registration success please login!!");
				redirectAttributes.addFlashAttribute("alert","alert2");
				return "redirect:/login";
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
