package nl.xillio.xill.plugins.document.constructs;

import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link PersistContentTypeConstruct}
 */
public class PersistContentTypeConstructTest extends TestUtils {

    DocumentDefinitionService definitionService = mock(DocumentDefinitionService.class);
    MetaExpression typeName = mockExpression(ATOMIC, false, 0, "myContentType");

    @BeforeMethod
    public void initialize() {
        definitionService = mock(DocumentDefinitionService.class);
        typeName = mockExpression(ATOMIC, false, 0, "myContentType");
    }

    /**
     * Test {@link PersistContentTypeConstruct#process(MetaExpression, DocumentDefinitionService)} under normal circumstances
     * @throws PersistenceException
     */
    @Test
    public void testProcessNormal() throws PersistenceException {

        // Run
        PersistContentTypeConstruct.process(typeName, definitionService);

        // Verify
        verify(definitionService).persist("myContentType");
    }

    /**
     * Test that {@link PersistContentTypeConstruct#process(MetaExpression, DocumentDefinitionService)} rethrows {@link PersistenceException PersistenceExceptions}.
     * @throws PersistenceException
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessError() throws PersistenceException {
        // Mock
        doThrow(PersistenceException.class).when(definitionService).persist(anyString());

        // Run
        PersistContentTypeConstruct.process(typeName, definitionService);
    }
}
