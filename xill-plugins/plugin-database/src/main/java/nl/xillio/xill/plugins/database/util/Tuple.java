package nl.xillio.xill.plugins.database.util;

/**
 * @param <T> Key type
 * @param <U> Value Type
 * @author Geert Konijnendijk
 * @author Sander Visser
 */
@SuppressWarnings("javadoc")
public class Tuple<T, U> {

    private T key;
    private U value;

    public Tuple(T key, U value) {
        super();
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public U getValue() {
        return value;
    }
}
