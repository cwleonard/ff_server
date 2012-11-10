package edu.psu.sweng.ff.webapp;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.DateBuilder.tomorrowAt;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;
import static org.quartz.CalendarIntervalScheduleBuilder.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import edu.psu.sweng.ff.jobs.TestJob;

public class ApplicationWatcher implements ServletContextListener {

	private final static String DEFAULT_TRIGGER_GROUP = "ff_trigger_group";
	
	private Scheduler scheduler;
	
	public void contextDestroyed(ServletContextEvent arg0) {

		System.out.println("Fantasy Football Web Application Shutdown!");

        try {
        	
        	if (scheduler != null) {
        		scheduler.shutdown();
        	}
        	
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	public void contextInitialized(ServletContextEvent arg0) {

		System.out.println("Fantasy Football Web Application Startup!");
		
		try {

			scheduler = StdSchedulerFactory.getDefaultScheduler();

			// make a test job

			JobDetail job = newJob(TestJob.class)
					.withIdentity("Test Job").build();
			
			Trigger trigger = newTrigger()
					.withIdentity(triggerKey("testTrigger", DEFAULT_TRIGGER_GROUP))
					.withSchedule(
							simpleSchedule().withIntervalInHours(1)
									.repeatForever())
					.startAt(futureDate(5, IntervalUnit.MINUTE)).build();			

			// now make points database update job
			
			JobDetail pointsjob = newJob(TestJob.class).withIdentity(
					"Points Update Job").build();
	
			Trigger pointstrigger = newTrigger()
					.withIdentity(triggerKey("pointsTrigger", DEFAULT_TRIGGER_GROUP))
					.withSchedule(
							calendarIntervalSchedule()
								.withIntervalInDays(1))
					.startAt(tomorrowAt(0, 1, 0)).build();

			// schedule the jobs
			scheduler.scheduleJob(job, trigger);
			scheduler.scheduleJob(pointsjob, pointstrigger);

			// start the scheduler
			scheduler.start();
	         
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
		
	}
	
}
