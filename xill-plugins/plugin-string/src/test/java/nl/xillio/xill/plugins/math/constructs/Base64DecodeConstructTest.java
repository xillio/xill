package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.constructs.Base64DecodeConstruct;
import nl.xillio.xill.plugins.string.services.string.StringService;
import nl.xillio.xill.plugins.string.services.string.UrlService;

import org.testng.annotations.Test;

/**
 * Test the {@link Base64DecodeConstruct}.
 */
public class Base64DecodeConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public void processNormalUsage() throws FileNotFoundException, IOException {
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
		StringService stringService = mock(StringService.class);
		when(stringService.parseBase64Binary(contentValue)).thenReturn(data);
		UrlService urlService = mock(UrlService.class);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlService);

		// Verify
		verify(stringService, times(1)).parseBase64Binary(contentValue);
		verify(urlService, times(1)).write(fileNameValue, data);

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
	public void processFileNotFoundException() throws FileNotFoundException, IOException {
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
		StringService stringService = mock(StringService.class);
		when(stringService.parseBase64Binary(contentValue)).thenReturn(data);
		UrlService urlService = mock(UrlService.class);
		doThrow(exception).when(urlService).write(fileNameValue, data);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlService);

		// Verify
		verify(stringService, times(1)).parseBase64Binary(contentValue);
		verify(urlService, times(1)).write(fileNameValue, data);
	}

	/**
	 * Tests the process when an IOException is thrown
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "IO Exception")
	public void processIOException() throws FileNotFoundException, IOException {
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
		StringService stringService = mock(StringService.class);
		when(stringService.parseBase64Binary(contentValue)).thenReturn(data);
		UrlService urlService = mock(UrlService.class);
		doThrow(exception).when(urlService).write(fileNameValue, data);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlService);

		// Verify
		verify(stringService, times(1)).parseBase64Binary(contentValue);
		verify(urlService, times(1)).write(fileNameValue, data);

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
	public void processFailureToConvert() throws FileNotFoundException, IOException
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

		StringService stringService = mock(StringService.class);
		UrlService urlService = mock(UrlService.class);

		// Run
		Base64DecodeConstruct.process(content, fileName, stringService, urlService);

		// Verify
		verify(stringService, times(0)).parseBase64Binary(anyString());
		verify(urlService, times(0)).write(anyString(), any());
	}

}
