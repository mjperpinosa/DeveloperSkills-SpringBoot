package com.devs.rest.devsrest.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

	String dbname = "BLUDB";
	
	public String getDbName() {
		return dbname;
	}
	
	public Connection getConnection() {
		Connection connection = null;
		String url = "jdbc:db2://";
		//String host = "127.0.0.1";
		String host = "dashdb-txn-sbox-yp-dal09-03.services.dal.bluemix.net";
//		String port = "3306";
		String port = "50000";
//		String dbname = "developers";
//		String username = "root";
		String username = "dwh64810";
		String password = "fk7t6@qp0xxg3mz8";
		
		try {
//			Class.forName("org.mariadb.jdbc.Driver");
			Class.forName("com.ibm.db2.jcc.DB2Driver");
//			connection = DriverManager.getConnection("jdbc:mariadb://"+host+":"+port+"/"+dbname, username, password);
			connection = DriverManager.getConnection(url+host+":"+port+"/"+dbname, username, password);
			System.out.println("Successfully connected to the cloud.");
		} catch(Exception e) {
			System.out.println("Error in making connection. " + e.getMessage());
			e.printStackTrace();
		}
		
		return connection;
	}
}
