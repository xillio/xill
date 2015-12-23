package nl.xillio.xill.plugins.file.services.permissions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represents a summary of the declared permissions on a file.
 *
 * @author Thomas Biesaart
 */
public class FilePermissions {
    private final File file;
    private final Map<String, FileOperations> users = new HashMap<>();
    private final Map<String, FileOperations> groups = new HashMap<>();

    public FilePermissions(File file) {
        this.file = file;
    }

    public void setUser(String user, boolean readable, boolean writable, boolean executable) {
        setOn(users, user, new FileOperations(readable, writable, executable));
    }

    public void setGroup(String group, boolean readable, boolean writable, boolean executable) {
        setOn(groups, group, new FileOperations(readable, writable, executable));
    }

    /**
     * This method will set or combine file permissions on a map with a string index.
     *
     * @param map        the map
     * @param key        the key
     * @param operations the operations to add.
     */
    private void setOn(Map<String, FileOperations> map, String key, FileOperations operations) {
        FileOperations current = map.get(key);
        if (current == null) {
            map.put(key, operations);
        } else {
            FileOperations combined = new FileOperations(
                    current.isReadable() || operations.isReadable(),
                    current.isWritable() || operations.isWritable(),
                    current.isExecutable() || operations.isExecutable()
            );

            map.put(key, combined);
        }
    }

    public File getFile() {
        return file;
    }

    public Map<String, Map<String, Map<String, Boolean>>> toMap() {
        Map<String, Map<String, Map<String, Boolean>>> result = new HashMap<>();
        result.put("users", toMap(this.users));
        result.put("groups", toMap(this.groups));

        return result;
    }

    private Map<String, Map<String, Boolean>> toMap(Map<String, FileOperations> input) {
        return input.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toMap()
                ));
    }

}
