package nl.xillio.xill.plugins.concurrency.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.concurrency.services.PipelineExecutor;
import nl.xillio.xill.plugins.concurrency.services.PipelineFactory;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.*;

public class RunConstructTest extends TestUtils {
    @Test
    public void testRun() {
        PipelineFactory factory = mock(PipelineFactory.class);
        PipelineExecutor executor = mock(PipelineExecutor.class);

        RunConstruct construct = new RunConstruct(factory, executor);

        MetaExpression result = process(construct, emptyList());

        assertTrue(result.isNull());
        verify(factory).build(anyList());
        verify(executor).execute(any(), any());
    }
}