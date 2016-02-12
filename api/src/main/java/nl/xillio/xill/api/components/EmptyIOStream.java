package nl.xillio.xill.api.components;

import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.NoStreamAvailableException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class represents an IOStream that has no providers.
 * Use this for expressions that have no binary representation.
 *
 * @author Thomas biesaart
 */
class EmptyIOStream implements IOStream {
    @Override
    public boolean hasInputStream() {
        return false;
    }

    @Override
    public BufferedInputStream getInputStream() throws IOException {
        throw new NoStreamAvailableException("This is no binary content");
    }

    @Override
    public boolean hasOutputStream() {
        return false;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new NoStreamAvailableException("This is no binary content");
    }

    @Override
    public void close() {
        // There are no streams to close
    }

    @Override
    public String getDescription() {
        return null;
    }
}
