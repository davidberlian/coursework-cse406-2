package com.cse406.cw.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Signup {
	private String username;
	private String password;
	private String password2;
	private String savings_id;
	private String email;

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	private String dob;
	
	public Signup() {
		
	}
	
	public Boolean checkfield() {

		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(this.email);

		if(this.username.isEmpty() ||
				this.password.isEmpty() ||
				this.password2.isEmpty() ||
				this.savings_id.isEmpty() ||
				this.dob.isEmpty() || !matcher.matches() ||
			 	!this.password.toString().equals(this.password2.toString())) {
			System.out.println("Dsini "+this.password + " " + this.password2 + " = "+ this.password.toString().equals(this.password2.toString()));
			return false;
		}else {
			return true;
		}
	}
	DB_Connection conn;
	public Boolean validate() {
		System.out.println("EXECUTE SQL");
		try {
		    conn  = new DB_Connection();
			System.out.println("DOB::::"+this.dob);
			String query = "SELECT * FROM user JOIN savings ON savings.user_id = user.id WHERE " +
					"savings.id='"+this.savings_id+"' AND " +
					"savings.dob = '"+this.dob+"' AND savings.user_id = 0";

			System.out.println(query);
			ArrayList<String[]> Response= conn.read_query(query,new String[]{"id"});
			if(Response.isEmpty()) {
				System.out.println("not found");
				return false;
			}else {
				System.out.println(Response.get(0)[0]);
				if(!Response.get(0)[0].isEmpty()) {
					Boolean result = false;		
					result = conn.write_query("INSERT INTO  `user`(`password`, `username`, email ) VALUES ('"+this.password+
					"','"+this.username+"','"+this.email+"')");
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

	public Boolean forgotCheck(){
		try{
			DB_Connection conn = new DB_Connection();
			System.out.println("DOB::::"+this.dob);
			String query = "SELECT * FROM user WHERE " +
					"username='"+this.username+"' AND " +
					"email = '"+this.email+"'";
			System.out.println(query);
			ArrayList<String[]> Response= conn.read_query(query,new String[]{"username"});
			if(Response.isEmpty()) {
				System.out.println("not found");
				return false;
			}else{
				return true;
			}
		}catch (Exception e){
			return false;
		}
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}