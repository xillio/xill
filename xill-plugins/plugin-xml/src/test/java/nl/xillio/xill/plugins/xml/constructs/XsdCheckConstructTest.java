package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.TestUtils;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.plugins.xml.services.XsdService;
import nl.xillio.xill.api.construct.ConstructContext;

import java.io.File;

import org.apache.logging.log4j.Logger;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link XsdCheckConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class XsdCheckConstructTest extends TestUtils {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		Logger logger = mock(Logger.class);
		XsdService xsdService = mock(XsdService.class);

		MetaExpression xsdFilenameVar = mock(MetaExpression.class);
		when(xsdFilenameVar.getStringValue()).thenReturn(".");
		MetaExpression xmlFilenameVar = mock(MetaExpression.class);
		when(xmlFilenameVar.getStringValue()).thenReturn(".");

		File file = mock(File.class);
		RobotID robotID = mock(RobotID.class);
		when(robotID.getPath()).thenReturn(file);

		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		when(xsdService.xsdCheck(any(), any(), any())).thenReturn(true);

		// Run
		MetaExpression result = XsdCheckConstruct.process(context, xsdFilenameVar, xmlFilenameVar, xsdService, logger);

		// Verify
		verify(xsdService).xsdCheck(any(), any(), any());

		// Assert
		assertTrue(result.getBooleanValue());
	}
}