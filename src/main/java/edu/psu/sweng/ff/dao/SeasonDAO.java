package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.psu.sweng.ff.common.DatabaseException;

public class SeasonDAO extends BaseDAO {

	private final static String SELECT_WEEK = "SELECT week FROM " +
			"ff_season WHERE current = 1";

	private final static String INC_WEEK = "UPDATE ff_season SET " +
			"week = week + 1 WHERE current = 1";

	public int getCurrentWeek() throws DatabaseException {

		int week = 0;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		try {

			stmt1 = conn.prepareStatement(SELECT_WEEK);
			rs = stmt1.executeQuery();

			if (rs.next()) {
				week = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}

		return week;
		
	}
	
	public void nextWeek() throws DatabaseException {
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;

		try {

			stmt1 = conn.prepareStatement(INC_WEEK);
			stmt1.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException();
		} finally {
			close(stmt1);
			close(conn);
		}
		
	}
	
	
}
