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
 * Creates XML document (node) from a string
 *
 * @author Zbynek Hochmann
 */
public class FromStringConstruct extends Construct {
	@Inject
	private NodeService nodeService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			source -> process(source, nodeService),
				new Argument("source", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression xmlStrVar, NodeService service) {
		XmlNode xmlNode = service.fromString(xmlStrVar.getStringValue());
		MetaExpression result = fromValue(xmlNode.toString());
		result.storeMeta(XmlNode.class, xmlNode);
		return result;
	}

}
