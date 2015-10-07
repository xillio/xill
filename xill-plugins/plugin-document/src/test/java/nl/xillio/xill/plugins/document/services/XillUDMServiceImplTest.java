package nl.xillio.xill.plugins.document.services;

import static nl.xillio.xill.plugins.document.util.DocumentTestUtil.createDecoratorMap;
import static nl.xillio.xill.plugins.document.util.DocumentTestUtil.mockReadableDocumentRevisionBuilder;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Test the methods in the {@link XillUDMServiceImpl}
 *
 * @author Geert Konijnendijk
 */
public class XillUDMServiceImplTest {

	// Default document ID, version ID and section name
	private static final String DOCUMENT_ID = "docid", VERSION_ID = "current", SECTION = "target";

	XillUDMServiceImpl xillUdmService;
	ConversionService conversionService;
	UDMService udmService;

	/**
	 * Create the services anew before each test
	 */
	@BeforeMethod
	public void initialize() {
		conversionService = mock(ConversionService.class);
		udmService = mock(UDMService.class);
		xillUdmService = spy(new XillUDMServiceImpl(conversionService));
		doReturn(udmService).when(xillUdmService).connect();
	}

	/**
	 * Generate combinations of versionIds and sections for use in {@link XillUDMServiceImplTest#testGetNormal(String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)}.
	 *
	 * @return Combinations of versionId and sections test data
	 */
	@DataProvider(name = "versionIdSection")
	public Object[][] generateVersionIdSection() {
		return new Object[][]{
			{"current", XillUDMService.Section.SOURCE},
			{"current", XillUDMService.Section.TARGET},
			{"v1", XillUDMService.Section.SOURCE},
			{"v1", XillUDMService.Section.TARGET}};
	}

	/**
	 * Test {@link XillUDMServiceImpl#get(String, String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)} under normal usage
	 *
	 * @param versionId version ID to test for
	 * @param section   "source" or "target"
	 */
	@Test(dataProvider = "versionIdSection")
	public void testGetNormal(String versionId, XillUDMService.Section section) {
		// Mock
		DocumentID docId = mock(DocumentID.class);

		DocumentRevisionBuilder documentRevisionBuilder = mock(DocumentRevisionBuilder.class);
		DocumentHistoryBuilder documentHistoryBuilder = mockDocumentHistoryBuilder(versionId, documentRevisionBuilder);
		DocumentBuilder documentBuilder = mockDocumentBuilder(documentHistoryBuilder);

		when(udmService.get(DOCUMENT_ID)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);

		Map<String, Map<String, Object>> result = new HashMap<>();

		when(conversionService.udmToMap(documentRevisionBuilder)).thenReturn(result);

		// Run
		Map<String, Map<String, Object>> actual = xillUdmService.get(DOCUMENT_ID, versionId, section);

		// Verify
		verifyRetrieval(versionId, section, documentHistoryBuilder, documentBuilder);
		verify(conversionService).udmToMap(documentRevisionBuilder);
		verify(udmService).release(docId);

		// Assert
		assertSame(actual, result);
	}

	/**
	 * Test {@link XillUDMServiceImpl#update(String, Map, String, String)} under normal usage
	 *
	 * @param versionId
	 *        version ID to test for
	 * @param section
	 *        "source" or "target"
	 */
	@Test(dataProvider = "versionIdSection")
	public void testUpdateNormal(String versionId, String section) throws PersistenceException {
		// Mock
		DocumentID docId = mock(DocumentID.class);

		

		Map<String, Map<String, Object>> body = createDecoratorMap();
		DocumentRevisionBuilder documentRevisionBuilder = mockReadableDocumentRevisionBuilder(body);
		
		DocumentHistoryBuilder documentHistoryBuilder = mockDocumentHistoryBuilder(versionId, documentRevisionBuilder);
		DocumentBuilder documentBuilder = mockDocumentBuilder(documentHistoryBuilder);

		when(udmService.get(DOCUMENT_ID)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);

		// Run
		xillUdmService.update(DOCUMENT_ID, body, versionId, section);

		// Verify
		verifyRetrieval(versionId, section, documentHistoryBuilder, documentBuilder);
		verify(conversionService).mapToUdm(body, documentRevisionBuilder);
	}

	/**
	 * Verify that the correct methods are called to retrieve the correct document version and section.
	 * 
	 * @param versionId
	 *        Version ID that should have been retrieved
	 * @param section
	 *        Section name that should have been retrieved
	 * @param documentHistoryBuilder
	 *        {@link DocumentHistoryBuilder} mock to verify on
	 * @param documentBuilder
	 *        {@link DocumentBuilder} mock to verify on
	 */
	private void verifyRetrieval(String versionId, String section, DocumentHistoryBuilder documentHistoryBuilder, DocumentBuilder documentBuilder) {
		verify(udmService).get(DOCUMENT_ID);
		if ("current".equals(versionId)) {
			verify(documentHistoryBuilder).current();
		} else {
			verify(documentHistoryBuilder).versions();
			verify(documentHistoryBuilder).revision(versionId);
		}
		if (XillUDMService.Section.SOURCE.equals(section)) {
			verify(documentBuilder).source();
		} else {
			verify(documentBuilder).target();
		}
	}

