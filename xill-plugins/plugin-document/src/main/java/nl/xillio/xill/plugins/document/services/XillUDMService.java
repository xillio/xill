package nl.xillio.xill.plugins.document.services;

import java.util.Map;

import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.services.XillService;

/**
 * 
 * Service for doing operations on the UDM from Xill.
 * 
 * @author Geert Konijnendijk
 * @author Luca Scalzotto
 *
 */
public interface XillUDMService extends XillService {

	public Map<String, Map<String, Object>> get(String documentId, String versionId, String section) throws VersionNotFoundException;

}
