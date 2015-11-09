package nl.xillio.xill.plugins.rest.data;

import nl.xillio.xill.api.data.MetadataExpression;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * This class is used for holding all parts of multipart mime body content
 *
 * @author Zbynek Hochmann
 */
public class MultipartBody implements MetadataExpression {

    private interface MultipartContent {
        void add(final MultipartEntityBuilder entity);
    }

    private class FileContent implements MultipartContent {

        private String name;
        private File file;

        public FileContent(final String name, final File file) {
            this.name = name;
            this.file = file;
        }

        public void add(final MultipartEntityBuilder entity) {
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

        public void add(final MultipartEntityBuilder entity) {
            ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), "UTF-8");
            entity.addTextBody(name, text, contentType);
        }
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private List<MultipartContent> multipartContentList = new ArrayList<>();

    public void setToRequest(final Request request) {
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        multipartContentList.stream().forEach(content -> content.add(entity));

        HttpEntity httpEntity = entity.build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int)httpEntity.getContentLength());
        try {
            httpEntity.writeTo(outputStream);
        } catch (IOException e) {
            LOGGER.error("Error while composing multipart content", e);
            return;
        }

        request.addHeader(httpEntity.getContentType());
        request.bodyByteArray(outputStream.toByteArray());
    }

    public void addFile(final String name, final String fileName) {
        File file = new File(fileName);
        multipartContentList.add(new FileContent(name, file));
    }

    public void addText(final String name, final String text) {
        multipartContentList.add(new TextContent(name, text));
    }

    @Override
    public String toString() {
        return String.format("REST Body [%1$d items]", multipartContentList.size());
    }
}
