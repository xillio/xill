package nl.xillio.xill.plugins.xml.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.xml.XMLXillPlugin;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * This interface represents some of the operations for the {@link XMLXillPlugin}.
 *
 * @author Zbynek Hochmann
 */

@ImplementedBy(XsdServiceImpl.class)
public interface XsdService {
    /**
     * Verifies if XML file is valid according to XSD specification
     *
     * @param xmlFile XML file document
     * @param xsdFile XSD file
     * @param logger  CT logger
     * @return true if XML file is valid, otherwise false
     */
    boolean xsdCheck(final File xmlFile, final File xsdFile, final Logger logger);
}
