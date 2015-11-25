package nl.xillio.xill.plugins.document.services;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class XillUDMServiceTest {

    /**
     * Test if the save method correctly routes to the sub methods.
     *
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        XillUDMService udmService = spyService(null);
        doReturn("nothing").when(udmService).create(any());
        doReturn("nothing").when(udmService).update(any());

        UDMDocument doc = mock(UDMDocument.class);
        when(doc.isNew()).thenReturn(true);

        // Test create
        udmService.save(doc);
        verify(udmService).create(any());
        verify(udmService, times(0)).update(any());

        // Test update
        when(doc.isNew()).thenReturn(false);
        udmService.save(doc);
        verify(udmService).create(any());
        verify(udmService).update(any());
    }

    @Test(expectedExceptions = PersistException.class)
    public void testPersistTranslatesException() throws Exception {
        // ID
        DocumentID id = mock(DocumentID.class);

        // Builder
        DocumentBuilder builder = mock(DocumentBuilder.class);
        when(builder.commit()).thenReturn(id);

        // UDMService
        UDMService service = mock(UDMService.class);
        doThrow(new PersistenceException("")).when(service).persist(any(DocumentID.class));

        // Spy
        XillUDMService xillUDMService = spyService(service);

        // Call
        xillUDMService.persist(builder);

    }


    private XillUDMService spyService(UDMService service) {
        XillUDMService udmService = spy(new XillUDMService(null));
        doReturn(service).when(udmService).getUdmService(anyString());
        return udmService;
    }


}