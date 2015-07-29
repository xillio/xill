package nl.xillio.xill.plugins.string.services.string;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author Ivor
 *
 */
public class RegexServiceImpl implements RegexService {

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
	public boolean matches(final Matcher matcher) {
		return matcher.matches();
	}

	@Override
	public String replaceAll(final Matcher matcher, final String replacement) {
		return matcher.replaceAll(replacement);
	}

	@Override
	public String replaceFirst(final Matcher matcher, final String replacement) {
		return matcher.replaceFirst(replacement);
	}

	@Override
	public List<String> tryMatch(final Matcher matcher) {
		List<String> list = new ArrayList<>();
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}

	@Override
	public List<String> tryMatchElseNull(final Matcher matcher) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i <= matcher.groupCount(); i++) {
			list.add(matcher.group(i));
		}
		return list;
	}

	@Override
	public String unescapeXML(String text, final int passes) {
		for (int i = 0; i < passes; i++) {
			text = StringEscapeUtils.unescapeXml(text);
		}
		return text;
	}
}
