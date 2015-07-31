package nl.xillio.xill.plugins.file.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.io.File;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import org.testng.annotations.Test;

/**
 * Test the ExistsConstruct
 */
public class ExistsConstructTest {

	@Test
	public void testProcessNormalTrueAndFalse() throws Exception {
		// URI
		String path = "This is the path";
		MetaExpression uri = mock(MetaExpression.class);
		when(uri.getStringValue()).thenReturn(path);

		// Context
		RobotID robotID = mock(RobotID.class);
		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		// FileUtils
		File file = mock(File.class);
		FileUtilities fileUtils = mock(FileUtilities.class);
		when(fileUtils.buildFile(robotID, path)).thenReturn(file);
		when(fileUtils.exists(file)).thenReturn(true, false);

		// Run the method once for true
		MetaExpression result = ExistsConstruct.process(context, fileUtils, uri);

		// Verify
		verify(fileUtils).buildFile(robotID, path);
		verify(fileUtils).exists(file);

		// Assert
		assertSame(result, ExpressionBuilderHelper.TRUE);

		// Run the method again for false
		result = ExistsConstruct.process(context, fileUtils, uri);

		// Verify
		verify(fileUtils, times(2)).buildFile(robotID, path);
		verify(fileUtils, times(2)).exists(file);

		// Assert
		assertSame(result, ExpressionBuilderHelper.FALSE);
	}
}
