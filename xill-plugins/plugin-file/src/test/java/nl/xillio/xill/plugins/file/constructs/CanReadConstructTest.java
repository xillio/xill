package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import opennlp.tools.parser.Cons;
import org.testng.annotations.BeforeMethod;
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

    private ConstructContext constructContext;
    private FileUtilities fileUtilities;

    @BeforeMethod
    public void initialize() {
        constructContext = mock(ConstructContext.class);

        fileUtilities = mock(FileUtilities.class);

        setFileResolverReturnValue(new File(""));
    }

    /**
     * Test the regular flow of the process method in the CanReadConstruct class.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    @Test
    public void testProcess() throws FileNotFoundException {

        String uri = "This is a test.";
        MetaExpression metaExpression = mock(MetaExpression.class);
        when(metaExpression.getStringValue()).thenReturn(uri);

        CanReadConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canRead(any());
    }

    /**
     * Test the exception flow of the process method in the CanReadConstruct class.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessException() throws FileNotFoundException {

        doThrow(new FileNotFoundException("File does not exist.")).when(fileUtilities).canRead(any(File.class));

        MetaExpression metaExpression = mock(MetaExpression.class);

        CanReadConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canExecute(any(File.class));
    }
}