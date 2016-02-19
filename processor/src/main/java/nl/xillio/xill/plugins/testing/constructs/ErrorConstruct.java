package nl.xillio.xill.plugins.testing.constructs;


import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This construct throws an error.
 *
 * @author Thomas Biesaart
 */
public class ErrorConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(this::process, new Argument("message", NULL));
    }

    MetaExpression process(MetaExpression message) {
        if (message.isNull()) {
            throw new RobotRuntimeException("Assertion failed");
        }

        throw new RobotRuntimeException(message.getStringValue());
    }
}
