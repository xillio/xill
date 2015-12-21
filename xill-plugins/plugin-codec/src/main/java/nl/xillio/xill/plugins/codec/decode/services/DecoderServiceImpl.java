package nl.xillio.xill.plugins.codec.decode.services;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

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
}
