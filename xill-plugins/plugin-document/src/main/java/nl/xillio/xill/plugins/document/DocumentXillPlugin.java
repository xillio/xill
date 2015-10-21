package nl.xillio.xill.plugins.document;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.udm.util.DocumentDefinitionServiceFactory;
import nl.xillio.xill.plugins.document.services.ConversionService;
import nl.xillio.xill.plugins.document.services.ConversionServiceImpl;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.services.XillUDMServiceImpl;

import com.google.inject.Binder;

/**
 * This plugin package is responsible for operations regarding the Unified Data Model.
 * 
 * @author Thomas Biesaart
 * @since 3.0.0
 */
public class DocumentXillPlugin extends XillPlugin {

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);

		binder.bind(ConversionService.class).to(ConversionServiceImpl.class);
		binder.bind(XillUDMService.class).to(XillUDMServiceImpl.class);
		binder.bind(DocumentDefinitionService.class).toProvider(new DocumentDefinitionServiceFactory());
	}
}
