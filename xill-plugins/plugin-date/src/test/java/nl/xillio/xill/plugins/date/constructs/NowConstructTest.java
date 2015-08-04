package nl.xillio.xill.plugins.date.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.time.ZonedDateTime;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.date.services.DateService;

import org.testng.annotations.Test;

/**
 * 
 * Tests the {@link NowConstruct}
 * 
 * @author Geert Konijnendijk
 *
 */
public class NowConstructTest {

	/**
	 * Test the process method under the default circumstances
	 */
	@Test
	public void testProcess() {
		// Mock

		// ZonedDateTime is final, don't mock
		ZonedDateTime mockDate = ZonedDateTime.now();
		DateService dateService = mock(DateService.class);
		when(dateService.now()).thenReturn(mockDate);

		// Run
		MetaExpression date = NowConstruct.process(dateService);

		// Verify
		verify(dateService).now();

		// Assert
		assertSame(date.getMeta(ZonedDateTime.class), mockDate);
	}
}
