package nl.xillio.xill.plugins.xml.constructs;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.plugins.xml.services.XpathService;

/**
 * Returns selected XML node(s) from XML document using XPath locator
 *
 * @author Zbynek Hochmann
 */
public class XpathConstruct extends Construct {
	@Inject
	private XpathService xpathService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(element, xpath, namespaces) -> process(element, xpath, namespaces, xpathService),
				new Argument("element", ATOMIC),
				new Argument("xpath", ATOMIC),
				new Argument("namespaces", NULL, OBJECT)
		);
	}

	@SuppressWarnings("unchecked")
	static MetaExpression process(MetaExpression elementVar, MetaExpression xpathVar, MetaExpression namespacesVar, XpathService service) {
		XmlNode node = assertMeta(elementVar, "node", XmlNode.class, "XML node");

		Map<String, String> namespaces = new LinkedHashMap<>();
		if (!namespacesVar.isNull()) {
			if (namespacesVar.getType() != ExpressionDataType.OBJECT) {
				throw new RobotRuntimeException("Invalid namespace data");
			}
			for (Entry<String, MetaExpression> pair : ((Map<String, MetaExpression>) namespacesVar.getValue()).entrySet()) {
				namespaces.put(pair.getKey(), pair.getValue().getStringValue());
			}
		}

		List<MetaExpression> output = new ArrayList<>();

		List<Object> result = service.xpath(node, xpathVar.getStringValue(), namespaces);
		if (result.isEmpty()) {
			return NULL;
		} else if (result.size() == 1) {
			return getOutput(result.get(0));
		} else {
			result.forEach(v -> output.add(getOutput(v)));
			return fromValue(output);
		}
	}

	static private MetaExpression getOutput(Object value) {
		if (value instanceof String) {
			return fromValue((String)value);
		} else if (value instanceof XmlNode) {
			XmlNode outputNode = (XmlNode) value;
			MetaExpression output = fromValue(outputNode.toString());
			output.storeMeta(XmlNode.class, outputNode);
			return output;
		} else {
			throw new RobotRuntimeException("Invalid XPath type!");
		}
	}

}
