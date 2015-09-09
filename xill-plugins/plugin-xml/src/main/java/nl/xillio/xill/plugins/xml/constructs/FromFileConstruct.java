package nl.xillio.xill.plugins.xml.constructs;

import java.io.File;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.plugins.xml.services.NodeService;

import com.google.inject.Inject;

/**
 * Creates XML document (node) from a file
 *
 * @author Zbynek Hochmann
 */
public class FromFileConstruct extends Construct {
	@Inject
	private NodeService nodeService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				filename -> process(context, filename, nodeService),
				new Argument("uri", ATOMIC)
		);
	}

	static MetaExpression process(final ConstructContext context, MetaExpression fileNameVar, NodeService service) {
		File xmlSource = getFile(context, fileNameVar.getStringValue());
		XmlNode xmlNode = service.fromFile(xmlSource);
		MetaExpression result = fromValue(xmlNode.toString());
		result.storeMeta(XmlNode.class, xmlNode);
		return result;
	}

}
