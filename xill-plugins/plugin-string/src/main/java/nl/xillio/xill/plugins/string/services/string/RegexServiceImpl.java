package nl.xillio.xill.plugins.string.services.string;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.plugins.string.constructs.RegexConstruct;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private RegexTimer regexTimer = null;
    private static Logger logger = LogManager.getLogger();

    /**
     * The implementation of the {@link RegexService}
     */
    @Inject
    public RegexServiceImpl(RegexTimer regexTimer) {
        this.regexTimer = regexTimer;
        new Thread(regexTimer).start();
    }


    @Override
    public Matcher getMatcher(final String regex, final String value, int timeout) throws FailedToGetMatcherException, IllegalArgumentException {
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
                } catch (InterruptedException e) {
                    logger.error("Exception while running timer.", e);
                }
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

}
