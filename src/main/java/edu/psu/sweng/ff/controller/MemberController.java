package edu.psu.sweng.ff.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;

import edu.psu.sweng.ff.common.DatabaseException;
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
		
		URI uri = uriInfo.getRequestUri();
		System.out.println("POST " + uri.toString());
		
		MemberDAO dao = new MemberDAO();
		String token = null;
		try {
			token = dao.authenticateUser(userName, Member.getHash(password));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Response response = null;
		if (token != null) {
			response = Response.ok().header(TOKEN_HEADER, token).build();
			//System.out.println("authenticated " + userName);
		} else {
			response = Response.status(Status.UNAUTHORIZED).build();
		}
		
		return response;
		
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTokenOwner(
		@HeaderParam(TOKEN_HEADER) String token
	    )
	{

		URI uri = uriInfo.getRequestUri();
		System.out.println("GET " + uri.toString());

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		//System.out.println(requester.getUserName() + " is loading self");

		Gson gson = new Gson();
		String json = gson.toJson(requester);

		return Response.ok().entity(json).build();
		
	}
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{userName}")
	public Response getMember(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("userName") String userName
	    )
	{

		URI uri = uriInfo.getRequestUri();
		System.out.println("GET " + uri.toString());

		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		//System.out.println(requester.getUserName() + " is loading " + userName);
		
		MemberDAO dao = new MemberDAO();
		Member m = null;
		try {
			m = dao.loadByUserName(userName);
		} catch (DatabaseException e) {
			throw new WebApplicationException(e);
		}
		
		Gson gson = new Gson();
		String json = gson.toJson(m);
		
		return Response.ok().entity(json).build();
		
	}

	@PUT
	@Path("/{username}")
	public Response updateMember(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("username") String userName,
		String json
		)
	{
		
		URI uri = uriInfo.getRequestUri();
		System.out.println("PUT " + uri.toString());

		Gson gson = new Gson();
		Member member = gson.fromJson(json, Member.class);
		
		
		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		// you can only update yourself
		if (!requester.equals(member)) {
			System.out.println("member " + requester.getUserName() + " cannot update "
					+ member.getUserName());
			return Response.status(Status.UNAUTHORIZED).build();
		}

		//System.out.println(requester.getUserName() + " is updating their account");

		MemberDAO dao = new MemberDAO();
		member.setAccessToken(UUID.randomUUID().toString());
		dao.update(member);
		return Response.ok().build();
		
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createMember(
		String json
		)
	{
		
		URI uri = uriInfo.getRequestUri();
		System.out.println("POST " + uri.toString());

		Response response = null;
		
		Gson gson = new Gson();
		Member member = gson.fromJson(json, Member.class);
		
		MemberDAO dao = new MemberDAO();
		member.setAccessToken(UUID.randomUUID().toString());
		boolean ok = dao.store(member);
		if (ok) {
			
			UriBuilder ub = uriInfo.getAbsolutePathBuilder();
			URI memberUri = ub.path(member.getUserName()).build();
			
			//System.out.println("created new member " + member.getUserName());
			
			response = Response.created(memberUri).header(TOKEN_HEADER, member.getAccessToken()).build();
			
		} else {

			System.out.println("creation of new member " + member.getUserName() + " failed!");
			response = Response.status(500).entity("error creating new member").build();
			
		}
		
		return response;
		
	}
	
	private Member lookupByToken(String t) {
		
		MemberDAO dao = new MemberDAO();
		return dao.loadByToken(t);
		
	}
	
}
