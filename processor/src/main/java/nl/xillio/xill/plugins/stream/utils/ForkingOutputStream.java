package nl.xillio.xill.plugins.stream.utils;

import nl.xillio.xill.api.components.MetaExpression;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This implementation of the OutputStream will delegate all calls to underlying streams.
 *
 * @author Thomas biesaart
 */
class ForkingOutputStream extends OutputStream {
    private final OutputStream[] streams;
    private final MetaExpression hostedList;


    public ForkingOutputStream(MetaExpression hostedList, OutputStream... streams) {
        this.streams = streams;
        this.hostedList = hostedList;
        hostedList.registerReference();
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream stream : streams) {
            stream.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (OutputStream stream : streams) {
            stream.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream stream : streams) {
            stream.write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream stream : streams) {
            stream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        hostedList.releaseReference();
    }
}
