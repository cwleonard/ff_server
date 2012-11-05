package edu.psu.sweng.ff.notification;

import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.dao.PlayerDAO;

public class DatabaseUpdate {
	
    // Suppress default constructor
    private DatabaseUpdate() {
        throw new AssertionError();
    }
    
    public static void clearCache() {
    	SportsDataInterface.clearCache();
    }
    
    public static void updateAll() {
    	updateTeamRosters();
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

    public static void main(String[] args) {
    	
    	DatabaseUpdate.updateAll();
    	
    }
    
}
