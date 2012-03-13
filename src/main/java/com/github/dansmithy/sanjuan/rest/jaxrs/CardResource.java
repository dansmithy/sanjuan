package com.github.dansmithy.sanjuan.rest.jaxrs;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.dansmithy.sanjuan.model.BuildingType;

@Path("/ws/cards")
public interface CardResource {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	Map<Integer, BuildingType.Card> getCards();

	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	Map<BuildingType.Card, BuildingType> getCardTypes();
}
