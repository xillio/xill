package nl.xillio.xill.plugins.concurrency.services;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.OptionsEnum;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;

import java.util.Map;

/**
 * This class is responsible for building a {@link WorkerConfiguration} from a MetaExpression.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class WorkerConfigurationFactory {

    public WorkerConfiguration build(MetaExpression expression) {
        Map<String, MetaExpression> options = assertObject(expression);

        WorkerConfiguration result = new WorkerConfiguration();

        for (Map.Entry<String, MetaExpression> entry : options.entrySet()) {
            MetaExpression value = entry.getValue();

            Option option = OptionsEnum.ofLabel(Option.class, entry.getKey())
                    .orElseThrow(() -> new RobotRuntimeException("Option `" + entry.getKey() + "` does not exist"));

            option.apply(value, result);
        }

        if (result.getRobot() == null) {
            throw new RobotRuntimeException("Every worker must have a `robot` option");
        }

        return result;
    }

    private Map<String, MetaExpression> assertObject(MetaExpression expression) {
        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new RobotRuntimeException("A worker configuration must be an object.\n" +
                    "Cannot parse " + expression + " as a worker configuration.");
        }

        return expression.getValue();
    }

    private enum Option implements OptionsEnum {
        CONFIG {
            @Override
            void apply(MetaExpression value, WorkerConfiguration configuration) {
                configuration.setConfiguration(value);
            }
        },
        THREAD_COUNT {
            @Override
            void apply(MetaExpression value, WorkerConfiguration configuration) {
                configuration.setThreadCount(value.getNumberValue().intValue());
            }
        },
        QUEUE_SIZE {
            @Override
            void apply(MetaExpression value, WorkerConfiguration configuration) {
                configuration.setOutputQueueSize(value.getNumberValue().intValue());
            }
        },
        ROBOT {
            @Override
            void apply(MetaExpression value, WorkerConfiguration configuration) {
                configuration.setRobot(value.getStringValue());
            }
        };

        abstract void apply(MetaExpression value, WorkerConfiguration configuration);
    }
}
