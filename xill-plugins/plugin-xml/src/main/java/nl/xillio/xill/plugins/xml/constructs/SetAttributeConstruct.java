package nl.xillio.xill.plugins.xml.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.plugins.xml.services.NodeService;

/**
 * Adds new attribute [and value]/sets attribute's value - at XML node
 *
 * @author Zbynek Hochmann
 */
public class SetAttributeConstruct extends Construct {
	@Inject
	private NodeService nodeService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(node, name, value) -> process(node, name, value, nodeService),
				new Argument("node", ATOMIC),
				new Argument("attrname", ATOMIC),
				new Argument("attrvalue", NULL, ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression nodeVar, MetaExpression nameVar, MetaExpression valueVar, NodeService service) {
		XmlNode xmlNode = assertMeta(nodeVar, "node", XmlNode.class, "XML node");
		service.setAttribute(xmlNode, nameVar.getStringValue(), valueVar.getStringValue());
		return NULL;
	}

}
