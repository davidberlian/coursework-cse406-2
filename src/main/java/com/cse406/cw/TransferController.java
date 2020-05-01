package com.cse406.cw;

import com.cse406.cw.models.Transfer;
import com.cse406.cw.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class TransferController {

    @GetMapping("/transfer")
    public String transfer(Model model, HttpServletRequest request, @RequestParam(defaultValue = "") String month) {

        try {
            User user = new User();
            HttpSession newSession = request.getSession();
            System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
            user.setUsername(newSession.getAttribute("username").toString());
            user.setToken(newSession.getAttribute("token").toString());

            if (user.checkToken()) {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                LocalDateTime now = LocalDateTime.now();
                model.addAttribute("date",now.format(dtf));

                System.out.println("Account Number : "+user.getAccountNumber());
                Transfer transfer = new Transfer(user.getAccountNumber());

                model.addAttribute("transfer", transfer);
                model.addAttribute("user", user);

                return "transfer.html";
            } else {
                return "redirect:/login";
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/transfer")
    public String transfer2(Model model, HttpServletRequest request, @ModelAttribute Transfer transfer){
        try{
            System.out.println(transfer.toString()+"POST MAPPING");
            User user = new User();
            HttpSession newSession = request.getSession();
            System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
            user.setUsername(newSession.getAttribute("username").toString());
            user.setToken(newSession.getAttribute("token").toString());

            model.addAttribute("user", user);

            if (user.checkToken()) {
                System.out.println("CHECK POINT 1 "+ user.getAccountNumber() + " , "+ transfer.getAccountNumber());
                if (user.getAccountNumber().contentEquals(transfer.getAccountNumber())) {
                    System.out.println("CHECK POINT 2");
                    if (transfer.getAmount() > 0) {
                        System.out.println("CHECK POINT 3");
                        if (transfer.checkDestination() && !transfer.getDestination_id().contentEquals(user.getAccountNumber())) {

                            model.addAttribute("transfer", transfer);
                            return "transfer_confirmation.html";
                        } else {
                            model.addAttribute("message", "system can not find destination account");
                        }
                    } else {
                        model.addAttribute("message", "transfer amount can not be zero");
                    }

                    transfer = new Transfer(user.getAccountNumber());
                    model.addAttribute("transfer", transfer);
                    model.addAttribute("alertClass", "alert");
                    return "transfer.html";
                }else{
                    throw new Exception();
                }
            }else {
                throw new Exception();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "redirect:/login";
        }
    }
    @PostMapping("/send_transfer")
    public String transfer3(Model model, HttpServletRequest request,@ModelAttribute Transfer transfer){
        try {
            System.out.println(transfer.toString() + "POST MAPPING SEND TRANSFER");
            User user = new User();
            HttpSession newSession = request.getSession();
            System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
            user.setUsername(newSession.getAttribute("username").toString());
            user.setToken(newSession.getAttribute("token").toString());

            model.addAttribute("user", user);
            System.out.println(transfer.toString());
            if (user.checkToken() && transfer.getAmount() > 0
            && transfer.checkDestination() && !transfer.getDestination_id().contentEquals(user.getAccountNumber())){
                transfer.setAccountNumber(user.getAccountNumber());

                if(transfer.checkBalance()){
                    System.out.println("BALANCE SUFFICIENT");
                    if(transfer.getId() > 0){
                        transfer.acceptRequest();
                        transfer.setMessage(transfer.getMessage()+" via REQUEST");
                    }else{
                        System.out.println("REQUEST ID NOT DETECTED");
                    }
                    if(transfer.send()){
                        model.addAttribute("transfer", transfer);
                        model.addAttribute("user", user);
                        return "transfer_receipt.html";
                    }else{
                        model.addAttribute("message", "SOMETHING WENT WRONG PLEASE TRY AGAIN");
                    }
                }else{
                    model.addAttribute("message", "BALANCE INSUFFICIENT");
                }

                model.addAttribute("transfer", transfer);
                model.addAttribute("user", user);
                return "transfer_confirmation.html";

            } else {
                throw new Exception();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "redirect:/login";
        }
    }
}
