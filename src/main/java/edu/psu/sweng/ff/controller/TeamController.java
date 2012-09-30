package edu.psu.sweng.ff.controller;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Team;
import edu.psu.sweng.ff.dao.MemberDAO;
import edu.psu.sweng.ff.dao.TeamDAO;

@Path("/team")
public class TeamController {

	private final static String TOKEN_HEADER = "X-UserToken";

	@Context UriInfo uriInfo;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getLeagues(
		@HeaderParam(TOKEN_HEADER) String token,
		@QueryParam("member") String mId
	    )
	{

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(requester.getUserName() + " is loading teams");
		
		TeamDAO dao = new TeamDAO();
		
		
		

		
		return Response.ok().entity(null).build();
		
	}
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("/{id}")
	public Response getLeagueById(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int id
	    )
	{

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(requester.getUserName() + " is loading team " + id);
		
		TeamDAO dao = new TeamDAO();
		Team t = dao.loadById(id);
		
		return Response.ok().entity(t).build();
		
	}

	@PUT
	@Path("/{id}")
	public Response updateLeague(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int id,
		Team team
		)
	{
		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		

		System.out.println(requester.getUserName() + " is updating team " + team.getId());

		TeamDAO dao = new TeamDAO();
		dao.update(team);
		
		return Response.ok().build();
		
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createLeague(
		@HeaderParam(TOKEN_HEADER) String token,
		Team team
		)
	{
		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		TeamDAO dao = new TeamDAO();
		int id = dao.store(team);
		team.setId(id);
		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder();
		URI leagueUri = ub.path(String.valueOf(id)).build();
		
		System.out.println("member " + requester.getUserName()
				+ " created new team " + team.getName() + " with id "
				+ team.getId());
		
		return Response.created(leagueUri).build();
		
	}
	
	private Member lookupByToken(String t) {
		
		MemberDAO dao = new MemberDAO();
		return dao.loadByToken(t);
		
	}
	
}
