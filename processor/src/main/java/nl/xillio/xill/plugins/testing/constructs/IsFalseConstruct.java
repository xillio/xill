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
public class IsFalseConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("value"),
                new Argument("message", NULL)
        );
    }

    MetaExpression process(MetaExpression value, MetaExpression message) {
        if (!value.getBooleanValue()) {
            return NULL;
        }

        String containedMessage = message.isNull() ? "" : ": " + message.getStringValue();

        throw new RobotRuntimeException("Assertion failed. Found [" + value.getStringValue() + "] but expected a false expression" + containedMessage);
    }
}
