package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Roster;
import edu.psu.sweng.ff.common.Team;

public class TeamDAO extends BaseDAO {

	private final static String SELECT_BY_ID = "SELECT name, logo, " +
		"owner_id, league_id, totalpoints, totalwins, totallosses FROM ff_teams WHERE id = ?";

	private final static String SELECT_BY_LEAGUE = "SELECT id, name, logo, " +
		"owner_id, totalpoints, totalwins, totallosses " +
		"FROM ff_teams WHERE league_id = ?";

	private final static String SELECT_BY_OWNER = "SELECT id, name, logo, owner_id, " +
		"league_id, totalpoints, totalwins, totallosses FROM ff_teams WHERE owner_id = ?";

	private final static String STORE = "INSERT INTO ff_teams (name, logo, " +
		"owner_id, league_id, totalpoints, totalwins, totallosses) VALUES (?, ?, ?, ?, ?, ?, ?)";

	private final static String REMOVE = "DELETE FROM ff_teams WHERE id = ?";
	
	private final static String UPDATE = "UPDATE ff_teams SET name = ?, logo = ?, " +
		"owner_id = ?, totalpoints = ?, totalwins = ?, totallosses = ? WHERE id = ?";
	
	private final static String GET_POINTS = "SELECT points FROM ff_rosters WHERE " +
			"team_id = ? AND week = (SELECT week FROM ff_season WHERE current = 1)";

	public List<Team> loadByLeague(League l) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();
		List<Team> teams = null;
		try {
			teams = this.loadByLeague(l, conn);
		} finally {
			close(conn);
		}
		return teams;
		
	}
	
	public List<Team> loadByLeague(League l, Connection conn) {
		
		List<Team> tl = new ArrayList<Team>();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_LEAGUE);
			stmt1.setInt(1, l.getId());
			
			rs = stmt1.executeQuery();
			
			MemberDAO mdao = new MemberDAO();
			RosterDAO rdao = new RosterDAO();
			while(rs.next()) {
				
				Team t = new Team();
				t.setId(rs.getInt(1));
				t.setName(rs.getString(2));
				t.setLogo(rs.getString(3));
				t.setOwner(mdao.loadByUserName(rs.getString(4), conn));
				t.setPoints(rs.getInt(5));
				t.setWins(rs.getInt(6));
				t.setLosses(rs.getInt(7));
				t.setLeagueId(l.getId());
				t.setRosters(rdao.loadByTeam(t, conn));
				t.setPointsThisWeek(this.pointsThisWeek(t.getId(), conn));
				tl.add(t);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
		}
		
		return tl;
		
	}

	public List<Team> loadByOwner(Member m) {
		
		List<Team> tl = new ArrayList<Team>();
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_OWNER);
			stmt1.setString(1, m.getUserName());
			
			rs = stmt1.executeQuery();
			
			MemberDAO mdao = new MemberDAO();
			RosterDAO rdao = new RosterDAO();
			while(rs.next()) {
				
				Team t = new Team();
				t.setId(rs.getInt(1));
				t.setName(rs.getString(2));
				t.setLogo(rs.getString(3));
				t.setOwner(mdao.loadByUserName(rs.getString(4), conn));
				t.setLeagueId(rs.getInt(5));
				t.setPoints(rs.getInt(6));
				t.setWins(rs.getInt(7));
				t.setLosses(rs.getInt(8));
				t.setRosters(rdao.loadByTeam(t, conn));
				t.setPointsThisWeek(this.pointsThisWeek(t.getId(), conn));
				tl.add(t);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return tl;
		
	}

	private int pointsThisWeek(int id, Connection conn) throws SQLException {
		
		int p = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			
			stmt = conn.prepareStatement(GET_POINTS);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				p = rs.getInt(1);
			}
			
		} finally {
			close(rs);
			close(stmt);
		}
		return p;
		
	}
	
	public Team loadById(int id) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();
		Team t = null;
		try {
			t = this.loadById(id, conn);
		} finally {
			close(conn);
		}
		return t;
		
	}
	
	public Team loadById(int id, Connection conn) {
		
		Team t = null;

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_ID);
			stmt1.setInt(1, id);
			
			rs = stmt1.executeQuery();
			
			if (rs.next()) {
				
				MemberDAO mdao = new MemberDAO();
				RosterDAO rdao = new RosterDAO();
				
				t = new Team();
				t.setId(id);
				t.setName(rs.getString(1));
				t.setLogo(rs.getString(2));
				t.setOwner(mdao.loadByUserName(rs.getString(3), conn));
				t.setLeagueId(rs.getInt(4));
				t.setPoints(rs.getInt(5));
				t.setWins(rs.getInt(6));
				t.setLosses(rs.getInt(7));
				t.setRosters(rdao.loadByTeam(t, conn));
				t.setPointsThisWeek(this.pointsThisWeek(t.getId(), conn));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
		}
		
		return t;
		
	}
	
	public int store(Team t) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		int newId = -1;
		
		try {

			stmt1 = conn.prepareStatement(STORE);
			stmt1.setString(1, t.getName());
			stmt1.setString(2, t.getLogo());
			stmt1.setString(3, t.getOwner().getUserName());
			stmt1.setInt(4, t.getLeagueId());
			stmt1.setInt(5, t.getPoints());
			stmt1.setInt(6, t.getWins());
			stmt1.setInt(7, t.getLosses());
			
			stmt1.executeUpdate();

			rs = stmt1.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
				newId = rs.getInt(1);
			}
			t.setId(newId);

			RosterDAO rdao = new RosterDAO();
			Iterator<Roster> ri = t.getRosters().iterator();
			while (ri.hasNext()) {
				Roster r = ri.next();
				r.setTeamId(t.getId());
				rdao.store(r);
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
	
	public boolean remove(Team t) {

		boolean ret = true;
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		
		try {

			stmt1 = conn.prepareStatement(REMOVE);
			stmt1.setInt(1, t.getId());
			
			if (stmt1.executeUpdate() != 1) {
				ret = false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} finally {
			close(stmt1);
			close(conn);
		}
		
		return ret;
		
	}

	
	public boolean update(Team t) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		
		try {

			stmt1 = conn.prepareStatement(UPDATE);
			stmt1.setString(1, t.getName());
			stmt1.setString(2, t.getLogo());
			stmt1.setString(3, t.getOwner().getUserName());
			stmt1.setInt(4, t.getPoints());
			stmt1.setInt(5, t.getWins());
			stmt1.setInt(6, t.getLosses());
			stmt1.setInt(7, t.getId());
			
			stmt1.executeUpdate();
			
			RosterDAO rdao = new RosterDAO();
			Iterator<Roster> ri = t.getRosters().iterator();
			while (ri.hasNext()) {
				Roster r = ri.next();
				r.setTeamId(t.getId());
				rdao.store(r);
			}
			
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
