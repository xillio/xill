package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import opennlp.tools.parser.Cons;
import org.aspectj.lang.annotation.Before;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests the CanWrite construct.
 *
 * Created by Anwar on 12/1/2015.
 */
public class CanWriteConstructTest {

    private ConstructContext constructContext;
    private FileUtilities fileUtilities;
    private MetaExpression metaExpression;

    /**
     * Initialization method for objects used in all tests.
     */
    @BeforeMethod
    public void initialize() {
        constructContext = mock(ConstructContext.class);

        fileUtilities = mock(FileUtilities.class);

        metaExpression = mock(MetaExpression.class);
    }

    /**
     * Test the regular flow of the CanWriteConstruct class.
     */
    @Test
    public void testProcess() throws FileNotFoundException {

        when(metaExpression.getStringValue()).thenReturn("");

        CanWriteConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canWrite(any(File.class));

    }

    /**
     * Test the exception flow of the process method in the CanWriteConstruct class.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "The specified file does not exist.")
    public void testProcessException() throws FileNotFoundException {

        doThrow(new FileNotFoundException("The specified file does not exist.")).when(fileUtilities).canWrite(any(File.class));

        CanExecuteConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canWrite(any(File.class));
    }
}