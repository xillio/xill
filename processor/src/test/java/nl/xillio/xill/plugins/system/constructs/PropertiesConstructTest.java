package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.services.properties.SystemPropertiesService;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test the {@link PropertiesConstruct}
 */
public class PropertiesConstructTest extends TestUtils {

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
