package nl.xillio.xill.plugins.xml.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.plugins.xml.XMLXillPlugin;

import java.util.List;
import java.util.Map;

/**
 * This interface represents some of the operations for the {@link XMLXillPlugin}.
 *
 * @author Zbynek Hochmann
 */

@ImplementedBy(XpathServiceImpl.class)
public interface XpathService {
    /**
     * Selects node(s) from XML document using XPath locator
     *
     * @param node       XML node
     * @param xpathQuery XPath locator specification
     * @param namespaces optional associative array containing namespace definitions
     * @return list of selected XML nodes
     */
    List<Object> xpath(final XmlNode node, final String xpathQuery, final Map<String, String> namespaces);
}
