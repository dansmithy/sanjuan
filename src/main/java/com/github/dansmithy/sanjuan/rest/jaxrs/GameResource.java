package com.github.dansmithy.sanjuan.rest.jaxrs;

import java.util.List;

import javax.annotation.security.RolesAllowed;
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

import org.codehaus.jackson.map.annotate.JsonView;

import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Tariff;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
import com.github.dansmithy.sanjuan.security.user.SanJuanRole;

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
	
	/**
	 * For testing
	 */
	Game getGame(Long gameId);
	
	@GET
	@Path("{gameId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(GameViews.Full.class)
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
	@Path("{gameId}/rounds/{roundNumber}/phases/{phaseNumber}/role")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Game chooseRole(@PathParam("gameId") Long gameId, @PathParam("roundNumber") Integer roundNumber, @PathParam("phaseNumber") Integer phaseNumber, RoleChoice choice);

	@PUT
	@Path("{gameId}/rounds/{roundNumber}/phases/{phaseNumber}/plays/{playNumber}/decision")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Game makePlay(@PathParam("gameId") Long gameId, @PathParam("roundNumber") Integer roundNumber, @PathParam("phaseNumber") Integer phaseNumber, @PathParam("playNumber") Integer playNumber, PlayChoice playChoice);

	@GET
	@Path("{gameId}/rounds/{roundNumber}/phases/{phaseNumber}/plays/{playNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(GameViews.PlayDetail.class)
	Play getPlay(@PathParam("gameId") Long gameId, @PathParam("roundNumber") Integer roundNumber, @PathParam("phaseNumber") Integer phaseNumber, @PathParam("playNumber") Integer playNumber);
	
	@PUT
	@Path("{gameId}/deck")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SanJuanRole.ADMIN })
	Deck orderDeck(@PathParam("gameId") Long gameId, List<Integer> deckOrder);
	
	@PUT
	@Path("{gameId}/tariffs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SanJuanRole.ADMIN })
	List<Tariff> orderTariffs(@PathParam("gameId") Long gameId, List<Integer> tariffOrder);	
}
