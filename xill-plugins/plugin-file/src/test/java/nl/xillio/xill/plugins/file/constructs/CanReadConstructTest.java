package nl.xillio.xill.plugins.file.constructs;

import junit.framework.Assert;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Tests the CanReadConstruct class.
 *
 * Created by Anwar on 12/1/2015.
 */
public class CanReadConstructTest extends TestUtils {

    private ConstructContext constructContext;
    private FileUtilities fileUtilities;
    private MetaExpression metaExpression;

    /**
     * Initialize objects that will be used in all tests.
     */
    @BeforeMethod
    public void initialize() {
        constructContext = mock(ConstructContext.class);

        fileUtilities = mock(FileUtilities.class);

        metaExpression = mock(MetaExpression.class);
    }

    /**
     * Test the regular flow of the process method in the CanReadConstruct class.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    @Test
    public void testProcess() throws IOException {

        setFileResolverReturnValue(new File(""));

        when(metaExpression.getStringValue()).thenReturn("");
        when(fileUtilities.canRead(any())).thenReturn(true);

        MetaExpression result = CanReadConstruct.process(constructContext, fileUtilities, metaExpression);

        Assert.assertTrue(result.getBooleanValue());
        verify(fileUtilities, times(1)).canRead(any(File.class));
    }

    /**
     * Test the exception flow of the process method in the CanReadConstruct class.
     *
     * @throws FileNotFoundException If the file is not found.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File not found, or not accessible")
    public void testProcessException() throws IOException {

        doThrow(new FileNotFoundException("")).when(fileUtilities).canRead(any(File.class));

        CanReadConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).canRead(any(File.class));
    }
}