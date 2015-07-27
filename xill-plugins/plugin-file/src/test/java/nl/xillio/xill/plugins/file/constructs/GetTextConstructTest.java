package nl.xillio.xill.plugins.file.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.File;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.extraction.TextExtractor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import org.testng.annotations.Test;

/**
 * Test the GetTextConstruct
 */
public class GetTextConstructTest {

	@Test
	public void testProcessNormal() throws Exception {
		// Timeout
		Number timeoutValue = 2000;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);

		// File
		String path = "This is a path";
		MetaExpression file = mock(MetaExpression.class);
		when(file.getStringValue()).thenReturn(path);

		// Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		// FileUtils
		File targetFile = mock(File.class);
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(robotID, path)).thenReturn(targetFile);

		// Extractor
		String resultText = "This is the result";
		TextExtractor extractor = mock(TextExtractor.class);
		when(extractor.extractText(targetFile, timeoutValue.intValue())).thenReturn(resultText);

		// Run the Method
		MetaExpression result = GetTextConstruct.process(context, extractor, fileUtils, file, timeout);

		// Verify
		verify(extractor, times(1)).extractText(targetFile, timeoutValue.intValue());
		verify(fileUtils, times(1)).buildFile(robotID, path);

		// Assert
		assertEquals(result.getStringValue(), resultText);
	}

	@Test(expectedExceptions = RobotRuntimeException.class,
			expectedExceptionsMessageRegExp = "Expected a valid number for timeout\\.")
	public void testProcessInvalidTimeout() {
		// Timeout
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(Double.NaN);

		// Run the Method
		GetTextConstruct.process(null, null, null, null, timeout);
	}
}
