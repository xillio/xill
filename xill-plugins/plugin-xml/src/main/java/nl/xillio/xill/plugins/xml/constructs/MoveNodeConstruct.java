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
 * Moves existing node into different position in existing XML document
 *
 * @author Zbynek Hochmann
 */
public class MoveNodeConstruct extends Construct {
    @Inject
    private NodeService nodeService;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                (parentnode, subnode, beforenode) -> process(parentnode, subnode, beforenode, nodeService),
                new Argument("parentnode", ATOMIC),
                new Argument("subnode", ATOMIC),
                new Argument("beforenode", NULL, ATOMIC)
        );
    }

    static MetaExpression process(MetaExpression parentNodeVar, MetaExpression subNodeVar, MetaExpression beforeNodeVar, NodeService service) {
        XmlNode parentXmlNode = assertMeta(parentNodeVar, "node", XmlNode.class, "XML node");
        XmlNode subXmlNode = assertMeta(subNodeVar, "node", XmlNode.class, "XML node");
        XmlNode beforeXmlNode = beforeNodeVar.isNull() ? null : assertMeta(beforeNodeVar, "node", XmlNode.class, "XML node");

        service.moveNode(parentXmlNode, subXmlNode, beforeXmlNode);
        return NULL;
    }

}
