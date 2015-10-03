package nl.xillio.xill.plugins.document.constructs;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import org.bson.Document;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the functionality of the RemoveWhereConstruct
 */
public class RemoveWhereConstructTest extends TestUtils {

	/**
	 * Test the process method's output under normal circumstances.
	 */
	@Test
	public void testProcessNormal() throws PersistenceException {
		String documentId = "DocumentID";
		LinkedHashMap<String, MetaExpression> filter = new LinkedHashMap<>();
		String section = "target";
		String versionId = "2.4";

		XillUDMService service = mock(XillUDMService.class);
		when(service.removeWhere(any(Document.class))).thenReturn(10L);
		when(service.removeWhere(any(), eq(versionId), eq(XillUDMService.Section.TARGET))).thenReturn(20L);

		// Run delete whole project
		MetaExpression result = RemoveWhereConstruct.process(fromValue(filter), NULL, fromValue(section), service);
		assertEquals(result.getNumberValue().longValue(), 10L);

		// Run delete version
		MetaExpression resultVersion = RemoveWhereConstruct.process(fromValue(filter), fromValue(versionId), fromValue(section), service);

		assertEquals(resultVersion.getNumberValue().longValue(), 20L);
	}

	/**
	 * @return Exceptions that can be thrown by {@link XillUDMService#get(String, String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)}
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][]{{VersionNotFoundException.class}, {DocumentNotFoundException.class}, {IllegalArgumentException.class}, {PersistenceException.class}};
	}

	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(Class<Exception> exceptionClass) throws PersistenceException {
		// Mock
		String documentId = "DocumentID";
		LinkedHashMap<String, MetaExpression> filter = new LinkedHashMap<>();
		String section = "target";
		String versionId = "2.4";

		XillUDMService udmService = mock(XillUDMService.class);
		when(udmService.removeWhere(any())).thenThrow(exceptionClass);

		// Run
		RemoveWhereConstruct.process(fromValue(filter), NULL, fromValue(section), udmService);
	}
}