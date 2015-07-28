package nl.xillio.xill.plugins.system.services.regex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import nl.xillio.xill.plugins.string.constructs.RegexConstruct;

/**
 * @author Ivor
 *
 */
public class RegexServiceImpl implements RegexService {

	@Override
	public List<String> tryMatch(String regex, String input, int timeout, RegexConstruct regexConstruct) {
		List<String> list = new ArrayList<>();
		Matcher matcher = regexConstruct.getMatcher(regex, input, timeout);
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}

	@Override
	public String escapeXML(String text) {
		return StringEscapeUtils.escapeXml11(text);
	}

	@Override
	public String unescapeXML(String text, int passes) {
		for (int i = 0; i < passes; i++) {
			text = StringEscapeUtils.unescapeXml(text);
		}
		return text;
	}

	@Override
	public String createMD5Construct(String input) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(StandardCharsets.UTF_8.encode(input));
		return String.format("%032x", new BigInteger(1, md5.digest()));
	}

	@Override
	public boolean endsWith(String first, String second) {
		return first.endsWith(second);
	}

	@Override
	public int indexOf(String haystack, String needle, int index) {
		return haystack.indexOf(needle, index);
	}

	@Override
	public String join(String[] input, String delimiter) {
		return StringUtils.join(input, delimiter);
	}

}
