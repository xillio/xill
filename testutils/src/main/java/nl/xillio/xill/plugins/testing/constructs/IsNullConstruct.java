package nl.xillio.xill.plugins.testing.constructs;


import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This construct checks for equality and throws an error if true.
 *
 * @author Thomas Biesaart
 */
public class IsNullConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("value"),
                new Argument("message", NULL, ATOMIC)
        );
    }

    MetaExpression process(MetaExpression value, MetaExpression message) {
        if (value.isNull()) {
            return NULL;
        }

        String containedMessage = message.isNull() ? "" : ": " + message.getStringValue();

        throw new RobotRuntimeException("Assertion failed. Found [" + value + "] but expected null" + containedMessage);
    }
}
