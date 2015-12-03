package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by Anwar on 12/1/2015.
 */
public class CanWriteConstructTest {

    @Test
    public void testProcess() {

        ConstructContext constructContext = mock(ConstructContext.class);

        FileUtilities fileUtilities = mock(FileUtilities.class);

        MetaExpression metaExpression = mock(MetaExpression.class);
        when(metaExpression.getStringValue()).thenReturn("");

        CanWriteConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canWrite(any());

    }
}