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
 * Removes existing node from XML document
 *
 * @author Zbynek Hochmann
 */
public class RemoveNodeConstruct extends Construct {
	@Inject
	private NodeService nodeService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				node -> process(node, nodeService),
				new Argument("node", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression nodeVar, NodeService service) {
		XmlNode xmlNode = assertMeta(nodeVar, "node", XmlNode.class, "XML node");
		service.removeNode(xmlNode);
		return NULL;
	}

}
