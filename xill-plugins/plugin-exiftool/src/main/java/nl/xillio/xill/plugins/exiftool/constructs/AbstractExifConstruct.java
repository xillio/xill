package nl.xillio.xill.plugins.exiftool.constructs;

import com.google.inject.Inject;
import nl.xillio.exiftool.query.Projection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.exiftool.services.ProjectionFactory;

/**
 * This class provides a base implementation for all constructs in this package.
 *
 * @author Thomas Biesaart
 */
public abstract class AbstractExifConstruct extends Construct {
    private ProjectionFactory projectionFactory;

    @Inject
    public void setProjectionFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }

    protected Projection getProjection(MetaExpression expression) {
        try {
            return projectionFactory.build(expression);
        } catch (IllegalArgumentException e) {
            throw new RobotRuntimeException(expression + " is not a valid projection: " + e.getMessage(), e);
        }
    }
}
