package nl.xillio.xill.plugins.document.util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.xillio.udm.builders.DecoratorBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;

/**
 * Test utils for testing the document plugin.
 *
 * @author Geert Konijnendijk
 *
 */
public class DocumentTestUtil {

	/**
	 * Mock a {@link DecoratorBuilder} supporting the {@link DecoratorBuilder#field(String, Object)} method.
	 *
	 * @return A mocked {@link DecoratorBuilder}
	 */
	public static DecoratorBuilder mockDecoratorBuilder() {
		DecoratorBuilder builder = mock(DecoratorBuilder.class);
		when(builder.field(any(), any())).thenReturn(builder);
		return builder;
	}

	/**
	 * Mock a {@link DocumentRevisionBuilder} supporting the {@link DocumentRevisionBuilder#decorator(String)} and {@link DocumentRevisionBuilder#decorators()} methods.
	 *
	 * @param object
	 *        Map backing the mocked {@link DocumentRevisionBuilder}
	 * @return A mocked {@link DocumentRevisionBuilder}
	 */
	public static DocumentRevisionBuilder mockReadableDocumentRevisionBuilder(final Map<String, Map<String, Object>> object) {
		DocumentRevisionBuilder builder = mock(DocumentRevisionBuilder.class);
		when(builder.decorators()).thenReturn(new ArrayList<>(object.keySet()));
		when(builder.decorator(anyString())).thenAnswer(i -> mockReadableDecoratorBuilder(object.get(i.getArgumentAt(0, String.class))));
		return builder;
	}

	/**
	 * Mock a {@link DecoratorBuilder} supporting the {@link DecoratorBuilder#field(String)} and {@link DecoratorBuilder#fields()} methods.
	 *
	 * @param decorator
	 *        Map backing the mocked {@link DecoratorBuilder}
	 * @return A mocked {@link DecoratorBuilder}
	 */
	public static DecoratorBuilder mockReadableDecoratorBuilder(final Map<String, Object> decorator) {
		DecoratorBuilder builder = mockDecoratorBuilder();
		when(builder.fields()).thenReturn(new ArrayList<>(decorator.keySet()));
		when(builder.field(anyString())).thenAnswer(i -> decorator.get(i.getArgumentAt(0, String.class)));
		return builder;
	}

	/**
	 * Creates a map representing a collection of decorators
	 *
	 * @return A collection of decorators represented as a map
	 */
	public static Map<String, Map<String, Object>> createDecoratorMap() {
		Map<String, Map<String, Object>> object = new HashMap<>();
		Map<String, Object> decorator1 = new HashMap<>();
		Map<String, Object> decorator2 = new HashMap<>();
		decorator1.put("test1", 1);
		decorator2.put("test2", 2);
		decorator2.put("test3", 3);
		object.put("d1", decorator1);
		object.put("d2", decorator2);
		return object;
	}

}
