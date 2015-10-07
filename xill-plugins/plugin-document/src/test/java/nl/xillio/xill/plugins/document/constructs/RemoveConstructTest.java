package nl.xillio.xill.plugins.document.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertSame;

import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;

public class RemoveConstructTest extends ConstructTest{

	/**
	 * Test {@link RemoveConstruct#process(MetaExpression, MetaExpression, MetaExpression, XillUDMService)} under normal circumstances
	 * @throws PersistenceException 
	 */
	@Test
	public void testProcessNormal() throws PersistenceException {
		// Mock
	  XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression docId = mockExpression(ATOMIC, false, 0, "docId");
		MetaExpression verId = mockExpression(ATOMIC, false, 0, "verId");
		MetaExpression sec = mockExpression(ATOMIC, false, 0, "source");
		
	
		Mockito.doNothing().when(udmService).remove(anyString(), anyString(), any());
	
		
		// Run
		// This is a real MetaExpression, parseObject can not be mocked
		MetaExpression result = RemoveConstruct.process(docId, verId, sec, udmService);
		
		// Verify
		verify(udmService).remove("docId", "verId", Section.SOURCE);

		// Assert
		assertSame(result,NULL);
	}

	/**
	 * @return Exceptions that can be thrown by {@link XillUDMService#remove(String, String, String)}
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][] { {VersionNotFoundException.class}, {DocumentNotFoundException.class}, {IllegalArgumentException.class}};
	}

	/**
	 * Test that {@link RemoveConstruct#process(MetaExpression, MetaExpression, MetaExpression, XillUDMService)} converts exceptions into {@link RobotRuntimeException RobotRuntimeExceptions}.
	 * 
	 * @param exceptionClass
	 *        Class of exception that can be thrown by {@link XillUDMService#Remove(String, String, String)}
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(Class<Exception> exceptionClass){
		// Mock
		XillUDMService udmService = mock(XillUDMService.class);
		MetaExpression docId = mockExpression(ATOMIC, false, 0, "docId");
		MetaExpression verId = mockExpression(ATOMIC, false, 0, "verId");
		MetaExpression sec = mockExpression(ATOMIC, false, 0, "source");

		
		try {
			Mockito.doThrow(exceptionClass).when(udmService).remove(anyString(), anyString(), any());
		} catch (PersistenceException e) {
		}
		// Run
		RemoveConstruct.process(docId, verId, sec, udmService);
	}

}
