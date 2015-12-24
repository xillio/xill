package nl.xillio.xill.plugins.file.services.permissions;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a collection of file permission flags.
 *
 * @author Thomas Biesaart
 */
public class FileOperations {
    private final boolean readable;
    private final boolean writable;
    private final boolean executable;

    public FileOperations(boolean readable, boolean writable, boolean executable) {
        this.readable = readable;
        this.writable = writable;
        this.executable = executable;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public boolean isExecutable() {
        return executable;
    }

    public Map<String, Boolean> toMap() {
        Map<String, Boolean> result = new HashMap<>();

        result.put("read", readable);
        result.put("write", writable);
        result.put("execute", executable);

        return result;
    }
}
