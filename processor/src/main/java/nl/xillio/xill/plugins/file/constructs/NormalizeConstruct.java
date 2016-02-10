package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

import java.nio.file.Path;

/**
 * This construct will return an absolute and normalized path.
 *
 * @author Thomas biesaart
 */
public class NormalizeConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                path -> process(context, path),
                new Argument("path", ExpressionDataType.ATOMIC)
        );
    }

    MetaExpression process(ConstructContext context, MetaExpression path) {
        Path resolvedValue = getPath(context, path);
        return fromValue(resolvedValue.toString());
    }


}
