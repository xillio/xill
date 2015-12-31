package nl.xillio.xill.plugins.collection.constructs;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * returns the length of the given {@link ExpressionDataType#LIST} or {@link ExpressionDataType#OBJECT}.
 * </p>
 *
 * @author Sander Visser
 */
public class LengthConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                list -> process(list),
                new Argument("list", LIST, OBJECT));
    }

    static MetaExpression process(final MetaExpression input) {

        int elements = 0;

        if (input.getType() == LIST) {
            @SuppressWarnings("unchecked")
            List<MetaExpression> list = (ArrayList<MetaExpression>) input.getValue();
            elements = list.size();

        } else {
            //can suppress warning since the input only accepts LIST or OBJECT.
            @SuppressWarnings("unchecked")
            Map<String, MetaExpression> object = (Map<String, MetaExpression>) input.getValue();
            elements = object.size();
        }
        return fromValue(elements);
    }
}
