package nl.xillio.xill.plugins.codec.decode.services;

import com.google.inject.ImplementedBy;
import org.apache.commons.codec.DecoderException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Interface defining behavior of decoding constructs.
 *
 * @author Paul van der Zandt
 * @author Pieter Dirk Soels
 * @since 3.0
 */
@ImplementedBy(DecoderServiceImpl.class)
public interface DecoderService {
    /**
     * Convert a string representing hexadecimal values into string of those same values. The returned string will be
     * (at most) half the length of the passed array, as it takes at least two characters to represent any given byte.
     * Some character sets use double bytes to represent diacritic characters.
     * An exception is thrown if the passed string has an odd number of characters or an illegal hexadecimal character
     * is found.
     *
     * @param hexString   A String containing hexadecimal digits
     * @param charsetName the charset name. Default is UTF-8
     * @return A String containing binary data decoded from the supplied hexString (representing characters).
     * @throws DecoderException Thrown if an odd number of characters is supplied to this function
     */
    String fromHex(String hexString, String charsetName) throws DecoderException;

    /**
     * Do URL decode of provided text
     *
     * @param text input string
     * @return urlDecoded string
     * @throws UnsupportedEncodingException If something goes wrong during the decoding
     */
    String urlDecode(String text) throws UnsupportedEncodingException;

    /**
     * Makes a file which represents the decoded version of a given base64-encoded file.
     *
     * @param input  The file we want to convert to base64.
     * @param output The file to which we are writing the output
     */
    void decodeFileBase64(File input, File output) throws IOException;

    /**
     * Returns a string which represents the decoded string given.
     *
     * @param inputString The string to decode.
     * @return String       The decoded string.
     * @throws IOException
     */
    String decodeStringBase64(String inputString) throws IOException;
}
