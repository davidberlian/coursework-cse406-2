package com.cse406.cw.models;

import java.util.ArrayList;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
  

public class User {
	
	private String username;
	private String password;
	private Double savings;
	private String token;
	private String fullName;

	private String accountNumber;

	public User() {

	}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	public User(String username, String token, String n) {
		this.username = username;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String object) {
		this.username = object;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Double getSavings() {
		this.savings = checkSavings();
		return this.savings;
	}

	public void setSavings(String savings) {
		this.savings = checkSavings();
	}

	public void setToken(String token) {
		this.token = token;
	}
	public String getToken() {
		return token;
	}
	
	DB_Connection conn = null;
	
	public Double checkSavings() {
		System.out.println("EXECUTE SQL");
		try {
			conn = new DB_Connection();
			ArrayList<String[]> Response= conn.read_query(
					"SELECT * "+
					"FROM `savings` "+
					"JOIN user ON user.id = savings.user_id "+
					"WHERE user.username ='"+this.username+"' "
					,new String[]{"last_transaction","total_savings"});
			if(Response.isEmpty()) {
				System.out.println("not found");
			}else {
				System.out.println(Response.get(0)[1]);
				return Double.parseDouble(Response.get(0)[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0D;
	}

	
	public Boolean check_password(){
		System.out.println("EXECUTE SQL");
		try {
			DB_Connection conn = new DB_Connection();
			ArrayList<String[]> Response= conn.read_query("SELECT * FROM user WHERE username='"+this.username+"'",new String[]{"username","password"});
			
			if(Response.isEmpty()) {
				System.out.println("not found");
				return false;
			}else {
				System.out.println(Response.get(0)[1]);
				if(Response.get(0)[1].equals(this.password)) {
					this.password ="";
					if(updateToken(1)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean check_admin_password(){
		System.out.println("EXECUTE SQL");
		try {
			DB_Connection conn = new DB_Connection();
			ArrayList<String[]> Response= conn.read_query("SELECT * FROM admin WHERE username='"+this.username+"'",new String[]{"username","password"});
			System.out.println("CHECK _ ADMIN_ PASSWORD ");
			if(Response.isEmpty()) {
				System.out.println("not found");
				return false;
			}else {
				System.out.println("found");
				System.out.println(Response.get(0)[1]);
				if(Response.get(0)[1].equals(this.password)) {
					System.out.println("correct password");
					this.password ="";
					if(updateToken(0)) {

						System.out.println("updaete token password");
						return true;
					}
				}else {
					System.out.println("wrong password");
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean deleteAccount() {
		
		conn = new DB_Connection();
		
		String query = "UPDATE savings SET user_id = 0 WHERE savings.id = '"+this.accountNumber+"'";
		
		Boolean result = conn.write_query(query);
		
		if(result) {
			query = "DELETE FROM user where user.username = '"+this.username+"'";
			result = conn.write_query(query);
			if(result) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
		
	}
	
	public Boolean checkToken() {

		conn = new DB_Connection();
		
		// remove all old token
		
		String query = "SELECT * FROM `token` JOIN user ON user.username = token.username JOIN savings ON savings.user_id = user.id  WHERE token.id = '"+this.token+
				"' AND active = true";
		
		System.out.println(query);
		
		ArrayList<String[]> result = conn.read_query(query, new String[] {"active", "first_name","last_name","savings.id"});
			
		if(!result.isEmpty()) {
			this.fullName = result.get(0)[1]+" "+result.get(0)[2];
			this.accountNumber =result.get(0)[3];
			return true;
		}else {
			return false;
		}
	}
	
	public Boolean checkTokenAdmin() {

		conn = new DB_Connection();
		
		// remove all old token
		
		String query = "SELECT * FROM `token_admin` JOIN admin ON admin.username = token_admin.username "
				+ "  WHERE token_admin.id = '"+this.token+
				"' AND active = true";
		
		System.out.println(query);
		
		ArrayList<String[]> result = conn.read_query(query, new String[] {});
			
		if(!result.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}

	public Boolean updateToken(int i) {
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		   LocalDateTime now = LocalDateTime.now();  
			   
			   
		   String temp_token = getMd5(dtf.format(now)+" "+username);
		   System.out.println("TOKEN : "+temp_token);
		   
		
			conn = new DB_Connection();
			
			// remove all old token
			Boolean result = false;
			if(i == 1) {
			result = conn.write_query("UPDATE `token` SET `active`=false WHERE username='"+username+"'"
					);
			
			result = conn.write_query("INSERT INTO  `token`(`id`, `username`) VALUES ('"+temp_token+
			"','"+username+"')"
			);
			}else {
				String query = "UPDATE `token_admin` SET `active`=false WHERE username='"+username+"'";
						
				result = conn.write_query(query);

				System.out.println(query);
				result = conn.write_query("INSERT INTO  `token_admin`(`id`, `username`) VALUES ('"+temp_token+
				"','"+username+"')"
				);
			}		
		   
		if(result) {
			System.out.println("156 true");
			this.token=temp_token;
			return true;
		}else {
			System.out.println("160 false");
			return false;
		}
	}
	
	public void removeToken(int i) {
		System.out.println("IM HERE + "+username);
		this.token = null;
		conn = new DB_Connection();
		Boolean result = false;
		if(i == 1) {
			
			result = conn.write_query("UPDATE `token` SET `active`=false WHERE username='"+username+"'");
			
		}else {
			result = conn.write_query("UPDATE `token_admin` SET `active`=false WHERE username='"+username+"'");
			
		}
		if(result) {
			System.out.println("woked + "+username);
		}else {
			System.out.println("not woked + "+username);
			
		}
	}
	
	
	public static String getMd5(String input) 
    { 
        try { 
  
            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5"); 
  
            // digest() method is called to calculate message digest 
            //  of an input digest() return array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
            return hashtext; 
        }  
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    } 
	@Override
	public String toString() {
	    return this.username+"|"+this.token;
	  }



	public String getFullName() {
		return fullName;
	}



	public void setFullName(String fullName) {
		this.fullName = fullName;
	}



	public String getAccountNumber() {
		return accountNumber;
	}



	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


}
