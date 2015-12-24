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
     * Do MD5 hash from the provided file
     *
     * @param file The filename
     * @return md5 hash
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    String fileToMD5(String file) throws NoSuchAlgorithmException, IOException;

    /**
     * Do MD5 hash from the provided string
     *
     * @param value The string
     * @return md5 hash
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    String stringToMD5(String value) throws NoSuchAlgorithmException, IOException;
}
