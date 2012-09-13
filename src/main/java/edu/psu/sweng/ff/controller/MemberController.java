package edu.psu.sweng.ff.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/member")
public class MemberController {

	@GET
	public Response getMember() {

		return Response.ok().entity("member").build();
		
	}
	
}
