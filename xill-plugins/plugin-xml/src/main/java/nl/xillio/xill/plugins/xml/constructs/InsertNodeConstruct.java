package nl.xillio.xill.plugins.xml.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;
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
				(basenode, nodestr, beforeChildNode) -> process(basenode, nodestr, beforeChildNode, nodeService),
				new Argument("basenode", ATOMIC),
				new Argument("nodestr", ATOMIC),
				new Argument("beforeChildNode", NULL, ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression baseNodeVar, MetaExpression newNodeVar, MetaExpression beforeChildNodeVar, NodeService service) {
		XmlNode baseNode = assertMeta(baseNodeVar, "node", XmlNode.class, "XML node");
		XmlNode beforeChildNode = beforeChildNodeVar.isNull() ? null : assertMeta(beforeChildNodeVar, "node", XmlNode.class, "XML node");  
		String newNodeStr = newNodeVar.getStringValue();

		XmlNode newNode = null;
		try {
			newNode = service.insertNode(baseNode, newNodeStr, beforeChildNode);
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("Function insertNode parse error!\n" + e.getMessage());
		} catch (Exception e) {
			throw new RobotRuntimeException("Function insertNode error!\n" + e.getMessage());
		}
		MetaExpression result = fromValue(newNode.toString());
		result.storeMeta(XmlNode.class, newNode);
		return result;
	}

}
