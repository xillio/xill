package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.AssertJUnit.assertEquals;


public class CanReadConstructTest extends TestUtils {
    private final CanReadConstruct construct = new CanReadConstruct();
    private Path testFile;

    @BeforeClass
    public void deployFile() throws IOException {
        testFile = Files.createTempFile("unitTest", getClass().getSimpleName());
    }

    @AfterClass
    public void deleteFile() throws IOException {
        Files.delete(testFile);
    }

    @Test
    public void testCanRead() {
        // Input
        MetaExpression path = fromValue(testFile.toString());
        setFileResolverReturnValue(testFile);

        // Processor
        ConstructProcessor processor = construct.prepareProcess(context(construct));
        processor.setArgument(0, path);

        // Run and cause the exception
        MetaExpression result = processor.process();

        assertEquals(result.getBooleanValue(), Files.isReadable(testFile));
    }
}