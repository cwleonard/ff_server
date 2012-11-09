package edu.psu.sweng.ff.notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.dao.DatabaseConnectionManager;
import edu.psu.sweng.ff.dao.PlayerDAO;
import edu.psu.sweng.ff.notification.sports_data_interface.Game;
import edu.psu.sweng.ff.notification.sports_data_interface.SportsDataInterface;

public class DatabaseUpdate {

	//private static final String CLEAR_DEFENSE_POINTS = "UPDATE ff_rosters SET defense_points = ? WHERE week = ? AND defense_team = ?";
	//private static final String UPDATE_ROSTER_POINTS = "UPDATE ff_rosters SET points = defense_points + (SELECT sum()) WHERE week = ? AND defense_team = ?";
	
    // Suppress default constructor
    private DatabaseUpdate() {
        throw new AssertionError();
    }
    
    public static void clearCache() {
    	SportsDataInterface.clearCache();
    }
    
    public static void updateAll() {
    	updateTeamRosters();
    	updatePoints(1);
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

    public static void updatePoints(int week) {
    	Game[] games = SportsDataInterface.getGames(week);
    	
		DatabaseConnectionManager dbcm = new DatabaseConnectionManager();
		Connection conn = dbcm.getConnection();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
    	
		//Update points awarded by game performance
    	for (Game game: games) {
			//try {
				//Home Team Defense Points
				//stmt = conn.prepareStatement(UPDATE_DEFENSE_POINTS);
				//stmt.setBigDecimal(1, game.getHomeDefensePoints());
				//stmt.setInt(2, week);
				//stmt.setString(3, game.getHomeName());
				//rs = stmt.executeQuery();
				//Away team Defense Points
				//stmt = conn.prepareStatement(UPDATE_DEFENSE_POINTS);
				//stmt.setBigDecimal(1, game.getAwayDefensePoints());
				//stmt.setInt(2, week);
				//stmt.setString(3, game.getAwayName());
				//rs = stmt.executeQuery();
			//} catch (SQLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//}
    	}
    	
    	//Calculate roster points
    	
    }
    
    public static void main(String[] args) {

    	DatabaseUpdate.updateAll();

    }

}
