package com.github.dansmithy.sanjuan.rest.jaxrs;

import java.util.List;

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
import javax.ws.rs.core.MediaType;

import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Player;

@Named
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
	Player joinGame(@PathParam("gameId") Integer gameId, String playerName);
	
	@GET
	@Path("{gameId}")
	@Produces(MediaType.APPLICATION_JSON)
	Game getGame(@PathParam("gameId") Integer gameId);
	
	@DELETE
	@Path("{gameId}")
	@Produces(MediaType.APPLICATION_JSON)
	void deleteGame(@PathParam("gameId") Integer gameId);
	
	@PUT
	@Path("{gameId}/state")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Game changeGameState(@PathParam("gameId") Integer gameId, String state);
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	List<Game> getGames(@QueryParam("player") String player);
	
}
