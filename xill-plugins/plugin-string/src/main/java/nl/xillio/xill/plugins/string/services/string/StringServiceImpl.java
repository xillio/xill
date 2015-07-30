package nl.xillio.xill.plugins.string.services.string;

import java.util.List;
import java.util.MissingFormatArgumentException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.google.inject.Singleton;

/**
 * This is the main implementation of the {@link StringService}
 */
@Singleton
public class StringServiceImpl implements StringService {

	@Override
	public boolean contains(final String parent, final String child) {
		return parent.contains(child);
	}

	@Override
	public boolean endsWith(final String first, final String second) {
		return first.endsWith(second);
	}

	@Override
	public String format(final String text, final List<Object> args) throws MissingFormatArgumentException {
		return String.format(text, args.toArray());
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
		return haystack.split(needle);
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
		return toTrim.trim();
	}

	@Override
	public String wrap(final String text, final int width, final boolean wrapLongWords) {
		return WordUtils.wrap(text, width, "\n", wrapLongWords);
	}
}
