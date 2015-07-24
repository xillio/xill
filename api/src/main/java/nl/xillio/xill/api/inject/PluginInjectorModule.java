package nl.xillio.xill.api.inject;

import java.util.List;

import com.google.inject.Module;

import nl.xillio.plugins.XillPlugin;

/**
 * This {@link Module} gives all plugins the opportunity to create bindings
 *
 */
public class PluginInjectorModule extends DefaultInjectorModule {
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
			plugin.configure(binder());
		}
	}
}
