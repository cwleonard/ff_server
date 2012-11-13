package edu.psu.sweng.ff.schedule;

import java.util.List;
import java.util.Random;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Matchup;
import edu.psu.sweng.ff.common.Schedule;
import edu.psu.sweng.ff.common.Team;

public class ScheduleGenerator {

	private Random random = new Random();
	
	private Team getOtherTeam(Team t, List<Team> teams) {
		
		Team ret = teams.get(random.nextInt(teams.size()));
		while (ret.equals(t)) {
			ret = teams.get(random.nextInt(teams.size()));
		}
		return ret;
		
	}
	
	private Matchup generateMatchup(int week, List<Team> teams) {
		
		Matchup m = new Matchup();
		m.setWeek(week);

		Team t1 = teams.get(random.nextInt(teams.size()));
		Team t2 = getOtherTeam(t1, teams);

		m.setTeamA(t1.getId());
		m.setTeamB(t2.getId());

		return m;
		
	}
	
	public Schedule generateSchedule(League l) {
		
		Schedule sched = new Schedule();
		sched.setLeagueId(l.getId());
		
		List<Team> teams = l.getTeams();
		
		for (int week = 1; week < 12; week++) {

			List<Matchup> matchups = sched.getMatchups(week);
			
			while (matchups.size() < (teams.size() / 2)) {
				
				Matchup m = generateMatchup(week, teams);
				while (matchups.contains(m)) {
					m = generateMatchup(week, teams);
				}
				matchups.add(m);
			
			}

		}
		
		return sched;
		
	}
	
}
