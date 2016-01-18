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

import static org.testng.Assert.assertEquals;

/**
 * This is the test class for the DecoderService.
 *
 * @author Pieter Dirk Soels
 */
public class DecoderServiceImplTest {

    private final String EXPECTED_TEXT = "Th!s is my inp|_|t string \\/\\/!th s0/\\/\\e weird ch@r@cters :D";
    private final String DEPLOY_BASE64 = "VGghcyBpcyBteSBpbnB8X3x0IHN0cmluZyBcL1wvIXRoIHMwL1wvXGUgd2VpcmQgY2hAckBjdGVycyA6RA==";
    private final String DEPLOY_PERCENT = "Th%21s+is+my+inp%7C_%7Ct+string+%5C%2F%5C%2F%21th+s0%2F%5C%2F%5Ce+weird+ch%40r%40cters+%3AD";
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
    public void testDecodeStringBase64() throws IOException {
        String result = decoderService.decodeStringBase64(DEPLOY_BASE64);

        assertEquals(result.trim(), EXPECTED_TEXT);
    }

    @Test
    public void testDecodeFromPercent() throws IOException {
        String result = decoderService.urlDecode(DEPLOY_PERCENT);

        assertEquals(result, EXPECTED_TEXT);
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