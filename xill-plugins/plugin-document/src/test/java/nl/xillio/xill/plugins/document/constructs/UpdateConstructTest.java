package nl.xillio.xill.plugins.document.constructs;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;

/**
 * Test the methods in {@link UpdateConstruct}
 * 
 * @author Geert Konijnendijk
 *
 */
public class UpdateConstructTest extends ConstructTest {

	/**
	 * Test {@link UpdateConstruct#process(MetaExpression, MetaExpression, MetaExpression, MetaExpression, XillUDMService)} under normal circumstances.
	 * 
	 * @throws PersistenceException
	 */
	@Test
	public void testProcessNormal() throws PersistenceException {
		// Mock
	  XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression docId = mockExpression(ATOMIC, false, 0, "docId");
		Map<String, MetaExpression> bodyMap = new HashMap<>();
		MetaExpression body = mockExpression(OBJECT);
		when(body.getValue()).thenReturn(bodyMap);
		MetaExpression verId = mockExpression(ATOMIC, false, 0, "verId");
		MetaExpression sec = mockExpression(ATOMIC, false, 0, "source");

		Map<String, Map<String, Object>> emptyMap = new HashMap<>();

		// Run
		// This is a real MetaExpression, parseObject can not be mocked
		MetaExpression result = UpdateConstruct.process(docId, body, verId, sec, udmService);
		
		// Verify
		verify(udmService).update(eq("docId"), eq(emptyMap), eq("verId"), eq(Section.SOURCE));

		// Assert
		assertSame(result, NULL);
	}

	/**
	 * @return Exceptions that can be thrown by {@link XillUDMService#get(String, String, String)}
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][] { {ModelException.class}, {DocumentNotFoundException.class}, {PersistenceException.class}, {VersionNotFoundException.class}};
	}

	/**
	 * Test that {@link UpdateConstruct#process(MetaExpression, MetaExpression, MetaExpression, MetaExpression, XillUDMService)} converts exceptions into {@link RobotRuntimeException
	 * RobotRuntimeExceptions}.
	 * 
	 * @param exceptionClass
	 *        Class of exception that can be thrown by {@link XillUDMService#get(String, String, String)}
	 * @throws PersistenceException
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(Class<Exception> exceptionClass) throws PersistenceException {
		// Mock
		XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression docId = mockExpression(ATOMIC, false, 0, "docId");
		Map<String, MetaExpression> bodyMap = new HashMap<>();
		MetaExpression body = mockExpression(OBJECT);
		when(body.getValue()).thenReturn(bodyMap);
		MetaExpression verId = mockExpression(ATOMIC, false, 0, "verId");
		MetaExpression sec = mockExpression(ATOMIC, false, 0, "source");

		doThrow(exceptionClass).when(udmService).update(anyString(), anyMap(), anyString(), any());

		// Run
		UpdateConstruct.process(docId, body, verId, sec, udmService);
	}

}
