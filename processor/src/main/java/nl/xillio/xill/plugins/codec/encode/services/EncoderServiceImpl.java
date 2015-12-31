package nl.xillio.xill.plugins.codec.encode.services;

import com.google.inject.Inject;
import me.biesaart.utils.FileUtilsService;
import me.biesaart.utils.IOUtilsService;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.binary.Hex;

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

    private final FileUtilsService fileUtilsService;
    private final IOUtilsService ioUtilsService;

    @Inject
    public EncoderServiceImpl(FileUtilsService fileUtilsService, IOUtilsService ioUtilsService) {
        this.fileUtilsService = fileUtilsService;
        this.ioUtilsService = ioUtilsService;
    }

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

    /**
     * Encode the given input from the input-stream and give it to the output-stream.
     *
     * @param in  The input stream to encode.
     * @param out The output stream for the encoded input.
     * @throws IOException
     */
    void encodeBase64(InputStream in, OutputStream out) throws IOException {
        try (OutputStream base64Out = new Base64OutputStream(out)) {
            ioUtilsService.copy(in, base64Out);
        }
    }

    @Override
    public void encodeFileBase64(final File input, final File output) throws IOException {
        // Initialize folders
        fileUtilsService.forceMkdir(output.getParentFile());

        // Initialize streams
        try (InputStream in = new FileInputStream(input); OutputStream out = new FileOutputStream(output)) {
            // Encode
            encodeBase64(in, out);
        }
    }

    @Override
    public String encodeStringBase64(final String stringInput) throws IOException {
        // Initialize streams for encoding
        try (InputStream inputStream = ioUtilsService.toInputStream(stringInput);
             PipedOutputStream outputStream = new PipedOutputStream();
             PipedInputStream pipedInputStream = new PipedInputStream(outputStream)) {

            // Encode
            encodeBase64(inputStream, outputStream);

            // Read and return the output stream
            return ioUtilsService.toString(pipedInputStream).trim();
        }
    }
}
