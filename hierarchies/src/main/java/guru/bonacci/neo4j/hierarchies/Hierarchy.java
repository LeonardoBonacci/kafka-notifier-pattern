package guru.bonacci.neo4j.hierarchies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Hierarchy {

	private String id;
	private String label;
	private String parentId;
	@JsonIgnore
	protected String jsonString;

	public Hierarchy withJsonString() {
		try {
			this.jsonString = new ObjectMapper().writeValueAsString(this);
			return this;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return this;
		}
	}
}