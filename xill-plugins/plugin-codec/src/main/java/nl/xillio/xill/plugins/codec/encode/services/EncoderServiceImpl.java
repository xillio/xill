package nl.xillio.xill.plugins.codec.encode.services;

import me.biesaart.utils.FileUtilsService;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of encoding methods.
 *
 * @author Paul van der Zandt
 * @author Pieter Dirk Soels
 * @since 3.0
 */
public class EncoderServiceImpl implements EncoderService {

    @Override
    public String toHex(String inputString, boolean toLowerCase, String charsetName) {
        final Charset charset = charsetName == null ? StandardCharsets.UTF_8 : Charset.forName(charsetName);
        return new String(Hex.encodeHex(inputString.getBytes(charset), toLowerCase));
    }
    
    @Override
    public String urlEncode(final String text, final boolean xWwwForm) throws UnsupportedEncodingException {
        String encText = URLEncoder.encode(text, "UTF-8");
        return xWwwForm ? encText : encText.replace("+", "%20");
    }

    @Override
    public void encodeFileBase64(final File input, final File output, final FileUtilsService fileUtilsService) {
        try {
            fileUtilsService.forceMkdir(output.getParentFile());
        } catch (IOException e) {
            throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
        }

        try(InputStream in = new FileInputStream(input); OutputStream out = new Base64OutputStream(new FileOutputStream(output))) {
            IOUtils.copy(in, out);
        } catch (FileNotFoundException e) {
            throw new RobotRuntimeException("The file could not be found or the filename is invalid: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
        }
    }
}
