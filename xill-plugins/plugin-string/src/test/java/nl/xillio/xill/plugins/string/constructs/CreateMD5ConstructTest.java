package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link CreateMD5Construct}.
 */
public class CreateMD5ConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 *
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void processNormalUsage() throws NoSuchAlgorithmException {
		// Mock
		String text = "string";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(text);

		String returnValue = "b45cffe084dd3d20d928bee85e7b0f21";
		RegexService regexService = mock(RegexService.class);
		when(regexService.createMD5Construct(text)).thenReturn(returnValue);

		// Run
		MetaExpression result = CreateMD5Construct.process(value, regexService);

		// Verify

		verify(regexService, times(1)).createMD5Construct(text);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}

	/**
	 * Test the process when it throws an error.
	 *
	 * @throws NoSuchAlgorithmException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "No such algorithm")
	public void processNoAlgorithmError() throws NoSuchAlgorithmException
	{
		// Mock
		String text = "string";
		MetaExpression value = mock(MetaExpression.class);
		when(value.getStringValue()).thenReturn(text);

		NoSuchAlgorithmException returnValue = new NoSuchAlgorithmException();
		RegexService regexService = mock(RegexService.class);
		when(regexService.createMD5Construct(text)).thenThrow(returnValue);

		// Run
		CreateMD5Construct.process(value, regexService);

		// Verify
		verify(regexService, times(1)).createMD5Construct(text);
	}
}
