package edu.psu.sweng.ff.notification.sports_data_interface;

import java.math.BigDecimal;

public class Game {
	private String homeId = "";
	private String homeName = "";
	private BigDecimal homeDefensePoints = new BigDecimal("0");
	private String awayId = "";
	private String awayName = "";
	private BigDecimal awayDefensePoints = new BigDecimal("0");
	private Player[] players;
	
	public void setHomeId(String homeTeamId) {
		homeId = homeTeamId;
	}
	
	public String getHomeId() {
		return homeId;
	}
	
	public void setAwayId(String awayTeamId) {
		awayId = awayTeamId;
	}
	
	public String getAwayId() {
		return awayId;
	}

	public String getHomeName() {
		return homeName;
	}

	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public BigDecimal getHomeDefensePoints() {
		return homeDefensePoints;
	}

	public void setHomeDefensePoints(BigDecimal homeDefensePoints) {
		this.homeDefensePoints = homeDefensePoints;
	}

	public String getAwayName() {
		return awayName;
	}

	public void setAwayName(String awayName) {
		this.awayName = awayName;
	}

	public BigDecimal getAwayDefensePoints() {
		return awayDefensePoints;
	}

	public void setAwayDefensePoints(BigDecimal awayDefensePoints) {
		this.awayDefensePoints = awayDefensePoints;
	}

	public Player[] getPlayers() {
		return players;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}
}
