package nl.xillio.xill.plugins.contenttype;

import com.google.inject.Binder;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.udm.util.DocumentDefinitionServiceFactory;

/**
 * This plugin package is responsible for operations regarding the Document Definitions in the UDM.
 *
 * @author Thomas Biesaart
 * @since 3.0.0
 */
public class ContentTypeXillPlugin extends XillPlugin {

    @Override
    public void configure(Binder binder) {
        binder.bind(DocumentDefinitionService.class).toProvider(new DocumentDefinitionServiceFactory());
    }
}
