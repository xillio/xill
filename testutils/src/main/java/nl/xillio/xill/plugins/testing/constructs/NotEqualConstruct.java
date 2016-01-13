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
public class NotEqualConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("actual"),
                new Argument("expected"),
                new Argument("message", NULL, ATOMIC)
        );
    }

    MetaExpression process(MetaExpression actual, MetaExpression expected, MetaExpression message) {
        if (!actual.equals(expected)) {
            return NULL;
        }

        String containedMessage = message.isNull() ? "" : ": " + message.getStringValue();

        throw new RobotRuntimeException("Assertion failed. Found [" + actual + "] but expected [" + expected + "]" + containedMessage);
    }
}
