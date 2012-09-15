package edu.psu.sweng.ff.controller;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.dao.MemberDAO;

@Path("/member")
public class MemberController {

	private final static String TOKEN_HEADER = "X-UserToken";

	@Context UriInfo uriInfo;
	
	@POST
	@Path("/authenticate")
	@Consumes("application/x-www-form-urlencoded")
	public Response authenticate(
		@FormParam("username") String userName,
		@FormParam("password") String password
		)
	{
		
		MemberDAO dao = new MemberDAO();
		String token = dao.authenticateUser(userName, password);
		
		Response response = null;
		if (token != null) {
			response = Response.ok().header(TOKEN_HEADER, token).build();
			System.out.println("authenticated " + userName);
		} else {
			response = Response.status(Status.UNAUTHORIZED).build();
		}
		
		return response;
		
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getTokenOwner(
		@HeaderParam(TOKEN_HEADER) String token
	    )
	{

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(requester.getUserName() + " is loading self");

		return Response.ok().entity(requester).build();
		
	}
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("/{userName}")
	public Response getMember(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("userName") String userName
	    )
	{

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(requester.getUserName() + " is loading " + userName);
		
		MemberDAO dao = new MemberDAO();
		Member m = dao.loadByUserName(userName);
		return Response.ok().entity(m).build();
		
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("/id/{id}")
	public Response getMemberById(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int id
	    )
	{

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(requester.getUserName() + " is loading user " + id);
		
		MemberDAO dao = new MemberDAO();
		Member m = dao.loadById(id);
		return Response.ok().entity(m).build();
		
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createMember(
		Member member
		)
	{
		
		MemberDAO dao = new MemberDAO();
		member.setAccessToken(UUID.randomUUID().toString());
		member.setPassword("password"); //TODO: use a real password
		member.setId(dao.nextMemberId());
		dao.store(member);
		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder();
		URI memberUri = ub.path("id/" + member.getId()).build();
		
		return Response.created(memberUri).build();
		
	}
	
	private Member lookupByToken(String t) {
		
		MemberDAO dao = new MemberDAO();
		return dao.loadByToken(t);
		
	}
	
}
