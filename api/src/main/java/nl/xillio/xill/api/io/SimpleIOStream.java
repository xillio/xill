package nl.xillio.xill.api.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is a simple implementation of {@link IOStream} based on two providers.
 *
 * @author Thomas biesaart
 */
public class SimpleIOStream implements IOStream {
    private final IOProvider<OutputStream> outputStreamSupplier;
    private final IOProvider<InputStream> inputStreamSupplier;

    /**
     * Create an IOStream from providers.
     *
     * @param outputStreamSupplier the output stream provider, can be null
     * @param inputStreamSupplier  the input stream provider, can be null
     */
    public SimpleIOStream(IOProvider<OutputStream> outputStreamSupplier, IOProvider<InputStream> inputStreamSupplier) {
        this.outputStreamSupplier = outputStreamSupplier;
        this.inputStreamSupplier = inputStreamSupplier;
    }

    @Override
    public boolean hasInputStream() {
        return inputStreamSupplier != null;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return inputStreamSupplier.get();
    }

    @Override
    public boolean hasOutputStream() {
        return outputStreamSupplier != null;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return outputStreamSupplier.get();
    }

    @FunctionalInterface
    public interface IOProvider<T> {
        T get() throws IOException;
    }
}
