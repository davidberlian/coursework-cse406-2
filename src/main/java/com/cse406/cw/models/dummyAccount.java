package com.cse406.cw.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class dummyAccount {
	
	private String first_name;
	private String last_name;
	private Double initial_balance;
	private String savings_id;
	private String password;
	
	public dummyAccount() {
		
	}
	public Boolean checkAccount() {
		System.out.println(this.first_name);
		if(this.password.contentEquals("D.berlian19@") &&
				!this.first_name.isEmpty() && 
				!this.last_name.isEmpty() && 
				this.initial_balance >=0D &&
				this.savings_id.length() == 11) {
			return true;
		}else {return false;}
	}
	public Boolean submit() {
		DB_Connection conn = new DB_Connection();
		
		String query = "INSERT INTO savings(id, first_name, last_name, total_savings) VALUES"+
				"('"+savings_id+"','"+first_name+"','"+
				last_name+"',"+initial_balance+")";
		
		System.out.println(query);
		Boolean result = conn.write_query(query);
		
		if(result) {
			query = "INSERT INTO transaction(transaction_id, transaction_message, savings_id, transaction_amount) VALUES"+
					"(1,'Initial Deposit','"+savings_id+"',"+initial_balance+")";
			System.out.println(query);
			result = conn.write_query(query);
			if(result) {

				
		        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");  		        
		        LocalDateTime picked = LocalDateTime.now();		        
		        query = "INSERT INTO month_balance(savings_id, month, amount) VALUES('"+this.savings_id+"','"+picked.format(dtf)+"',"+this.initial_balance+")";
		        System.out.println(query);
					result = conn.write_query(query);
				if(result) {
					return true;
				}
			}
		}
		
	   
		return false;
	
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getSavings_id() {
		return savings_id;
	}
	public void setSavings_id(String savings_id) {
		this.savings_id = savings_id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Double getInitial_balance() {
		return initial_balance;
	}
	public void setInitial_balance(Double initial_balance) {
		this.initial_balance = initial_balance;
	}
}
