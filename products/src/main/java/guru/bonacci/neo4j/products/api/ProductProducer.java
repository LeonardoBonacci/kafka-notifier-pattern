package guru.bonacci.neo4j.products.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import guru.bonacci.neo4j.products.Product;
import io.smallrye.reactive.messaging.kafka.Record;
import lombok.extern.slf4j.Slf4j;

/**
 * For curl testing only
 */
@Slf4j
@ApplicationScoped
public class ProductProducer {

    @Inject @Channel("products-out")
    Emitter<Record<String, Product>> emitter;

    public void sendToKafka(Product product) {
    	log.info("Sending to Kafka '{}'", product);
        emitter.send(Record.of(product.getId(), product));
    }
}