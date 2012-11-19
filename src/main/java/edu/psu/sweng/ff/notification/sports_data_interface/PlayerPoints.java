package edu.psu.sweng.ff.notification.sports_data_interface;

public class PlayerPoints {
	private String playerId = "";
	private int points = 0;
	
	public PlayerPoints(String playerId, int points)
	{
		this.playerId = playerId;
		this.points = points;
	}
	
	public String getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
}
