package nl.xillio.xill.plugins.codec.encode.constructs;

import nl.xillio.xill.plugins.codec.constructs.AbstractEncodingConstruct;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This construct will encode streams or strings to base64.
 *
 * @author Thomas Biesaart
 * @see AbstractEncodingConstruct
 */
public class ToBase64Construct extends AbstractEncodingConstruct {
    @Override
    protected InputStream encode(BufferedInputStream inputStream) {
        return new Base64InputStream(inputStream, true);
    }

    @Override
    protected OutputStream encode(OutputStream outputStream) {
        return new Base64OutputStream(outputStream, true);
    }
}
