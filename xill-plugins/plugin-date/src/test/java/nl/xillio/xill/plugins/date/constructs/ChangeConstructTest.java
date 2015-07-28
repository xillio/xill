package nl.xillio.xill.plugins.date.constructs;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.date.services.DateService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
		dateService = mock(DateService.class);
		when(dateService.changeTimeZone(any(), any())).then(returnsFirstArg());
		when(dateService.add(any(), any())).then(returnsFirstArg());

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
		changes.put("Nanos", mockNumberExpression(42));
		changes.put("Hours", mockNumberExpression(42));
		changes.put("Centuries", mockNumberExpression(42));

		// Run
		MetaExpression newDate = ChangeConstruct.process(log, dateExpression, changesExpression, dateService);

		// Verify
		verify(dateService).add(any(), any());
		verify(dateService, times(0)).changeTimeZone(any(), any());

		// Assert
	}

	/**
	 * Test the preocess mthod with just time zone change
	 */
	@Test
	public void testProcessZone() {
		// Mock
		MetaExpression zone = mock(MetaExpression.class);
		when(zone.getStringValue()).thenReturn("Europe/Amsterdam");
		changes.put("zone", zone);

		// Run
		MetaExpression newDate = ChangeConstruct.process(log, dateExpression, changesExpression, dateService);

		// Verify
		verify(dateService).changeTimeZone(any(), any());

		// Assert
	}

	/**
	 * Test the process with no changes at all
	 */
	@Test
	public void testProcessEmpty() {
		// Mock

		// Run
		MetaExpression newDate = ChangeConstruct.process(log, dateExpression, changesExpression, dateService);

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
}
