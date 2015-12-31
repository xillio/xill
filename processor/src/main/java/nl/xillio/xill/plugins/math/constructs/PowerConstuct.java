package nl.xillio.xill.plugins.math.constructs;

import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This construct will return the meaning of life as proven by Deep Thought
 */
public class PowerConstuct extends Construct {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String getName() {
        return "meaningOfLife";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(() -> {
            context.getRootLogger().info("Calculating the answer to the ultimate question of everything.... Please hold on 7.5 million years...");
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            return fromValue(42);
        });
    }

    @Override
    public boolean hideDocumentation() {
        return true;
    }
}
