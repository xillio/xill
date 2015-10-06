package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.data.XmlNode;

/**
 * Returns all texts from XML node and its subnodes
 *
 * @author Zbynek Hochmann
 */
public class GetTextConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				node -> process(node),
				new Argument("node", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression xmlNodeVar) {
		XmlNode xmlNode = assertMeta(xmlNodeVar, "node", XmlNode.class, "XML node");
		return fromValue(xmlNode.getText());
	}

}
