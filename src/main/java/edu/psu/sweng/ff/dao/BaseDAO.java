package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BaseDAO {
	
	protected void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	protected void close(Statement s) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	protected void close(Connection c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	protected void rollback(Connection c) {
		if (c != null) {
			try {
				c.rollback();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	protected String prepString(String s) {
		
		String ret = "null";
		if (s != null) {
			ret = s.replaceAll("'", "''");
			ret = "'" + ret + "'";
		}
		return ret;
		
	}
	
	public static void main(String[] args) {
		
		BaseDAO b = new BaseDAO();
		System.out.println(b.prepString("casey's description"));
		
	}
}
