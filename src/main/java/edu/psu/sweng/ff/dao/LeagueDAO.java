package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Member;

public class LeagueDAO extends BaseDAO {

	private final static String LOAD_ALL = "SELECT id, name, commissioner_id FROM ff_leagues";
	
	private final static String SELECT_BY_ID = "SELECT name, commissioner_id " +
		"FROM ff_leagues WHERE id = ?";

	private final static String SELECT_BY_MEMBER_ID = "SELECT l.id, l.name, l.commissioner_id " +
		"FROM ff_leagues l, ff_league_member lm WHERE lm.league_id = l.id AND " +
		"lm.member_id = ?";

	private final static String STORE = "INSERT INTO ff_leagues (name, " +
		"commissioner_id) VALUES (?, ?)";

	private final static String UPDATE = "UPDATE ff_leagues SET name = ?, " +
		"commissioner_id = ? WHERE id = ?";

	private final static String STORE_MEMBER_RELATIONSHIP = "INSERT INTO ff_league_member " +
		"(league_id, member_id) VALUES (?, ?)";
	
	private final static String REMOVE = "DELETE FROM ff_leagues WHERE id = ?";

	public List<League> loadAll() {

		List<League> ll = new ArrayList<League>();
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(LOAD_ALL);
			rs = stmt1.executeQuery();
			
			MemberDAO mdao = new MemberDAO();
			TeamDAO tdao = new TeamDAO();
			DraftDAO ddao = new DraftDAO();
			ScheduleDAO sdao = new ScheduleDAO();

			while (rs.next()) {
				
				League l = new League();
				l.setId(rs.getInt(1));
				l.setName(rs.getString(2));
				l.setCommissioner(mdao.loadByUserName(rs.getString(3), conn));
				l.setDraft(ddao.loadByLeague(l, conn));
				l.setTeams(tdao.loadByLeague(l, conn));
				l.setSchedule(sdao.loadByLeague(l, conn));
				
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
				l.setCommissioner(mdao.loadByUserName(rs.getString(2), conn));
				DraftDAO ddao = new DraftDAO();
				l.setDraft(ddao.loadByLeague(l, conn));
				ScheduleDAO sdao = new ScheduleDAO();
				l.setSchedule(sdao.loadByLeague(l, conn));

				TeamDAO tdao = new TeamDAO();
				l.setTeams(tdao.loadByLeague(l, conn));

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
			stmt1.setString(1, m.getUserName());
			
			rs = stmt1.executeQuery();

			ll = new ArrayList<League>();

			MemberDAO mdao = new MemberDAO();
			TeamDAO tdao = new TeamDAO();
			DraftDAO ddao = new DraftDAO();
			ScheduleDAO sdao = new ScheduleDAO();

			while (rs.next()) {
				
				League l = new League();
				l.setId(rs.getInt(1));
				l.setName(rs.getString(2));
				l.setCommissioner(mdao.loadByUserName(rs.getString(3), conn));
				l.setDraft(ddao.loadByLeague(l, conn));
				l.setTeams(tdao.loadByLeague(l, conn));
				l.setSchedule(sdao.loadByLeague(l, conn));
				
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
	
	public boolean store(League l) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;

		int newId = -1;
		
		try {

			stmt1 = conn.prepareStatement(STORE);
			stmt1.setString(1, l.getName());
			stmt1.setString(2, l.getCommissioner().getUserName());
			stmt1.executeUpdate();
			
			rs = stmt1.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
				newId = rs.getInt(1);
			}
			l.setId(newId);
			l.getDraft().setLeagueId(newId);
			DraftDAO ddao = new DraftDAO();
			ddao.store(l.getDraft());
			
			// commissioner joins league by creating it
			this.joinLeague(newId, l.getCommissioner().getUserName(), conn);
			
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

	public boolean remove(League l) {

		boolean ret = true;
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		try {

			stmt1 = conn.prepareStatement(REMOVE);
			stmt1.setInt(1, l.getId());

			if (stmt1.executeUpdate() != 1) {
				ret = false;
			} else {
				DraftDAO ddao = new DraftDAO();
				ddao.remove(l.getDraft());
				ScheduleDAO sdao = new ScheduleDAO();
				sdao.remove(l.getSchedule());
			}

		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return ret;
		
	}

	public boolean update(League l) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		try {

			stmt1 = conn.prepareStatement(UPDATE);
			stmt1.setString(1, l.getName());
			stmt1.setString(2, l.getCommissioner().getUserName());
			stmt1.setInt(3, l.getId());
			
			stmt1.executeUpdate();
			
			DraftDAO ddao = new DraftDAO();
			ddao.store(l.getDraft());
			
			ScheduleDAO sdao = new ScheduleDAO();
			sdao.store(l.getSchedule());
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return true;
		
	}
	
	public void joinLeague(int lid, String mid) {

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
	
	private void joinLeague(int lid, String mid, Connection conn) {
		
		PreparedStatement stmt1 = null;
		
		try {

			stmt1 = conn.prepareStatement(STORE_MEMBER_RELATIONSHIP);
			stmt1.setInt(1, lid);
			stmt1.setString(2, mid);
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt1);
		}
		
	}

	
}
