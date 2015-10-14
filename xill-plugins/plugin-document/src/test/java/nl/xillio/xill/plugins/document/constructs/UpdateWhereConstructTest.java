package nl.xillio.xill.plugins.document.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.LinkedHashMap;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;

import org.bson.Document;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test the methods in the {@link UpdateWhereConstruct}.
 * 
 * @author Geert Konijnendijk
 *
 */
public class UpdateWhereConstructTest extends ConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void testProcessNormal() throws PersistenceException {
		// Mock
		LinkedHashMap<String, MetaExpression> filter = new LinkedHashMap<>();
		LinkedHashMap<String, MetaExpression> body = new LinkedHashMap<>();
		String section = "target";
		String versionId = "2.4";

		XillUDMService service = mock(XillUDMService.class);
		when(service.updateWhere(any(Document.class), anyMap())).thenReturn(10L);
		when(service.updateWhere(any(Document.class), anyMap(), eq(versionId), eq(XillUDMService.Section.TARGET))).thenReturn(20L);

		// Run delete whole project
		MetaExpression result = UpdateWhereConstruct.process(fromValue(filter), fromValue(body), NULL, fromValue(section), service);

		// Verify
		verify(service).updateWhere(any(Document.class), anyMap());

		// Assert
		assertEquals(result.getNumberValue().longValue(), 10L);

		// Run delete version
		MetaExpression resultVersion = UpdateWhereConstruct.process(fromValue(filter), fromValue(body), fromValue(versionId), fromValue(section), service);

		// Verify
		verify(service).updateWhere(any(Document.class), anyMap(), eq(versionId), eq(Section.TARGET));

		// Assert
		assertEquals(resultVersion.getNumberValue().longValue(), 20L);
	}

	/**
	 * @return Exceptions that can be thrown by {@link XillUDMService#get(String, String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)}
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][] { {VersionNotFoundException.class}, {DocumentNotFoundException.class}, {IllegalArgumentException.class}, {PersistenceException.class}};
	}

	/**
	 * Test that {@link UpdateWhereConstruct#process(MetaExpression, MetaExpression, MetaExpression, MetaExpression, XillUDMService)} wraps exceptions correctly
	 * 
	 * @param exceptionClass
	 * @throws PersistenceException
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(final Class<Exception> exceptionClass) throws PersistenceException {
		LinkedHashMap<String, MetaExpression> filter = new LinkedHashMap<>();
		LinkedHashMap<String, MetaExpression> body = new LinkedHashMap<>();
		String section = "target";
		XillUDMService udmService = mock(XillUDMService.class);
		when(udmService.updateWhere(any(Document.class), anyMap())).thenThrow(exceptionClass);

		// Run
		UpdateWhereConstruct.process(fromValue(filter), fromValue(body), NULL, fromValue(section), udmService);
	}
}
