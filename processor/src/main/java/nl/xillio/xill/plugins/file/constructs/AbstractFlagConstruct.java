package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;

/**
 * This class provides a base for all boolean property constructs.
 *
 * @author Thomas biesaart
 */
abstract class AbstractFlagConstruct extends AbstractFilePropertyConstruct<Boolean> {

    @Override
    protected MetaExpression parse(Boolean input) {
        return fromValue(input);
    }
}
