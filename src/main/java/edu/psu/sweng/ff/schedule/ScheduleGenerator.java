package edu.psu.sweng.ff.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Matchup;
import edu.psu.sweng.ff.common.Schedule;
import edu.psu.sweng.ff.common.Team;

public class ScheduleGenerator {

	public Schedule generateSchedule(League l) {
		
		Schedule sched = new Schedule();
		sched.setLeagueId(l.getId());
		
		List<Team> teams = l.getTeams();
		Collections.shuffle(teams);
		
		int matchupsPerWeek = teams.size() / 2;
		//System.out.println("matchups per week = " + matchupsPerWeek);
		//System.out.println((matchupsPerWeek * 11) + " games total will be played");
		
		Set<Matchup> matchups = new HashSet<Matchup>();
		for (int i = 0; i < teams.size(); i++) {
			
			for (int j = 0; j < teams.size(); j++) {
				if (j != i) {
					Matchup m = new Matchup();
					m.setTeamA(teams.get(i).getId());
					m.setTeamB(teams.get(j).getId());
					matchups.add(m);
				}
			}
			
		}
		
//		System.out.println("made " + matchups.size() + " unique matchups");
//		
//		Iterator<Matchup> it = matchups.iterator();
//		while (it.hasNext()) {
//			Matchup m = it.next();
//			System.out.println(m.getTeamA() + " vs. " + m.getTeamB());
//		}
		
		List<Matchup> ml = new ArrayList<Matchup>(matchups);
		List<Matchup> neverPlayed = new ArrayList<Matchup>(matchups);
		
		int idx = 0;
		for (int week = 1; week < 12; week++) {

			List<Matchup> m2 = sched.getMatchups(week);

			List<Integer> playingThisWeek = new ArrayList<Integer>();

			while (m2.size() < matchupsPerWeek) {

				Matchup m = null;
				if (neverPlayed.size() > 0) {
					m = neverPlayed.get(idx % neverPlayed.size());
				} else {
					m = ml.get(idx % ml.size());
				}
				
				if (!playingThisWeek.contains(m.getTeamA()) && !playingThisWeek.contains(m.getTeamB())) {
					Matchup clone = new Matchup();
					clone.setWeek(week);
					clone.setTeamA(m.getTeamA());
					clone.setTeamB(m.getTeamB());
					playingThisWeek.add(clone.getTeamA());
					playingThisWeek.add(clone.getTeamB());
					m2.add(clone);
					neverPlayed.remove(clone);
				}
				
				idx++;
				
			}

		}
		
		return sched;
		
	}
	
	
}