	/**
	 * Test that {@link XillUDMServiceImpl#get(String, String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)} does not eat exceptions of type {@link DocumentNotFoundException}.
	 */
	@Test(expectedExceptions = DocumentNotFoundException.class)
	public void testGetNonExistentDocument() {
		// Mock
		prepareNonExistentDocument();

		// Run
		xillUdmService.get("docId", "versionId", XillUDMService.Section.SOURCE);
	}

	/**
	 * Test that {@link XillUDMServiceImpl#update(String, Map, String, String)} does not eat exceptions of type {@link DocumentNotFoundException}.
	 * 
	 * @throws PersistenceException
	 */
	@Test(expectedExceptions = DocumentNotFoundException.class)
	public void testUpdateNonExistentDocument() throws PersistenceException {
		// Mock
		prepareNonExistentDocument();
		Map<String, Map<String, Object>> body = new HashMap<>();

		// Run
		xillUdmService.update("docId", body, "versionId", "section");
	}

	/**
	 * Mock all necessary objects to test for a non-existent document
	 */
	private void prepareNonExistentDocument() {
		when(udmService.get(anyString())).thenThrow(DocumentNotFoundException.class);
	}

	/**
	 * Test that {@link XillUDMServiceImpl#get(String, String, String)} throws a {@link IllegalArgumentException} when section is something else than "source" or "target".
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetNonExistentSection() {
		// Mock
		String documentId = "docId";
		String versionId = "current";
		XillUDMService.Section section = null;
		String section = mockNonExistentSection();

		// Run
		xillUdmService.get(DOCUMENT_ID, VERSION_ID, section);
	}

	/**
	 * Test that {@link XillUDMServiceImpl#update(String, Map, String, String)} throws a {@link IllegalArgumentException} when section is something else than "source" or "target".
	 * 
	 * @throws PersistenceException
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testUpdateNonExistentSection() throws PersistenceException {
		// Mock
		String section = mockNonExistentSection();
		Map<String, Map<String, Object>> body = new HashMap<>();

		// Run
		xillUdmService.update(DOCUMENT_ID, body, VERSION_ID, section);
	}

	/**
	 * Mock all necessary objects to test for a non-existent section
	 * 
	 * @return The non-existent section name
	 */
	private String mockNonExistentSection() {
		String section = "nonExistent";
>>>>>>> develop
		DocumentID docId = mock(DocumentID.class);

		DocumentBuilder documentBuilder = mock(DocumentBuilder.class);

		when(udmService.get(DOCUMENT_ID)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);
		return section;
	}

	/**
	 * Test that {@link XillUDMServiceImpl#get(String, String, nl.xillio.xill.plugins.document.services.XillUDMService.Section)} throws a {@link VersionNotFoundException} when a non-existent version is requested.
	 */
	@Test(expectedExceptions = VersionNotFoundException.class)
	public void testGetNonExistentRevision() {
		// Mock
		String versionId = mockNonExistentRevision();

		// Run
		xillUdmService.get(DOCUMENT_ID, versionId, SECTION);
	}

	/**
	 * Test that {@link XillUDMServiceImpl#update(String, Map, String, String)} throws a {@link VersionNotFoundException} when a non-existent version is requested.
	 * 
	 * @throws PersistenceException
	 */
	@Test(expectedExceptions = VersionNotFoundException.class)
	public void testUpdateNonExistentRevision() throws PersistenceException {
		// Mock
		String versionId = mockNonExistentRevision();
		Map<String, Map<String, Object>> body = new HashMap<>();

		// Run
		xillUdmService.update(DOCUMENT_ID, body, versionId, SECTION);
	}

	/**
	 * Mock all necessary objects to test for a non-existent version
	 * 
	 * @return The non-existent version ID
	 */
	private String mockNonExistentRevision() {
		String versionId = "nonExistent";
		XillUDMService.Section section = XillUDMService.Section.SOURCE;
		DocumentID docId = mock(DocumentID.class);

		DocumentHistoryBuilder documentHistoryBuilder = mock(DocumentHistoryBuilder.class);
		when(documentHistoryBuilder.versions()).thenReturn(new ArrayList<>());

		DocumentBuilder documentBuilder = mockDocumentBuilder(documentHistoryBuilder);

		when(udmService.get(DOCUMENT_ID)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);
		return versionId;
	}

