package nl.xillio.xill.plugins.system.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.services.wait.WaitService;
import nl.xillio.xill.testutils.ConstructTest;

import org.testng.annotations.Test;

/**
 * Test the {@link WaitConstruct}
 */
public class WaitConstructTest extends ConstructTest {

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
