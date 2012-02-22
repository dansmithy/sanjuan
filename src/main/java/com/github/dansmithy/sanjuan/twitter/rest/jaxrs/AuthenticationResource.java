package com.github.dansmithy.sanjuan.twitter.rest.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;

@Path("/ws/auth")
public interface AuthenticationResource {

	@GET
	@Path("/authToken")
	Response initiateAuth();

	@GET
	@Path("/authValidate")
	Response validateAuth(@QueryParam("oauth_token") String token, @QueryParam("oauth_verifier") String verifier, @QueryParam("denied") boolean denied);

	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	TwitterUser getUser();
}
