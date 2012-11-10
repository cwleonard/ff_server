package edu.psu.sweng.ff.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.psu.sweng.ff.notification.DatabaseUpdate;

public class DatabaseUpdateJob implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		System.out.println("...Running points update job.");
		DatabaseUpdate.updatePoints(1);
		
	}

}
