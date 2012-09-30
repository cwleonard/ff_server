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
		String token = null;
		try {
			token = dao.authenticateUser(userName, Member.getHash(password));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			System.out.println("unknown token " + token);
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
			System.out.println("unknown token " + token);
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
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		System.out.println(requester.getUserName() + " is loading user " + id);
		
		MemberDAO dao = new MemberDAO();
		Member m = dao.loadById(id);
		return Response.ok().entity(m).build();
		
	}
	
	@PUT
	@Path("/id/{id}")
	public Response updateMember(
		@HeaderParam(TOKEN_HEADER) String token,
		@PathParam("id") int id,
		Member member
		)
	{
		Member requester = this.lookupByToken(token);
		if (requester == null) {
			System.out.println("unknown token " + token);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		// ids have to match
		if (member.getId() != id) {
			System.out.println("member id " + member.getId() + " != specified id " + id);
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		// you can only update yourself
		if (requester.getId() != member.getId()) {
			System.out.println("member " + requester.getUserName() + " (id "
					+ requester.getId() + ") cannot update "
					+ member.getUserName() + "(id " + member.getId() + ")");
			return Response.status(Status.UNAUTHORIZED).build();
		}

		System.out.println(requester.getUserName() + " is updating their account");

		MemberDAO dao = new MemberDAO();
		member.setAccessToken(UUID.randomUUID().toString());
		dao.update(member);
		return Response.ok().build();
		
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createMember(
		Member member
		)
	{
		
		MemberDAO dao = new MemberDAO();
		member.setAccessToken(UUID.randomUUID().toString());
		int id = dao.store(member);
		member.setId(id);
		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder();
		URI memberUri = ub.path("id/" + id).build();
		
		System.out.println("created new member " + member.getUserName() + " with id " + member.getId());
		
		return Response.created(memberUri).build();
		
	}
	
	private Member lookupByToken(String t) {
		
		MemberDAO dao = new MemberDAO();
		return dao.loadByToken(t);
		
	}
	
}
