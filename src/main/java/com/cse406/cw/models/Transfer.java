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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    private String response;

    private int id;
    private String receiver;
    private String requester;
    private String name1;
    private String name2;
    private String time;
    private String status;

    public String toString2(){
        return this.id+","+
                this.receiver+","+
                this.requester+","+
                this.name1+","+
                this.name2+","+
                this.time+","+
                this.status+",";
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Transfer(String receiver, String requester, String name1,String name2, String message,
                    String amount, String status,String time, int id){
        this.receiver = receiver;
        this.requester = requester;
        this.message = message;
        this.name1 = name1;
        this.name2 = name2;
        this.amount = Double.parseDouble(amount);
        this.time = time;
        this.status = status;
        this.id = id;
    }

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

    public ArrayList<String[]> loadRequest(){
        ArrayList<String[]> a = new ArrayList<String[]>();
        System.out.println("EXECUTE SQL");
        try {
            DB_Connection conn = new DB_Connection();
            String query =
                    "SELECT tr.id as id, " +
                            "tr.savings_id as requester, " +
                            "tr.destination_id as receiver, " +
                            "s1.first_name as name1, " +
                            "s2.first_name as name2, " +
                            "tr.transaction_message as message, " +
                            "tr.transaction_amount as amount, " +
                            "tr.status as status," +
                            "tr.transaction_time as time "+
                            "FROM transaction_request as tr "+
                            "JOIN savings as s1 ON s1.id = tr.savings_id "+
                            "JOIN savings as s2 ON s2.id = tr.destination_id "+
                            "WHERE tr.savings_id ='"+this.accountNumber+"' " +
                            "OR tr.destination_id = '"+this.accountNumber+"' " +
                            "ORDER BY time desc";
            System.out.println(query);
            ArrayList<String[]> Response= conn.read_query(
                    query
                    ,new String[]{"requester","receiver","name1", "name2", "message","amount","status","time","id"});
            if(Response.isEmpty()) {
                System.out.println("not found");
            }else {
                return Response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
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

    public boolean createNewRequest(){
        try{
            DB_Connection conn = new DB_Connection();
            String query = "INSERT INTO transaction_request (savings_id, destination_id, transaction_amount, transaction_message) VALUES "+
                                "('"+this.requester+"','"+this.receiver+"','"+this.amount+"','"+this.message+"')";
            System.out.println(query);

            Boolean Response= conn.write_query(query);
            if(Response)return true;
            return false;
        }catch (Exception e){
            e.getMessage();
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

    public Boolean rejectRequest(){
        try {
            //INSERT INTO `transaction`(`id`, `savings_id`, `transaction_id`, `transaction_message`, `transaction_amount`,
            // `transaction_time`) VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6])

            DB_Connection conn = new DB_Connection();
            String query =
                    "UPDATE transaction_request SET status = 2 "+
                            "WHERE id = "+this.id;

            System.out.println(query);

            Boolean Response= conn.write_query(query);
            if(Response) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Boolean acceptRequest(){
        try {
            //INSERT INTO `transaction`(`id`, `savings_id`, `transaction_id`, `transaction_message`, `transaction_amount`,
            // `transaction_time`) VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6])

            DB_Connection conn = new DB_Connection();
            String query =
                    "UPDATE transaction_request SET status = 1 "+
                            "WHERE id = "+this.id;

            System.out.println(query);

            Boolean Response= conn.write_query(query);
            if(Response) {
                return true;
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
