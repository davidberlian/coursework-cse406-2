package com.cse406.cw.models;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DB_Connection{
	

    private Connection con = null;
    private final String url = "jdbc:mysql://";
    private final String serverName = "davidberlian.com";
    private final String portNumber = "3306";
    private final String databaseName = "cse406";
    private final String userName = "David";
    private final String password = "D.berlian19@";
    

	Statement stmt = null;
	ResultSet rs = null;
	
	public Connection getCon() {
		con = this.getConnection();
		return con;
	}
	

    // Constructor
    public DB_Connection() {
    }
    
    public void DisableAutocommit() {
    	try {
    		con=this.getConnection();
			con.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void Commit() {
    	try {    
    		con=this.getConnection();
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void EnableAutocommit() {
    	try {
    		con=this.getConnection();
	    	con.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    private String getConnectionUrl() {
        return url + serverName + ":" + portNumber + "/" + databaseName + "?";
    }

    private java.sql.Connection getConnection() {
        try {
        	 Class.forName("com.mysql.cj.jdbc.Driver");
            con = java.sql.DriverManager.getConnection(getConnectionUrl(), userName, password);
            if (con != null) {
                System.out.println("Connection Successful!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Trace in getConnection() : " + e.getMessage());
        }
        return con;
    }
    

    public void displayDbProperties() {
        java.sql.DatabaseMetaData dm = null;
        java.sql.ResultSet rs = null;
        try {
            con = this.getConnection();
            if (con != null) {
                dm = con.getMetaData();
                System.out.println("Driver Information");
                System.out.println("\tDriver Name: " + dm.getDriverName());
                System.out.println("\tDriver Version: " + dm.getDriverVersion());
                System.out.println("\nDatabase Information ");
                System.out.println("\tDatabase Name: " + dm.getDatabaseProductName());
                System.out.println("\tDatabase Version: " + dm.getDatabaseProductVersion());
                System.out.println("Avalilable Catalogs ");
                rs = dm.getCatalogs();
                while (rs.next()) {
                    System.out.println("\tcatalog: " + rs.getString(1));
                }
                rs.close();
                rs = null;
                closeConnection();
            } else {
                System.out.println("Error: No active Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dm = null;
    }

    
    private void closeConnection() {
        try {
            if (con != null) {
            	System.out.println("Connection Closed!");
                con.close();
            }
            con = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
    	DB_Connection myDbTest = new DB_Connection();
        myDbTest.displayDbProperties();
    }
    
    public static ArrayList<String[]> reader(String query, String[] args) {
    	DB_Connection myDbTest = new DB_Connection();
        return myDbTest.read_query(query, args);
    }
    
    public boolean write_query(String query) {
    	stmt = null;
		rs = null;

		 try {
		     con = this.getConnection();
		     if (con != null) {
		    	 stmt = con.createStatement();
		    	 int a = stmt.executeUpdate(query);
		    	 closeConnection();
		    	 if(a > 0) {
		    		return true;
		    	 }else {
		    		 return false;
		    	 }
			 } else {
			     System.out.println("Error: No active Connection");
			 }
		 } catch (Exception e) {
			     e.printStackTrace();
		 }
		 return false;
    }
    
    public ArrayList<String[]> read_query(String query, String[] args){
    	 stmt = null;
		 rs = null;

		 ArrayList<String[]> anotherList = new ArrayList<String[]>();
		 try {
		     con = this.getConnection();
		     if (con != null) {
		    	 stmt = con.createStatement();
		    	 rs = stmt.executeQuery(query);
		    	 
		    	 if(stmt.execute(query)) {
		    		 rs = stmt.getResultSet();
		    		 System.out.println("113+ "+rs);
		    		 while(rs.next()) {
		    			 
		    			 String[] result = new String[args.length];
		    			 for(int i=0; i<args.length; i++){
		    				 result[i] = rs.getString(args[i]);
		    			 }
		    			 anotherList.add(result);
		    		 }
	    			 
		    		 
		    	 }
				
		    	 closeConnection();
		    	 
			 } else {
			     System.out.println("Error: No active Connection");
			 }
		     return anotherList;
		 } catch (Exception e) {
			     e.printStackTrace();
		 }
		 return anotherList;
    }
    
}