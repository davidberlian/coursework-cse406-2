package com.cse406.cw.models;

import java.util.ArrayList;


public class Signup {
	private String username;
	private String password;
	private String password2;
	private String savings_id;
	private String dob;
	
	public Signup() {
		
	}
	
	public Boolean checkfield() {
		if(this.username.isBlank() ||
				this.password.isBlank() ||
				this.password2.isBlank() ||
				this.savings_id.isBlank() ||
				this.dob.isBlank()) {
			return false;
		}else {
			return true;
		}
	}
	
	public Boolean validate() {
		System.out.println("EXECUTE SQL");
		try {
		    DB_Connection conn = new DB_Connection();
			String query = "SELECT * FROM user JOIN savings ON savings.user_id = user.id WHERE savings.id='"+this.savings_id+"' AND savings.user_id = 0";
			System.out.println(query);
			ArrayList<String[]> Response= conn.read_query(query,new String[]{"id"});
			if(Response.isEmpty()) {
				System.out.println("not found");
				return false;
			}else {
				System.out.println(Response.get(0)[0]);
				if(!Response.get(0)[0].isBlank()) {
					Boolean result = false;		
					result = conn.write_query("INSERT INTO  `user`(`password`, `username`) VALUES ('"+this.password+
					"','"+this.username+"')");
					if(result) {						
						Response= conn.read_query("SELECT * FROM user WHERE username='"+this.username+"'",new String[]{"id"});
						if(Response.isEmpty()) {
							return false;
						}else {
							result = conn.write_query("UPDATE  `savings` SET user_id = '"+Response.get(0)[0]+"' WHERE savings.id = "+this.savings_id);
							if(result) {
								return true;
							}else {
								return false;
							}
						}
					}else {
						return false;
					}
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword2() {
		return password2;
	}
	public void setPassword2(String password2) {
		this.password2 = password2;
	}
	public String getSavings_id() {
		return savings_id;
	}
	public void setSavings_id(String savings_id) {
		this.savings_id = savings_id;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
}