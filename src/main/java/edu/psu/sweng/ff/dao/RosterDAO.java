package edu.psu.sweng.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.psu.sweng.ff.common.DatabaseException;
import edu.psu.sweng.ff.common.Matchup;
import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.common.Roster;
import edu.psu.sweng.ff.common.RosterStore;
import edu.psu.sweng.ff.common.Team;

public class RosterDAO extends BaseDAO implements RosterStore {

	private final static String SELECT_BY_TEAM = "SELECT " +
		"starter, player_id FROM ff_roster_player WHERE team_id = ? and week = ?";
	
	private final static String SELECT2 = "SELECT points FROM ff_rosters WHERE team_id = ? AND week = ?";
	
	private final static String STORE = "INSERT INTO ff_rosters (team_id, " +
		"week, defense_team) VALUES (?, ?, ?)";
	
	private final static String STORE_PLAYER = "INSERT INTO ff_roster_player (team_id, " +
			"week, starter, player_id) VALUES (?, ?, ?, ?)";
	
	private final static String CLEAR_ROSTER = "DELETE FROM ff_rosters WHERE " +
		"team_id = ? AND week = ?";

	private final static String CLEAR_PLAYERS = "DELETE FROM ff_roster_player WHERE " +
	"team_id = ? AND week = ?";

	private final static String REMOVE = "DELETE FROM ff_rosters WHERE team_id = ?";
	
	private final static String SET_MATCHUP = "UPDATE ff_rosters SET points = 0, won = 0, tied = 0, " +
			"opponent_id = ? WHERE team_id = ?";
	
	public Roster loadByTeamAndWeek(Team t, int week) throws DatabaseException {

		Roster r = new Roster();
		r.setTeamId(t.getId());
		r.setWeek(week);

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		try {

			stmt1 = conn.prepareStatement(SELECT_BY_TEAM);
			stmt1.setInt(1, t.getId());
			stmt1.setInt(2, week);

			rs = stmt1.executeQuery();

			while(rs.next()) {

				boolean starter = rs.getBoolean(1);
				String pid = rs.getString(2);

				PlayerDAO pDao = new PlayerDAO();
				Player p = pDao.getById(pid, week, conn);

				if (starter) {
					r.addStartingPlayer(p);
				} else {
					r.addBenchPlayer(p);
				}

			}
			
			this.setPoints(r, conn);

		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException();
		} finally {
			close(rs);
			close(stmt1);
			close(conn);
		}

		return r;
		
	}

	private void setPoints(Roster r, Connection conn) throws SQLException {
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			stmt = conn.prepareStatement(SELECT2);
			stmt.setInt(1, r.getTeamId());
			stmt.setInt(2, r.getWeek());
			rs = stmt.executeQuery();
			if (rs.next()) {
				r.setPoints(rs.getInt(1));
			}
			
		} finally {
			close(stmt);
		}
		
	}
	
	public List<Roster> loadByTeam(Team t) throws DatabaseException {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();
		List<Roster> rosters = null;
		try {
			rosters = this.loadByTeam(t, conn);
		} finally {
			close(conn);
		}
		return rosters;
		
	}
	
	public List<Roster> loadByTeam(Team t, Connection conn) throws DatabaseException {

		List<Roster> rosters = new ArrayList<Roster>();

		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		for (int w = 1; w <= 13; w++) {

			Roster r = new Roster();
			r.setTeamId(t.getId());
			r.setWeek(w);

			try {

				stmt1 = conn.prepareStatement(SELECT_BY_TEAM);
				stmt1.setInt(1, t.getId());
				stmt1.setInt(2, w);

				rs = stmt1.executeQuery();

				PlayerDAO pDao = new PlayerDAO();

				while(rs.next()) {

					boolean starter = rs.getBoolean(1);
					String pid = rs.getString(2);

					Player p = pDao.getById(pid, w, conn);
					
					if (starter) {
						r.addStartingPlayer(p);
					} else {
						r.addBenchPlayer(p);
					}

				}
				
				this.setPoints(r, conn);

			} catch (Exception e) {
				e.printStackTrace();
				throw new DatabaseException();
			} finally {
				close(rs);
				close(stmt1);
			}
			
			// only include a roster if it has players
			if (r.getStartingPlayers().size() > 0 || r.getBenchPlayers().size() > 0) {
				rosters.add(r);
			}

		}

		return rosters;
		
	}
	
	public void setMatchup(Matchup matchup) throws DatabaseException {
		
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt1 = null;
		
		try {
			
			stmt1 = conn.prepareStatement(SET_MATCHUP);
			stmt1.setInt(1, matchup.getTeamA());
			stmt1.setInt(2, matchup.getTeamB());
			stmt1.executeUpdate();
			
			stmt1.clearParameters();
			stmt1.setInt(1, matchup.getTeamB());
			stmt1.setInt(2, matchup.getTeamA());
			stmt1.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException();
		} finally {
			close(stmt1);
			close(conn);
		}
		
	}

	public void store(Roster r) throws DatabaseException {

		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();

		PreparedStatement stmt0 = null;
		PreparedStatement stmt1 = null;

		int teamId = r.getTeamId();
		int week = r.getWeek();
		String def = r.getDefenseTeam();
		if (def == null || def.length() == 0) {
			def = "Lions";
		}

		List<Player> starters = r.getStartingPlayers();
		List<Player> bench = r.getBenchPlayers();
		
		try {
			
			stmt0 = conn.prepareStatement(CLEAR_PLAYERS);
			stmt0.setInt(1, teamId);
			stmt0.setInt(2, week);
			stmt0.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException();
		} finally {
			close(stmt0);
		}

		try {
			
			stmt0 = conn.prepareStatement(CLEAR_ROSTER);
			stmt0.setInt(1, teamId);
			stmt0.setInt(2, week);
			stmt0.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException();
		} finally {
			close(stmt0);
		}

		try {
			
			stmt0 = conn.prepareStatement(STORE);
			stmt0.setInt(1, teamId);
			stmt0.setInt(2, week);
			stmt0.setString(3, def);
			stmt0.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException();
		} finally {
			close(stmt0);
		}
		
		for (Iterator<Player> ips = starters.iterator(); ips.hasNext();) {

			Player p = ips.next();
			
			try {

				stmt1 = conn.prepareStatement(STORE_PLAYER);
				stmt1.setInt(1, teamId);
				stmt1.setInt(2, week);
				stmt1.setBoolean(3, true);
				stmt1.setString(4, p.getId());
				
				stmt1.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				throw new DatabaseException();
			} finally {
				close(stmt1);
			}

		}

		for (Iterator<Player> ipb = bench.iterator(); ipb.hasNext();) {

			Player p = ipb.next();
			
			try {

				stmt1 = conn.prepareStatement(STORE_PLAYER);
				stmt1.setInt(1, teamId);
				stmt1.setInt(2, week);
				stmt1.setBoolean(3, false);
				stmt1.setString(4, p.getId());
				
				stmt1.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				throw new DatabaseException();
			} finally {
				close(stmt1);
			}

		}
		
		close(conn);

	}
	
}
