package nl.xillio.xill.plugins.date.constructs;

import static nl.xillio.xill.plugins.date.utils.MockUtils.mockIntExpression;
import static nl.xillio.xill.plugins.date.utils.MockUtils.mockNullExpression;
import static nl.xillio.xill.plugins.date.utils.MockUtils.mockStringExpression;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.services.DateService;

import org.testng.annotations.Test;

public class OfConstructTest {

	String zoneId = "Europe/Amsterdam";

	// ZonedDateTime is final, don't mock
	ZonedDateTime date = ZonedDateTime.now();

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		DateService dateService = mockDateService(date);
		MetaExpression[] values = mockParameters(zoneId);

		// Run
		MetaExpression dateExpression = OfConstruct.process(values, dateService);

		// Verify
		verify(dateService).constructDate(0, 1, 2, 3, 4, 5, 6, ZoneId.of(zoneId));

		// Assert
		assertSame(dateExpression.getMeta(ZonedDateTime.class), date);
	}

	/**
	 * Test the process method when one of the input values is null
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "^.*cannot\\sbe\\snull.*$")
	public void testProcessInputNull() {
		// Mock
		DateService dateService = mockDateService(date);
		MetaExpression[] values = mockParameters(zoneId);
		values[0] = mockNullExpression();

		// Run
		MetaExpression dateExpression = OfConstruct.process(values, dateService);

		// Verify
		verify(dateService, never()).constructDate(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any());

		// Assert
	}

	/**
	 * Test the process method with an invalid zone
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "^.*Invalid\\szone\\sID.*$")
	public void testProcessInvalidZone() {
		// Mock
		DateService dateService = mockDateService(date);
		MetaExpression[] values = mockParameters("Wrong");

		// Run
		MetaExpression dateExpression = OfConstruct.process(values, dateService);

		// Verify
		verify(dateService, never()).constructDate(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any());

		// Assert
	}

	/**
	 * Mock a DateService with only a {@link DateService#constructDate(int, int, int, int, int, int, int, ZoneId)} method.
	 * 
	 * @param date
	 *        Date to return from the {@link DateService#constructDate(int, int, int, int, int, int, int, ZoneId)} method
	 */
	private DateService mockDateService(ZonedDateTime date) {
		DateService dateService = mock(DateService.class);
		when(dateService.constructDate(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any())).thenReturn(date);
		return dateService;
	}

	/**
	 * @param zoneId
	 *        ZoneId to use in the parameters
	 * @return An array of input parameters for the {@link OfConstruct#process(MetaExpression[], DateService)} method
	 */
	private MetaExpression[] mockParameters(String zoneId) {
		MetaExpression[] values =
		{mockIntExpression(0), mockIntExpression(1), mockIntExpression(2), mockIntExpression(3), mockIntExpression(4), mockIntExpression(5), mockIntExpression(6), mockStringExpression(zoneId)};
		return values;
	}
}
