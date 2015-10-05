package nl.xillio.xill.plugins.xml.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.plugins.xml.services.NodeService;

/**
 * Removes attribute from a XML node
 *
 * @author Zbynek Hochmann
 */
public class RemoveAttributeConstruct extends Construct {
	@Inject
	private NodeService nodeService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(node, name) -> process(node, name, nodeService),
				new Argument("node", ATOMIC),
				new Argument("attrname", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression nodeVar, MetaExpression nameVar, NodeService service) {
		XmlNode xmlNode = assertMeta(nodeVar, "node", XmlNode.class, "XML node");
		return fromValue(service.removeAttribute(xmlNode, nameVar.getStringValue()));
	}

}
