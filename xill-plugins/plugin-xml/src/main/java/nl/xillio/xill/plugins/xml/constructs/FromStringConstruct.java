package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;

/**
 * Creates XML document (node) from a string
 *
 * @author Zbynek Hochmann
 */
public class FromStringConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			source -> process(source),
				new Argument("source", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression xmlStrVar) {
		
		XmlNode xmlNode;
		try {
			xmlNode = new XmlNode(xmlStrVar.getStringValue());
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("The XML source is invalid.", e);
		} catch (Exception e) {
			throw new RobotRuntimeException("Error occured.", e);
		}

		MetaExpression result = fromValue(xmlNode.toString());;
		result.storeMeta(XmlNode.class, xmlNode);
		return result;
	}

}
