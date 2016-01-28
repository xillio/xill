package nl.xillio.xill.plugins.collection.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.collection.services.duplicate.Duplicate;

/**
 * Returns a deep copy of the given list or object.
 *
 * @author Sander Visser
 */
public class DuplicateConstruct extends Construct {

    @Inject
    private Duplicate duplicate;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                input -> process(input, duplicate),
                new Argument("collection", LIST, OBJECT));
    }

    /**
     * Returns a deep copy of the given list or object.
     *
     * @param input the list or object.
     * @return the deep copy of the list or object.
     */
    static MetaExpression process(final MetaExpression input, final Duplicate duplicate) {
        Object obj = extractValue(input);
        obj = duplicate.duplicate(obj);
        MetaExpression output = MetaExpression.parseObject(obj);
        return output;

    }
}
