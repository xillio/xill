package nl.xillio.xill.plugins.concurrency.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.concurrency.data.Pipeline;
import nl.xillio.xill.plugins.concurrency.services.PipelineExecutor;
import nl.xillio.xill.plugins.concurrency.services.PipelineFactory;

/**
 * This construct will run a pipeline defined using the pipeline worker elements.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
class RunConstruct extends Construct {
    private final PipelineFactory pipelineFactory;
    private final PipelineExecutor pipelineExecutor;

    @Inject
    public RunConstruct(PipelineFactory pipelineFactory, PipelineExecutor pipelineExecutor) {
        this.pipelineFactory = pipelineFactory;
        this.pipelineExecutor = pipelineExecutor;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("pipeline", LIST)
        );
    }

    private MetaExpression process(MetaExpression configuration) {
        Pipeline pipeline = pipelineFactory.build(configuration.getValue());
        
        return NULL;
    }
}
