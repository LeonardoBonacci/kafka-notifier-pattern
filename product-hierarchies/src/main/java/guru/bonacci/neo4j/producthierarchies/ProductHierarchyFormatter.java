package guru.bonacci.neo4j.producthierarchies;

import static guru.bonacci.neo4j.producthierarchies.ProductHierarchyQueries.JSON_STRING;
import static guru.bonacci.neo4j.producthierarchies.ProductHierarchyQueries.PARENT;
import static guru.bonacci.neo4j.producthierarchies.ProductHierarchyQueries.PH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.internal.InternalNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
class ProductHierarchyFormatter {

	@Inject
    ObjectMapper mapper;

	
	//TODO jsonString returns from Neo4j somewhat strangely formatted
	@SuppressWarnings("unchecked")
	String format(Map<String, Object> resultItem) {
		var nodes = new ArrayList<>((List<InternalNode>)resultItem.get(PH));
    	Collections.reverse(nodes);
    	JsonNode ph = nodes.stream()
			.map(this::parse)
			// our case requires some nesting..
    		.reduce(null, ((partial, element) -> { 
				if (partial == null) {
    				return element;
    			}	
    			element.set(PARENT, partial);
    			return element;
    		}));
    	try {
        	return mapper.writeValueAsString(ph);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null; // Oops!!
		}		
	}

	//TODO jsonString returns from Neo4j somewhat strangely formatted
	private ObjectNode parse(InternalNode n) {
    	var unescapedJson = n.get(JSON_STRING).toString().replace("\\", "");
    	var json = StringUtils.substring(unescapedJson, 1, unescapedJson.length() - 1);
		try {
			return (ObjectNode)new ObjectMapper().readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null; // Help!!
		}
	}
	// @formatter:on
}
