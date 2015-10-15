package nl.xillio.xill.plugins.document.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;

import org.bson.Document;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
/**
 * Test the functionality of the FindWhereConstruct
 */
public class FindWhereConstructTest extends TestUtils {

	/**
	 * Test the output for the process construct under normal circumstances.
	 */
	@Test
	public void testProcessNormal() throws PersistenceException {
		MetaExpression filter = fromValue(new LinkedHashMap<>());
		MetaExpression version = fromValue("current");
		MetaExpression section = fromValue("source");
		Iterable<Map<String, Map<String, Object>>> result = mock(Iterable.class);
		XillUDMService udmService = mock(XillUDMService.class);
		when(udmService.findWhere(eq(new Document()), eq("current"), eq(Section.SOURCE))).thenReturn(result);

		// Run the method
		MetaExpression processResult = FindWhereConstruct.process(filter, version, section, udmService);

		// Check for the existence of an iterator
		assertNotNull(processResult.getMeta(MetaExpressionIterator.class));
	}


	/**
	 * @return Exceptions that can be thrown by the construct.
	 */
	@DataProvider(name = "exceptions")
	private Object[][] expectedExceptions() {
		return new Object[][] { {VersionNotFoundException.class}, {DocumentNotFoundException.class}, {IllegalArgumentException.class}, {PersistenceException.class}};
	}


	/**
	 * Test if the construct catches al exceptions.
	 */
	@Test(dataProvider = "exceptions", expectedExceptions = RobotRuntimeException.class)
	public void testProcessError(Class<Exception> exceptionClass) throws PersistenceException {
		MetaExpression filter = fromValue(new LinkedHashMap<>());
		MetaExpression version = fromValue("current");
		MetaExpression section = fromValue("source");
		XillUDMService xillUDMService = mock(XillUDMService.class);
		when(xillUDMService.findWhere(any(), anyString(), any())).thenThrow(exceptionClass);

		// Run the method
		FindWhereConstruct.process(filter, version, section, xillUDMService);
	}
}