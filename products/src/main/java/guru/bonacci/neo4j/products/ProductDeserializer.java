package guru.bonacci.neo4j.products;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ProductDeserializer extends ObjectMapperDeserializer<Product> {

	public ProductDeserializer() {
        super(Product.class);
    }
	
    @Override
    public Product deserialize(String topic, byte[] data) {
    	return (Product)super.deserialize(topic, data).withJsonString();
    }
}