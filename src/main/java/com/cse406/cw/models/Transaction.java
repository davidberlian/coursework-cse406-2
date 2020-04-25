package com.cse406.cw.models;

import java.util.ArrayList;

public class Transaction {
	
	private String datetime;
	private String message;
	private String amount;
	
	public Transaction(String datetime, String message, String amount) {
		this.datetime = datetime;
		this.message = message;
		this.amount = amount;
	}
	
	public static ArrayList<String[]> loadTransaction(String username){
		ArrayList<String[]> a = new ArrayList<String[]>();
		System.out.println("EXECUTE SQL");
		try {
			DB_Connection conn = new DB_Connection();
			String query = 
					"SELECT * "+
					"FROM `transaction` "+
					"JOIN savings ON savings.id = transaction.savings_id "+
					"JOIN user ON user.id = savings.user_id "+
					"WHERE user.username ='"+username+"' ";
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

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}