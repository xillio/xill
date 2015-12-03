package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;

import static org.mockito.Mockito.*;

/**
 * Tests the CanExecuteConstruct class.
 *
 * Created by Anwar on 12/1/2015.
 */
public class CanExecuteConstructTest extends TestUtils {

    @Test
    public void testProcess() {

        ConstructContext constructContext = mock(ConstructContext.class);

        FileUtilities fileUtilities = mock(FileUtilities.class);

        String uri = "This is a URI.";

        setFileResolverReturnValue(new File(uri));

        MetaExpression metaExpression = mock(MetaExpression.class);
        when(metaExpression.getStringValue()).thenReturn(uri);

        CanExecuteConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canExecute(any());
    }

}