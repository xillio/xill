package nl.xillio.xill.docgen.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.docgen.PropertiesProvider;

/**
 * This class represents a parameter in a {@link ConstructDocumentationEntity}.
 */
public class Parameter implements PropertiesProvider  {
	private String name = null;
	private String defaultValue = null;
	private List<String> types = new ArrayList<>();


	@Override
	public Map<String, Object> getProperties() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("name", name);
		map.put("defaultValue", defaultValue);
		map.put("types", types);
		return map;
	}
	
	/**
	 * Set the name of the parameter.
	 * @param name
	 * 					The name.
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Returns the name of the parameter.
	 * @return
	 * 			The name of the parameter.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Add all parameter types.
	 * We default to ANY if there are no types given.
	 * @param types
	 * 					The parameter types as String.
	 */
	public void setType(String types){
		if(types != null){
			String[] theseTypes = types.split(", ");
			for(String type : theseTypes){
				this.types.add(type);
			}
		}
		else{
			this.types.add("ANY");
		}
	}
	
	/**
	 * Set the default value for the parameter.
	 * @param defaultValue
	 * 					The default value.
	 */
	public void setDefault(String defaultValue){
		this.defaultValue = defaultValue;
	}
}
