package nl.xillio.xill.plugins.codec.hash.services;

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
    public String stringToMD5(final String value) throws NoSuchAlgorithmException,IOException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(StandardCharsets.UTF_8.encode(value));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }

    @Override
    public String fileToMD5(final String file) throws NoSuchAlgorithmException,IOException {
        return Files.hash(new File(file), Hashing.md5()).toString();
    }
}
