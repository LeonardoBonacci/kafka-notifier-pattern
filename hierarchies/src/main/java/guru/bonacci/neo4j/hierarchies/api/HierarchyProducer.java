package guru.bonacci.neo4j.hierarchies.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import guru.bonacci.neo4j.hierarchies.Hierarchy;
import io.smallrye.reactive.messaging.kafka.Record;
import lombok.extern.slf4j.Slf4j;

/**
 * To facilitate testing
 * curl --header "Content-Type: application/json"   --request POST   --data '{"id":"n1", "parentId":"root"}'   http://localhost:8080/hierarchies
 */
@Slf4j
@ApplicationScoped
public class HierarchyProducer {

    @Inject @Channel("hierarchies-out")
    Emitter<Record<String, Hierarchy>> emitter;

    public void sendToKafka(Hierarchy hierarchy) {
    	log.info("Sending to Kafka '{}'", hierarchy);
        emitter.send(Record.of(hierarchy.getId(), hierarchy));
    }
}