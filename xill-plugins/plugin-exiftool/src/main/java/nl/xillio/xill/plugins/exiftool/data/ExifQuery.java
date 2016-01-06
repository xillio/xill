package nl.xillio.xill.plugins.exiftool.data;

import nl.xillio.exiftool.ExifTool;
import nl.xillio.xill.api.data.MetadataExpression;

/**
 * This class represents a wrapper around the resources needed for an exiftool query.
 *
 * @author Thomas Biesaart
 */
public class ExifQuery implements AutoCloseable, MetadataExpression {
    private final ExifTool exifTool;

    public ExifQuery(ExifTool exifTool) {
        this.exifTool = exifTool;
    }

    @Override
    public void close() {
        exifTool.close();
    }
}
