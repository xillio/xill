package nl.xillio.xill.plugins.system.constructs;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;


import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Returns a string containing the current version
 */
public class VersionConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(v -> process(v, context.getRootLogger()), new Argument("requiredVersion", NULL));
	}

	private static MetaExpression process(final MetaExpression requiredVersion, Logger log) {
		String version = Xill.class.getPackage().getImplementationVersion();
		
		if(requiredVersion != NULL) {
			try {
			int[] versionParts = Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
			int[] requiredVersionParts = Arrays.stream(requiredVersion.getStringValue().split("\\.")).mapToInt(Integer::parseInt).toArray();
			
			for(int i = 0; i < requiredVersionParts.length && i < versionParts.length; i++) {
				if(versionParts[i] > requiredVersionParts[i]) {
					//This is a newer version
					break;
				}
				
				if(versionParts[i] < requiredVersionParts[i]) {
					//This is an older version
					log.error("Version " + requiredVersion.getStringValue() + " is not supported in " + version);
					break;
				}
			}
			
			}catch(NumberFormatException e) {
				log.error("Failed to parse version number", e);
			}
			
		}
		
		
		return fromValue(Xill.class.getPackage().getImplementationVersion());
	}
}
