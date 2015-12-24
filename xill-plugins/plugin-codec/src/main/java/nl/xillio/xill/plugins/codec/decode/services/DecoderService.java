package nl.xillio.xill.plugins.codec.decode.services;

import com.google.inject.ImplementedBy;
import org.apache.commons.codec.DecoderException;

/**
 * Interface defining behavior of decoding constructs.
 *
 * @author Paul van der Zandt
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
     * @param hexString
     *            A String containing hexadecimal digits
     * @param charsetName
     *            the charset name. Default is UTF-8
     * @return A String containing binary data decoded from the supplied hexString (representing characters).
     * @throws DecoderException
     *             Thrown if an odd number of characters is supplied to this function
     */
    String fromHex(String hexString, String charsetName) throws DecoderException;

    /**
     * Converts the string argument into an array of bytes.
     *
     * @param text The string we're convering.
     * @return An array of bytes.
     */
    byte[] parseBase64Binary(String text);

}
