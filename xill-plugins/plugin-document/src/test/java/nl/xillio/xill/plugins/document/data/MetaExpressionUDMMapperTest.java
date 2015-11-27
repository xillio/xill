package nl.xillio.xill.plugins.document.data;

import com.google.inject.Guice;
import com.google.inject.Injector;
import nl.xillio.udm.UDMTestModule;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.services.xill.UDMDocumentFactory;
import nl.xillio.xill.plugins.document.services.xill.UDMDocumentFactoryTest;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class MetaExpressionUDMMapperTest extends TestUtils {

    @Test
    public void testApplyTo() throws Exception {
        DocumentBuilder builder = mockBuilder();
        MetaExpression input = new UDMDocumentFactory().buildStructure(
                "Agenda",
                UDMDocumentFactoryTest.buildBody(),
                emptyList()
        );

        new MetaExpressionUDMMapper(input).applyTo(builder);

        // Verify the calls
        assertEquals(builder.contentType(), "Agenda");
        assertEquals(builder.target().current().decorator("decorator").field("field"), 432);
        assertEquals(builder.target().current().version(), "5425");
    }

    private DocumentBuilder mockBuilder() {
        Injector injector = Guice.createInjector(new UDMTestModule());
        return injector.getInstance(DocumentBuilder.class);
    }


}