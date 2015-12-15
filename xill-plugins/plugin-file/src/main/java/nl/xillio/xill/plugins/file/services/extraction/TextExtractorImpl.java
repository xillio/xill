package nl.xillio.xill.plugins.file.services.extraction;

import com.google.inject.Singleton;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This is the main implementation of the {@link TextExtractor} service using {@link Tika}
 */
@Singleton
public class TextExtractorImpl implements TextExtractor {
    private final Tika tika = new Tika();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public String extractText(final File file, int timeoutValue) throws IOException {
        try {
            //Run this through an executor so it can time out.
            Future<String> future = executor.submit(() -> tika.parseToString(file));
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RobotRuntimeException("Failed to extract text from " + file.getAbsolutePath(), e);
        }
    }

}
