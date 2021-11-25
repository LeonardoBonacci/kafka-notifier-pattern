package guru.bonacci.neo4j.producthierarchies.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestSseElementType;

import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/producthierarchies")
public class ProductHierarchyResource {

	@Channel("product-hierarchies-in")
	Multi<String> productHierarchies;

	
	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	@RestSseElementType(MediaType.APPLICATION_JSON)
	public Multi<String> resultStream() {
		return productHierarchies.invoke(ph -> log.info("Gotcha : {}", ph));
	}
}
