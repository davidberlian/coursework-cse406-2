package com.cse406.cw;

import com.cse406.cw.models.Transfer;
import com.cse406.cw.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Controller
public class RequestController {

    @PostMapping("/request")
    public String request2(Model model, HttpServletRequest request, @ModelAttribute Transfer transfer){
        System.out.println(transfer.getResponse());

        //TODO send the money -> transaction check balance, update balance, insert transaction
        //TODO update request accordingly
        //TODO set flash attribute

        

        return "redirect:/request";
    }
    @GetMapping("/request")
    public String request(Model model,HttpServletRequest request){

        User user = new User();
        HttpSession newSession = request.getSession();
        System.out.println("SESSION DATA" + newSession.getAttribute("token").toString());
        user.setUsername(newSession.getAttribute("username").toString());
        user.setToken(newSession.getAttribute("token").toString());

        if(user.checkToken()) {

            //TODO load request

            Transfer transfer = new Transfer(user.getAccountNumber());
            ArrayList<String[]> list = transfer.loadRequest();
            ArrayList<Transfer> listOfRequest = new ArrayList<Transfer>();

            for(int i = 0; i<list.size(); i++){
                String status = "";
                String name1 = "";
                if(list.get(0)[0].equals(user.getAccountNumber())) {

                    name1 = "REQUEST SENT TO "+list.get(0)[3];

                   if( list.get(0)[6].equals("0")){
                       status = "waiting";
                   }else if( list.get(0)[6].equals("1")){
                       status = "Received";
                   }else if( list.get(0)[6].equals("2")) {
                       status = "Rejected";
                   }
                }else {

                    name1 = "REQUEST FROM "+list.get(0)[2];

                    if( list.get(0)[6].equals("0")){
                        status = "option";
                    }else if( list.get(0)[6].equals("1")){
                        status = "Sent";
                    }else if( list.get(0)[6].equals("2")) {
                        status = "Rejected";
                    }
                }
                listOfRequest.add(new Transfer(list.get(i)[0],list.get(i)[1],
                            name1,list.get(i)[3],
                            list.get(i)[4],list.get(i)[5],
                            status ,list.get(i)[7], Integer.parseInt(list.get(i)[8])));
            }

            model.addAttribute("user", user);
            model.addAttribute("listOfRequest", listOfRequest);

            return "request";
        }else{
            return "redirect:/login";
        }
    }
}
