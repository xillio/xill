package nl.xillio.xill.plugins.contenttype.constructs;

import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;


public class DecoratorConstructTest extends TestUtils {

    /**
     * Test if the construct invokes the dds.
     *
     * @throws Exception
     */
    @Test
    public void testPrepareProcess() throws Exception {
        DocumentDefinitionService dds = mock(DocumentDefinitionService.class);
        LinkedHashMap<String, MetaExpression> decorator = new LinkedHashMap<>();
        decorator.put("type", fromValue("STRING"));
        decorator.put("required", fromValue(false));
        MetaExpression value = fromValue(decorator);

        DecoratorConstruct construct = new DecoratorConstruct(dds);
        ConstructProcessor processor = construct.prepareProcess(null);

        ConstructProcessor.process(processor, fromValue("user"), value);

        verify(dds, times(1)).loadDecorators(anyString());
    }

}