package nl.xillio.xill.plugins.document.constructs;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.util.LinkedHashMap;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.XillUDMService;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test the methods in {@link CreateConstruct}
 * 
 * @author Luca Scalzotto
 *
 */
public class CreateConstructTest extends ConstructTest {

	/**
	 * Create a random MetaExpression containing a body object used as an argument in the create construct.
	 */
	private MetaExpression createBodyObject() {
		LinkedHashMap<String, MetaExpression> bodyMap = new LinkedHashMap<>();
		LinkedHashMap<String, MetaExpression> decorator = new LinkedHashMap<>();
		decorator.put("name", fromValue("someName"));
		bodyMap.put("someDecorator", fromValue(decorator));
		return fromValue(bodyMap);
	}
	
	/**
	 * Test {@link CreateConstruct#process(MetaExpression, MetaExpression, XillUDMService)} under normal circumstances.
	 * @throws PersistenceException Should never throw this, but needs this declaration because {@link UDMService#create(String, Map)} throws this.
	 */
	@Test
	public void testProcessNormal() throws PersistenceException {
		// Mock
		XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression contentType = mockExpression(ATOMIC, false, 0, "file");
		MetaExpression body = createBodyObject();
		DocumentID returnId = mock(DocumentID.class);
		
		when(udmService.create(anyString(), anyObject())).thenReturn(returnId);

		// Run
		// This is a real MetaExpression, parseObject can not be mocked
		MetaExpression result = CreateConstruct.process(contentType, body, udmService);
		
		// Verify
		verify(udmService).create(anyString(), anyObject());

		// Assert
		assertSame(result.getType(), ExpressionDataType.ATOMIC);
	}
	
	/**
	 * @return Exceptions that can be thrown by {@link XillUDMService#create(String, Map)}
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][] { {PersistenceException.class}};
	}
	
	/**
	 * Test that {@link CreateConstruct#process(MetaExpression, MetaExpression, XillUDMService)} converts exceptions into {@link RobotRuntimeException RobotRuntimeExceptions}.
	 * 
	 * @param exceptionClass
	 *        Class of exception that can be thrown by {@link XillUDMService#create(String, Map)}
	 * @throws PersistenceException Should never throw this, but needs this declaration because {@link UDMService#create(String, Map)} throws this.
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(Class<Exception> exceptionClass) throws PersistenceException {
		// Mock
		XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression contentType = mockExpression(ATOMIC, false, 0, "file");
		MetaExpression body = createBodyObject();
		
		when(udmService.create(anyString(), anyObject())).thenThrow(exceptionClass);

		// Run
		CreateConstruct.process(contentType, body, udmService);
	}
}
