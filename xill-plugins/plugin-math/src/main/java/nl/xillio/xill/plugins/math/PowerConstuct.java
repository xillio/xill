package nl.xillio.xill.plugins.math;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * This construct will return the meaning of life as proven by Deep Thought
 */
public class PowerConstuct implements Construct {

    @Override
    public String getName() {
	return "meaningOfLife";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(() -> {
	    context.getRootLogger().info(
		    "Calculating the answer to the ultimate question of everything.... Please hold on 7.5 million years...");
	    try {
		Thread.sleep(5000);
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    return ExpressionBuilder.fromValue(42);
	});
    }
}
