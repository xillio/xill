package nl.xillio.xill.plugins.hash.services;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is the main implementation of the {@link HashService}
 *
 * @author Zbynek Hochmann
 */
public class HashServiceImpl implements HashService {
    @Override
    public String md5(final String value, final boolean fromFile) throws NoSuchAlgorithmException, IOException {
        if (fromFile) {
            return Files.hash(new File(value), Hashing.md5()).toString();
        } else {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(value));
            return String.format("%032x", new BigInteger(1, md5.digest()));
        }
    }
}
