package nl.xillio.xill.plugins.system.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.system.services.wait.WaitService;

/**
 * Pauses the execution of instructions for the specified amount of
 * milliseconds.
 */
public class WaitConstruct extends Construct {

    @Inject
    WaitService wait;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                delay -> process(delay, wait),
                new Argument("delay", fromValue(100), ATOMIC));
    }

    static MetaExpression process(final MetaExpression delayVar, final WaitService service) {
        int delay = delayVar.getNumberValue().intValue();

        service.wait(delay);

        return NULL;
    }
}
