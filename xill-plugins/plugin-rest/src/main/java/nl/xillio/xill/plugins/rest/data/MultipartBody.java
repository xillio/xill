package nl.xillio.xill.plugins.rest.data;

import nl.xillio.xill.api.data.MetadataExpression;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for holding all parts of multipart mime body content
 *
 * @author Zbynek Hochmann
 */
public class MultipartBody implements MetadataExpression {

    private interface MultipartContent {
        void addTo(final MultipartEntityBuilder entity);
    }

    private class FileContent implements MultipartContent {

        private String name;
        private File file;

        public FileContent(final String name, final File file) {
            this.name = name;
            this.file = file;
        }

        public void addTo(final MultipartEntityBuilder entity) {
            entity.addBinaryBody(name, file);
        }
    }

    private class TextContent implements MultipartContent {
        private String name;
        private String text;

        public TextContent(final String name, final String text) {
            this.name = name;
            this.text = text;
        }

        public void addTo(final MultipartEntityBuilder entity) {
            ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), "UTF-8");
            entity.addTextBody(name, text, contentType);
        }
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private List<MultipartContent> multipartContentList = new ArrayList<>();

    /**
     * Build HTTP data from all content parts and assign it to REST request
     *
     * @param request Existing REST request
     */
    public void setToRequest(final Request request) {
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        multipartContentList.stream().forEach(content -> content.addTo(entity)); // Feed the entitybuilder with all particular content parts

        HttpEntity httpEntity = entity.build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int) httpEntity.getContentLength());
        try {
            httpEntity.writeTo(outputStream);
        } catch (IOException e) {
            LOGGER.error("Error while composing multipart content", e);
            return;
        }

        request.addHeader(httpEntity.getContentType());
        request.bodyByteArray(outputStream.toByteArray());
    }

    /**
     * Add a content of file to a multipart data (as binary part)
     *
     * @param name     The content name
     * @param fileName The filenamepath
     */
    public void addFile(final String name, final String fileName) {
        File file = new File(fileName);
        multipartContentList.add(new FileContent(name, file));
    }

    /**
     * Add text content to multipart data
     *
     * @param name The content name
     * @param text The content text
     */
    public void addText(final String name, final String text) {
        multipartContentList.add(new TextContent(name, text));
    }

    @Override
    public String toString() {
        return "REST multipart body";
    }
}
