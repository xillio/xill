package nl.xillio.xill.docgen.impl;

import nl.xillio.xill.docgen.DocumentationEntity;
import org.testng.annotations.Test;

import javax.print.Doc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Test the {@link InMemoryDocumentationSearcher}
 */
public class InMemoryDocumentationSearcherTest {
	@Test
	public void testSearch() throws Exception {
		InMemoryDocumentationSearcher searcher = spySearcher();

		// searchByName
		doReturn(new String[] {"Result A"}).when(searcher).searchByName(anyString());
		// searchByTag
		doReturn(new String[] {"Result B", "Result C"}).when(searcher).searchByTags(anyString());

		// Call the method
		String[] result = searcher.search("This is my query");

		// Verify calls
		verify(searcher).searchByName(anyString());
		verify(searcher).searchByTags(anyString());

		// Assertions
		assertEquals(result, new String[]{"Result A", "Result B", "Result C"});
	}

	@Test
	public void testSearchByTags() throws Exception {
		InMemoryDocumentationSearcher searcher = spySearcher();

		// Insert document
		String packet = "UnitTest";
		DocumentationEntity entity = mock(DocumentationEntity.class);
		when(entity.getTags()).thenReturn(Collections.singletonList("myTag"));
		when(entity.getIdentity()).thenReturn("construct");
		searcher.index(packet, entity);

		// Run the method
		String[] result = searcher.searchByTags("myTag");

		assertEquals(result, new String[] { "UnitTest.construct"});

	}

	@Test
	public void testSearchByName() throws Exception {
		InMemoryDocumentationSearcher searcher = spySearcher();

		// Insert document
		String packet = "UnitTest";
		DocumentationEntity entity = mock(DocumentationEntity.class);
		when(entity.getTags()).thenReturn(Collections.singletonList("myTag"));
		when(entity.getIdentity()).thenReturn("construct");
		searcher.index(packet, entity);

		// Run the method
		String[] result = searcher.searchByName("c");

		// Check result
		assertEquals(result, new String[]{ "UnitTest.construct"});

		// Now run again without results
		// Run the method
		String[] result2 = searcher.searchByName("crt");

		// Check result
		assertEquals(result2, new String[]{});


	}

	private InMemoryDocumentationSearcher spySearcher() {
		return spy(new InMemoryDocumentationSearcher());
	}
}