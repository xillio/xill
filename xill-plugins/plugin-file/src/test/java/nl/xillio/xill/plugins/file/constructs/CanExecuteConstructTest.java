package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.mockito.Mockito.*;

/**
 * Tests the CanExecuteConstruct class.
 *
 * Created by Anwar on 12/1/2015.
 */
public class CanExecuteConstructTest extends TestUtils {

    private ConstructContext constructContext;
    private FileUtilities fileUtilities;
    private MetaExpression metaExpression;

    @BeforeMethod
    public void initialize() {
        constructContext = mock(ConstructContext.class);

        fileUtilities = mock(FileUtilities.class);

        metaExpression = mock(MetaExpression.class);
    }

    /**
     * Test the regular process flow of the CanExecute construct.
     *
     * @throws FileNotFoundException If the file does not exist.
     */
    @Test
    public void testProcess() throws FileNotFoundException {

        setFileResolverReturnValue(new File(""));

        when(metaExpression.getStringValue()).thenReturn("");

        CanExecuteConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canExecute(any());
    }

    /**
     * Test the exception flow of the process method in the CanExecuteConstruct class.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "The specified file does not exist.")
    public void testProcessException() throws FileNotFoundException {

        doThrow(new FileNotFoundException("The specified file does not exist.")).when(fileUtilities).canExecute(any(File.class));

        CanExecuteConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canExecute(any(File.class));
    }
}