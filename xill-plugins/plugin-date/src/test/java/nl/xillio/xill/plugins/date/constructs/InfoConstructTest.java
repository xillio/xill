package nl.xillio.xill.plugins.date.constructs;

import static nl.xillio.xill.plugins.date.utils.MockUtils.mockDateExpression;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.date.services.DateService;

import org.testng.annotations.Test;

public class InfoConstructTest {
	@Test
	public void testProcess() {
		// Mock
		DateService dateService = mock(DateService.class);
		Map<String, Long> fieldValues = new HashMap<>();
		fieldValues.put("Field1", 10l);
		fieldValues.put("Field2", 20l);
		fieldValues.put("Field3", 30l);
		when(dateService.getFieldValues(any())).thenReturn(fieldValues);
		ZoneId zoneId = mock(ZoneId.class);
		when(dateService.getTimezone(any())).thenReturn(zoneId);
		when(dateService.isInFuture(any())).thenReturn(true);
		when(dateService.isInPast(any())).thenReturn(false);
		// ZonedDateTime is final, don't mock
		ZonedDateTime date = ZonedDateTime.now();
		MetaExpression dateExpression = mockDateExpression(date);

		// Run
		MetaExpression info = InfoConstruct.process(dateExpression, dateService);

		// Verify
		verify(dateService).getFieldValues(any());
		verify(dateService).getTimezone(any());
		verify(dateService).isInFuture(any());
		verify(dateService).isInPast(any());

		// Test

	}
}
