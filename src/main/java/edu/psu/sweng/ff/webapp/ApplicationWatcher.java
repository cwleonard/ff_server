package edu.psu.sweng.ff.webapp;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

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
            
			JobDetail job = newJob(TestJob.class)
					.withIdentity("testjob").build();
			
			Trigger trigger = newTrigger()
					.withIdentity(triggerKey("myTrigger", "myTriggerGroup"))
					.withSchedule(
							simpleSchedule().withIntervalInHours(1)
									.repeatForever())
					.startAt(futureDate(5, IntervalUnit.MINUTE)).build();			
	         
	         scheduler.scheduleJob(job, trigger);
	         
	         scheduler.start();

	         
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
		
	}

}
