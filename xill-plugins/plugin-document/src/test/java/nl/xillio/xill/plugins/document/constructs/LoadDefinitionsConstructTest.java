package nl.xillio.xill.plugins.document.constructs;

import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link LoadDefinitionsConstruct}
 */
public class LoadDefinitionsConstructTest extends TestUtils{

    private LoadDefinitionsConstruct construct;
    private DocumentDefinitionService definitionService;
    private ConstructContext context;

    @BeforeClass
    public void initialize(){
        definitionService = mock(DocumentDefinitionService.class);
        construct = spy(new StubLoadDefinitionsConstruct(definitionService));
        context = mock(ConstructContext.class);
    }

    /**
     * Test construct when given a filename
     * @throws FileNotFoundException
     */
    @Test
    public void testProcessFile() throws FileNotFoundException {
        // Mock
        MetaExpression json = mockExpression(ATOMIC, false, 0, "file.json");

        // Run
        construct.process(json, context);

        // Verify
        verify(construct).load(any(File.class));
    }

    /**
     * Test that a FileNotFoundException is caught by {@link LoadDefinitionsConstruct#process(MetaExpression, ConstructContext)}.
     * @throws FileNotFoundException
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessFileNotFound() throws FileNotFoundException {
        // Mock
        MetaExpression json = mockExpression(ATOMIC, false, 0, "file.json");
        doThrow(FileNotFoundException.class).when(construct).load(any(File.class));

        // Run
        construct.process(json, context);
    }

    /**
     * Test construct when given an object
     */
    @Test
    public void testProcessJson(){
        // Mock
        MetaExpression json = mockExpression(OBJECT);

        // Run
        construct.process(json, context);

        // Verify
        verify(construct).load(anyString());
    }

    /**
     * Implementation of {@link LoadDefinitionsConstruct} used for testing
     */
    private static class StubLoadDefinitionsConstruct extends LoadDefinitionsConstruct {
        public StubLoadDefinitionsConstruct(DocumentDefinitionService definitionService) {
            super(definitionService);
        }

        @Override protected void load(File json) throws FileNotFoundException {}
        @Override protected void load(String json) {}
    }

}
