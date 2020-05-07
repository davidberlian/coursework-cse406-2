package com.cse406.cw;

import com.cse406.cw.models.Transfer;
import com.cse406.cw.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

@Controller
public class RequestController {

    @PostMapping("/request")
    public String request2(RedirectAttributes redirectAttrs, Model model, HttpServletRequest request, @ModelAttribute Transfer transfer){
        System.out.println(transfer.getResponse()+" "+transfer.toString2());

        User user = new User();
        HttpSession newSession = request.getSession();
        System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
        user.setUsername(newSession.getAttribute("username").toString());
        user.setToken(newSession.getAttribute("token").toString());

        model.addAttribute("user", user);

        if(user.checkToken()) {
            if(transfer.getResponse().equals("send")){
                // Send the money and update to 1
                transfer.setAccountNumber(transfer.getRequester());
                transfer.setDestination_id(transfer.getReceiver());
                if (!user.getAccountNumber().contentEquals(transfer.getDestination_id())) {
                    System.out.println("CHECK POINT 2");
                    if (transfer.getAmount() > 0) {
                        System.out.println("CHECK POINT 3" + transfer.getAccountNumber());
                        if (transfer.checkDestination() && transfer.checkBalance()) {
                            model.addAttribute("transfer", transfer);
                            return "transfer_confirmation.html";
                        }else {
                        	   redirectAttrs.addAttribute("alertClass", "alert");
                        	   redirectAttrs.addAttribute("message2", "FAILED to accept request please ensure you have enough balance");
                           
                        	return "redirect:/request";
                        }
                    }
                }
            	return "redirect:/request";

            }else{
                // update to 2
                if(transfer.rejectRequest()){
                    return "redirect:/request";
                }

            }
        }else{
            return "redirect:/login";
        }
        return "redirect://login";
    }
    @GetMapping("/request")
    public String request(Model model,HttpServletRequest request, @ModelAttribute Transfer newTransferRequest){
        try {
            User user = new User();
            HttpSession newSession = request.getSession();
            System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
            user.setUsername(newSession.getAttribute("username").toString());
            user.setToken(newSession.getAttribute("token").toString());

            if (user.checkToken()) {

				NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
				String userBalance = format.format(user.getSavings());
				model.addAttribute("userBalance", userBalance);
                model.addAttribute("user", user);

        		model.addAttribute( "message2" ,model.asMap().get("message2"));
        		model.addAttribute( "alert" ,model.asMap().get("alert"));
        		
                
                if (newTransferRequest != null) {
                    if (newTransferRequest.getDestination_id() != null
                            && newTransferRequest.getAmount() > 0) {

                        newTransferRequest.setRequester(user.getAccountNumber());
                        newTransferRequest.setReceiver(newTransferRequest.getDestination_id());

                        if (newTransferRequest.createNewRequest()) {

                            model.addAttribute("alertClass", "alert2");
                            model.addAttribute("message", "REQUEST SENT");
                        } else {

                            model.addAttribute("alertClass", "alert");
                            model.addAttribute("message", "REQUEST FAILED pleaase use correct information");
                        }

                        System.out.println("REQUEST SENT");
                    }
                } else {
                    System.out.println("NO REQUEST");
                }

                Transfer transfer = new Transfer(user.getAccountNumber());
                ArrayList<String[]> list = transfer.loadRequest();
                ArrayList<Transfer> listOfRequest = new ArrayList<Transfer>();

                for (int i = 0; i < list.size(); i++) {
                    String status = "";
                    String name1 = "";
                    if (list.get(i)[0].equals(user.getAccountNumber())) {

                        name1 = "REQUEST SENT TO " + list.get(i)[3];

                        if (list.get(i)[6].equals("0")) {
                            status = "waiting";
                        } else if (list.get(i)[6].equals("1")) {
                            status = "Received";
                        } else if (list.get(i)[6].equals("2")) {
                            status = "Rejected";
                        }
                    } else {

                        name1 = "REQUEST FROM " + list.get(i)[2];

                        if (list.get(i)[6].equals("0")) {
                            status = "option";
                        } else if (list.get(i)[6].equals("1")) {
                            status = "Sent";
                        } else if (list.get(i)[6].equals("2")) {
                            status = "Rejected";
                        }
                    }
                    listOfRequest.add(new Transfer(list.get(i)[0], list.get(i)[1],
                            name1, list.get(i)[3],
                            list.get(i)[4], list.get(i)[5],
                            status, list.get(i)[7], Integer.parseInt(list.get(i)[8])));
                }

                model.addAttribute("newTransferRequest", new Transfer(user.getAccountNumber()));
                model.addAttribute("listOfRequest", listOfRequest);

                return "request";
            } else {
                return "redirect:/login";
            }
        }catch (Exception e){
            return "redirect:/home";
        }
    }
}
