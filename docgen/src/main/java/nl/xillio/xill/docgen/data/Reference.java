package nl.xillio.xill.docgen.data;

import java.util.HashMap;
import java.util.Map;

import nl.xillio.xill.docgen.PropertiesProvider;

/**
 *Represents a reference from a {@link ConstructDocumentationEntity} to another {@link ConstructDocumentationEntity}.
 */
public class Reference implements PropertiesProvider {
	private String packet;
	private String construct;
	
	/**
	 * The constructor for the {@link Reference}
	 * @param packet
	 * 						The package which we reference to.
	 * @param construct
	 * 						The construct which we reference to.
	 */
	public Reference(String packet, String construct){
		this.packet = packet;
		this.construct = construct;
	}

	@Override
	public Map<String, Object> getProperties() {
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("package", packet);
		properties.put("construct", construct);
		return properties;
	}
}
