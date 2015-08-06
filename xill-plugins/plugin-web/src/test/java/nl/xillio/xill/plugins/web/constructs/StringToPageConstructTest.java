package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.annotations.Test;

/**
 * test the {@link StringToPageConstruct}.
 *
 */
public class StringToPageConstructTest {
	
	/**
	 * Test the process under normal circumstances.
	 * @throws IOException 
	 */
	@Test
	public void testProcessNormalUsage() throws IOException{
			// mock
			WebService webService = mock(WebService.class);
			FileService fileService = mock(FileService.class);
			
			//the content variable
			String contentValue = "This is the content";
			MetaExpression content = mock(MetaExpression.class);
			when(content.getStringValue()).thenReturn(contentValue);
			
			//the file variable
			File file = mock(File.class);
			
			//the process
			when(fileService.createTempFile(anyString(), anyString())).thenReturn(file);
			when(fileService.getAbsolutePath(file)).thenReturn("file");
			
			// run
			StringToPageConstruct.process(content, fileService, webService);
			
			// verify
			verify(fileService, times(1)).createTempFile(anyString(), anyString());
			verify(fileService, times(1)).getAbsolutePath(file);
	}
	
	/**
	 * Test the process under normal circumstances.
	 * @throws IOException 
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "An IO error occurred.")
	public void testProcessIOException() throws IOException{
			// mock
			WebService webService = mock(WebService.class);
			FileService fileService = mock(FileService.class);
			
			//the content variable
			String contentValue = "This is the content";
			MetaExpression content = mock(MetaExpression.class);
			when(content.getStringValue()).thenReturn(contentValue);
			
			//the process
			when(fileService.createTempFile(anyString(), anyString())).thenThrow(new IOException());
			
			// run
			StringToPageConstruct.process(content, fileService, webService);
	}

}
