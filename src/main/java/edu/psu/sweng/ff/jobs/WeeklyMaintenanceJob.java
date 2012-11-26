package edu.psu.sweng.ff.jobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.psu.sweng.ff.common.DatabaseException;
import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Matchup;
import edu.psu.sweng.ff.common.Roster;
import edu.psu.sweng.ff.common.Schedule;
import edu.psu.sweng.ff.common.Team;
import edu.psu.sweng.ff.dao.LeagueDAO;
import edu.psu.sweng.ff.dao.RosterDAO;
import edu.psu.sweng.ff.dao.ScheduleDAO;
import edu.psu.sweng.ff.dao.SeasonDAO;
import edu.psu.sweng.ff.dao.TeamDAO;
import edu.psu.sweng.ff.notification.DatabaseUpdate;

public class WeeklyMaintenanceJob implements Job {

	private int specificWeek = -1;
	private int specificLeague = -1;
	
	public static void main(String[] args) {
		WeeklyMaintenanceJob job = new WeeklyMaintenanceJob();
		if (args.length > 0) {
			job.specificLeague = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			job.specificWeek = Integer.parseInt(args[1]);
		}
		try {
			job.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		System.out.println("...Running weekly maintenance job.");

		SeasonDAO sdao = new SeasonDAO();
		
		try {
			
			int newWeek = 0;
			if (specificWeek > 0) {
				newWeek = specificWeek;
			} else {
				try {
					sdao.nextWeek();
				} catch (DatabaseException e) {
					e.printStackTrace();
					System.out.println("Error incrementing week. Maintenance aborted!");
					return;
				}
				newWeek = sdao.getCurrentWeek();
			}
			
			if (newWeek > 1) {
				System.out.println("...finishing up points for week " + (newWeek - 1));
				DatabaseUpdate.updatePoints(newWeek - 1);
			}
			
			System.out.println("......setting up for week " + newWeek);
			
			LeagueDAO ldao = new LeagueDAO();
			TeamDAO tdao = new TeamDAO();
			RosterDAO rdao = new RosterDAO();
			ScheduleDAO schdao = new ScheduleDAO();
			
			List<League> leagues = null;
			if (specificLeague >= 0) {
				leagues = new ArrayList<League>();
				leagues.add(ldao.loadById(specificLeague));
			} else {
				leagues = ldao.loadAll();
			}
			
			Iterator<League> lit = leagues.iterator();
			while (lit.hasNext()) {

				League l = lit.next();
				
				System.out.println("......working on league " + l.getId()
						+ " (\"" + l.getName() + "\")");

				Schedule schedule = schdao.loadByLeague(l);
				
				List<Team> teams = tdao.loadByLeague(l);
				Iterator<Team> ti = teams.iterator();
				while (ti.hasNext()) {

					Team t = ti.next();

					System.out.println(".........working on team " + t.getId()
							+ "(\"" + t.getName() + "\")");

					// store updated points
					tdao.updatePoints(t);

					Roster r = t.getRoster(newWeek);
					if (r.getStartingPlayers().size() == 0) {
						// roster for this week has not been created yet
						System.out.println("............creating week " + 
								newWeek + " roster");
						r = t.getRoster(newWeek - 1);
						r.setWeek(newWeek);
						rdao.store(r);
					}

				}
				
				System.out.println(".........setting opponents");
				
				List<Matchup> matchups = schedule.getMatchups(newWeek);
				Iterator<Matchup> mit = matchups.iterator();
				while (mit.hasNext()) {
					
					Matchup matchup = mit.next();
					rdao.setMatchup(matchup);
					
				}
				
			}
			
		} catch (DatabaseException e) {
			e.printStackTrace();
			System.out.println("Error setting up new week. Maintenance aborted!");
			return;
		}
		
	}

}
