package nl.xillio.xill.api.data;

import java.time.Instant;

/**
 * This class is responsible for building a date.
 *
 * @author Thomas Biesaart
 */
public interface DateFactory {
    /**
     * Build a Xill Date wrapper from an Instant.
     *
     * @param instant the instant
     * @return the Date
     */
    Date from(Instant instant);
}
