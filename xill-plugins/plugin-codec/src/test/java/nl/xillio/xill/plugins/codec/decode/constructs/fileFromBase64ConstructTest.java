package nl.xillio.xill.plugins.codec.decode.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import org.testng.annotations.Test;

/**
 * Test the {@link FileFromBase64Construct}.
 */
public class FileFromBase64ConstructTest extends TestUtils {

	/**
	 * Test the process method under normal circumstances.
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public void processNormalUsage() throws IOException {
		// Mock
		String contentValue = "PG5vdGU+DQo8dG8+VG92ZTwvdG8+DQo8ZnJvbT5KYW5pPC9mcm9tPg0KPGhlYWRpbmc+UmVtaW5kZXI8L2hlYWRpbmc+DQo8Ym9keT5Eb24ndCBmb3JnZXQgbWUgdGhpcyB3ZWVrZW5kITwvYm9keT4NCjwvbm90ZT4=";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentValue);
		when(content.isNull()).thenReturn(false);

		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		byte[] data = new byte[10];
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.parseBase64Binary(contentValue)).thenReturn(data);
		UrlUtilityService urlUtilityService = mock(UrlUtilityService.class);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlUtilityService, null);

		// Verify
		verify(stringService, times(1)).parseBase64Binary(contentValue);
		verify(urlUtilityService, times(1)).write(null, data);

		// Assert
		// We return NULL, nothing to assert.
	}

	/**
	 * Tests the process when a fileNotFoundException is thrown
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void processFileNotFoundException() throws IOException {
		// Mock
		String contentValue = "PG5vdGU+DQo8dG8+VG92ZTwvdG8+DQo8ZnJvbT5KYW5pPC9mcm9tPg0KPGhlYWRpbmc+UmVtaW5kZXI8L2hlYWRpbmc+DQo8Ym9keT5Eb24ndCBmb3JnZXQgbWUgdGhpcyB3ZWVrZW5kITwvYm9keT4NCjwvbm90ZT4=";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentValue);
		when(content.isNull()).thenReturn(false);

		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		byte[] data = new byte[10];
		Exception exception = new FileNotFoundException();
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.parseBase64Binary(contentValue)).thenReturn(data);
		UrlUtilityService urlUtilityService = mock(UrlUtilityService.class);
		doThrow(exception).when(urlUtilityService).write(null, data);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlUtilityService, null);

		// Verify
		verify(stringService, times(1)).parseBase64Binary(contentValue);
		verify(urlUtilityService, times(1)).write(null, data);
	}

	/**
	 * Tests the process when an IOException is thrown
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Error writing to file: .*")
	public void processIOException() throws IOException {
		// Mock
		String contentValue = "PG5vdGU+DQo8dG8+VG92ZTwvdG8+DQo8ZnJvbT5KYW5pPC9mcm9tPg0KPGhlYWRpbmc+UmVtaW5kZXI8L2hlYWRpbmc+DQo8Ym9keT5Eb24ndCBmb3JnZXQgbWUgdGhpcyB3ZWVrZW5kITwvYm9keT4NCjwvbm90ZT4=";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentValue);
		when(content.isNull()).thenReturn(false);

		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(false);

		byte[] data = new byte[10];
		Exception exception = new IOException();
		StringUtilityService stringService = mock(StringUtilityService.class);
		when(stringService.parseBase64Binary(contentValue)).thenReturn(data);
		UrlUtilityService urlUtilityService = mock(UrlUtilityService.class);
		doThrow(exception).when(urlUtilityService).write(null, data);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlUtilityService, null);

		// Verify
		verify(stringService, times(1)).parseBase64Binary(contentValue);
		verify(urlUtilityService, times(1)).write(null, data);

		// Assert
		// We return NULL, nothing to assert.
	}

	/**
	 * Tests the process when it fails to return a value.
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void processFailureToConvert() throws IOException
	{
		// Mock
		String contentValue = "PG5vdGU+DQo8dG8+VG92ZTwvdG8+DQo8ZnJvbT5KYW5pPC9mcm9tPg0KPGhlYWRpbmc+UmVtaW5kZXI8L2hlYWRpbmc+DQo8Ym9keT5Eb24ndCBmb3JnZXQgbWUgdGhpcyB3ZWVrZW5kITwvYm9keT4NCjwvbm90ZT4=";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentValue);
		when(content.isNull()).thenReturn(true);

		String fileNameValue = "C:/tmp/test.txt";
		MetaExpression fileName = mock(MetaExpression.class);
		when(fileName.getStringValue()).thenReturn(fileNameValue);
		when(fileName.isNull()).thenReturn(true);

		StringUtilityService stringService = mock(StringUtilityService.class);
		UrlUtilityService urlUtilityService = mock(UrlUtilityService.class);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlUtilityService, null);

		// Verify
		verify(stringService, times(0)).parseBase64Binary(anyString());
		verify(urlUtilityService, times(0)).write(any(File.class), any());
	}

}
