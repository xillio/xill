package nl.xillio.xill.plugins.string.services.string;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import nl.xillio.xill.plugins.string.constructs.RegexConstruct;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author Ivor
 *
 */
public class RegexServiceImpl implements RegexService {

	@Override
	public List<String> tryMatch(final String regex, final String input, final int timeout, final RegexConstruct regexConstruct) {
		List<String> list = new ArrayList<>();
		Matcher matcher = regexConstruct.getMatcher(regex, input, timeout);
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
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
	public String createMD5Construct(final String input) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(StandardCharsets.UTF_8.encode(input));
		return String.format("%032x", new BigInteger(1, md5.digest()));
	}
}
