package nl.xillio.xill.plugins.collection.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

import java.util.Map;

public class ContainsKeyConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(ContainsKeyConstruct::process,
                new Argument("collection", OBJECT),
                new Argument("key", ATOMIC));
    }

    static MetaExpression process(final MetaExpression object, final MetaExpression key) {
        Map<String, MetaExpression> map = object.getValue();
        return fromValue(map.containsKey(key.getStringValue()));
    }
}
