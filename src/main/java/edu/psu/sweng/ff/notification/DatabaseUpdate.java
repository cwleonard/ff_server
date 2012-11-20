package edu.psu.sweng.ff.notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.dao.DatabaseConnectionManager;
import edu.psu.sweng.ff.dao.PlayerDAO;
import edu.psu.sweng.ff.notification.sports_data_interface.Game;
import edu.psu.sweng.ff.notification.sports_data_interface.PlayerPoints;
import edu.psu.sweng.ff.notification.sports_data_interface.SportsDataInterface;

public class DatabaseUpdate {

	private static final String CLEAR_DEFENSE_POINTS = "DELETE FROM ff_defensepoints WHERE week = ?";
	private static final String STORE_DEFENSE_POINTS = "INSERT INTO ff_defensepoints (NFLteam, week, points) VALUES (?, ?, ?)";
	private static final String CLEAR_PLAYER_POINTS = "DELETE FROM ff_playerpoints WHERE week = ?";
	private static final String STORE_PLAYER_POINTS = "INSERT INTO ff_playerpoints (player_id, week, points) VALUES (?, ?, ?)";
	private static final String UPDATE_ROSTER_POINTS = "UPDATE ff_rosters AS tr INNER JOIN (SELECT r.team_id, r.week, d.points + sum(pp.points) AS total_points FROM ff_rosters AS r INNER JOIN ff_defensepoints AS d ON r.defense_team = d.NFLteam AND r.week = d.week INNER JOIN ff_roster_player AS rp ON r.team_id = rp.team_id AND r.week = rp.week INNER JOIN ff_playerpoints AS pp ON rp.player_id = pp.player_id AND rp.week = pp.week WHERE r.week = ? AND rp.starter = 1 GROUP BY r.team_id, r.week) AS tp ON tr.team_id = tp.team_id AND tr.week = tp.week SET tr.points = tp.total_points";
	private static final String UPDATE_MATCHUP_WINS = "UPDATE ff_rosters AS r INNER JOIN ff_rosters AS o ON r.opponent_id = o.team_id AND r.week = o.week SET r.won = if(r.points > o.points, 1, 0), r.tied = if(r.points = o.points, 1, 0) WHERE r.week = ?";

	// Suppress default constructor
    private DatabaseUpdate() {
        throw new AssertionError();
    }
    
    public static void clearCache() {
    	SportsDataInterface.clearCache();
    }
    
    public static void updateAll() {
    	updateTeamRosters();
    	for (int i = 1; i <= 13; i++)
    		updatePoints(i);
    }
    
    public static void updateTeamRosters() {
    	String[] teamIds = SportsDataInterface.getTeamIds();
    	
    	for (String teamId: teamIds) {
    		updateTeamRoster(teamId);
    	}
    }
    
    private static void updateTeamRoster(String teamId) {
		Player[] players = SportsDataInterface.getPlayers(teamId);
		
		PlayerDAO playerDAO = new PlayerDAO();

		for (Player player: players) {
			playerDAO.store(player);
		}
    }

    //Update points awarded by game performance
    public static void updatePoints(int week) {
    	Game[] games = SportsDataInterface.getGames(week);
    	
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();
		
		PreparedStatement stmt = null;
		
		//Clear Team Defense Points
		try {
			stmt = conn.prepareStatement(CLEAR_DEFENSE_POINTS);
			stmt.setInt(1, week);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Clear Player Points
		try {
			stmt = conn.prepareStatement(CLEAR_PLAYER_POINTS);
			stmt.setInt(1, week);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	for (Game game: games) {
			try {
				//Home Team Defense Points
				stmt = conn.prepareStatement(STORE_DEFENSE_POINTS);
				stmt.setString(1, game.getHomeName());
				stmt.setInt(2, week);
				stmt.setBigDecimal(3, game.getHomeDefensePoints());
				stmt.executeUpdate();
				//Away team Defense Points
				stmt = conn.prepareStatement(STORE_DEFENSE_POINTS);
				stmt.setString(1, game.getAwayName());
				stmt.setInt(2, week);
				stmt.setBigDecimal(3, game.getAwayDefensePoints());
				stmt.executeUpdate();
				//Player points
				PlayerPoints[] players = game.getPlayerPoints();
				for (PlayerPoints playerPoints: players) {
					stmt = conn.prepareStatement(STORE_PLAYER_POINTS);
					stmt.setString(1, playerPoints.getPlayerId());
					stmt.setInt(2, week);
					stmt.setInt(3, playerPoints.getPoints());
					stmt.executeUpdate();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	//Calculate roster points
		try {
			stmt = conn.prepareStatement(UPDATE_ROSTER_POINTS);
			stmt.setInt(1, week);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	//Determine match-up wins and ties
		try {
			stmt = conn.prepareStatement(UPDATE_MATCHUP_WINS);
			stmt.setInt(1, week);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {

    	DatabaseUpdate.updateAll();

    }

}
