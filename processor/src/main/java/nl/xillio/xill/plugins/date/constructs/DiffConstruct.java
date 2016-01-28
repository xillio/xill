package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Returns the difference between two dates. By default the function will return
 * the absolute difference. Optionally you can set 'absolute' to false to get
 * the relative difference.
 *
 * @author Sander
 */
public class DiffConstruct extends BaseDateConstruct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor((dateVar, otherVar, absolute) -> process(dateVar, otherVar, absolute, getDateService()), new Argument("date"), new Argument("other"), new Argument("absolute", TRUE));
    }

    static MetaExpression process(final MetaExpression dateVar, final MetaExpression otherVar, final MetaExpression absolute, DateService dateService) {
        Date date = getDate(dateVar, "date");
        Date other = getDate(otherVar, "other");

        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();

        Map<String, Double> diff = dateService.difference(date, other, absolute.getBooleanValue());

        diff.forEach((k, v) -> result.put(k, fromValue(v)));

        return fromValue(result);
    }
}
