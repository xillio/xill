package nl.xillio.xill.plugins.rest.data;

import me.biesaart.utils.IOUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.services.inject.InjectorUtils;
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by Anwar on 11/27/2015.
 */
public class ContentTest {

    /**
     * Tests the getMeta() method of the Content class.
     */
    @Test
    public void testGetMeta() throws IOException {

        HttpResponse fullResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);
        HttpEntity httpEntity = mock(HttpEntity.class);
        when(fullResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(IOUtils.toInputStream("XILLIDE"));
        Header header = new BasicHeader("type", "application/json");
        when(httpEntity.getContentType()).thenReturn(header);

        Content content = new Content(fullResponse);
        Header[] headers = new Header[]{ new BasicHeader("type", "XILLSERVER") };
        when(fullResponse.getStatusLine().getStatusCode()).thenReturn(200);
        when(fullResponse.getAllHeaders()).thenReturn(headers);



        // Call to getMeta()
        MetaExpression result = content.getMeta();
        assertEquals(result.toString(), "{\"body\":\"XILLIDE\",\"status\":200,\"headers\":{\"type\":\"XILLSERVER\"}}");
    }

    @Test
    public void testPartialGetMetaBody() throws IOException, JsonException {

        HttpResponse fullResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);
        when(fullResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream("application/json"));
        Header header = new BasicHeader("type", "application/json");
        HttpEntity httpEntity = mock(HttpEntity.class);
        when(httpEntity.getContentType()).thenReturn(header);
        JsonParser jsonParser = InjectorUtils.get(JsonParser.class);
        Object o = jsonParser.fromJson("{\"body\":\"XILLIDE\"}", Object.class);
        assertEquals("{\"body\":\"XILLIDE\"}", MetaExpression.parseObject(o).toString());
    }
}