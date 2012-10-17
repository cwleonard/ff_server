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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.dao.LeagueDAO;
import edu.psu.sweng.ff.dao.MemberDAO;
import edu.psu.sweng.ff.dao.PlayerDAO;

@Path("/league")
public class LeagueController {

	private final static String TOKEN_HEADER = "X-UserToken";

	@Context UriInfo uriInfo;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
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
		System.out.println(requester.getUserName() + " is loading leagues");
		
		LeagueDAO dao = new LeagueDAO();
		List<League> leagues = null;
		if (mId == null || mId.length() == 0) {
			leagues = dao.loadAll();
		} else {
			MemberDAO mdao = new MemberDAO();
			leagues = dao.loadByMember(mdao.loadById(Integer.parseInt(mId)));
		}

		Gson gson = new Gson();
		String json = gson.toJson(leagues);

		return Response.ok().entity(json).build();
		
	}
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
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
		System.out.println(requester.getUserName() + " is loading league " + id);
		
		LeagueDAO dao = new LeagueDAO();
		League l = dao.loadById(id);
		
		Gson gson = new Gson();
		String json = gson.toJson(l);

		return Response.ok().entity(json).build();
		
	}

	@PUT
	@Path("/{id}")
	public Response updateLeague(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int id,
		String json
		)
	{
		Gson gson = new Gson();
		League league = gson.fromJson(json, League.class);

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		

		System.out.println(requester.getUserName() + " is updating league " + league.getId());

		LeagueDAO dao = new LeagueDAO();
		dao.update(league);
		
		return Response.ok().build();
		
	}

	@POST
	@Path("/{id}/join")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response joinLeague(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int lid
		)
	{
		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}

		System.out.println(requester.getUserName() + " is joining league " + lid);

		LeagueDAO dao = new LeagueDAO();
		dao.joinLeague(lid, requester.getId());

		return Response.ok().build();
		
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createLeague(
		@HeaderParam(TOKEN_HEADER) String token,
		String json
		)
	{
		Gson gson = new Gson();
		League league = gson.fromJson(json, League.class);
		
		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		LeagueDAO dao = new LeagueDAO();
		int id = dao.store(league);
		league.setId(id);
		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder();
		URI leagueUri = ub.path(String.valueOf(id)).build();
		
		System.out.println("member " + requester.getUserName()
				+ " created new leauge " + league.getName() + " with id "
				+ league.getId());
		
		return Response.created(leagueUri).build();
		
	}
	
	@POST
	@Path("/{id}/startdraft")
	public Response startDraft(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int leagueId
		)
	{
		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		LeagueDAO dao = new LeagueDAO();
		League l = dao.loadById(leagueId);
		
		if (l.getCommissioner().equals(requester)) {

			System.out.println("member " + requester.getUserName()
					+ " is starting the draft process on league " + l.getId());
			
			try {
			
				l.getDraft().setPlayerSource(new PlayerDAO());
				l.startDraft();
			
			} catch (Exception e) {
				throw new WebApplicationException(e);
			}
			
		} else {
			
			throw new WebApplicationException();
			
		}
	
		return Response.ok().build();
	}
	
	private Member lookupByToken(String t) {
		
		MemberDAO dao = new MemberDAO();
		return dao.loadByToken(t);
		
	}
	
}
