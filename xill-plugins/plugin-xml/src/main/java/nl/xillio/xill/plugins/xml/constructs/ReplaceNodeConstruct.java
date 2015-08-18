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
 * Replaces existing node with new XML node
 *
 * @author Zbynek Hochmann
 */
public class ReplaceNodeConstruct extends Construct {
	@Inject
	private NodeService nodeService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(orgnode, replacementxml) -> process(orgnode, replacementxml, nodeService),
				new Argument("orgnode", ATOMIC),
				new Argument("replacementxml", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression orgNodeVar, MetaExpression replXmlStrVar, NodeService service) {
		XmlNode orgXmlNode = assertMeta(orgNodeVar, "node", XmlNode.class, "XML node");
		String replXmlStr = replXmlStrVar.getStringValue();

		XmlNode newNode = null;
		try {
			newNode = service.replaceNode(orgXmlNode, replXmlStr);
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("Function replaceNode parse error!\n" + e.getMessage());
		} catch (Exception e) {
			throw new RobotRuntimeException("Function replaceNode error!\n" + e.getMessage());
		}

		MetaExpression result = fromValue(newNode.toString());
		result.storeMeta(XmlNode.class, newNode);
		return result;
	}

}
