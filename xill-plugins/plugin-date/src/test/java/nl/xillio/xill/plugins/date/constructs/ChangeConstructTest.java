package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.date.data.Date;
import nl.xillio.xill.plugins.date.services.DateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Test the {@link ChangeConstruct}
 *
 * @author Geert Konijnendijk
 */
public class ChangeConstructTest {

	private static final Logger log = LogManager.getLogger();

	private DateService dateService;
	private Date date;
	private MetaExpression dateExpression;
	private LinkedHashMap<String, MetaExpression> changes;
	private MetaExpression changesExpression;

	/**
	 * Setup for all tests in this class
	 */
	@BeforeMethod
	public void setup() {
		// Mock DateService, simply returns the date passed in
		dateService = mock(DateService.class);

		// Answer returning the first argument as a ZonedDateTime
		Answer<Date> returnAsZonedDateTime = invocation -> invocation.getArgumentAt(0, Date.class);

		when(dateService.changeTimeZone(any(), any())).then(returnAsZonedDateTime);
		when(dateService.add(any(), any())).then(returnAsZonedDateTime);

		date = mock(Date.class);
		dateExpression = mock(MetaExpression.class);
		when(dateExpression.getMeta(Date.class)).thenReturn(date);
		changes = new LinkedHashMap<>();
		changesExpression = mock(MetaExpression.class);
		when(changesExpression.getValue()).thenReturn(changes);
	}

	/**
	 * Test the process method with just time unit changes
	 */
	@Test
	public void testProcessUnits() {
		// Mock
		addUnitsChange();

		// Run
		MetaExpression newDate = runProcess();

		// Verify
		verify(dateService).add(any(), any());
		verify(dateService, times(0)).changeTimeZone(any(), any());

		// Assert
		assertEqualDate(newDate);
	}

	/**
	 * Test the process method with just time zone change
	 */
	@Test
	public void testProcessZone() {
		// Mock
		addZoneChange();

		// Run
		MetaExpression newDate = runProcess();

		// Verify
		verify(dateService).changeTimeZone(any(), any());

		// Assert
		assertEqualDate(newDate);
	}

	/**
	 * Test both time unit changes and time zone change
	 */
	@Test
	public void testProcessUnitsZone() {
		// Mock
		addUnitsChange();
		addZoneChange();

		// Run
		MetaExpression newDate = runProcess();

		// Verify
		verify(dateService).add(any(), any());
		verify(dateService).changeTimeZone(any(), any());

		// Assert
		assertEqualDate(newDate);
	}

	/**
	 * Test the process with no changes at all
	 */
	@Test
	public void testProcessEmpty() {
		// Mock

		// Run
		MetaExpression newDate = runProcess();

		// Verify
		verify(dateService, times(0)).changeTimeZone(any(), any());

		// Assert
		assertSame(newDate.getMeta(Date.class), date);
	}

	private MetaExpression mockNumberExpression(int number) {
		MetaExpression integer = mock(MetaExpression.class);
		when(integer.getNumberValue()).thenReturn(number);
		return integer;
	}

	private void addUnitsChange() {
		changes.put("Nanos", mockNumberExpression(42));
		changes.put("Hours", mockNumberExpression(42));
		changes.put("Centuries", mockNumberExpression(42));
	}

	private void addZoneChange() {
		MetaExpression zone = mock(MetaExpression.class);
		when(zone.getStringValue()).thenReturn("Europe/Amsterdam");
		changes.put("zone", zone);
	}

	private MetaExpression runProcess() {
		return ChangeConstruct.process(log, dateExpression, changesExpression, dateService);
	}

	private void assertEqualDate(MetaExpression newDate) {
		assertEquals(newDate.getMeta(Date.class), date);
	}
}
