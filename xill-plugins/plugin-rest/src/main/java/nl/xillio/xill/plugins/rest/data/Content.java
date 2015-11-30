package nl.xillio.xill.plugins.rest.data;

import com.google.inject.ConfigurationException;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.api.data.XmlNodeFactory;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.services.inject.InjectorUtils;
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;

/**
 * Support class for storing the content:
 * either for request body
 * or for the response
 */
public class Content {

    private static final Logger LOGGER = LogManager.getLogger();
    private HttpResponse fullResponse;
    private String content = "";
    private ContentType type = ContentType.TEXT_PLAIN;
    private MultipartBody multipartBody = null;
    private XmlNodeFactory xmlNodeFactory;
    private static final String ENCODING = "UTF-8";

    /**
     * Acquire content from Xill variable
     *
     * @param contentVar     request's body content
     * @param contentTypeVar request's body content type
     */
    public Content(final MetaExpression contentVar, final MetaExpression contentTypeVar) {
        if (contentVar.isNull()) {
            return;
        }

        if (contentTypeVar.isNull()) {
            if ((contentVar.getType() == ExpressionDataType.OBJECT) || (contentVar.getType() == ExpressionDataType.LIST)) {
                // JSON format
                this.content = contentVar.toString();
                this.type = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), ENCODING);
            } else if (contentVar.getMeta(XmlNode.class) != null) {
                // XML format
                XmlNode xmlNode = contentVar.getMeta(XmlNode.class);
                this.content = xmlNode.getXmlContent();
                this.type = ContentType.create(ContentType.APPLICATION_XML.getMimeType(), ENCODING);
            } else if (contentVar.getMeta(MultipartBody.class) != null) {
                // MultipartBody
                this.multipartBody = contentVar.getMeta(MultipartBody.class);
            } else {
                // Plain text content
                this.content = contentVar.getStringValue();
                this.type = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), ENCODING);
            }
            this.type.withCharset("UTF-8");
        } else {
            this.type = ContentType.create(contentTypeVar.getStringValue(), ENCODING);
            this.content = contentVar.getStringValue();
        }
    }

    /**
     * Acquire content from string
     *
     * @param text content
     */
    public Content(final String text) {
        this.content = text;
        this.type = ContentType.TEXT_PLAIN;
    }

    /**
     * Acquire content from Apache Fluent response
     *
     * @param fullResponse the response
     */
    public Content(HttpResponse fullResponse) {

        this.fullResponse = fullResponse;

        if(fullResponse == null) {
            return;
        }

        try {
            this.content = IOUtils.toString(fullResponse.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.type = ContentType.create(fullResponse.getEntity().getContentType().getValue());
    }

    /**
     * @return the content as a string
     */
    public String getContent() {
        if (this.multipartBody != null) {
            throw new RobotRuntimeException("Multipart can be used for request only!");
        }
        // else
        return this.content;
    }

    /**
     * @return the type of the content
     */
    public ContentType getType() {
        if (this.multipartBody != null) {
            throw new RobotRuntimeException("Multipart can be used for request only!");
        }
        // else
        return this.type;
    }

    /**
     * @return true if content is empty
     */
    public boolean isEmpty() {
        return (this.content.isEmpty() && (this.multipartBody == null));
    }

    /**
     * Create the Xill variable according to the type of content and fill it with proper content
     * It should be used for REST response only!
     *
     * @return new Xill variable (JSON-&gt;OBJECT type / XML-&gt;XmlNode / other-&gt;ATOMIC string)
     */
    public MetaExpression getMeta() {
        LinkedHashMap<String, MetaExpression> response = new LinkedHashMap<>();
        response.put("body", getMetaBody());

        if (fullResponse != null) {
            response.put("status", fromValue(fullResponse.getStatusLine().getStatusCode()));
            response.put("headers", fromValue(getMetaHeaders(fullResponse.getAllHeaders())));
        }

        return fromValue(response);
    }

    private LinkedHashMap<String, MetaExpression> getMetaHeaders(Header[] allHeaders) {
        LinkedHashMap<String, MetaExpression> headers = new LinkedHashMap<>();

        for (Header header : allHeaders) {
            headers.put(header.getName(), fromValue(header.getValue()));
        }

        return headers;
    }

    private MetaExpression getMetaBody() {
        // Only this is branch is covered with unit tests (FOR NOW) in accordance with a discussion with Thomas Biesaart.
        if (this.getType().getMimeType().contains("json")) {
            JsonParser jsonParser = InjectorUtils.get(JsonParser.class);
            try {
                Object result = jsonParser.fromJson(this.getContent(), Object.class);
                return MetaExpression.parseObject(result); // Mockito does not provide mechanisms for testing static methods.
                // In order to test static methods, we would need to use PowerMockito.
            } catch (JsonException e) {
                LOGGER.error("Failed to parse response as json. Falling back.", e);
            }
        }

        if (this.getType().getMimeType().contains("xml")) {
            if (xmlNodeFactory == null) {
                // We have no factory yet
                try {
                    xmlNodeFactory = InjectorUtils.get(XmlNodeFactory.class);
                } catch (ConfigurationException e) {
                    LOGGER.error("No binding found for XmlNodeFactory", e);
                    throw new RobotRuntimeException("Did not detect the XML plugin, do you have it installed?", e);
                }
            }

            try {
                XmlNode xml = xmlNodeFactory.fromString(this.getContent());
                MetaExpression result = fromValue(xml.toString());
                result.storeMeta(XmlNode.class, xml);
                return result;
            } catch (Exception e) {
                LOGGER.error("Failed to parse XML. Falling back.", e);
            }
        }

        return fromValue(this.content);
    }

    /**
     * Set the single content or multipart body to the REST request
     *
     * @param request REST request
     */
    public void setToRequest(final Request request) {
        if (this.multipartBody == null) {
            request.bodyString(this.getContent(), this.getType());
        } else {
            this.multipartBody.setToRequest(request);
        }
    }
}
