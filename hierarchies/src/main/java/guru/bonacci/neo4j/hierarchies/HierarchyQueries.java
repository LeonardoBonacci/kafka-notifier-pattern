package guru.bonacci.neo4j.hierarchies;

import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.AS_JSON;
import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.HID;
import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.LABEL;
import static guru.bonacci.neo4j.hierarchies.HierarchyQueries.PARENT_ID;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

class HierarchyQueries {

	static final String HID = "hid";
	static final String LABEL = "label";
	static final String PARENT_ID = "parentId";
	static final String AS_JSON = "asJson";
	
	static final String ID = "id";
	static final String P = "p";

	
	static String query(Hierarchy h) {
		return h.getParentId() == null ? ROOT_PARENT_QUERY : QUERY;
	}
	
	// @formatter:off
    static Value params(Hierarchy hierarchy) {
    	return Values.parameters(
    			HID, hierarchy.getId(), 
    			LABEL, hierarchy.getLabel(), 
    			PARENT_ID, hierarchy.getParentId(), 
    			AS_JSON, hierarchy.getJsonString());    	
    }
	
	private final static String ROOT_PARENT_QUERY = 
		"MERGE (h:Hierarchy {id: $hid})" + "\n" 
	  + "SET" + "\n" 
      +   "h.jsonString = $asJson," + "\n"
	  +   "h.tmp = null" + "\n" 
	  + "WITH h" + "\n" 
	  + "MATCH (p:Product)-[*]->(h:Hierarchy {id: h.id})" + "\n" 
	  + "WHERE h.parentId IS NULL AND h.tmp IS NULL" + "\n" 
	  + "RETURN p";
	
	private final static String QUERY = 
		"MERGE (parent:Hierarchy {id: $parentId})" + "\n"  
	  + "ON CREATE" + "\n" 
	  + "SET parent.tmp = true" + "\n" 
	  + "WITH parent" + "\n" 
	  + "MERGE (h:Hierarchy {id: $hid})" + "\n" 
	  + "SET" + "\n" 
	  +   "h.label = $label," + "\n" 
	  +   "h.parentId = $parentId," + "\n" 
	  +   "h.jsonString = $asJson," + "\n"
	  +   "h.tmp = null" + "\n" 
	  + "WITH h, parent" + "\n" 
	  + "OPTIONAL MATCH (h)-[oldR:CHILD_OF]->(:Hierarchy)" + "\n" 
	  + "DELETE oldR" + "\n" 
	  + "MERGE (h)-[r:CHILD_OF]->(parent)" + "\n" 
	  + "WITH h" + "\n"  
	  + "MATCH (p:Product)-[*]->(:Hierarchy {id: h.id})-[*]->(root:Hierarchy)" + "\n"
	  + "WHERE root.parentId IS NULL AND root.tmp IS NULL" + "\n"
	  + "RETURN p";
	// @formatter:on
}
