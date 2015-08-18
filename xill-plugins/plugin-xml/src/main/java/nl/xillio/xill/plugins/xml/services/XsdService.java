package nl.xillio.xill.plugins.xml.services;

import nl.xillio.xill.plugins.xml.XmlXillPlugin;
import com.google.inject.ImplementedBy;
import org.apache.logging.log4j.Logger;

/**
 * This interface represents some of the operations for the {@link XmlXillPlugin}.
 *
 * @author Zbynek Hochmann
 */

@ImplementedBy(XsdServiceImpl.class)
public interface XsdService {
	/**
	 * Verifies if XML file is valid according to XSD specification
	 * 
	 * @param xmlFileName	XML file document
	 * @param xsdFileName	XSD file
	 * @param logger			CT logger			
	 * @return true if XML file is valid, otherwise false
	 */
	boolean xsdCheck(final String xmlFileName, final String xsdFileName, final Logger logger);
}
