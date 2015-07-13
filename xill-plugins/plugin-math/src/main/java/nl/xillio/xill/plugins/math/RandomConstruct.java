package nl.xillio.xill.plugins.math;

import java.io.InputStream;
import java.util.List;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * The construct of the Random function which is capable of generating random
 * numbervalues or getting a random index.
 *
 * @author Ivor
 *
 */
public class RandomConstruct implements HelpComponent {

    @Override
    public String getName() {
	return "random";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(RandomConstruct::process, new Argument("value", ExpressionBuilder.fromValue(0)));
    }

    private static MetaExpression process(final MetaExpression value) {
	if (value.getType() == ExpressionDataType.LIST) {
	    @SuppressWarnings("unchecked")
	    List<MetaExpression> list = (List<MetaExpression>) value.getValue();
	    int size = list.size();

	    if (size == 0) {
		return ExpressionBuilder.NULL;
	    }

	    int index = (int) (Math.random() * size);
	    return list.get(index);
	} else if (value.getType() == ExpressionDataType.ATOMIC) {
	    int intValue = value.getNumberValue().intValue();

	    if (intValue <= 0) {
		return ExpressionBuilder.fromValue(Math.random());
	    }
	    return ExpressionBuilder.fromValue((int) (Math.random() * intValue));
	} else {
	    throw new RobotRuntimeException("Incorrect type entered");
	}
    }

    @Override
    public InputStream openDocumentationStream() {
	return getClass().getResourceAsStream("/helpfiles/random.xml");
    }

}
