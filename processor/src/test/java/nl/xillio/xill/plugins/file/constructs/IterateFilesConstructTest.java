package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilitiesImpl;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class IterateFilesConstructTest extends TestUtils {
    private Path testFile;
    private IterateFilesConstruct construct = new IterateFilesConstruct();
    private FileUtilitiesImpl fileUtilities = new FileUtilitiesImpl();

    @BeforeClass
    public void createTestFiles() throws IOException {
        testFile = Files.createTempDirectory(getClass().getSimpleName());

        for (int i = 0; i < 3; i++) {
            Path file = testFile.resolve("file" + i);
            Files.createFile(file);
        }

        construct.setFileIterator(fileUtilities);
    }

    @AfterClass
    public void deleteTestFiles() throws IOException {
        fileUtilities.delete(testFile);
    }

    @Test
    public void testNormalFlow() {
        MetaExpression path = fromValue(testFile.toString());

        ConstructProcessor processor = construct.prepareProcess(context(construct));
        processor.setArgument(0, path);

        setFileResolverReturnValue(testFile);
        MetaExpression result = processor.process();

        assertNotNull(result.getMeta(MetaExpressionIterator.class));

        // Count elements
        int count = 0;
        MetaExpressionIterator iterator = result.getMeta(MetaExpressionIterator.class);
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(count, 3);
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*Unit Test File.*")
    public void testFileNotExist() {
        Path noExistFile = Paths.get("Unit Test File");

        ConstructProcessor processor = construct.prepareProcess(context(construct));
        processor.setArgument(0, fromValue(noExistFile.toString()));
        setFileResolverReturnValue(noExistFile);

        processor.process();
    }
}