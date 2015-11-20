package nl.xillio.xill.plugins.contenttype.constructs;

import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

public class SaveConstructTest extends TestUtils{

    /**
     * Test if this construct invokes the dds
     * @throws Exception
     */
    @Test
    public void testPrepareProcess() throws Exception {
        DocumentDefinitionService dds = mock(DocumentDefinitionService.class);
        MetaExpression decorators = fromValue(Collections.singletonList(fromValue("myField")));
        MetaExpression decoratorName = fromValue("myDecorator");

        SaveConstruct construct = new SaveConstruct(dds);
        ConstructProcessor processor = construct.prepareProcess(null);

        ConstructProcessor.process(processor, decoratorName, decorators);

        verify(dds, times(1)).loadContentTypes(anyString());
        verify(dds, times(1)).persist(decoratorName.getStringValue());
    }
}