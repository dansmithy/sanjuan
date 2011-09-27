package com.github.dansmithy.sanjuan.rest.jaxrs;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/ws/admin")
public interface AdminResource {

	@GET
	@Path("/mongo")
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, String> mongoDetails();
}
