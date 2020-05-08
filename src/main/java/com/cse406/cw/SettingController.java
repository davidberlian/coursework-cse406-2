package com.cse406.cw;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cse406.cw.models.Signup;
import com.cse406.cw.models.User;

import org.springframework.stereotype.Controller;


@Controller
public class SettingController {

@GetMapping("/setting")
	public String setting(Model model, HttpServletRequest request) { 

		User user = new User();
		HttpSession newSession = request.getSession();
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());

		if(user.checkToken()) {		
			model.addAttribute( "message" ,model.asMap().get("message"));
			model.addAttribute( "alert" ,model.asMap().get("alert"));			
			model.addAttribute("user", new Signup());
			model.addAttribute("old_user", user);
			return "setting";

		}else {
			System.out.println("Failed");
			model.addAttribute("message", "Please Login!");
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "login";
		}
	
	}

	@PostMapping("/removeaccount")
	public String removeAccount(RedirectAttributes redirectAttrs,Model model, HttpServletRequest request, @ModelAttribute User new_user) { 

		User user = new User();
		HttpSession newSession = request.getSession();
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());

		if(user.checkToken()) {
			System.out.println("DATA FROM FORM "+new_user.getUsername() + " + " + new_user.getPassword());
			if(user.getUsername().equals(new_user.getUsername())) {
				user.setPassword(new_user.getPassword());
				if(user.check_password()) {
					if(user.deleteAccount()) {
						redirectAttrs.addFlashAttribute("alert","alert2");
						redirectAttrs.addFlashAttribute("message","Your acccount has been successfully deleted");
						user.removeToken(1);
						return "redirect:/login";	
					}else {
						redirectAttrs.addFlashAttribute("alert","alert");
						redirectAttrs.addFlashAttribute("message2","Something went wrong please try again");
						return "redirect:/setting";							
					}
				}else {
					redirectAttrs.addFlashAttribute("alert","alert");
					redirectAttrs.addFlashAttribute("message2","Your password did not match");
					return "redirect:/setting";		
				}
			}else {
				System.out.println("Failed");
				model.addAttribute("message", "Please Login!");
			    model.addAttribute("user", new User());
				newSession.invalidate();
				return "redirect:/login";
			}
		}else {
			System.out.println("Failed");
			model.addAttribute("message", "Please Login!");
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "redirect:/login";
		}
	}
	@PostMapping("/changepassword")
	public String changePassword(RedirectAttributes redirectAttrs,Model model, HttpServletRequest request, @ModelAttribute Signup new_user) { 

		User user = new User();
		HttpSession newSession = request.getSession();
		System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
		user.setUsername(newSession.getAttribute("username").toString());
		user.setToken(newSession.getAttribute("token").toString());

		if(user.checkToken()) {
			
			System.out.println("CHANNGEPASSWORD");
			System.out.println(new_user.toString2());
			
			
			
			if(new_user.getUsername().equals(user.getUsername())) {
				if(new_user.getPassword().length()>7 && new_user.getPassword().equals(new_user.getPassword2())) {
					user.setPassword(new_user.getOldPassword());
					if(user.check_password()) {
						new_user.updatePassword();
						user.removeToken(1);
						newSession.invalidate();
						redirectAttrs.addFlashAttribute("alert","alert2");	
						redirectAttrs.addFlashAttribute("message","Your password has been successfully updated");
						return "redirect:/login";
					}else {
						redirectAttrs.addFlashAttribute("alert","alert");
						redirectAttrs.addFlashAttribute("message","Your password did not match");
						return "redirect:/setting";						
					}
				}else {
					redirectAttrs.addFlashAttribute("alert","alert");
					redirectAttrs.addFlashAttribute("message","Your new password did not match or less than 7");
					return "redirect:/setting";
				}
					
			}else {
				
				return "redirect:/logout";
			}				

		}else {
			System.out.println("Failed");
			model.addAttribute("message", "Please Login!");
		    model.addAttribute("user", new User());
			newSession.invalidate();
			return "redirect:/login";
		}
	
	}
}
	