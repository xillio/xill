package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

import java.util.LinkedHashMap;

/**
 * Returns detailed info on the specified date.
 *
 * @author Sander
 */
public class InfoConstruct extends BaseDateConstruct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(dateVar -> process(dateVar, getDateService()), new Argument("date"));

    }

    static MetaExpression process(final MetaExpression dateVar, DateService dateService) {
        Date date = getDate(dateVar, "date");

        LinkedHashMap<String, MetaExpression> info = new LinkedHashMap<>();

        // Get ChronoField values
        dateService.getFieldValues(date).forEach((k, v) -> info.put(k, fromValue(v)));

        info.put("timeZone", fromValue(dateService.getTimezone(date).toString()));
        info.put("isInFuture", fromValue(dateService.isInFuture(date)));
        info.put("isInPast", fromValue(dateService.isInPast(date)));

        return fromValue(info);

    }
}
