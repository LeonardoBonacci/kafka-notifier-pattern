package guru.bonacci.neo4j.hierarchies;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class HierarchyDeserializer extends ObjectMapperDeserializer<Hierarchy> {

	public HierarchyDeserializer() {
        super(Hierarchy.class);
    }
	
    @Override
    public Hierarchy deserialize(String topic, byte[] data) {
    	return (Hierarchy)super.deserialize(topic, data).withJsonString();
    }
}