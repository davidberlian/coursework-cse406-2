package com.cse406.cw.models;

import java.util.ArrayList;
import java.util.Locale;
import java.text.NumberFormat;

public class Transaction {
	
	private String datetime;
	private String message;
	private Double amount;
	private String amountstring;
	private String creditdebit;
	private String currentbalance;


	public Transaction(String datetime, String message, String amount) {

		this.datetime = datetime;
		this.message = message;
		System.out.println("nilai amount 0= "+amount);
		if(amount.contains("-")) {
			this.amount = Double.parseDouble(amount.substring(1));
			this.amount *= -1;
			this.creditdebit = "DEBIT";
		}else {
			this.amount = Double.parseDouble(amount);
			this.creditdebit = "CREDIT";
		}
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
		this.amountstring = format.format(this.amount);
	}
	
	public static Double loadTransactionBalance(String username, String month) {
		Double balance = 0D;
		
		try {
			DB_Connection conn = new DB_Connection();
			String query = 
					"SELECT amount FROM month_balance "+
					"JOIN savings ON savings.id = month_balance.savings_id "+
					"JOIN user ON user.id = savings.user_id "+
					" WHERE month = '"+month+"'"+
					"AND user.username = '"+username+"'";
			System.out.println(query);
			ArrayList<String[]> Response= conn.read_query(query,new String[]{"amount"});
			if(Response.isEmpty()) {
				return 0D;
			}else {
				return Double.parseDouble(Response.get(0)[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return balance;
	}
	
	public static ArrayList<String[]> loadTransaction(String username, String time){
		ArrayList<String[]> a = new ArrayList<String[]>();
		System.out.println("EXECUTE SQL");
		try {
			DB_Connection conn = new DB_Connection();
			String query = 
					"SELECT transaction_message,transaction_amount,transaction_time "+
					"FROM `transaction` "+
					"JOIN savings ON savings.id = transaction.savings_id "+
					"JOIN user ON user.id = savings.user_id "+
					"WHERE user.username ='"+username+"' "+
					"AND transaction_time LIKE '"+time+"%'";
			System.out.println(query);
			ArrayList<String[]> Response= conn.read_query(
					query
					,new String[]{"transaction_message","transaction_amount","transaction_time"});
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

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}


	public String getAmountstring() {
		return amountstring;
	}

	public void setAmountstring(String amountstring) {
		this.amountstring = amountstring;
	}

	public String getCreditdebit() {
		return creditdebit;
	}

	public void setCreditdebit(String creditdebit) {
		this.creditdebit = creditdebit;
	}

	public String getCurrentbalance() {
		return currentbalance;
	}

	public void setCurrentbalance(String currentbalance) {
		this.currentbalance = currentbalance;
	}
}

	/*
	 * 
	 * 
	 * 
DELIMITER $$
CREATE EVENT update_monthly_balance
ON SCHEDULE EVERY '1' MONTH
DO CALL check_new_balance;
*/