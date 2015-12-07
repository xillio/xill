package nl.xillio.migrationtool.gui.editor;


import netscape.javascript.JSObject;
import nl.xillio.xill.api.XillProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Objects;

public class XillJSObject {
    private static final Logger LOGGER = LogManager.getLogger();
    private final XillProcessor processor;

    public XillJSObject(XillProcessor processor) {
        Objects.requireNonNull(processor);
        this.processor = processor;
    }

    public void info(String message) {
        LOGGER.info(message);
    }

    public void getCompletions(JSObject query) {

        try {
            String prefix = query.getMember("prefix").toString();
            String currentLine = query.getMember("currentLine").toString();
            int column = ((Number) query.getMember("column")).intValue();
            int row = ((Number) query.getMember("row")).intValue();

            processor.getCompletions(currentLine, prefix, column, row).forEach(
                    (key, value) -> value.forEach(
                            item -> {
                                int parPos = item.lastIndexOf("(");
                                String caption = parPos > 0 ? item.substring(0, parPos) : item;
                                query.call("callback", caption, item, 10, key);
                            }
                    )
            );
        } catch (Exception e) {
            LOGGER.error("ugh", e);
            throw e;
        }
    }

    public String[] getPluginNames() {
        Collection<String> names = processor.listPackages();
        return names.toArray(new String[names.size()]);
    }
}
