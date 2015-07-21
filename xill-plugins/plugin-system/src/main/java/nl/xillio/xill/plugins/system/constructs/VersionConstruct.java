package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Returns a string containing the current version
 */
public class VersionConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(VersionConstruct::process);
	}

	private static MetaExpression process() {
		return fromValue(Xill.class.getPackage().getImplementationVersion());
	}
}
