package nl.xillio.xill.plugins.document.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.util.HashMap;

import nl.xillio.xill.TestUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;

/**
 * Test the methods in {@link GetConstruct}
 *
 * @author Geert Konijnendijk
 */
public class GetConstructTest extends TestUtils {

	/**
	 * Test {@link GetConstruct#process(MetaExpression, MetaExpression, MetaExpression, XillUDMService)} under normal circumstances
	 */
	@Test
	public void testProcessNormal() {
		// Mock
		XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression docId = mockExpression(ATOMIC, false, 0, "docId");
		MetaExpression verId = mockExpression(ATOMIC, false, 0, "verId");
		MetaExpression sec = mockExpression(ATOMIC, false, 0, "source");

		when(udmService.get("docId", "verId", XillUDMService.Section.SOURCE)).thenReturn(new HashMap<>());

		// Run
		// This is a real MetaExpression, parseObject can not be mocked
		MetaExpression result = GetConstruct.process(docId, verId, sec, udmService);

		// Verify
		verify(udmService).get("docId", "verId", XillUDMService.Section.SOURCE);

		// Assert
		assertSame(result.getType(), ExpressionDataType.OBJECT);
	}

	/**
	 * @return Exceptions that can be thrown by {@link XillUDMService#get(String, String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)}
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][] {{VersionNotFoundException.class}, {DocumentNotFoundException.class}, {IllegalArgumentException.class}};
	}

	/**
	 * Test that {@link GetConstruct#process(MetaExpression, MetaExpression, MetaExpression, XillUDMService)} converts exceptions into {@link RobotRuntimeException RobotRuntimeExceptions}.
	 *
	 * @param exceptionClass
	 *        Class of exception that can be thrown by {@link XillUDMService#get(String, String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)}
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(final Class<Exception> exceptionClass) {
		// Mock
		XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression docId = mockExpression(ATOMIC, false, 0, "docId");
		MetaExpression verId = mockExpression(ATOMIC, false, 0, "verId");
		MetaExpression sec = mockExpression(ATOMIC, false, 0, "source");

		when(udmService.get(anyString(), anyString(), any(XillUDMService.Section.class))).thenThrow(exceptionClass);

		// Run
		GetConstruct.process(docId, verId, sec, udmService);
	}

}
