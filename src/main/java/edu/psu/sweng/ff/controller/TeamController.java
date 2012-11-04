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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Team;
import edu.psu.sweng.ff.dao.MemberDAO;
import edu.psu.sweng.ff.dao.TeamDAO;

@Path("/team")
public class TeamController {

	private final static String TOKEN_HEADER = "X-UserToken";

	@Context UriInfo uriInfo;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getByOwner(
		@HeaderParam(TOKEN_HEADER) String token
	    )
	{

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(requester.getUserName() + " is loading their teams");
		
		TeamDAO dao = new TeamDAO();
		List<Team> teams = dao.loadByOwner(requester);
		
		Gson gson = new Gson();
		String json = gson.toJson(teams);
		
		return Response.ok().entity(json).build();
		
	}
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{id}")
	public Response getById(
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
		
		Gson gson = new Gson();
		String json = gson.toJson(t);

		return Response.ok().entity(json).build();
		
	}

	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}")
	public Response updateTeam(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int id,
		String json
		)
	{
		Gson gson = new Gson();
		Team team = gson.fromJson(json, Team.class);
		
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
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createTeam(
		@HeaderParam(TOKEN_HEADER) String token,
		String json
		)
	{
		Gson gson = new Gson();
		Team team = gson.fromJson(json, Team.class);
		
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
