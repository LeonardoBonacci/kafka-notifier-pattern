package guru.bonacci.neo4j.hierarchies.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import guru.bonacci.neo4j.hierarchies.Hierarchy;

/**
 * To facilitate testing
 * curl --header "Content-Type: application/json"   --request POST   --data '{"id":"n1", "parentId":"root"}'   http://localhost:8080/hierarchies
 */
@Path("/hierarchies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HierarchyResource {

    @Inject
    HierarchyProducer producer;

    @POST
    public Response send(Hierarchy hierarchy) {
        producer.sendToKafka(hierarchy);
        return Response.accepted().build();
    }
}
