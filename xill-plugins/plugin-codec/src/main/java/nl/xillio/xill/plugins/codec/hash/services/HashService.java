package nl.xillio.xill.plugins.codec.hash.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.codec.hash.HashXillPlugin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * This interface represents some of the operations for the {@link HashXillPlugin}.
 *
 * @author Zbynek Hochmann
 */
@ImplementedBy(HashServiceImpl.class)
public interface HashService {
    /**
     * Do MD5 hash from the provided text or file (content)
     *
     * @param value     The text or filename
     * @param fromFile  It should be false for text and true for file
     * @return          md5 hash
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    String md5(String value, boolean fromFile) throws NoSuchAlgorithmException, IOException;
}
