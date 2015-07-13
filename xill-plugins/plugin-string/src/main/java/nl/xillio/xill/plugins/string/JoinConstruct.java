package nl.xillio.xill.plugins.string;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Concatenates a list of elements using a delimiter
 */
public class JoinConstruct implements Construct {

    @Override
    public String getName() {
	return "join";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(JoinConstruct::process, new Argument("list"),
		new Argument("delimiter", ExpressionBuilder.fromValue("")));
    }

    @SuppressWarnings("unchecked")
    private static MetaExpression process(final MetaExpression list, final MetaExpression delimiter) {
	String output = "";

	switch (list.getType()) {
	case ATOMIC:
	    output = list.getStringValue();
	    break;
	case LIST:
	    String[] stringList = ((List<MetaExpression>) list.getValue()).stream().map(MetaExpression::getStringValue)
		    .toArray(i -> new String[i]);
	    output = StringUtils.join(stringList, delimiter.getStringValue());
	    break;
	case OBJECT:
	    String[] stringObject = ((Map<String, MetaExpression>) list.getValue()).values().stream()
		    .map(MetaExpression::getStringValue).toArray(i -> new String[i]);
	    output = StringUtils.join(stringObject, delimiter.getStringValue());
	    break;
	}

	return ExpressionBuilder.fromValue(output);
    }
}