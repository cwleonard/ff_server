package edu.psu.sweng.ff.schedule;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Matchup;
import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Schedule;
import edu.psu.sweng.ff.common.Team;
import edu.psu.sweng.ff.notification.Email;
import edu.psu.sweng.ff.notification.EmailNotifier;

public class ScheduleGeneratorTest {

	@Test
	public void testGenerate() {

		League league = new League();
		league.setId(9999);
		
		Team team1 = new Team();
		team1.setId(1);
		team1.setLeagueId(league.getId());

		Team team2 = new Team();
		team2.setId(2);
		team2.setLeagueId(league.getId());

		Team team3 = new Team();
		team3.setId(3);
		team3.setLeagueId(league.getId());

		Team team4 = new Team();
		team4.setId(4);
		team4.setLeagueId(league.getId());

		Team team5 = new Team();
		team5.setId(5);
		team5.setLeagueId(league.getId());

		league.getTeams().add(team1);
		league.getTeams().add(team2);
		league.getTeams().add(team3);
		league.getTeams().add(team4);
		league.getTeams().add(team5);
		
		ScheduleGenerator sgen = new ScheduleGenerator();
		Schedule sched = sgen.generateSchedule(league);
		
		// make sure we get a schedule
		assertFalse(sched == null);
		
		int teams[][] = new int[5][5];
		
		for (int w = 1; w < 12; w++) {
			
			List<Matchup> week = sched.getMatchups(w);
			assertTrue(week.size() > 0);
			
			Iterator<Matchup> mit = week.iterator();
			while (mit.hasNext()) {
				
				Matchup m = mit.next();
				assertEquals(w, m.getWeek());
				assertFalse(m.getTeamA() == m.getTeamB());
				teams[m.getTeamA()-1][m.getTeamB()-1]++;
				teams[m.getTeamB()-1][m.getTeamA()-1]++;
				
			}
			
		}
		
		for (int i = 0; i < teams.length; i++) {
			for (int j = 0; j < teams[i].length; j++) {
				System.out.print("[");
				System.out.print(teams[i][j]);
				System.out.print("]");
			}
			System.out.println();
		}

		// make sure teams aren't matched against themselves
		for (int i = 0; i < 5; i++) {
			assertTrue(teams[i][i] == 0);
		}
		
		// make sure teams meet other teams at least once
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (i != j) {
					assertTrue("need > 0 at " + i + "," + j, teams[i][j] > 0);
				}
			}
		}
		
	}
	
}
