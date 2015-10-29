package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.plugins.date.services.DateService;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static nl.xillio.xill.plugins.date.utils.MockUtils.mockNullExpression;
import static nl.xillio.xill.plugins.date.utils.MockUtils.mockStringExpression;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;

/**
 * Test the {@link ParseConstruct}
 *
 * @author Geert Konijnendijk
 */
public class ParseConstructTest {

	@DataProvider(name = "dateFormat")
	private Object[][] generateDateAndFormat() {
		MetaExpression formatString = mockStringExpression("yyyy-MM-dd");
		MetaExpression dateString = mockStringExpression("2015-08-03");
		MetaExpression nullExpression = mockNullExpression();
		return new Object[][] {{dateString, formatString}, {nullExpression, formatString}, {dateString, nullExpression}, {nullExpression, nullExpression}};
	}

	/**
	 * Test the process method under normal circumstances
	 *
	 * @param dateString   Date variable
	 * @param formatString Format variable
	 */
	@Test(dataProvider = "dateFormat")
	public void testProcess(MetaExpression dateString, MetaExpression formatString) {
		// Mock
		DateService dateService = mock(DateService.class);
		// ZonedDateTime is final, don't mock
		Date parsed = mock(Date.class);
		when(dateService.now()).thenReturn(parsed);
		when(dateService.parseDate(any(), any())).thenReturn(parsed);

		// Run
		MetaExpression parsedExpression = ParseConstruct.process(dateString, formatString, dateService);

		// Verify
		if (dateString.isNull()) {
			verify(dateService).now();
			verify(dateService, never()).parseDate(any(), any());
		} else {
			verify(dateService, never()).now();
			verify(dateService).parseDate(dateString.getStringValue(), formatString.getStringValue());
		}

		// Assert
		assertSame(parsedExpression.getMeta(Date.class), parsed);
	}
}
