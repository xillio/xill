package nl.xillio.xill.plugins.file.constructs;

import me.biesaart.utils.IOUtils;
import me.biesaart.utils.IOUtilsService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.SimpleIOStream;
import nl.xillio.xill.plugins.file.services.files.FileStreamFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.testng.Assert.assertEquals;


public class GetTextConstructTest extends TestUtils {

    @Test
    public void testNormalUsageFromFile() throws IOException {
        // Create test file
        Path file = Files.createTempFile(getClass().getSimpleName(), ".txt");
        Files.copy(IOUtils.toInputStream("File Test"), file, StandardCopyOption.REPLACE_EXISTING);

        GetTextConstruct construct = new GetTextConstruct(new FileStreamFactory(), new IOUtilsService());
        setFileResolverReturnValue(file);

        MetaExpression result = ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                fromValue(file.toAbsolutePath().toString())
        );

        assertEquals(result.getStringValue(), "File Test");

        // Delete test file
        Files.delete(file);
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*I don't exist.*")
    public void testFromFileNotExists() {
        // Create test file
        Path file = Paths.get("I don't exist");

        GetTextConstruct construct = new GetTextConstruct(new FileStreamFactory(), new IOUtilsService());
        setFileResolverReturnValue(file);

        ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                fromValue(file.toAbsolutePath().toString())
        );
    }

    @Test
    public void testIfStreamClosed() throws IOException {
        // Create test file
        Path file = Files.createTempFile(getClass().getSimpleName(), ".txt");
        Files.copy(IOUtils.toInputStream("File Test"), file, StandardCopyOption.REPLACE_EXISTING);

        GetTextConstruct construct = new GetTextConstruct(new FileStreamFactory(), new IOUtilsService());
        setFileResolverReturnValue(file);

        MetaExpression result = ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                fromValue(file.toAbsolutePath().toString())
        );

        assertEquals(result.getStringValue(), "File Test");

        // Try to delete the test file, this can only happen if the getText actually closed the stream
        Files.delete(file);

        // If the stream is correctly closed, the delete is not queued and we will get a no such file Exception
        // Else you would get an exception when trying to open a stream with the not yet deleted file. in this case the test fails.
        try {
            Files.newInputStream(file.toAbsolutePath());
        }
        catch(java.nio.file.NoSuchFileException e)        {
            // We found the correct exception. This is expcected behavior.
        }
    }
}