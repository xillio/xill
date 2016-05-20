package nl.xillio.xill.plugins.date;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.errors.InvalidUserInputException;
import nl.xillio.xill.plugins.date.services.DateService;

import java.time.ZonedDateTime;

/**
 * This class contains some utility for the constructs to use
 */
public abstract class BaseDateConstruct extends Construct {

    /**
     * Service used by all extending classes
     */
    @Inject
    private DateService dateService;

    /**
     * Get the date from a variable
     *
     * @param dateVar The expression
     * @param name    The name of the parameter
     * @return        The date.
     */
    protected static Date getDate(final MetaExpression dateVar, final String name) {
        Date date = dateVar.getMeta(Date.class);

        if (date == null) {
            throw new InvalidUserInputException("Expected a date variable.", dateVar.getStringValue(), "Create a date using either Date.parse() or Date.of().");
        }

        return date;
    }

    /**
     * Get the current date and time.
     *
     * @return the current {@link ZonedDateTime}
     */
    protected static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    /**
     * Create a {@link MetaExpression} from {@link ZonedDateTime}
     *
     * @param date    The provided date object from to retrieve the value.
     * @return        The date value.
     */
    protected static MetaExpression fromValue(final Date date) {
        MetaExpression value = fromValue(date.toString());
        value.storeMeta(date);
        return value;
    }

    public DateService getDateService() {
        return dateService;
    }
}
