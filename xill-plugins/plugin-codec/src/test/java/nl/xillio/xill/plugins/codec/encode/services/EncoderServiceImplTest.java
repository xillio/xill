package nl.xillio.xill.plugins.codec.encode.services;

import me.biesaart.utils.FileUtilsService;
import me.biesaart.utils.IOUtilsService;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Pieter Dirk Soels
 */
public class EncoderServiceImplTest {

    private final String DEPLOY_TEXT = "This is my input string :D";
    private final String EXPECTED_BASE64 = "VGhpcyBpcyBteSBpbnB1dCBzdHJpbmcgOkQ=";
    private File input;
    private List<File> deployedFiles = new ArrayList<>();
    private EncoderServiceImpl encoderService;

    @BeforeClass
    public void deployTestFiles() throws IOException {
        input = createTmp();
        FileUtils.write(input, DEPLOY_TEXT);
        encoderService = new EncoderServiceImpl(new FileUtilsService(), new IOUtilsService());
    }

    @Test
    public void testEncodeFileBase64() throws IOException {
        File output = createTmp();
        encoderService.encodeFileBase64(input, output);

        String content = FileUtils.readFileToString(output);

        assertEquals(content.trim(), EXPECTED_BASE64);
    }

    @Test
    public void testEncodeStringBase64() throws Exception {
        String result = encoderService.encodeStringBase64(DEPLOY_TEXT);

        assertEquals(result.trim(), EXPECTED_BASE64);
    }

    private File createTmp() throws IOException {
        File file = File.createTempFile("unitTest", "");
        deployedFiles.add(file);
        return file;
    }

    @AfterClass
    public void deleteTestFiles() throws IOException {
        for(File file : deployedFiles) {
            FileUtils.forceDelete(file);
        }
    }
}