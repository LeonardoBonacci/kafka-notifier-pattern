package guru.bonacci.neo4j.products;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

class ProductQueries {

	static final String PID = "pid";
	static final String LABEL = "label";
	static final String PARENT_ID = "parentId";
	static final String AS_JSON = "asJson";

	static final String ID = "id";
	static final String P = "p";

	static String query(String parentId) {
		//TODO use "MATCH (h:Hierarchy {id: $parentId}) RETURN h"; instead
		return parentId.equals("root") ? ROOT_PARENT_QUERY : QUERY;
	}

	 // @formatter:off
	 static Value params(Product product) {
    	return Values.parameters(
    			PID, product.getId(), 
    			LABEL, product.getLabel(),
    			PARENT_ID, product.getParentId(), 
    			AS_JSON, product.getJsonString());	
	 }	

	private final static String QUERY = 
		"MERGE (parent:Hierarchy {id: $parentId})" + "\n"  
	  + "ON CREATE" + "\n" 
	    + "SET parent.tmp = true" + "\n" 
	    + "WITH parent" + "\n" 
	  + "MERGE (p:Product {id: $pid})" + "\n" 
	  + "SET" + "\n" 
	    + "p.parentId = $parentId," + "\n" 
	    + "p.jsonString = $asJson," + "\n"
	    + "p.tmp = null" + "\n" 
	  + "WITH p, parent" + "\n" 
	  + "OPTIONAL MATCH (p)-[oldR:CHILD_OF]->(:Hierarchy)" + "\n" 
	  + "DELETE oldR" + "\n" 
	  + "MERGE (p)-[:CHILD_OF]->(parent)" + "\n" 
	  + "RETURN p"; 

	private final static String ROOT_PARENT_QUERY = 
		"MERGE (parent:Hierarchy {id: $parentId})" + "\n"
	  +	"ON CREATE" + "\n"
	  +   "SET parent.tmp = true" + "\n"
	  +   "WITH parent" + "\n"
	  +	"MERGE (p:Product {id: $pid)" + "\n"
	  +	"SET" + "\n"
	  +	  "p.parentId = $parentId," + "\n"
	  +	  "p.jsonString = $asJson," + "\n"
	  +   "p.tmp = null" + "\n"
	  +	"WITH p, parent" + "\n"
	  +	"OPTIONAL MATCH (p)-[oldR:CHILD_OF]->(:Hierarchy)" + "\n"
	  +	"DELETE oldR" + "\n"
	  +	"MERGE (p)-[:CHILD_OF]->(parent)" + "\n"
	  +	"RETURN p";
	// @formatter:on
}
