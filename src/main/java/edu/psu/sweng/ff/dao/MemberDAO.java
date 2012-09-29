package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.psu.sweng.ff.common.Member;

public class MemberDAO extends BaseDAO {

	private final static String AUTH = "SELECT token FROM members " +
	    "WHERE username = ? AND passwordhash = ?";
	
	private final static String SELECT_BY_ID = "SELECT firstname, lastname, " +
		"username, email, mobilenumber, hideemail, hidename, " +
		"passwordhash, token FROM members WHERE id = ?";

	private final static String SELECT_BY_USERNAME = "SELECT id, firstname, lastname, " +
		"username, email, mobilenumber, hideemail, hidename, " +
		"passwordhash, token FROM members WHERE username = ?";

	private final static String SELECT_BY_TOKEN = "SELECT id, firstname, lastname, " +
		"username, email, mobilenumber, hideemail, hidename, " +
		"passwordhash FROM members WHERE token = ?";
	
	private final static String STORE = "INSERT INTO members (firstname, lastname, username, " +
			"email, mobilenumber, hideemail, hidename, passwordhash, token) VALUES (" +
			"?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private final static String UPDATE = "UPDATE members SET firstname = ?, lastname = ?, " +
		"username = ?, email = ?, mobilenumber = ?, hideemail = ?, hidename = ?, " +
		"passwordhash = ?, token = ? WHERE id = ?";

	public String authenticateUser(String u, String p) {

		String token = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(AUTH);
			stmt1.setString(1, u);
			stmt1.setString(2, p);
			
			rs = stmt1.executeQuery();
			
			if(rs.next()) {
				token = rs.getString(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return token;

	}
	
	public Member loadById(int id) {

		Member m = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_ID);
			stmt1.setInt(1, id);
			
			rs = stmt1.executeQuery();
			
			while(rs.next()) {
				
				m = new Member();
				m.setId(id);
				m.setFirstName(rs.getString(1));
				m.setLastName(rs.getString(2));
				m.setUserName(rs.getString(3));
				m.setEmail(rs.getString(4));
				m.setMobileNumber(rs.getString(5));
				m.setHideEmail(rs.getBoolean(6));
				m.setHideName(rs.getBoolean(7));
				m.setPasswordHash(rs.getString(8));
				m.setAccessToken(rs.getString(9));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return m;
		
	}
	
	public Member loadByUserName(String un) {
		
		Member m = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_USERNAME);
			stmt1.setString(1, un);
			
			rs = stmt1.executeQuery();
			
			while(rs.next()) {
				
				m = new Member();
				m.setId(rs.getInt(1));
				m.setFirstName(rs.getString(2));
				m.setLastName(rs.getString(3));
				m.setUserName(rs.getString(4));
				m.setEmail(rs.getString(5));
				m.setMobileNumber(rs.getString(6));
				m.setHideEmail(rs.getBoolean(7));
				m.setHideName(rs.getBoolean(8));
				m.setPasswordHash(rs.getString(9));
				m.setAccessToken(rs.getString(10));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return m;
		
	}
	
	public Member loadByToken(String t) {

		Member m = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_TOKEN);
			stmt1.setString(1, t);
			
			rs = stmt1.executeQuery();
			
			while(rs.next()) {
				
				m = new Member();
				m.setId(rs.getInt(1));
				m.setFirstName(rs.getString(2));
				m.setLastName(rs.getString(3));
				m.setUserName(rs.getString(4));
				m.setEmail(rs.getString(5));
				m.setMobileNumber(rs.getString(6));
				m.setHideEmail(rs.getBoolean(7));
				m.setHideName(rs.getBoolean(8));
				m.setPasswordHash(rs.getString(9));
				m.setAccessToken(t);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return m;
	
	}
	
	public int store(Member m) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		int newId = -1;
		
		try {

			stmt1 = conn.prepareStatement(STORE);
			stmt1.setString(1, m.getFirstName());
			stmt1.setString(2, m.getLastName());
			stmt1.setString(3, m.getUserName());
			stmt1.setString(4, m.getEmail());
			stmt1.setString(5, m.getMobileNumber());
			stmt1.setBoolean(6, m.isHideEmail());
			stmt1.setBoolean(7, m.isHideName());
			stmt1.setString(8, m.getPasswordHash());
			stmt1.setString(9, m.getAccessToken());
			
			stmt1.executeUpdate();
			
			rs = stmt1.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
				newId = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return newId;
		
	}
	
	public void update(Member m) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		
		try {

			stmt1 = conn.prepareStatement(UPDATE);
			stmt1.setString(1, m.getFirstName());
			stmt1.setString(2, m.getLastName());
			stmt1.setString(3, m.getUserName());
			stmt1.setString(4, m.getEmail());
			stmt1.setString(5, m.getMobileNumber());
			stmt1.setBoolean(6, m.isHideEmail());
			stmt1.setBoolean(7, m.isHideName());
			stmt1.setString(8, m.getPasswordHash());
			stmt1.setString(9, m.getAccessToken());
			stmt1.setInt(10, m.getId());
			
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt1);
			close(conn);
		}
		
		return;
		
	}
	
}
