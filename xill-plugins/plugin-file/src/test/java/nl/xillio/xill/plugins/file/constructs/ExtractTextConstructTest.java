package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.extraction.TextExtractor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the GetTextConstruct
 */
public class ExtractTextConstructTest {

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
		FileUtilities fileUtils = mock(FileUtilities.class);

		// Extractor
		String resultText = "This is the result";
		TextExtractor extractor = mock(TextExtractor.class);
		when(extractor.extractText(any(), eq(timeoutValue.intValue()))).thenReturn(resultText);

		// Run the Method
		MetaExpression result = ExtractTextConstruct.process(context, extractor, fileUtils, file, timeout);

		// Verify
		verify(extractor, times(1)).extractText(any(), eq(timeoutValue.intValue()));

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
		ExtractTextConstruct.process(null, null, null, null, timeout);
	}
}
