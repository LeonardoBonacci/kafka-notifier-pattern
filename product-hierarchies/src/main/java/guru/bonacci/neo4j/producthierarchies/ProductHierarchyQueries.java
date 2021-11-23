package guru.bonacci.neo4j.producthierarchies;

class ProductHierarchyQueries {

	static final String PID = "pid";
	static final String PH = "ph";
	static final String PARENT = "parent";

	static final String JSON_STRING = "jsonString";

	
	static final String query() {
		return QUERY;
	}

	// @formatter:off
	private final static String QUERY = 
	    "MATCH path = (p:Product {id: $pid})-[*]->(root:Hierarchy)" + "\n"
	  + "WHERE root.parentId IS NULL AND root.tmp IS NULL" + "\n"
	  + "RETURN nodes(path) AS ph";
	// @formatter:on
}
