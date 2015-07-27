package nl.xillio.xill.plugins.system.constructs;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.system.services.version.VersionProvider;

/**
 * Returns a string containing the current version
 */
public class VersionConstruct extends Construct {

	@Inject
	private VersionProvider versionProvider;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			v -> process(v, context.getRootLogger(), versionProvider),
			new Argument("requiredVersion", NULL, ATOMIC));
	}

	static MetaExpression process(final MetaExpression requiredVersion, final Logger log, final VersionProvider versionProvider) {
		String version = versionProvider.getVersion();

		if (version.equals(VersionProvider.DEVELOP)) {
			log.warn("Running in develop mode, all versions are accepted.");
		} else if (requiredVersion != NULL) {
			try {
				int[] versionParts = Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
				int[] requiredVersionParts = Arrays.stream(requiredVersion.getStringValue().split("\\.")).mapToInt(Integer::parseInt).toArray();

				for (int i = 0; i < requiredVersionParts.length && i < versionParts.length; i++) {
					if (versionParts[i] > requiredVersionParts[i]) {
						// This is a newer version
						break;
					}

					if (versionParts[i] < requiredVersionParts[i]) {
						// This is an older version
						log.error("Version " + requiredVersion.getStringValue() + " is not supported in " + version);
						break;
					}
				}

			} catch (NumberFormatException e) {
				log.error("Failed to parse version number", e);
			}

		}

		return fromValue(version);
	}
}
