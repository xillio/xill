package nl.xillio.xill.plugins.exiftool.services;

import com.google.inject.Singleton;
import nl.xillio.exiftool.query.Projection;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;

/**
 * This class is responsible for building a projection object from a MetaExpression.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class ProjectionFactory {

    public Projection build(MetaExpression object) {
        if (object.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("A projection must be an OBJECT");
        }

        Projection result = new Projection();

        Map<String, MetaExpression> map = object.getValue();

        map.forEach((key, value) -> result.put(key, value.getBooleanValue()));

        return result;
    }
}
