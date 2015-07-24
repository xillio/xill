package nl.xillio.xill.api.inject;

import com.google.inject.AbstractModule;

/**
 * This module is the main module that will run for the injector at runtime
 */
public class DefaultInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			//Some default injectors
			bind(String[].class).toInstance(new String[0]);
			bind(int[].class).toInstance(new int[0]);
			bind(boolean[].class).toInstance(new boolean[0]);
			
			//Some generic dependencies for plugins
			bind(ProcessBuilder.class).toConstructor(ProcessBuilder.class.getConstructor(String[].class));

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

}
