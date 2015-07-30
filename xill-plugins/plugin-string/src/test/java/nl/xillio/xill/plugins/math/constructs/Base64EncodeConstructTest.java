package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.constructs.Base64EncodeConstruct;
import nl.xillio.xill.plugins.string.services.string.StringService;
import nl.xillio.xill.plugins.string.services.string.UrlService;

import org.testng.annotations.Test;

/**
 * Test the {@link Base64EncodeConstruct}.
 */
public class Base64EncodeConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public void processNormalUsage() throws IOException {
		// Mock
		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		byte[] bytes = new byte[10];
		String resultValue = "pretty";
		StringService stringService = mock(StringService.class);
		when(stringService.printBase64Binary(bytes)).thenReturn(resultValue);
		UrlService urlService = mock(UrlService.class);
		when(urlService.readFileToByteArray(fileNameValue)).thenReturn(bytes);

		// Run
		Base64EncodeConstruct.process(fileName, stringService, urlService);

		// Verify
		verify(stringService, times(1)).printBase64Binary(bytes);
		verify(urlService, times(1)).readFileToByteArray(fileNameValue);

		// Assert
		// We return NULL, nothing to assert.
	}

	/**
	 * Tests the process when a fileNotFoundException is thrown
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "IO Exception")
	public void processIOException() throws FileNotFoundException, IOException {
		// Mock
		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		Exception exception = new IOException();
		StringService stringService = mock(StringService.class);
		UrlService urlService = mock(UrlService.class);
		when(urlService.readFileToByteArray(fileNameValue)).thenThrow(exception);

		// Run
		Base64EncodeConstruct.process(fileName, stringService, urlService);

		// Verify
		verify(stringService, times(0)).printBase64Binary(any());
		verify(urlService, times(1)).readFileToByteArray(fileNameValue);
	}

	/**
	 * Tests the process when it fails to return a value.
	 *
	 * @throws IOException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void processFailureToConvert() throws IOException
	{
		// Mock
		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(true);

		byte[] bytes = new byte[10];
		String resultValue = "pretty";
		StringService stringService = mock(StringService.class);
		when(stringService.printBase64Binary(bytes)).thenReturn(resultValue);
		UrlService urlService = mock(UrlService.class);
		when(urlService.readFileToByteArray(fileNameValue)).thenReturn(bytes);

		// Run
		Base64EncodeConstruct.process(fileName, stringService, urlService);

		// Verify
		verify(stringService, times(0)).printBase64Binary(bytes);
		verify(urlService, times(0)).readFileToByteArray(fileNameValue);
	}

}
