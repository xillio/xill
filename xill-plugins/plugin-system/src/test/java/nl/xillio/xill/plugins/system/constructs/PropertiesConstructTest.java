package nl.xillio.xill.plugins.system.constructs;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.services.properties.SystemPropertiesService;
import nl.xillio.xill.testutils.ConstructTest;

import org.testng.annotations.Test;

/**
 * Test the {@link PropertiesConstruct}
 */
public class PropertiesConstructTest extends ConstructTest {

	/**
	 * Test getting all properties under normal circumstances
	 */
	@Test
	public void testProcessAll() {
		// Mock context
		SystemPropertiesService properties = mock(SystemPropertiesService.class);

		// Run construct
		MetaExpression expression = PropertiesConstruct.process(NULL, properties);

		// Verify
		verify(properties).getProperties();

		// Assert
		assertNotNull(expression);
		assertEquals(expression.getType(), OBJECT);
	}

	/**
	 * Test getting a property under normal circumstances
	 */
	@Test
	public void testProcessOne() {
		// Mock context
		String key = "I am a key";
		MetaExpression property = mockExpression(ATOMIC);
		when(property.getStringValue()).thenReturn(key);
		SystemPropertiesService properties = mock(SystemPropertiesService.class);

		// Run construct
		MetaExpression expression = PropertiesConstruct.process(property, properties);

		// Verify
		verify(properties).getProperty(eq(key));

		// Assert
		assertNotNull(expression);
		assertEquals(expression.getType(), ATOMIC);
	}
}
