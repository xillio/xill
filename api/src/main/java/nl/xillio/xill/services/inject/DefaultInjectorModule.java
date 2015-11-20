package nl.xillio.xill.services.inject;

import com.google.inject.AbstractModule;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.services.json.GsonParser;
import nl.xillio.xill.services.json.JsonParser;
import nl.xillio.xill.services.json.PrettyGsonParser;
import nl.xillio.xill.services.json.PrettyJsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This module is the main module that will run for the injector at runtime
 */
public class DefaultInjectorModule extends AbstractModule {

	private static final Logger LOGGER = LogManager.getLogger(DefaultInjectorModule.class);

	@Override
	protected void configure() {
		try {
			//Some default injectors
			bind(String[].class).toInstance(new String[0]);
			bind(int[].class).toInstance(new int[0]);
			bind(boolean[].class).toInstance(new boolean[0]);

			//Some generic dependencies for plugins
			bind(ProcessBuilder.class).toConstructor(ProcessBuilder.class.getConstructor(String[].class));
			bind(JsonParser.class).toInstance(new GsonParser());
			bind(PrettyJsonParser.class).toInstance(new PrettyGsonParser());

			requestStaticInjection(Construct.class);

		} catch (NoSuchMethodException | SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
