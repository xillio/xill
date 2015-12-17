package nl.xillio.xill.plugins.string.services.string;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * This is the main implementation of the {@link StringUtilityService}
 */
@Singleton
public class StringUtilityServiceImpl implements StringUtilityService {

    @Override
    public boolean contains(final String parent, final String child) {
        return parent.contains(child);
    }

    @Override
    public boolean endsWith(final String first, final String second) {
        return first.endsWith(second);
    }

    @Override
    public String format(final String text, final List<Object> args) throws IllegalFormatException {
        return String.format(text, args.toArray());
    }


    @Override
    public String createMD5Construct(final String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(StandardCharsets.UTF_8.encode(input));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }

    @Override
    public String escapeXML(final String text) {
        return StringEscapeUtils.escapeXml11(text);
    }


    @Override
    public String unescapeXML(String text, final int passes) {
        for (int i = 0; i < passes; i++) {
            text = StringEscapeUtils.unescapeXml(text);
        }
        return text;
    }

    @Override
    public int indexOf(final String haystack, final String needle, final int index) {
        return haystack.indexOf(needle, index);
    }

    @Override
    public String join(final String[] input, final String delimiter) {
        return StringUtils.join(input, delimiter);
    }

    @Override
    public byte[] parseBase64Binary(final String text) {
        return DatatypeConverter.parseBase64Binary(text);
    }

    @Override
    public String printBase64Binary(final byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    @Override
    public String repeat(final String value, final int repeat) {
        return StringUtils.repeat(value, repeat);
    }

    @Override
    public String replaceAll(final String haystack, final String needle, final String replacement) {
        return haystack.replace(needle, replacement);
    }

    @Override
    public String replaceFirst(final String haystack, final String needle, final String replacement) {
        return StringUtils.replaceOnce(haystack, needle, replacement);
    }

    @Override
    public String[] split(final String haystack, final String needle) {
        return haystack.split(needle,-1);
    }

    @Override
    public boolean startsWith(final String haystack, final String needle) {
        return haystack.startsWith(needle);
    }

    @Override
    public String subString(final String text, final int start, final int end) {
        return text.substring(start, end);
    }

    @Override
    public String toLowerCase(final String toLower) {
        return toLower.toLowerCase();
    }

    @Override
    public String toUpperCase(final String toUpper) {
        return toUpper.toUpperCase();
    }

    @Override
    public String trim(final String toTrim) {
        // Ensure unicode non breaking space is converted also
        return replaceAll(toTrim, "\u00A0", " ").trim();
    }

    @Override
    public String trimInternal(final String toTrim) {
        return StringUtils.replacePattern(trim(toTrim), "\\s+", " ");
    }

    @Override
    public String wrap(final String text, final int width, final boolean wrapLongWords) {
        return WordUtils.wrap(text, width, "\n", wrapLongWords);
    }

    @Override
    public String urlEncode(final String text, final boolean xWwwForm) throws UnsupportedEncodingException {
        String encText = URLEncoder.encode(text.toString(), "UTF-8");
        return xWwwForm ? encText : encText.replace("+", "%20");
    }
}
