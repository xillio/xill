package nl.xillio.xill.plugins.document.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import nl.xillio.udm.builders.DecoratorBuilder;

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

}
