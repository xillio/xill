package nl.xillio.xill.plugins.string;

import org.apache.commons.lang3.text.WordUtils;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Wraps a piece of text to a certain width
 */
public class WrapConstruct implements Construct {

    @Override
    public String getName() {
	return "wrap";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(WrapConstruct::process, new Argument("text"), new Argument("width"),
		new Argument("wrapLongWords", ExpressionBuilder.fromValue(false)));
    }

    private static MetaExpression process(final MetaExpression text, final MetaExpression width,
	    final MetaExpression wrapLong) {

	String result = WordUtils.wrap(text.getStringValue(), width.getNumberValue().intValue(), "\n",
		wrapLong.getBooleanValue());
	return ExpressionBuilder.fromValue(result);
    }
}