package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DatabaseConnectionManager {

	private Connection conn;

	public Connection getConnection() {

		if (conn == null) {
			this.connect();
		}

		return this.conn;

	}

	private void connect() {

//		try {
//
//			InitialContext ctx = new InitialContext();
//			DataSource ds = (DataSource) ctx
//					.lookup("java:comp/env/jdbc/MySQLDB");
//			conn = ds.getConnection();
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			System.err.println("context lookup failed, trying the old-fashioned way...");

		    try {
		    	
		    	String userName = "sweng_ff";
		    	String password = "rTf4bR6TK15EeHM";
		    	String url = "jdbc:mysql://192.168.1.80/sweng500";
		    	Class.forName("com.mysql.jdbc.Driver").newInstance();

				conn = DriverManager.getConnection(url, userName, password);

		    } catch (Exception e2) {
		    	e2.printStackTrace();
		    }
			
//		}

	}

}