	/**
	 * Generates non-matching bodies and builders represented as a map
	 * 
	 * @return An array of objects to be passed as parameters
	 */
	@DataProvider(name = "mismatchedBodyBuilder")
	private Object[][] mismatchedBodyBuilder() {
		// Create maps
		Map<String, Map<String, Object>> empty = new HashMap<>();
		Map<String, Map<String, Object>> withDecorator1 = new HashMap<>();
		withDecorator1.put("d1", new HashMap<>());
		Map<String, Map<String, Object>> withDecorator2 = new HashMap<>();
		withDecorator1.put("d2", new HashMap<>());
		Map<String, Map<String, Object>> withField1 = new HashMap<>();
		withField1.put("d1", new HashMap<>());
		withField1.get("d1").put("f1", 1);
		Map<String, Map<String, Object>> withField2 = new HashMap<>();
		withField2.put("d1", new HashMap<>());
		withField2.get("d1").put("f2", 1);
		
		// Array of non-matching maps
		return new Object[][] {
				// number of decorators
				{empty, withDecorator1},
				// name of decorators
				{withDecorator1, withDecorator2},
				// number of fields
				{withDecorator1, withField1},
				// name of fields
				{withField1, withField2}
		};
	}

	/**
	 * Test that {@link XillUDMService#update(String, Map, String, String)} throws a {@link ModelException} when the decorators and fields in the body and the {@link DocumentRevisionBuilder} do not
	 * match.
	 * 
	 * @param body
	 *        The revision body
	 * @param builder
	 *        A map representing the builder
	 * @throws PersistenceException
	 */
	@Test(dataProvider = "mismatchedBodyBuilder", expectedExceptions = ModelException.class)
	public void testUpdateWrongBody(Map<String, Map<String, Object>> body, Map<String, Map<String, Object>> builder) throws PersistenceException {
		// Mock
		DocumentID docId = mock(DocumentID.class);

		DocumentRevisionBuilder documentRevisionBuilder = mockReadableDocumentRevisionBuilder(builder);
		DocumentHistoryBuilder documentHistoryBuilder = mockDocumentHistoryBuilder(VERSION_ID, documentRevisionBuilder);
		DocumentBuilder documentBuilder = mockDocumentBuilder(documentHistoryBuilder);

		when(udmService.get(DOCUMENT_ID)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);

		// Run
		xillUdmService.update(DOCUMENT_ID, body, VERSION_ID, SECTION);
	}

	/**
	 * Mock a {@link DocumentHistoryBuilder} with {@link DocumentHistoryBuilder#current()}, {@link DocumentHistoryBuilder#versions()} and {@link DocumentHistoryBuilder#revision(String)}.
	 *
	 * @param versionId
	 * @param documentRevisionBuilder
	 * @return
	 */
	private DocumentHistoryBuilder mockDocumentHistoryBuilder(String versionId, DocumentRevisionBuilder documentRevisionBuilder) {
		DocumentHistoryBuilder documentHistoryBuilder = mock(DocumentHistoryBuilder.class);
		when(documentHistoryBuilder.current()).thenReturn(documentRevisionBuilder);
		if (!"current".equals(versionId)) {
			ArrayList<String> versions = new ArrayList<String>();
			versions.add(versionId);
			when(documentHistoryBuilder.versions()).thenReturn(versions);
		} else {
			when(documentHistoryBuilder.versions()).thenReturn(new ArrayList<>());
		}
		when(documentHistoryBuilder.revision(anyString())).thenReturn(documentRevisionBuilder);
		return documentHistoryBuilder;
	}

	/**
	 * Mock a {@link DocumentBuilder} with {@link DocumentBuilder#source()} and {@link DocumentBuilder#target()}
	 *
	 * @param documentHistoryBuilder {@link DocumentHistoryBuilder} returned as source and target
	 * @return A mocked {@link DocumentBuilder}
	 */
	private DocumentBuilder mockDocumentBuilder(DocumentHistoryBuilder documentHistoryBuilder) {
		DocumentBuilder documentBuilder = mock(DocumentBuilder.class);
		when(documentBuilder.source()).thenReturn(documentHistoryBuilder);
		when(documentBuilder.target()).thenReturn(documentHistoryBuilder);
		return documentBuilder;
	}

	/**
	 * Test removing entire entries from the database.
	 *
	 * @throws PersistenceException
	 */
	@Test
	public void testRemoveWhereNormalCircumstances() throws PersistenceException {
		Document filter = mock(Document.class);
		UDMService udmService = mock(UDMService.class);
		XillUDMServiceImpl service = spy(new XillUDMServiceImpl(null));
		doReturn(udmService).when(service).connect();
		when(udmService.delete(filter)).thenReturn(112L);

		long result = service.removeWhere(filter);
		assertEquals(result, 112L);
	}

	/**
	 * Test removing single versions from the database.
	 *
	 * @throws PersistenceException
	 */
	@Test
	public void testRemoveWhereNormalVersionCircumstances() throws PersistenceException {
		Document filter = mock(Document.class);
		UDMService udmService = mock(UDMService.class);
		XillUDMServiceImpl service = spy(new XillUDMServiceImpl(null));
		doReturn(udmService).when(service).connect();

		Document expectedQuery = new Document("$pull",
			new Document("source.versions",
				new Document("version", "1.2")
			)
		);

		when(udmService.update(same(filter), eq(expectedQuery))).thenReturn(1337L);

		long result = service.removeWhere(filter, "1.2", XillUDMService.Section.SOURCE);

		assertEquals(result, 1337L);
	}

}
