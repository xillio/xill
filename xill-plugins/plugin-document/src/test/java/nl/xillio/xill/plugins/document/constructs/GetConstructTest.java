package nl.xillio.xill.plugins.document.constructs;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.util.HashMap;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test the methods in {@link GetConstruct}
 * 
 * @author Geert Konijnendijk
 *
 */
public class GetConstructTest extends ConstructTest {

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
		
		when(udmService.get("docId", "verId", "source")).thenReturn(new HashMap<>());

		// Run
		// This is a real MetaExpression, parseObject can not be mocked
		MetaExpression result = GetConstruct.process(docId, verId, sec, udmService);
		
		// Verify
		verify(udmService).get("docId", "verId", "source");

		// Assert
		assertSame(result.getType(), ExpressionDataType.OBJECT);
	}

	/**
	 * @return Exceptions that can be thrown by {@link XillUDMService#get(String, String, String)}
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][] { {VersionNotFoundException.class}, {DocumentNotFoundException.class}, {IllegalArgumentException.class}};
	}

	/**
	 * Test that {@link GetConstruct#process(MetaExpression, MetaExpression, MetaExpression, XillUDMService)} converts exceptions into {@link RobotRuntimeException RobotRuntimeExceptions}.
	 * 
	 * @param exceptionClass
	 *        Class of exception that can be thrown by {@link XillUDMService#get(String, String, String)}
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(Class<Exception> exceptionClass) {
		// Mock
		XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression docId = mockExpression(ATOMIC, false, 0, "docId");
		MetaExpression verId = mockExpression(ATOMIC, false, 0, "verId");
		MetaExpression sec = mockExpression(ATOMIC, false, 0, "source");

		when(udmService.get(anyString(), anyString(), anyString())).thenThrow(exceptionClass);

		// Run
		GetConstruct.process(docId, verId, sec, udmService);
	}

}
