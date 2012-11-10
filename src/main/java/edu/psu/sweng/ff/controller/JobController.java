package edu.psu.sweng.ff.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

@Path("/jobs")
public class JobController {

	@Context UriInfo uriInfo;
	
	@GET
	@Produces({MediaType.TEXT_HTML})
	public Response listJobs()
	{

		StringBuilder html = new StringBuilder();
		try {
			
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			
			List<String> groups = scheduler.getTriggerGroupNames();
			
			html.append("<html><head><title>Job Details</title></head><body>");
			html.append("<table border=\"1\">");
			html.append("<thead><tr>");
			html.append("<th>Group Name</th>");
			html.append("<th>Job Name</th>");
			html.append("<th>Next Run</th>");
			html.append("</tr></thead>");
			html.append("<tbody>");
			
			Iterator<String> it = groups.iterator();
			while (it.hasNext()) {
				
				String g = it.next();
				Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(g));
				
				Iterator<TriggerKey> tki = triggerKeys.iterator();
				while (tki.hasNext()) {
					
					TriggerKey tk = tki.next();
					Trigger t = scheduler.getTrigger(tk);
					JobKey jk = t.getJobKey();
					JobDetail j = scheduler.getJobDetail(jk);
					
					html.append("<tr>");
					html.append("<td>").append(jk.getGroup()).append("</td>");
					html.append("<td>").append(jk.getName()).append("</td>");
					html.append("<td>").append(t.getNextFireTime()).append("</td>");
					html.append("</tr>");

				}
				
			}
			
			html.append("</tbody>");
			html.append("</table>");

			html.append("</body></html>");
			
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
		
		return Response.ok().entity(html.toString()).build();
		
	}
	
	
}
