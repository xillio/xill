package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

import java.time.DateTimeException;

/**
 * converts the date to a string using the provided format.
 * if no format is given the pattern "yyyy-MM-dd HH:mm:ss" is used.
 *
 * @author Sander
 */
public class FormatConstruct extends BaseDateConstruct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {

        return new ConstructProcessor((dateVar, formatVar) -> process(dateVar, formatVar, getDateService()), new Argument("date"), new Argument("format", NULL));
    }

    static MetaExpression process(final MetaExpression dateVar,
                                  final MetaExpression formatVar, DateService dateService) {

        Date date = getDate(dateVar, "date");

        try {
            String formatString = formatVar.isNull() ? null : formatVar.getStringValue();
            return fromValue(dateService.formatDate(date, formatString));
        } catch (DateTimeException | IllegalArgumentException e) {
            throw new RobotRuntimeException("Could not format date", e);
        }
    }
}
