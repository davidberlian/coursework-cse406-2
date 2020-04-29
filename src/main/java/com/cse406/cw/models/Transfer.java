package com.cse406.cw.models;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import com.cse406.cw.models.DB_Connection;

public class Transfer {
    private String accountNumber;
    private String fullName;
    private String destination_id;
    private Double amount;
    private String amountCurr;
    private String message;

    public String toString(){
        return this.accountNumber + " " + this.message + " " + this.destination_id + " " + this.amount;
    }
    public Transfer(){

    }
    public Transfer(String accountNumber){
        this.accountNumber = accountNumber;
        this.destination_id = "";
        this.amount = 0D;
        this.message = "";
    }

    public String getDestination_id() {
        return destination_id;
    }

    public void setDestination_id(String destination_id) {
        this.destination_id = destination_id;
    }

    public Double getAmount() {
        return amount;
    }


    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean checkDestination(){
        try {
            DB_Connection conn = new DB_Connection();
            String query =
                    "SELECT id, concat(first_name, concat(\" \",last_name)) as full_name FROM savings "+
                            " WHERE id = '"+this.destination_id+"'";
            System.out.println(query);

            ArrayList<String[]> Response= conn.read_query(query,new String[]{"id","full_name"});
            if(Response.isEmpty()) {
                return false;
            }else {
                this.fullName = Response.get(0)[1];
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean checkBalance(){
        try {
            //INSERT INTO `transaction`(`id`, `savings_id`, `transaction_id`, `transaction_message`, `transaction_amount`,
            // `transaction_time`) VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6])

            DB_Connection conn = new DB_Connection();
            String query =
                    "SELECT total_savings FROM savings WHERE id ="+this.accountNumber;
            System.out.println(query);

            ArrayList<String[]> Response= conn.read_query(query,new String[]{"total_savings"});
            if(Response.isEmpty()) {
                return false;
            }else {
                System.out.println("BALANCE = "+Response.get(0)[0]);
                if(Double.parseDouble(Response.get(0)[0]) > this.amount){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean send(){
        try {
            //INSERT INTO `transaction`(`id`, `savings_id`, `transaction_id`, `transaction_message`, `transaction_amount`,
            // `transaction_time`) VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6])

            DB_Connection conn = new DB_Connection();
            String query =
                    "INSERT INTO transaction (savings_id, transaction_id, transaction_message, transaction_amount) VALUES "+
                            "('"+this.accountNumber+"',2,' TO "+this.fullName.substring(0,this.fullName.indexOf(" ")) + " , " +this.message+"',"+(-1*this.amount)+")";
            System.out.println(query);

            Boolean Response= conn.write_query(query);
            if(Response) {
                query = "INSERT INTO transaction (savings_id, transaction_id, transaction_message, transaction_amount) VALUES "+
                        "('"+this.destination_id+"',2,' FROM "+this.fullName.substring(0,this.fullName.indexOf(" ")) + " , " +this.message+"',"+this.amount+")";
                System.out.println(query);
                Response = conn.write_query(query);
                if(Response){
                    query = "UPDATE savings SET total_savings = total_savings +"+ this.amount +" WHERE id = '"+this.destination_id+"'";
                    System.out.println(query);
                    Response = conn.write_query(query);
                    if(Response){
                        query = "UPDATE savings SET total_savings = total_savings -"+ this.amount +"  WHERE id = '"+this.accountNumber+"'";
                        System.out.println(query);
                        Response = conn.write_query(query);
                        if(Response){
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAmountCurr() {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        return format.format(this.amount);
    }

    public void setAmountCurr(String amountCurr) {
        this.amountCurr = amountCurr;
    }
}
