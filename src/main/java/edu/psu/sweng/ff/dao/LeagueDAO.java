package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Member;

public class LeagueDAO extends BaseDAO {


	private final static String SELECT_BY_ID = "SELECT name, commissioner, autodraft, " +
		"week, season FROM leagues WHERE id = ?";

	private final static String SELECT_BY_MEMBER_ID = "SELECT l.id, l.name, l.commissioner, l.autodraft, " +
		"l.week, l.season FROM leagues l, league_member lm WHERE lm.league_id = l.id AND " +
		"lm.member_id = ?";

	private final static String STORE = "INSERT INTO leagues (name, commissioner, autodraft, " +
		"week, season) VALUES (?, ?, ?, ?, ?)";

	private final static String UPDATE = "UPDATE leagues SET name = ?, commissioner = ?, " +
		"autodraft = ?, week = ?, season = ? WHERE id = ?";
	
	private final static String STORE_MEMBER_RELATIONSHIP = "INSERT INTO league_member " +
		"(league_id, member_id) VALUES (?, ?)";
	
	public List<League> loadAll() {

		List<League> l = new ArrayList<League>();
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return l;
		
	}
	
	public League loadById(int id) {

		League l = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_ID);
			stmt1.setInt(1, id);
			
			rs = stmt1.executeQuery();
			
			if (rs.next()) {
				
				l = new League();
				l.setId(id);
				l.setName(rs.getString(1));
				MemberDAO mdao = new MemberDAO();
				l.setCommissioner(mdao.loadById(rs.getInt(2)));
				l.setAutoDraft(rs.getBoolean(3));
				l.setWeek(rs.getInt(4));
				l.setSeason(null);

				TeamDAO tdao = new TeamDAO();
				l.setTeams(tdao.loadByLeague(l));

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return l;
		
	}
	
	public List<League> loadByMember(Member m) {
		
		List<League> ll = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_MEMBER_ID);
			stmt1.setInt(1, m.getId());
			
			rs = stmt1.executeQuery();

			ll = new ArrayList<League>();

			MemberDAO mdao = new MemberDAO();
			TeamDAO tdao = new TeamDAO();

			while (rs.next()) {
				
				League l = new League();
				l.setId(rs.getInt(1));
				l.setName(rs.getString(2));
				l.setCommissioner(mdao.loadById(rs.getInt(3)));
				l.setAutoDraft(rs.getBoolean(4));
				l.setWeek(rs.getInt(5));
				l.setSeason(null);
				
				l.setTeams(tdao.loadByLeague(l));
				
				ll.add(l);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return ll;
		
	}
	
	public int store(League l) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;

		int newId = -1;
		
		try {

			stmt1 = conn.prepareStatement(STORE);
			stmt1.setString(1, l.getName());
			stmt1.setInt(2, l.getCommissioner().getId());
			stmt1.setBoolean(3, l.isAutoDraft());
			stmt1.setInt(4, l.getWeek());
			stmt1.setInt(5, 0); //TODO: season - fix this
			stmt1.executeUpdate();
			
			rs = stmt1.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
				newId = rs.getInt(1);
			}
			
			// commissioner is currently the only member of this league
			stmt2 = conn.prepareStatement(STORE_MEMBER_RELATIONSHIP);
			stmt2.setInt(1, newId);
			stmt2.setInt(2, l.getCommissioner().getId());
			stmt2.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(stmt2);
			close(conn);
		}
		
		return newId;
		
	}

	public void update(League l) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		try {

			stmt1 = conn.prepareStatement(UPDATE);
			stmt1.setString(1, l.getName());
			stmt1.setInt(2, l.getCommissioner().getId());
			stmt1.setBoolean(3, l.isAutoDraft());
			stmt1.setInt(4, l.getWeek());
			stmt1.setInt(5, 0); //TODO: season - fix this
			
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return;
		
	}

	
}