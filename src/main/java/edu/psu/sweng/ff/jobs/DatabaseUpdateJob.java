package edu.psu.sweng.ff.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.psu.sweng.ff.common.DatabaseException;
import edu.psu.sweng.ff.dao.SeasonDAO;
import edu.psu.sweng.ff.notification.DatabaseUpdate;

public class DatabaseUpdateJob implements Job {

	private int specificWeek = -1;
	
	public static void main(String[] args) {
		DatabaseUpdateJob job = new DatabaseUpdateJob();
		if (args.length > 0) {
			job.specificWeek = Integer.parseInt(args[0]);
		}
		try {
			job.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		try {

			int week;
			if (specificWeek > 0) {
				week = specificWeek;
			} else {
				SeasonDAO sdao = new SeasonDAO();
				week = sdao.getCurrentWeek();
			}

			System.out.println("...Running points update job (week " + week + ")");

			DatabaseUpdate.updatePoints(week);
			
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
	}

}
