package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Team;

public class TeamDAO extends BaseDAO {

	private final static String SELECT_BY_ID = "SELECT name, logo, " +
		"owner FROM teams WHERE id = ?";
	
	private final static String SELECT_BY_LEAGUE = "SELECT t.id, t.name, t.logo, t.owner " +
		"FROM teams t, league_team lt WHERE t.id = lt.team_id AND lt.league_id = ?";

	private final static String SELECT_BY_OWNER = "SELECT t.id, t.name, t.logo, t.owner, " +
		"lt.league_id FROM teams t, league_team lt WHERE t.id = lt.team_id AND t.owner = ?";

	private final static String STORE = "INSERT INTO teams (name, logo, owner) VALUES (" +
		"?, ?, ?)";
	
	private final static String UPDATE = "UPDATE teams SET name = ?, logo = ?, " +
		"owner = ? WHERE id = ?";

	private final static String ADD_TEAM = "INSERT INTO league_team (league_id, team_id) " +
		"VALUES (?, ?)";

	public List<Team> loadByLeague(League l) {
		
		List<Team> tl = new ArrayList<Team>();
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_LEAGUE);
			stmt1.setInt(1, l.getId());
			
			rs = stmt1.executeQuery();
			
			MemberDAO mdao = new MemberDAO();
			while(rs.next()) {
				
				Team t = new Team();
				t.setId(rs.getInt(1));
				t.setName(rs.getString(2));
				t.setLogo(rs.getString(3));
				t.setOwner(mdao.loadById(rs.getInt(4)));
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

	public List<Team> loadByOwner(Member m) {
		
		List<Team> tl = new ArrayList<Team>();
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_OWNER);
			stmt1.setInt(1, m.getId());
			
			rs = stmt1.executeQuery();
			
			MemberDAO mdao = new MemberDAO();
			while(rs.next()) {
				
				Team t = new Team();
				t.setId(rs.getInt(1));
				t.setName(rs.getString(2));
				t.setLogo(rs.getString(3));
				t.setOwner(mdao.loadById(rs.getInt(4)));
				t.setLeagueId(rs.getInt(5));
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

	public Team loadById(int id) {

		Team t = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(SELECT_BY_ID);
			stmt1.setInt(1, id);
			
			rs = stmt1.executeQuery();
			
			if (rs.next()) {
				
				MemberDAO mdao = new MemberDAO();
				
				t = new Team();
				t.setId(id);
				t.setName(rs.getString(1));
				t.setLogo(rs.getString(2));
				t.setOwner(mdao.loadById(rs.getInt(3)));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
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
			stmt1.setInt(3, t.getOwner().getId());
			
			stmt1.executeUpdate();
			
			rs = stmt1.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
				newId = rs.getInt(1);
			}
			
			this.addTeamToLeague(newId, t.getLeagueId());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return newId;
		
	}
	
	private void addTeamToLeague(int t, int l) {
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		try {

			stmt1 = conn.prepareStatement(ADD_TEAM);
			stmt1.setInt(1, l);
			stmt1.setInt(2, t);
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
	}
	

	public void update(Team t) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		
		try {

			stmt1 = conn.prepareStatement(UPDATE);
			stmt1.setString(1, t.getName());
			stmt1.setString(2, t.getLogo());
			stmt1.setInt(3, t.getOwner().getId());
			stmt1.setInt(4, t.getId());
			
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
