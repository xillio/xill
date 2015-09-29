package nl.xillio.xill.plugins.document;

import com.google.inject.Binder;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.document.services.ConversionService;
import nl.xillio.xill.plugins.document.services.ConversionServiceImpl;

/**
 * This plugin package is responsible for operations regarding the Unified Data Model.
 * @author Thomas Biesaart
 * @since 3.0.0
 */
public class DocumentXillPlugin extends XillPlugin {
	
	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		
		binder.bind(ConversionService.class).to(ConversionServiceImpl.class);
	}
}
