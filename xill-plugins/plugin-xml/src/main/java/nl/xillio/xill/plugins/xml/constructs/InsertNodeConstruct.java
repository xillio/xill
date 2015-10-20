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
 * Inserts new node into existing XML document
 *
 * @author Zbynek Hochmann
 */
public class InsertNodeConstruct extends Construct {
	@Inject
	private NodeService nodeService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(basenode, xml, beforenode) -> process(basenode, xml, beforenode, nodeService),
				new Argument("basenode", ATOMIC),
				new Argument("xml", ATOMIC),
				new Argument("beforenode", NULL, ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression baseNodeVar, MetaExpression newNodeVar, MetaExpression beforeChildNodeVar, NodeService service) {
		XmlNode baseNode = assertMeta(baseNodeVar, "node", XmlNode.class, "XML node");
		XmlNode beforeChildNode = beforeChildNodeVar.isNull() ? null : assertMeta(beforeChildNodeVar, "node", XmlNode.class, "XML node");  
		String newNodeStr = newNodeVar.getStringValue();

		XmlNode newNode = service.insertNode(baseNode, newNodeStr, beforeChildNode);

		MetaExpression result = fromValue(newNode.toString());
		result.storeMeta(XmlNode.class, newNode);
		return result;
	}

}
