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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
import com.github.dansmithy.sanjuan.security.user.SanJuanRole;

@Named
@RolesAllowed({ SanJuanRole.PLAYER })
@Path("/ws/games")
public interface GameResource {

	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	Game createNewGame(String ownerName);
	
	@POST
	@Path("{gameId}/players")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Player joinGame(@PathParam("gameId") Long gameId, String playerName);
	
	@GET
	@Path("{gameId}")
	@Produces(MediaType.APPLICATION_JSON)
	Response getGame(@PathParam("gameId") Long gameId, @Context Request request);
	
	@DELETE
	@Path("{gameId}")
	@Produces(MediaType.APPLICATION_JSON)
	void deleteGame(@PathParam("gameId") Long gameId);
	
	@PUT
	@Path("{gameId}/state")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Game startGame(@PathParam("gameId") Long gameId, String state);
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	List<Game> getGames(@QueryParam("player") String player, @QueryParam("state") String state);
	
	@PUT
	@Path("{gameId}/rounds/{roundIndex}/phases/{phaseIndex}/role")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Game chooseRole(@PathParam("gameId") Long gameId, @PathParam("roundIndex") Integer roundIndex, @PathParam("phaseIndex") Integer phaseIndex, RoleChoice choice);

	@PUT
	@Path("{gameId}/rounds/{roundIndex}/phases/{phaseIndex}/plays/{playIndex}/decision")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Game makePlay(@PathParam("gameId") Long gameId, @PathParam("roundIndex") Integer roundIndex, @PathParam("phaseIndex") Integer phaseIndex, @PathParam("playIndex") Integer playIndex, PlayChoice playChoice);

}
