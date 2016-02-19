package nl.xillio.xill.plugins.codec.hash.constructs;

import nl.xillio.xill.plugins.codec.constructs.AbstractDigestConstruct;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This construct will consume an input stream or string and get its SHA-256 hash.
 * It will optionally forward the input data to an output stream.
 *
 * @author Thomas Biesaart
 */
public class ToSHA256Construct extends AbstractDigestConstruct {

    @Override
    protected MessageDigest getDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }
}
