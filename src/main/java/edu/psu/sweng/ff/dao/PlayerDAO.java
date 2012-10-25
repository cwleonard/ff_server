package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.common.PlayerSource;

public class PlayerDAO extends BaseDAO implements PlayerSource {

	private final static String STORE = "INSERT INTO players (guid, firstname, " +
		"lastname, birthdate, height, weight, college, nfl_team, " +
		"position) VALUES (?,?,?,?,?,?,?,?,?)";
	
	private final static String LOAD_BY_ID = "SELECT firstname, lastname, " +
		"birthdate, height, weight, college, nfl_team, position " +
		"FROM players WHERE guid = ?";
	
	private final static String LOAD_BY_TYPE = "SELECT guid, firstname, lastname, " +
		"birthdate, height, weight, college, nfl_team, position " +
		"FROM players WHERE position = ?";

	private final static String LOAD_BY_TYPES = "SELECT guid, firstname, lastname, " +
		"birthdate, height, weight, college, nfl_team, position " +
		"FROM players WHERE position IN ";
	
	private final static String RESTRICT = " AND guid NOT IN (SELECT player_id " +
			"FROM rosters, league_team WHERE league_id = ? AND " +
			"rosters.team_id = league_team.team_id)";

	public List<Player> getByType(String type) {
		return this.getByType(-1, type);
	}
	
	public List<Player> getByType(String... types) {
		return this.getByType(-1, types);
	}
	
	public List<Player> getByType(int leagueId, String type) {

		List<Player> lp = new ArrayList<Player>();

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			if (leagueId != -1) {
				stmt1 = conn.prepareStatement(LOAD_BY_TYPE + RESTRICT);
				stmt1.setString(1, type);
				stmt1.setInt(2, leagueId);
			} else {
				stmt1 = conn.prepareStatement(LOAD_BY_TYPE);
				stmt1.setString(1, type);
			}
			
			rs = stmt1.executeQuery();
			
			while (rs.next()) {
				
				Player p = new Player();
				p.setId(rs.getString(1));
				p.setFirstName(rs.getString(2));
				p.setLastName(rs.getString(3));
				p.setBirthdate(new java.util.Date(rs.getDate(4).getTime()));
				p.setHeight(rs.getInt(5));
				p.setWeight(rs.getInt(6));
				p.setCollege(rs.getString(7));
				p.setNflTeam(rs.getString(8));
				p.setPosition(rs.getString(9));
				lp.add(p);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return lp;

	}

	public List<Player> getByType(int leagueId, String... types) {

		List<Player> lp = new ArrayList<Player>();

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		StringBuilder typeParams = new StringBuilder("(");
		for (int i = 0; i < types.length; i++) {
			if (i > 0) typeParams.append(",");
			typeParams.append("?");
		}
		typeParams.append(")");
		
		try {

			if (leagueId != -1) {
				stmt1 = conn.prepareStatement(LOAD_BY_TYPES + typeParams.toString() + RESTRICT);
				for (int i = 0; i < types.length; i++) {
					stmt1.setString(i+1, types[i]);
				}
				stmt1.setInt(types.length+1, leagueId);
			} else {
				stmt1 = conn.prepareStatement(LOAD_BY_TYPES + typeParams.toString());
				for (int i = 0; i < types.length; i++) {
					stmt1.setString(i+1, types[i]);
				}
			}
			
			rs = stmt1.executeQuery();
			
			while (rs.next()) {
				
				Player p = new Player();
				p.setId(rs.getString(1));
				p.setFirstName(rs.getString(2));
				p.setLastName(rs.getString(3));
				p.setBirthdate(new java.util.Date(rs.getDate(4).getTime()));
				p.setHeight(rs.getInt(5));
				p.setWeight(rs.getInt(6));
				p.setCollege(rs.getString(7));
				p.setNflTeam(rs.getString(8));
				p.setPosition(rs.getString(9));
				lp.add(p);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		
		return lp;
		
	}
	
	public Player getById(String id) {
		
		Player p = null;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(LOAD_BY_ID);
			stmt1.setString(1, id);
			
			rs = stmt1.executeQuery();
			
			if (rs.next()) {
				
				p = new Player();
				p.setId(id);
				p.setFirstName(rs.getString(1));
				p.setLastName(rs.getString(2));
				p.setBirthdate(new java.util.Date(rs.getDate(3).getTime()));
				p.setHeight(rs.getInt(4));
				p.setWeight(rs.getInt(5));
				p.setCollege(rs.getString(6));
				p.setNflTeam(rs.getString(7));
				p.setPosition(rs.getString(8));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}
		
		return p;
		
	}
	
	public void store(Player p) {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;

		try {

			stmt1 = conn.prepareStatement(STORE);
			stmt1.setString(1, p.getId());
			stmt1.setString(2, p.getFirstName());
			stmt1.setString(3, p.getLastName());
			stmt1.setDate(4, new Date(p.getBirthdate().getTime()));
			stmt1.setInt(5, p.getHeight());
			stmt1.setInt(6, p.getWeight());
			stmt1.setString(7, p.getCollege());
			stmt1.setString(8, p.getNflTeam());
			stmt1.setString(9, p.getPosition());
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt1);
			close(conn);
		}
		
	}
	

}
