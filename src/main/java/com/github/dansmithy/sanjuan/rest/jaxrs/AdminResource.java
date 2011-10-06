package com.github.dansmithy.sanjuan.rest.jaxrs;

import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.dansmithy.sanjuan.security.user.SanJuanRole;


@Path("/ws/admin")
public interface AdminResource {

	@GET
	@Path("/mongo")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SanJuanRole.ADMIN })
	Map<String, String> mongoDetails();
	
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	String getVersion();
	
	@GET
	@Path("/version.js")
	@Produces("application/x-javascript")
	String getVersionInJsonFormat();	
}
