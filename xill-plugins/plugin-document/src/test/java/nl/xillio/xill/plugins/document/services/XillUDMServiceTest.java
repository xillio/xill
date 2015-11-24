package nl.xillio.xill.plugins.document.services;

import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.data.UDMDocument;
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

    @Test
    public void testPersistTranslates() throws Exception {


    }


    private XillUDMService spyService(UDMService service) {
        XillUDMService udmService = spy(new XillUDMService(null));
        doReturn(service).when(udmService).getUdmService(anyString());
        return udmService;
    }


}