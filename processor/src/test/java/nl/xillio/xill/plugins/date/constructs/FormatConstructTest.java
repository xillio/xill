package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.date.services.DateService;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;

import static nl.xillio.xill.plugins.date.utils.MockUtils.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the {@link FormatConstruct}
 *
 * @author Geert Konijnendijk
 */
public class FormatConstructTest {

    @DataProvider(name = "format")
    private Object[][] formatProvider() {
        ZonedDateTime date = ZonedDateTime.now();
        //Date date = new nl.xillio.xill.plugins.date.data.Date(ZonedDateTime.now());
        MetaExpression dateExpression = mockDateExpression(date);
        return new Object[][]{{dateExpression, mockStringExpression("yyyy-MM-dd")}, {dateExpression, mockNullExpression()}};
    }

    /**
     * Test the process method with both null and non-null format variable
     */
    @Test(dataProvider = "format")
    public void testProcess(MetaExpression dateExpression, MetaExpression formatExpression) {
        // Mock
        DateService dateService = mock(DateService.class);
        String returnString = "2015-8-3";
        when(dateService.formatDate(any(), any())).thenReturn(returnString);

        // Run
        MetaExpression formatted = FormatConstruct.process(dateExpression, formatExpression, dateService);

        // Verify
        verify(dateService).formatDate(any(), any());

        // Assert
        assertEquals(formatted.getStringValue(), returnString);
    }
}
