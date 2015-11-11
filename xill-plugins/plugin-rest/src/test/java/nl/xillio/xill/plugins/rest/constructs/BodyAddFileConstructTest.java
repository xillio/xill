package nl.xillio.xill.plugins.rest.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.rest.data.MultipartBody;
import nl.xillio.xill.plugins.rest.services.RestService;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * Tests for the {@link BodyAddFileConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class BodyAddFileConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
        MultipartBody body = mock(MultipartBody.class);
        when(body.toString()).thenReturn("REST multipart body");

		MetaExpression bodyVar = mock(MetaExpression.class);
        when(bodyVar.getMeta(MultipartBody.class)).thenReturn(body);

        MetaExpression nameVar = mock(MetaExpression.class);
        when(nameVar.getStringValue()).thenReturn("data");
        MetaExpression fileNameVar = mock(MetaExpression.class);
        when(fileNameVar.getStringValue()).thenReturn("c:/tmp/something.pdf");

        RestService restService = mock(RestService.class);

		// Run
		MetaExpression result = BodyAddFileConstruct.process(bodyVar, nameVar, fileNameVar, restService);

		// Verify
		verify(restService).bodyAddFile(any(), anyString(), anyString());

		// Assert
        assertTrue(result.isNull());
	}
}