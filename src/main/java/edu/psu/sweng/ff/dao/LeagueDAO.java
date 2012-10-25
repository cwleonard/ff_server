package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.psu.sweng.ff.common.Draft;
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
	
	private final static String LOAD_DRAFT_BY_LEAGUE_ID = "SELECT id, automatic, round, team_index, member_id " +
			"FROM drafts WHERE league_id = ?";

	private final static String SAVE_DRAFT = "INSERT INTO drafts (id, automatic, round, team_index, member_id) " +
		"VALUES (?, ?, ?, ?, ?)";

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
				Draft d = this.loadDraft(id);
				if (d != null) {
					d.setLeague(l);
					l.setDraft(d);
				}

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
	
	
	
	public Draft loadDraft(int leagueId) {
		
		Draft d = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(LOAD_DRAFT_BY_LEAGUE_ID);
			stmt1.setInt(1, leagueId);
			
			rs = stmt1.executeQuery();

			if (rs.next()) {
				
				d = new Draft();
				d.setAutomatic(rs.getBoolean(2));
				d.setRound(rs.getInt(3));
				d.setTeamIndex(rs.getInt(4));
				MemberDAO mdao = new MemberDAO();
				d.setWaitingFor(mdao.loadById(rs.getInt(5)));
				
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
				Draft d = this.loadDraft(l.getId());
				if (d != null) {
					d.setLeague(l);
					l.setDraft(d);
				}
				
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
			
			// commissioner joins league by creating it
			this.joinLeague(newId, l.getCommissioner().getId(), conn);
			
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
	
	public void joinLeague(int lid, int mid) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();
		try {
			this.joinLeague(lid, mid, conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		
	}
	
	private void joinLeague(int lid, int mid, Connection conn) {
		
		PreparedStatement stmt1 = null;
		
		try {

			stmt1 = conn.prepareStatement(STORE_MEMBER_RELATIONSHIP);
			stmt1.setInt(1, lid);
			stmt1.setInt(2, mid);
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt1);
		}
		
	}

	
}
