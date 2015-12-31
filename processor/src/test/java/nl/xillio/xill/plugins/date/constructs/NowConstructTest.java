package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.plugins.date.services.DateService;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;

/**
 * Tests the {@link NowConstruct}
 *
 * @author Geert Konijnendijk
 */
public class NowConstructTest {

    /**
     * Test the process method under the default circumstances
     */
    @Test
    public void testProcess() {
        // Mock

        // ZonedDateTime is final, don't mock
        Date mockDate = mock(Date.class);
        DateService dateService = mock(DateService.class);
        when(dateService.now()).thenReturn(mockDate);

        // Run
        MetaExpression date = NowConstruct.process(dateService);

        // Verify
        verify(dateService).now();

        // Assert
        assertSame(date.getMeta(Date.class), mockDate);
    }
}
