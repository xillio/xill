package nl.xillio.xill.plugins.system;

import com.google.inject.Binder;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.system.services.info.SystemInfoService;
import nl.xillio.xill.plugins.system.services.info.SystemInfoServiceImpl;
import nl.xillio.xill.plugins.system.services.properties.SystemPropertiesService;
import nl.xillio.xill.plugins.system.services.properties.SystemPropertyiesServiceImpl;

/**
 * This package includes all system constructs
 */
public class SystemXillPlugin extends XillPlugin {

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);

		binder.bind(SystemInfoService.class).to(SystemInfoServiceImpl.class);
		binder.bind(SystemPropertiesService.class).to(SystemPropertyiesServiceImpl.class);
	}
}
