package nl.xillio.xill.plugins.string.services.string;

import com.google.inject.Singleton;
import me.biesaart.utils.Log;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.constructs.RegexConstruct;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * This is the main implementation of the RegexService
 */
@Singleton
public class RegexServiceImpl implements RegexService {

    // Regex for escaping a string so it can be included inside a regex
    public static final Pattern REGEX_ESCAPE_PATTERN = Pattern.compile("\\\\[a-zA-Z0-9]|\\[|\\]|\\^|\\$|\\-|\\.|\\{|\\}|\\?|\\*|\\+|\\||\\(|\\)");

    private static final Logger LOGGER = Log.get();
    private final CachedTimer cachedTimer;


    /**
     * The implementation of the {@link RegexService}
     */
    public RegexServiceImpl() {
        cachedTimer = new CachedTimer();
        new Thread(cachedTimer).start();
    }


    @Override
    public Matcher getMatcher(final String regex, final String value, int timeout) throws FailedToGetMatcherException, IllegalArgumentException {
        if (timeout < 0) {
            timeout = RegexConstruct.REGEX_TIMEOUT * 1000;
        }
        return Pattern.compile(regex, Pattern.DOTALL).matcher(new TimeoutCharSequence(value, timeout + cachedTimer.getCachedTime()));
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
    public String escapeRegex(String toEscape) {
        Matcher matcher = REGEX_ESCAPE_PATTERN.matcher(toEscape);
        return matcher.replaceAll("\\\\$0");
    }

    private class TimeoutCharSequence implements CharSequence {

        private final CharSequence inner;
        private final long endtime;

        public TimeoutCharSequence(final CharSequence inner, long endtime) {
            super();
            this.endtime = endtime;
            this.inner = inner;
        }

        @Override
        public char charAt(final int index) {
            if (cachedTimer.getCachedTime() > this.endtime) {
                throw new RobotRuntimeException("Pattern match timed out!");
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
            return new TimeoutCharSequence(inner.subSequence(start, end), endtime);
        }

        @Override
        public String toString() {
            return inner.toString();
        }
    }

    private class CachedTimer implements Runnable {
        private long cachedTime;
        private boolean isRunning = true;

        public CachedTimer() {
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        }

        @Override
        public void run() {
            while (isRunning) {
                cachedTime = System.currentTimeMillis();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error("Exception while running timer", e);
                }
            }
        }

        public long getCachedTime() {
            return cachedTime;
        }

        public void stop() {
            this.isRunning = false;
        }

    }

}
