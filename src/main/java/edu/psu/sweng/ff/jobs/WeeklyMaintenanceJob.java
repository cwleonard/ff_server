package edu.psu.sweng.ff.jobs;

import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.psu.sweng.ff.common.DatabaseException;
import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Roster;
import edu.psu.sweng.ff.common.Team;
import edu.psu.sweng.ff.dao.LeagueDAO;
import edu.psu.sweng.ff.dao.RosterDAO;
import edu.psu.sweng.ff.dao.SeasonDAO;
import edu.psu.sweng.ff.dao.TeamDAO;

public class WeeklyMaintenanceJob implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		System.out.println("...Running weekly maintenance job.");

		SeasonDAO sdao = new SeasonDAO();
		try {
			sdao.nextWeek();
		} catch (DatabaseException e) {
			e.printStackTrace();
			System.out.println("Error incrementing week. Maintenance aborted!");
			return;
		}
		
		try {
			
			int newWeek = sdao.getCurrentWeek();
			
			System.out.println("......setting up for week " + newWeek);
			
			LeagueDAO ldao = new LeagueDAO();
			TeamDAO tdao = new TeamDAO();
			RosterDAO rdao = new RosterDAO();
			
			List<League> leagues = ldao.loadAll();
			Iterator<League> lit = leagues.iterator();
			while (lit.hasNext()) {

				League l = lit.next();

				System.out.println("......working on league " + l.getId()
						+ " (\"" + l.getName() + "\")");
				
				List<Team> teams = tdao.loadByLeague(l);
				Iterator<Team> ti = teams.iterator();
				while (ti.hasNext()) {
					
					Team t = ti.next();
					
					System.out.println(".........working on team " + t.getId()
							+ "(\"" + t.getName() + "\")");
					
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
				
			}
			
		} catch (DatabaseException e) {
			e.printStackTrace();
			System.out.println("Error setting up new week. Maintenance aborted!");
			return;
		}
		
	}

}
