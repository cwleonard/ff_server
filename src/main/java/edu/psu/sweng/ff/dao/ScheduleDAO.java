package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;

import edu.psu.sweng.ff.common.DatabaseException;
import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Matchup;
import edu.psu.sweng.ff.common.Schedule;

public class ScheduleDAO extends BaseDAO {

	private final static String STORE = "INSERT INTO ff_schedule (league_id, week, " +
		"team_a, team_b) VALUES (?, ?, ?, ?)";

	private final static String REMOVE = "DELETE FROM ff_schedule WHERE league_id = ?";
	
	private final static String LOAD = "SELECT week, team_a, team_b FROM " +
		"ff_schedule WHERE league_id = ?";
	
	public Schedule loadByLeague(League l) throws DatabaseException {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();
		Schedule schedule = null;
		try {
			schedule = this.loadByLeague(l, conn);
		} finally {
			close(conn);
		}
		return schedule;
		
	}

	public Schedule loadByLeague(League l, Connection conn) throws DatabaseException {

		Schedule schedule = null;

		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {

			stmt1 = conn.prepareStatement(LOAD);
			stmt1.setInt(1, l.getId());
			
			rs = stmt1.executeQuery();
			
			while (rs.next()) {
				
				if (schedule == null) {
					schedule = new Schedule();
				}
				
				Matchup m = new Matchup();
				m.setWeek(rs.getInt(1));
				m.setTeamA(rs.getInt(2));
				m.setTeamB(rs.getInt(3));
				schedule.getMatchups(m.getWeek()).add(m);
				
			}
			
		} catch (Exception e) {
			throw new DatabaseException();
		} finally {
			close(rs);
			close(stmt1);
		}
		
		return schedule;
		
	}
	
	public boolean store(Schedule s) throws DatabaseException {

		if (s == null) return false;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		
		try {

			// remove first (so we're always replacing)
			stmt2 = conn.prepareStatement(REMOVE);
			stmt2.setInt(1, s.getLeagueId());
			stmt2.executeUpdate();
			
			stmt1 = conn.prepareStatement(STORE);
			
			Collection<Matchup> matchups = s.getAllMatchups();
			Iterator<Matchup> im = matchups.iterator();
			while (im.hasNext()) {
				
				Matchup m = im.next();
				
				stmt1.clearParameters();
				stmt1.setInt(1, s.getLeagueId());
				stmt1.setInt(2, m.getWeek());
				stmt1.setInt(3, m.getTeamA());
				stmt1.setInt(4, m.getTeamB());
				stmt1.executeUpdate();
				
			}
			
		} catch (Exception e) {
			throw new DatabaseException();
		} finally {
			close(stmt1);
			close(stmt2);
			close(conn);
		}
		
		return true;
		
	}

	public boolean remove(Schedule s) throws DatabaseException {

		if (s == null) return false;
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		
		try {

			stmt1 = conn.prepareStatement(REMOVE);
			stmt1.setInt(1, s.getLeagueId());
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			throw new DatabaseException();
		} finally {
			close(stmt1);
			close(conn);
		}
		
		return true;
		
	}

}
