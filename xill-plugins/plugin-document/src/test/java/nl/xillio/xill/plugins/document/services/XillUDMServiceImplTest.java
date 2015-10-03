package nl.xillio.xill.plugins.document.services;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

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
	 * Generate combinations of versionIds and sections for use in {@link XillUDMServiceImplTest#testGetNormal(String, String)}.
	 *
	 * @return Combinations of versionId and sections test data
	 */
	@DataProvider(name = "versionIdSection")
	public Object[][] generateVersionIdSection() {
		return new Object[][]{
			{"current", "source"},
			{"current", "target"},
			{"v1", "source"},
			{"v1", "target"}};
	}

	/**
	 * Test {@link XillUDMServiceImpl#get(String, String, String)} under normal usage
	 *
	 * @param versionId version ID to test for
	 * @param section   "source" or "target"
	 */
	@Test(dataProvider = "versionIdSection")
	public void testGetNormal(String versionId, String section) {
		// Mock
		String documentId = "docid";
		DocumentID docId = mock(DocumentID.class);

		DocumentRevisionBuilder documentRevisionBuilder = mock(DocumentRevisionBuilder.class);
		DocumentHistoryBuilder documentHistoryBuilder = mockDocumentHistoryBuilder(versionId, documentRevisionBuilder);
		DocumentBuilder documentBuilder = mockDocumentBuilder(documentHistoryBuilder);

		when(udmService.get(documentId)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);

		Map<String, Map<String, Object>> result = new HashMap<>();

		when(conversionService.udmToMap(documentRevisionBuilder)).thenReturn(result);

		// Run
		Map<String, Map<String, Object>> actual = xillUdmService.get(documentId, versionId, section);

		// Verify
		verify(udmService).get(documentId);
		if ("current".equals(versionId)) {
			verify(documentHistoryBuilder).current();
		} else {
			verify(documentHistoryBuilder).versions();
			verify(documentHistoryBuilder).revision(versionId);
		}
		if ("source".equals(section)) {
			verify(documentBuilder).source();
		} else {
			verify(documentBuilder).target();
		}
		verify(conversionService).udmToMap(documentRevisionBuilder);
		verify(udmService).release(docId);

		// Assert
		assertSame(actual, result);
	}

	/**
	 * Test that {@link XillUDMServiceImpl#get(String, String, String)} does not eat exceptions of type {@link DocumentNotFoundException}.
	 */
	@Test(expectedExceptions = DocumentNotFoundException.class)
	public void testGetNonExistentDocument() {
		// Mock
		when(udmService.get(anyString())).thenThrow(DocumentNotFoundException.class);

		// Run
		xillUdmService.get("docId", "versionId", "section");
	}

	/**
	 * Test that {@link XillUDMServiceImpl#get(String, String, String)} throws a {@link IllegalArgumentException} when section is something else than "source" or "target".
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetNonExistentSection() {
		// Mock
		String documentId = "docId";
		String versionId = "current";
		String section = "nonExistent";
		DocumentID docId = mock(DocumentID.class);

		DocumentBuilder documentBuilder = mock(DocumentBuilder.class);

		when(udmService.get(documentId)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);

		// Run
		xillUdmService.get(documentId, versionId, section);
	}

	/**
	 * Test that {@link XillUDMServiceImpl#get(String, String, String)} throws a {@link VersionNotFoundException} when a non-existent version is requested.
	 */
	@Test(expectedExceptions = VersionNotFoundException.class)
	public void testGetNonExistentRevision() {
		// Mock
		String documentId = "docId";
		String versionId = "nonExistent";
		String section = "source";
		DocumentID docId = mock(DocumentID.class);

		DocumentHistoryBuilder documentHistoryBuilder = mock(DocumentHistoryBuilder.class);
		when(documentHistoryBuilder.versions()).thenReturn(new ArrayList<>());

		DocumentBuilder documentBuilder = mockDocumentBuilder(documentHistoryBuilder);

		when(udmService.get(documentId)).thenReturn(docId);
		when(udmService.document(docId)).thenReturn(documentBuilder);

		// Run
		xillUdmService.get(documentId, versionId, section);

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
			ArrayList<String> versions = new ArrayList<>();
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

}
