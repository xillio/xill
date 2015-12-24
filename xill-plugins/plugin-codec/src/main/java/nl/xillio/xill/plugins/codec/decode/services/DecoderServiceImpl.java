package nl.xillio.xill.plugins.codec.decode.services;

import me.biesaart.utils.FileUtilsService;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of Decoding methods.
 *
 * @author Paul van der Zandt
 * @since 3.0
 */
public class DecoderServiceImpl implements DecoderService {

    @Override
    public String fromHex(String hexString, String charsetName) throws DecoderException {
        final Charset charset = charsetName == null ? StandardCharsets.UTF_8 : Charset.forName(charsetName);
        final Hex hex = new Hex(charset);
        return new String(hex.decode(hexString.getBytes()), charset);
    }

    @Override
    public void decodeFileBase64(final File input, final File output, final FileUtilsService fileUtilsService) {
        try {
            fileUtilsService.forceMkdir(output.getParentFile());
        } catch (IOException e) {
            throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
        }

        try (InputStream in = new Base64InputStream(new FileInputStream(input)); OutputStream out = new FileOutputStream(output)){
            IOUtils.copy(in, out);
        } catch (FileNotFoundException e) {
            throw new RobotRuntimeException("The file could not be found or the filename is invalid: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
        }
    }
}
