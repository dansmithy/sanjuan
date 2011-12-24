package com.github.dansmithy.sanjuan.rest.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.annotate.JsonView;

import com.github.dansmithy.sanjuan.rest.beans.Data;

@Path("/ws/views")
public interface ViewsResource {

	@GET
	@Path("/filtered")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(GameViews.PlayDetail.class)
	Data getFiltered();
	
	@GET
	@Path("/full")
	@Produces(MediaType.APPLICATION_JSON)
	Data getFull();
}
