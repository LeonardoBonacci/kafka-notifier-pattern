package guru.bonacci.neo4j.producthierarchies;

import static guru.bonacci.neo4j.producthierarchies.ProductHierarchyQueries.PID;
import static guru.bonacci.neo4j.producthierarchies.ProductHierarchyQueries.query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.neo4j.driver.reactive.RxResult;
import org.neo4j.driver.reactive.RxSession;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ProductHierarchyProcessor {

	@Inject
	ProductHierarchyFormatter formatter;

	@Inject
    Driver driver;
	

	// @formatter:off
	@Incoming("product-notifications")
	@Outgoing("product-hierarchies-out") 
    public Multi<String> process(Record<String, String> record) {
        log.info("Product change for: {}", record.value());
        
        var pid = record.value();
        return Multi.createFrom().<RxSession, String>resource(
                driver::rxSession,
                session -> session.readTransaction(tx -> {
            	   	RxResult result = tx.run(query(), Values.parameters(PID, pid));
                    return Multi.createFrom().publisher(result.records())
                            .map(r -> r.asMap())
                            .map(formatter::format);
                })
        ).withFinalizer(session -> {
            return Uni.createFrom().publisher(session.close());
        });
    }
	// @formatter:on
}