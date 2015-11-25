package nl.xillio.xill.services.inject;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Module;

import nl.xillio.plugins.XillPlugin;

/**
 * This {@link Module} gives all plugins the opportunity to create bindings
 *
 */
public class PluginInjectorModule extends DefaultInjectorModule {
	private static final Logger log = LogManager.getLogger();
	private final List<XillPlugin> plugins;

	/**
	 * Create a new {@link PluginInjectorModule}
	 *
	 * @param plugins
	 *        the plugins
	 */
	public PluginInjectorModule(final List<XillPlugin> plugins) {
		this.plugins = plugins;
	}

	@Override
	protected void configure() {
		super.configure();

		for (XillPlugin plugin : plugins) {
			try {
				plugin.configure(binder());
			} catch (Exception e) {
				log.error("Exception while configuring binders for " + plugins, e);
			}

			for(Method method : plugin.getClass().getMethods()) {
				
			}
		}
	}
}
