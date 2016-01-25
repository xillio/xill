package nl.xillio.xill.plugins.document.services;

import com.mongodb.MongoException;
import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.interfaces.FindResult;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;
import org.bson.Document;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

    @Test(expectedExceptions = ValidationException.class)
    public void testPersistTranslatesModelException() throws Exception {
        // ID
        DocumentID id = mock(DocumentID.class);

        // Builder
        DocumentBuilder builder = mock(DocumentBuilder.class);
        when(builder.commit()).thenReturn(id);

        // UDMService
        UDMService service = mock(UDMService.class);
        doThrow(new ModelException("")).when(service).persist(any(DocumentID.class));

        // Spy
        XillUDMService xillUDMService = spyService(service);

        // Call
        xillUDMService.persist(builder);

    }

    @Test
    public void testGetMap() {
        UDMService udmService = mock(UDMService.class);
        when(udmService.toJSON(any())).thenReturn("{\"value\": 5436}");
        XillUDMService xillUDMService = spyService(udmService);

        xillUDMService.getMap("This is my id");

    }

    @Test
    public void testFindWhereTransformation() throws PersistenceException {
        // Result
        FindResult result = mock(FindResult.class);
        when(result.iterator()).thenReturn(result);
        when(result.hasNext()).thenReturn(true, true, false);

        // Service
        UDMService udmService = mock(UDMService.class);
        when(udmService.find(any(), any())).thenReturn(result);
        when(udmService.toJSON(any())).thenReturn("{ \"test\": \"MyValue\"}");

        // Spy
        XillUDMService xillUDMService = spyService(udmService);

        // Call
        Iterator<Map<String, Object>> returnValue = xillUDMService.findMapWhere(null, null);

        Map<String, String> expectedValue = new HashMap<>();
        expectedValue.put("test", "MyValue");

        int count = 0;
        while (returnValue.hasNext()) {
            assertTrue(returnValue.next().equals(expectedValue));
            count++;
        }
        assertEquals(count, 2);
    }

    @Test(expectedExceptions = PersistenceException.class)
    public void testFindWhereException() throws PersistenceException {
        // Service
        UDMService udmService = mock(UDMService.class);
        when(udmService.find(any(), any())).thenThrow(new MongoException(""));

        // Spy
        XillUDMService xillUDMService = spyService(udmService);

        xillUDMService.findMapWhere(null, null);

    }


    private XillUDMService spyService(UDMService service) {
        XillUDMService udmService = spy(new XillUDMService(null));
        doReturn(service).when(udmService).getUdmService(anyString());
        return udmService;
    }


}