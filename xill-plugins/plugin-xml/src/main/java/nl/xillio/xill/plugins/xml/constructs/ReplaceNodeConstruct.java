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
                (node, xml) -> process(node, xml, nodeService),
                new Argument("node", ATOMIC),
                new Argument("xml", ATOMIC)
        );
    }

    static MetaExpression process(MetaExpression orgNodeVar, MetaExpression replXmlStrVar, NodeService service) {
        XmlNode orgXmlNode = assertMeta(orgNodeVar, "node", XmlNode.class, "XML node");
        String replXmlStr = replXmlStrVar.getStringValue();

        XmlNode newNode = service.replaceNode(orgXmlNode, replXmlStr);

        MetaExpression result = fromValue(newNode.toString());
        result.storeMeta(newNode);
        return result;
    }

}
