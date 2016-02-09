package nl.xillio.xill.plugins.file.services.files;

import me.biesaart.utils.IOUtils;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class FileStreamFactoryTest {
    private Path testDir;
    private Path testFile;
    private Path testFileNotExist;
    private FileStreamFactory factory = new FileStreamFactory();

    @BeforeClass
    public void createFiles() throws IOException {
        testDir = Files.createTempDirectory(getClass().getSimpleName());
        testFile = testDir.resolve("testFile");
        Files.createFile(testFile);
        testFileNotExist = testDir.resolve("I DONT EXIST");
    }

    @AfterClass
    public void deleteFiles() throws IOException {
        new FileUtilitiesImpl().delete(testDir);

    }

    @Test
    public void testOpenAppend() throws Exception {
        Path newFile = testDir.resolve("testAppend");
        // Save some text to file
        String text = "Hello World";
        Files.copy(IOUtils.toInputStream(text), newFile, StandardCopyOption.REPLACE_EXISTING);

        try (IOStream stream = factory.openAppend(newFile)) {
            IOUtils.write("\nHello World Again", stream.getOutputStream());
        }

        List<String> lines = Files.readAllLines(newFile);

        assertEquals(lines, Arrays.asList("Hello World", "Hello World Again"));
    }

    @Test
    public void testOpenRead() throws Exception {
        // Save some text to file
        String text = "Hello World";
        Files.copy(IOUtils.toInputStream(text), testFile, StandardCopyOption.REPLACE_EXISTING);

        IOStream stream = factory.openRead(testFile);
        String actual = IOUtils.toString(stream.getInputStream());

        assertEquals(actual, text);
        stream.close();
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testOpenReadNotExist() throws Exception {
        factory.openRead(testFileNotExist);
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testOpenDitReadNotExist() throws Exception {
        factory.openRead(testDir);
    }

    @Test
    public void testOpenWrite() throws Exception {
        Path newFile = testDir.resolve("testWrite");
        // Save some text to file
        String text = "Hello World";
        Files.copy(IOUtils.toInputStream(text), newFile, StandardCopyOption.REPLACE_EXISTING);

        try (IOStream stream = factory.openWrite(newFile)) {
            IOUtils.write("Hello World Again", stream.getOutputStream());
        }

        List<String> lines = Files.readAllLines(newFile);

        assertEquals(lines, Collections.singletonList("Hello World Again"));
    }
}