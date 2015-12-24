package nl.xillio.xill.plugins.codec.encode.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Test the {@link FileToBase64Construct}.
 */
public class FileToBase64ConstructTest extends TestUtils {

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
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.printBase64Binary(bytes)).thenReturn(resultValue);
		UrlUtilityService urlUtilityService = mock(UrlUtilityService.class);
		when(urlUtilityService.readFileToByteArray(null)).thenReturn(bytes);

		// Run
		Base64EncodeConstruct.process(fileName, stringService, urlUtilityService, null);

		// Verify
		verify(stringService, times(1)).printBase64Binary(bytes);
		verify(urlUtilityService, times(1)).readFileToByteArray(null);

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
	public void processIOException() throws IOException {
		// Mock
		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		Exception exception = new IOException();
		StringUtilityService stringService = mock(StringUtilityService.class);
		UrlUtilityService urlUtilityService = mock(UrlUtilityService.class);
		when(urlUtilityService.readFileToByteArray(null)).thenThrow(exception);

		// Run
		Base64EncodeConstruct.process(fileName, stringService, urlUtilityService, null);
	}

	/**
	 * Tests the process when it fails to return a value.
	 *
	 * @throws IOException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void processFailureToConvert() throws IOException {
		// Mock
		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(true);

		byte[] bytes = new byte[10];
		String resultValue = "pretty";
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.printBase64Binary(bytes)).thenReturn(resultValue);
		UrlUtilityService urlUtilityService = mock(UrlUtilityService.class);
		when(urlUtilityService.readFileToByteArray(null)).thenReturn(bytes);

		// Run
		Base64EncodeConstruct.process(fileName, stringService, urlUtilityService, null);

		// Verify
		verify(stringService, times(0)).printBase64Binary(bytes);
		verify(urlUtilityService, times(0)).readFileToByteArray(null);
	}

}
