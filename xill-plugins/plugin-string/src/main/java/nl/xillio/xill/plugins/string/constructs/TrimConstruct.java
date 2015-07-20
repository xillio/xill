package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 * Returns the trimmed string. </br>
 * If the input is a list it returns a list where every element is
 * trimmed. </br>
 * If optional parameter 'internal' is set to true the routine will also replace
 * slack whitespace inside the string with a single space. </br>
 * </br>
 *
 *
 * @author Sander
 *
 */
public class TrimConstruct extends Construct {

    @Override
    public String getName() {

	return "trim";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(TrimConstruct::process, new Argument("string"), new Argument("internal", FALSE));
    }

    private static MetaExpression process(final MetaExpression string, final MetaExpression internal) {

	assertType(internal, "internal", ATOMIC);
	assertNotNull(string, "string");
	assertNotType(string, "string", OBJECT);

	if (string.getType() == ExpressionDataType.LIST) {

	    List<MetaExpression> stringList = new ArrayList<>();

	    @SuppressWarnings("unchecked")
	    List<MetaExpression> list = (List<MetaExpression>) string.getValue();

	    list.forEach(str -> {
		if (!str.isNull()) {
		    stringList.add(doTrimming(str, internal));

		}
	    });
	    return fromValue(stringList);

	}
	return fromValue(doTrimming(string, internal).getStringValue());

    }

    private static MetaExpression doTrimming(final MetaExpression string, final MetaExpression internal) {
	String text = string.getStringValue();

	text = text.replaceAll("\u00A0", " ");
	text = text.trim();

	if (internal.getBooleanValue()) {
	    text = text.replaceAll("[\\s]+", " ");
	}
	return fromValue(text);
    }
}
