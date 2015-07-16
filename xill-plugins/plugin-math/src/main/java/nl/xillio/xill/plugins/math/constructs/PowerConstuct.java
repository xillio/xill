package nl.xillio.xill.plugins.math.constructs;

import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * This construct will return the meaning of life as proven by Deep Thought
 */
public class PowerConstuct extends Construct {

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
				e.printStackTrace();
			}

			return fromValue(42);
		});
	}
}
