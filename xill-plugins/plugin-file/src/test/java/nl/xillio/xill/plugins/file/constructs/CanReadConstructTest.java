package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;

import static org.mockito.Mockito.*;

/**
 * Tests the CanReadConstruct class.
 *
 * Created by Anwar on 12/1/2015.
 */
public class CanReadConstructTest extends TestUtils {

    @Test
    public void testProcess() {

        ConstructContext constructContext = mock(ConstructContext.class);

        FileUtilities fileUtilities = mock(FileUtilities.class);

        setFileResolverReturnValue(new File(""));

        String uri = "This is a test.";
        MetaExpression metaExpression = mock(MetaExpression.class);
        when(metaExpression.getStringValue()).thenReturn(uri);

        CanReadConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canRead(any());
    }
}