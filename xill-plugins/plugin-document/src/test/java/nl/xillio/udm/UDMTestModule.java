package nl.xillio.udm;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.services.DocumentDefinitionService;
import org.mockito.Mockito;


public class UDMTestModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    DocumentBuilder documentBuilder() {
        return new DocumentBuilderImpl(
                Mockito.mock(DocumentDefinitionService.class),
                new Document(new DocumentID())
        );
    }
}
