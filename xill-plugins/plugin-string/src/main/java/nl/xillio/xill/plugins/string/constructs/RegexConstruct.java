package nl.xillio.xill.plugins.string.constructs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Returns a list of matches of the specified regex on the provided
 * string. </br>
 * </br>
 *
 *
 * @author Sander
 */
public class RegexConstruct extends Construct implements HelpComponent {

	private RegexTimer regexTimer = null;

	/**
	 * The default timeout for regular expressions.
	 */
	public final static int REGEX_TIMEOUT = 5;

	/**
	 * Create a new {@link RegexConstruct} and start the regexTimer {@link Thread}
	 */
	public RegexConstruct() {
		regexTimer = new RegexTimer();
		new Thread(regexTimer).start();
	}
	
	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(this::process, new Argument("string"), new Argument("regex"), new Argument("timeout", fromValue(REGEX_TIMEOUT)));
	}

	private MetaExpression process(final MetaExpression valueVar, final MetaExpression regexVar, final MetaExpression timeoutVar) {

		String regex = regexVar.getStringValue();
		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			Matcher matcher = getMatcher(regex, valueVar.getStringValue(), timeout);

			if (matcher.matches()) {
				List<MetaExpression> list = new ArrayList<>();
				for (int i = 0; i <= matcher.groupCount(); i++) {
					String capture = matcher.group(i);
					if (capture != null) {
						list.add(i, fromValue(capture));
					} else {
						list.add(i, NULL);
					}
				}
				return fromValue(list);
			}
			return NULL;
		} catch (PatternSyntaxException e) {
			throw new RobotRuntimeException("Invalid pattern in regex()" + regex + " - ");
		} catch (Exception e) {
			throw new RobotRuntimeException("Error while executing the regex");
		}
	}

	/**
	 *
	 * @param regex
	 * @param value
	 * @param timeout
	 * @return the matcher
	 */
	public Matcher getMatcher(final String regex, final String value, int timeout) {
		if (timeout < 0) {
			timeout = RegexConstruct.REGEX_TIMEOUT;
		}

		regexTimer.setTimer(timeout);
		return Pattern.compile(regex, Pattern.DOTALL).matcher(new TimeoutCharSequence(value));
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

		@Override
		public IntStream chars() {
			return inner.chars();
		}

		@Override
		public IntStream codePoints() {
			return inner.codePoints();
		}
	}

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

		public synchronized boolean timeOut() {
			return timeout;
		}

		public void stop() {
			stop = true;
		}
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/regex.xml");
	}

}
