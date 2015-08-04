package nl.xillio.xill.plugins.date.constructs;

import static nl.xillio.xill.plugins.date.utils.MockUtils.mockBoolExpression;
import static nl.xillio.xill.plugins.date.utils.MockUtils.mockDateExpression;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.date.services.DateService;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * Tests for the {@link DiffConstruct}
 * 
 * @author Geert Konijnendijk
 *
 */
public class DiffConstructTest {

	/**
	 * @return Data containing two maps (which should be returned from the mock {@link DateService#difference(java.time.temporal.Temporal, java.time.temporal.Temporal, boolean)}) for testing absolute
	 *         and relative difference.
	 */
	@DataProvider(name = "differences")
	private Object[][] mapProvider() {
		Map<String, Double> absoluteDifference = new HashMap<>();
		absoluteDifference.put("Unit1", 10.0);
		absoluteDifference.put("Unit2", 20.0);
		MetaExpression trueExpression = mockBoolExpression(true);

		Map<String, Double> relativeDifference = new HashMap<>();
		relativeDifference.put("Unit1", -10.0);
		relativeDifference.put("Unit2", 20.0);
		MetaExpression falseExpression = mockBoolExpression(false);

		return new Object[][] { {absoluteDifference, trueExpression}, {relativeDifference, falseExpression}};
	}

	/**
	 * Test the process method
	 * 
	 * @param differences
	 *        Map with differences as could be returned by {@link DateService#difference(java.time.temporal.Temporal, java.time.temporal.Temporal, boolean)}.
	 * @param absolute
	 *        Whether the difference should be absolute or relative
	 */
	@Test(dataProvider = "differences")
	public void testProcess(Map<String, Double> differences, MetaExpression absolute) {
		// Mock
		ZonedDateTime date1 = ZonedDateTime.now(), date2 = ZonedDateTime.now();
		MetaExpression date1Expression = mockDateExpression(date1), date2Expression = mockDateExpression(date2);
		DateService dateService = mock(DateService.class);
		when(dateService.difference(any(), any(), anyBoolean())).thenReturn(differences);

		// Run
		MetaExpression difference = DiffConstruct.process(date1Expression, date2Expression, absolute, dateService);

		// Verify
		verify(dateService).difference(any(), any(), anyBoolean());

		// Assert
		((Map<String, MetaExpression>) difference.getValue()).forEach((k, v) -> assertEquals(differences.get(k), v.getNumberValue().doubleValue(), 10e-9));

	}
}
