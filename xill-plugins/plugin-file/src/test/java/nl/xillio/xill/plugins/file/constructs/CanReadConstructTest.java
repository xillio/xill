package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import opennlp.tools.parser.Cons;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.mockito.Mockito.*;

/**
 * Tests the CanReadConstruct class.
 *
 * Created by Anwar on 12/1/2015.
 */
public class CanReadConstructTest extends TestUtils {

    @Test
    public void testProcess() throws FileNotFoundException {

        ConstructContext constructContext = mock(ConstructContext.class);

        FileUtilities fileUtilities = mock(FileUtilities.class);

        setFileResolverReturnValue(new File(""));

        String uri = "This is a test.";
        MetaExpression metaExpression = mock(MetaExpression.class);
        when(metaExpression.getStringValue()).thenReturn(uri);

        CanReadConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canRead(any());
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessException() throws FileNotFoundException {

        ConstructContext constructContext = mock(ConstructContext.class);

        FileUtilities fileUtilities = mock(FileUtilities.class);
        doThrow(new RobotRuntimeException("File does not exist.")).when(fileUtilities).canRead(any());

        MetaExpression metaExpression = mock(MetaExpression.class);

        CanReadConstruct.process(constructContext, fileUtilities, metaExpression);

    }
}