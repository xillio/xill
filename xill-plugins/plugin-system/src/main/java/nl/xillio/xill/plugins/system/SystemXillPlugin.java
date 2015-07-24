package nl.xillio.xill.plugins.system;

import com.google.inject.Binder;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.system.services.info.SystemInfoService;
import nl.xillio.xill.plugins.system.services.info.SystemInfoServiceImpl;

/**
 * This package includes all system constructs
 */
public class SystemXillPlugin extends XillPlugin {

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);

		binder.bind(SystemInfoService.class).to(SystemInfoServiceImpl.class);
	}
}
