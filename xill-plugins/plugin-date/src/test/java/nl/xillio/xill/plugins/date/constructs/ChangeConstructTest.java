package nl.xillio.xill.plugins.date.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.date.services.DateService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * Test the {@link ChangeConstruct}
 * 
 * @author Geert Konijnendijk
 *
 */
public class ChangeConstructTest {

	private static final Logger log = LogManager.getLogger();

	private DateService dateService;
	private ZonedDateTime date;
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
		Answer<ZonedDateTime> returnAsZonedDateTime = new Answer<ZonedDateTime>() {
			@Override
			public ZonedDateTime answer(InvocationOnMock invocation) throws Throwable {
				return ZonedDateTime.from(invocation.getArgumentAt(0, ChronoZonedDateTime.class));
			}
		};

		when(dateService.changeTimeZone(any(), any())).then(returnAsZonedDateTime);
		when(dateService.add(any(), any())).then(returnAsZonedDateTime);

		// ZonedDateTime is final, don't mock
		date = ZonedDateTime.of(2015, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
		dateExpression = mock(MetaExpression.class);
		when(dateExpression.getMeta(ZonedDateTime.class)).thenReturn(date);
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
		assertSame(newDate.getMeta(ZonedDateTime.class), date);
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
		assertEquals(newDate.getMeta(ZonedDateTime.class), date);
	}
}
