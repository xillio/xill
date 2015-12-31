package nl.xillio.xill.plugins.codec.decode.services;

import com.google.inject.Inject;
import me.biesaart.utils.FileUtilsService;
import me.biesaart.utils.IOUtilsService;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of Decoding methods.
 *
 * @author Paul van der Zandt
 * @author Pieter Dirk Soels
 * @since 3.0
 */
public class DecoderServiceImpl implements DecoderService {

    private final FileUtilsService fileUtilsService;
    private final IOUtilsService ioUtilsService;

    @Inject
    public DecoderServiceImpl(FileUtilsService fileUtilsService, IOUtilsService ioUtilsService) {
        this.fileUtilsService = fileUtilsService;
        this.ioUtilsService = ioUtilsService;
    }

    @Override
    public String fromHex(String hexString, String charsetName) throws DecoderException {
        final Charset charset = charsetName == null ? StandardCharsets.UTF_8 : Charset.forName(charsetName);
        final Hex hex = new Hex(charset);
        return new String(hex.decode(hexString.getBytes()), charset);
    }

    /**
     * Decode the given input from the input-stream and give it to the output-stream.
     *
     * @param in  The input stream to decode.
     * @param out The output stream for the decoded input.
     * @throws IOException
     */
    void decodeBase64(InputStream in, OutputStream out) throws IOException {
        try (OutputStream base64Out = new Base64OutputStream(out, false)) {
            ioUtilsService.copy(in, base64Out);
        }
    }

    @Override
    public void decodeFileBase64(final File input, final File output) throws IOException {
        // Initialize folders
        fileUtilsService.forceMkdir(output.getParentFile());

        // Initialize streams
        try (InputStream in = new FileInputStream(input); OutputStream out = new FileOutputStream(output)) {
            // Encode
            decodeBase64(in, out);
        }
    }

    @Override
    public String decodeStringBase64(final String stringInput) throws IOException {
        // Initialize streams for encoding
        try (InputStream inputStream = ioUtilsService.toInputStream(stringInput);
             PipedOutputStream outputStream = new PipedOutputStream();
             PipedInputStream pipedInputStream = new PipedInputStream(outputStream)) {

            // Encode
            decodeBase64(inputStream, outputStream);

            // Read and return the output stream
            return ioUtilsService.toString(pipedInputStream);
        }
    }
}
