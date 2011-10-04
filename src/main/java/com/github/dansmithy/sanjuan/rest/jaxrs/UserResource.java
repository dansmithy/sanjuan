package com.github.dansmithy.sanjuan.rest.jaxrs;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.dansmithy.sanjuan.model.User;
import com.github.dansmithy.sanjuan.security.user.SanJuanRole;

@Named
@RolesAllowed({ SanJuanRole.ADMIN })
@Path("/ws/users")
public interface UserResource {

	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	User createUser(User user);
	
	@PUT
	@Path("{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	User updateUser(@PathParam("username") String username, User user);
	
	@DELETE
	@Path("{username}")
	@Produces(MediaType.APPLICATION_JSON)
	void deleteUser(@PathParam("username") String username);
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	List<User> getUsers();

}
