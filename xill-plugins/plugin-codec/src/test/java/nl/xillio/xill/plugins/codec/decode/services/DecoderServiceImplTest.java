package nl.xillio.xill.plugins.codec.decode.services;

import me.biesaart.utils.FileUtilsService;
import me.biesaart.utils.IOUtilsService;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Pieter Dirk Soels
 */
public class DecoderServiceImplTest {

    private final String EXPECTED_TEXT = "This is my input string :D";
    private final String DEPLOY_BASE64 = "VGhpcyBpcyBteSBpbnB1dCBzdHJpbmcgOkQ=";
    private File input;
    private List<File> deployedFiles = new ArrayList<>();
    private DecoderServiceImpl decoderService;

    @BeforeClass
    public void deployTestFiles() throws IOException {
        input = createTmp();
        FileUtils.write(input, DEPLOY_BASE64);
        decoderService = new DecoderServiceImpl(new FileUtilsService(), new IOUtilsService());
    }

    @Test
    public void testDecodeFileBase64() throws IOException {
        File output = createTmp();
        decoderService.decodeFileBase64(input, output);

        String content = FileUtils.readFileToString(output);

        assertEquals(content.trim(), EXPECTED_TEXT);
    }

    @Test
    public void testDecodeStringBase64() throws Exception {
        String result = decoderService.decodeStringBase64(DEPLOY_BASE64);

        assertEquals(result.trim(), EXPECTED_TEXT);
    }

    private File createTmp() throws IOException {
        File file = File.createTempFile("unitTest", "");
        deployedFiles.add(file);
        return file;
    }

    @AfterClass
    public void deleteTestFiles() throws IOException {
        for (File file : deployedFiles) {
            FileUtils.forceDelete(file);
        }
    }
}