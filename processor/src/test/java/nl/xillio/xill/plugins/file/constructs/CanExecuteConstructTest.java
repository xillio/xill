package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.AssertJUnit.assertEquals;


public class CanExecuteConstructTest extends TestUtils {
    private final CanExecuteConstruct construct = new CanExecuteConstruct();
    private Path testFile;

    @BeforeClass
    public void deployFile() throws IOException {
        testFile = Files.createTempFile("unitTest", getClass().getSimpleName());
    }

    @AfterClass
    public void deleteFile() throws IOException {
        Files.delete(testFile);
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*Unit Test Path.*")
    public void testPrepareProcessNotExists() {
        // Input
        MetaExpression path = fromValue("Unit Test Path");
        setFileResolverReturnValue(Paths.get(path.getStringValue()));

        // Processor
        ConstructProcessor processor = construct.prepareProcess(context(construct));
        processor.setArgument(0, path);

        // Run and cause the exception
        processor.process();
    }

    @Test
    public void testExistingPath() {
        // Input
        MetaExpression path = fromValue(testFile.toString());
        setFileResolverReturnValue(testFile);

        // Processor
        ConstructProcessor processor = construct.prepareProcess(context(construct));
        processor.setArgument(0, path);

        // Run and cause the exception
        MetaExpression result = processor.process();

        assertEquals(result.getBooleanValue(), Files.isExecutable(testFile));
    }
}