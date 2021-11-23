package guru.bonacci.neo4j.hierarchies;

import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.ID;
import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.P;
import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.params;
import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.neo4j.driver.Driver;
import org.neo4j.driver.reactive.RxResult;
import org.neo4j.driver.reactive.RxSession;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class HierarchyProcessor {

	@Inject
    Driver driver;

	// @formatter:off
    @Incoming("hierarchies-in")
	@Outgoing("product-notifications") 
    public Multi<Record<String, String>> process(Record<String, Hierarchy> record) {
        log.info("Receiving hierarchy: {} - {}", record.key(), record.value().getJsonString());

        var hierarchy = record.value();
        return Multi.createFrom().<RxSession, Record<String, String>>resource(
                driver::rxSession,
                session -> session.writeTransaction(tx -> {
                		RxResult result = tx.run(query(hierarchy), params(hierarchy));
                		return Multi.createFrom().publisher(result.records())
                				.map(r -> r.get(P).asMap().get(ID).toString())
                				.invoke(pid -> log.info("Affected product {}", pid))
                				.map(pid -> Record.of(pid,  pid));
                })
        ).withFinalizer(session -> {
            return Uni.createFrom().publisher(session.close());
        });
    }
    // @formatter:on
}
