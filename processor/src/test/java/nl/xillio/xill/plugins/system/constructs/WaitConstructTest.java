package nl.xillio.xill.plugins.system.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.services.wait.WaitService;

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
