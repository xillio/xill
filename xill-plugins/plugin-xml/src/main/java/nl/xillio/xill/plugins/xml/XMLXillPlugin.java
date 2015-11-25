package nl.xillio.xill.plugins.xml;

import com.google.inject.Provides;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.data.XmlNodeFactory;
import nl.xillio.xill.plugins.xml.services.NodeServiceImpl;

/**
 * This package includes all Xml constructs.
 */
public class XMLXillPlugin extends XillPlugin {

    @Provides
    XmlNodeFactory xmlNodeFactory() {
        return new NodeServiceImpl();
    }
}
