package nl.xillio.xill.plugins.string.services.string;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import nl.xillio.xill.plugins.string.constructs.RegexConstruct;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.inject.Singleton;

/**
 * @author Ivor
 *
 */
@Singleton
public class RegexServiceImpl implements RegexService {

	private class RegexTimer implements Runnable {
		private long targetTime = 0;
		private boolean stop = false;
		private boolean timeout = false;

		public RegexTimer() {
			Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
		}

		@Override
		public void run() {
			while (!stop) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {}
				if (targetTime > 0 && targetTime < System.currentTimeMillis()) {
					timeout = true;
					targetTime = 0;
				}
			}
		}

		public void setTimer(final int millis) {
			timeout = false;
			targetTime = System.currentTimeMillis() + millis;
		}

		public void stop() {
			stop = true;
		}

		public synchronized boolean timeOut() {
			return timeout;
		}
	}

	private class TimeoutCharSequence implements CharSequence {

		private final CharSequence inner;

		public TimeoutCharSequence(final CharSequence inner) {
			super();
			this.inner = inner;
		}

		@Override
		public char charAt(final int index) {
			if (regexTimer.timeOut()) {
				throw new RuntimeException("Pattern match timed out!");
			}
			return inner.charAt(index);
		}

		@Override
		public IntStream chars() {
			return inner.chars();
		}

		@Override
		public IntStream codePoints() {
			return inner.codePoints();
		}

		@Override
		public int length() {
			return inner.length();
		}

		@Override
		public CharSequence subSequence(final int start, final int end) {
			return new TimeoutCharSequence(inner.subSequence(start, end));
		}

		@Override
		public String toString() {
			return inner.toString();
		}
	}

	private RegexTimer regexTimer = null;

	/**
	 * The implementation of the {@link RegexService}
	 */
	public RegexServiceImpl() {
		regexTimer = new RegexTimer();
		new Thread(regexTimer).start();
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
	public Matcher getMatcher(final String regex, final String value, int timeout) throws IllegalArgumentException, PatternSyntaxException {
		if (timeout < 0) {
			timeout = RegexConstruct.REGEX_TIMEOUT;
		}

		regexTimer.setTimer(timeout);
		return Pattern.compile(regex, Pattern.DOTALL).matcher(new TimeoutCharSequence(value));
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
