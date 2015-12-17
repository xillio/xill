package nl.xillio.xill.plugins.codec.encode.services;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of encoding methods.
 *
 * @author Paul van der Zandt
 * @since 3.0
 */
public class EncoderServiceImpl implements EncoderService {

    @Override
    public String toHex(String inputString, boolean toLowerCase, String charsetName) {
        final Charset charset = charsetName == null ? StandardCharsets.UTF_8 : Charset.forName(charsetName);
        return new String(Hex.encodeHex(inputString.getBytes(charset), toLowerCase));
    }
}
