package nl.xillio.xill.plugins.date.constructs;

import static nl.xillio.xill.plugins.date.utils.MockUtils.mockDateExpression;
import static nl.xillio.xill.plugins.date.utils.MockUtils.mockNullExpression;
import static nl.xillio.xill.plugins.date.utils.MockUtils.mockStringExpression;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.services.DateService;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test the {@link LocalizedFormatConstruct}
 * 
 * @author Geert Konijnendijk
 *
 */
public class LocalizedFormatConstructTest {

	@DataProvider(name = "localeFormatPermutations")
	private Object[][] localeFormatProvider() {
		String[] formatStyles = {"Full", null};
		FormatStyle[] expectedStyles = {FormatStyle.FULL, FormatStyle.MEDIUM};
		String[] locales = {"en-GB", "en", null};
		Locale[] expectedLocales = {Locale.UK, Locale.ENGLISH, null};

		final List<List<Object>> permutations = new ArrayList<>();
		for (int i = 0; i < formatStyles.length; i++) {
			for (int j = 0; j < formatStyles.length; j++) {
				for (int k = 0; k < locales.length; k++) {
					ArrayList<Object> permutation = new ArrayList<>();
					permutation.add(mockStringExpression(formatStyles[i]));
					permutation.add(expectedStyles[i]);
					permutation.add(mockStringExpression(formatStyles[j]));
					permutation.add(expectedStyles[j]);
					permutation.add(mockStringExpression(locales[k]));
					permutation.add(expectedLocales[k]);

					permutations.add(permutation);
				}
			}
		}

		Object[][] result = new Object[permutations.size()][];
		for (int i = 0; i < permutations.size(); i++) {
			List<Object> permutation = permutations.get(i);
			int permutationSize = permutation.size();
			result[i] = new Object[permutationSize];
			for (int j = 0; j < permutationSize; j++) {
				result[i][j] = permutation.get(j);
			}
		}

		return result;
	}

	/**
	 * Test the process method with different permutations of normal input
	 * 
	 * @param localeExpression
	 *        Locale variable
	 * @param expectedLocale
	 *        Locale expected to be passed to the {@link DateService}
	 * @param dateStyleExpression
	 *        FormatStyle variable
	 * @param expectedDateStyle
	 *        FormatStyle expected to be passed to the {@link DateService}
	 * @param timeStyleExpression
	 *        FormatStyle variable
	 * @param expectedTimeStyle
	 *        Locale expected to be passed to the {@link DateService}
	 */
	@Test(dataProvider = "localeFormatPermutations")
	public void testProcess(MetaExpression dateStyleExpression, FormatStyle expectedDateStyle, MetaExpression timeStyleExpression,
	    FormatStyle expectedTimeStyle, MetaExpression localeExpression, Locale expectedLocale) {

		// Mock
		DateService dateService = mock(DateService.class);
		String returnString = "2015-08-03 13:40";
		when(dateService.formatDateLocalized(any(), any(), any(), any())).thenReturn(returnString);
		ZonedDateTime date = ZonedDateTime.now();
		MetaExpression dateExpression = mockDateExpression(date);

		// Run
		MetaExpression formatted = LocalizedFormatConstruct.process(dateExpression, localeExpression, dateStyleExpression, timeStyleExpression, dateService);

		// Verify
		verify(dateService).formatDateLocalized(date, expectedDateStyle, expectedTimeStyle, expectedLocale);

		// Assert
		assertSame(formatted.getStringValue(), returnString);
	}

	@DataProvider(name = "wrongStyle")
	private Object[][] wrongStyleProvider() {
		return new Object[][] { {mockStringExpression("Wrong"), mockNullExpression()}, {mockNullExpression(), mockStringExpression("Wrong")}};
	}

	/**
	 * Test the process method with strings unparsable by {@link FormatStyle}.
	 * 
	 * @param dateStyleExpression
	 *        Sate style String MetaExpression
	 * @param timeStyleExpression
	 *        Time style String MetaExpression
	 */
	@Test(dataProvider = "wrongStyle", expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "^.*style\\shas\\sto\\sbe\\s'full','long','medium'\\sor\\s'short'.*$")
	public void testProcessWrongFormat(MetaExpression dateStyleExpression, MetaExpression timeStyleExpression) {
		// Mock
		DateService dateService = mock(DateService.class);
		ZonedDateTime date = ZonedDateTime.now();
		MetaExpression dateExpression = mockDateExpression(date);

		// Run
		MetaExpression formatted = LocalizedFormatConstruct.process(dateExpression, null, dateStyleExpression, timeStyleExpression, dateService);

		// Verify
		verify(dateService, never()).formatDateLocalized(any(), any(), any(), any());

		// Assert

	}
}
