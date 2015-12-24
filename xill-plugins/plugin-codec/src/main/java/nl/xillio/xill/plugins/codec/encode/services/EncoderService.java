package nl.xillio.xill.plugins.codec.encode.services;

import com.google.inject.ImplementedBy;
import me.biesaart.utils.FileUtilsService;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Interface defining behavior of encoding constructs.
 *
 * @author Paul van der Zandt
 * @since 3.0
 */
@ImplementedBy(EncoderServiceImpl.class)
public interface EncoderService {

    /**
     * Converts a string into a string of characters representing the hexadecimal values of each byte in order.
     * The returned string will be double the length of the passed string, as it takes two characters to represent any
     * given byte.
     *
     * @param inputString a String to convert to Hex characters
     * @param toLowerCase <code>true</code> converts to lowercase, <code>false</code> to uppercase
     * @param charsetName the charset name. Default is UTF-8
     * @return A String containing hexadecimal characters
     */
    String toHex(String inputString, boolean toLowerCase, String charsetName);

    /**
     * Do URL encode of provided text
     *
     * @param text            input string
     * @param usePlusEncoding the output format (true means that spaces will be + otherwise %20)
     * @return urlencoded string
     * @throws UnsupportedEncodingException If something goes wrong during the encoding
     */
    String urlEncode(String text, boolean usePlusEncoding) throws UnsupportedEncodingException;

    /**
     * Returns a string which represents the printed form of the data.
     *
     * @param input The file we want to convert to base64.
     * @param output The file to which we are writing the output
     * @param fileUtilsService A service we use to handle file allocation.
     */
    void encodeFileBase64(File input, File output, FileUtilsService fileUtilsService);
}
