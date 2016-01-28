package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.services.wait.WaitService;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link WaitConstruct}
 */
public class WaitConstructTest extends TestUtils {

    /**
     * Test normal usage
     */
    @Test
    public void process() {
        // Mock context
        Number delay = 5;
        MetaExpression delayVar = mockExpression(ATOMIC);
        when(delayVar.getNumberValue()).thenReturn(delay);

        WaitService service = mock(WaitService.class);

        // Run
        WaitConstruct.process(delayVar, service);

        // Verify
        verify(service).wait(delay.intValue());
    }
}
