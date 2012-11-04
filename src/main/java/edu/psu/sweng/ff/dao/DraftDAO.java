package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.psu.sweng.ff.common.Draft;
import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Member;

public class DraftDAO extends BaseDAO {
	
	private final static String CHECK = "SELECT league_id FROM ff_drafts WHERE league_id = ?";

	private final static String SELECT_BY_LEAGUE = "SELECT automatic, round, team_index, member_id "
			+ "FROM ff_drafts WHERE league_id = ?";

	private final static String STORE = "INSERT INTO ff_drafts (league_id, automatic, round, " +
			"team_index, member_id) VALUES (?, ?, ?, ?, ?)";

	private final static String UPDATE = "UPDATE ff_drafts SET automatic = ?, round = ?, "
			+ "team_index = ?, member_id = ? WHERE league_id = ?";

	private final static String REMOVE = "DELETE FROM ff_drafts WHERE league_id = ?";
	
	public Draft loadByLeague(League l) {

		Draft d = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_LEAGUE);
			stmt1.setInt(1, l.getId());
			
			rs = stmt1.executeQuery();
			
			if (rs.next()) {
				
				d = new Draft();
				d.setLeagueId(l.getId());
				d.setAutomatic(rs.getBoolean(1));
				d.setRound(rs.getInt(2));
				d.setTeamIndex(rs.getInt(3));
				MemberDAO mdao = new MemberDAO();
				d.setWaitingFor(mdao.loadByUserName(rs.getString(4)));

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return d;
		
	}
	
	public boolean store(Draft d) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;

		try {
			
			stmt1 = conn.prepareStatement(CHECK);
			stmt1.setInt(1, d.getLeagueId());
			rs = stmt1.executeQuery();

			if (rs.next()) {
				// already there. update.
				stmt2 = conn.prepareStatement(UPDATE);
				stmt2.setBoolean(1, d.isAutomatic());
				stmt2.setInt(2, d.getRound());
				stmt2.setInt(3, d.getTeamIndex());
				Member m = d.getWaitingFor();
				if (m != null) {
					stmt2.setString(4, m.getUserName());
				} else {
					stmt2.setNull(4, java.sql.Types.VARCHAR);
				}
				stmt2.setInt(5, d.getLeagueId());
				stmt2.executeUpdate();
			} else {
				// not there yet. insert.
				stmt2 = conn.prepareStatement(STORE);
				stmt2.setInt(1, d.getLeagueId());
				stmt2.setBoolean(2, d.isAutomatic());
				stmt2.setInt(3, d.getRound());
				stmt2.setInt(4, d.getTeamIndex());
				Member m = d.getWaitingFor();
				if (m != null) {
					stmt2.setString(5, m.getUserName());
				} else {
					stmt2.setNull(5, java.sql.Types.VARCHAR);
				}
				stmt2.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(rs);
			close(stmt1);
			close(stmt2);
			close(conn);
		}
		
		return true;
		
	}

	public boolean remove(Draft d) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;

		try {
			
			stmt1 = conn.prepareStatement(REMOVE);
			stmt1.setInt(1, d.getLeagueId());
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(stmt1);
			close(conn);
		}
		
		return true;
		
	}

}
