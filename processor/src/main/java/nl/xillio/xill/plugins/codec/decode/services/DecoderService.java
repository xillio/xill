package nl.xillio.xill.plugins.codec.decode.services;

import com.google.inject.ImplementedBy;
import org.apache.commons.codec.DecoderException;

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
     * @return A String containing binary data decoded from the supplied hexString (representing characters)
     * @throws DecoderException Thrown if an odd number of characters is supplied to this function
     */
    String fromHex(String hexString, String charsetName) throws DecoderException;

    /**
     * Do URL decode of provided text
     *
     * @param text input string
     * @return urlDecoded string
     */
    String urlDecode(String text);

    /**
     * Unescapes a text containing an XML entity.
     *
     * @param text   The text that requires unescaping
     * @param passes The number of passes the decoder runs
     * @return An unescaped text
     */
    String unescapeXML(String text, int passes);
}
