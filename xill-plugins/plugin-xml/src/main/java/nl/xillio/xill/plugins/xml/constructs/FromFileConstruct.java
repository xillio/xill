package nl.xillio.xill.plugins.xml.constructs;

import java.io.File;
import java.io.IOException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;
import org.apache.commons.io.FileUtils;

/**
 * Creates XML document (node) from a file
 *
 * @author Zbynek Hochmann
 */
public class FromFileConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				filename -> process(filename),
				new Argument("filename", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression fileNameVar) {

		String fileName = fileNameVar.getStringValue();
		String content = "";

		try {
			content = FileUtils.readFileToString(new File(fileName));
		} catch (IOException e) {
			throw new RobotRuntimeException("Read file error.", e);
		}

		if (content.isEmpty()) {
			throw new RobotRuntimeException("The file is empty.");
		}

		XmlNode xmlNode;
		try {
			xmlNode = new XmlNode(content);
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("The XML source is invalid.", e);
		} catch (Exception e) {
			throw new RobotRuntimeException("Error occured.", e);
		}

		MetaExpression result = fromValue(xmlNode.toString());
		result.storeMeta(XmlNode.class, xmlNode);
		return result;
	}

}
