package nl.xillio.xill.plugins.rest.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.rest.data.MultipartBody;
import nl.xillio.xill.plugins.rest.services.RestService;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;

/**
 * Tests for the {@link BodyCreateConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class BodyCreateConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
        MultipartBody body = mock(MultipartBody.class);
        when(body.toString()).thenReturn("REST multipart body");

        RestService restService = mock(RestService.class);
        when(restService.bodyCreate()).thenReturn(body);

		// Run
		MetaExpression result = BodyCreateConstruct.process(restService);

		// Verify
		verify(restService).bodyCreate();

		// Assert
		assertSame(result.getMeta(MultipartBody.class), body);
	}
}