var xillCoreOverride = null;

/**
 * This object represents a communication interface to the xill back-end.
 * This here you can see a default implementation to allow running in the browser
 */
window.xillCore = {
    debug: function (message) {
        this.log("DEBUG", message);
    },
    info: function (message) {
        this.log("INFO", message);
    },
    warn: function (message) {
        this.log("WARN", message);
    },
    error: function (message) {
        this.log("ERROR", message);
    },
    getKeywords: function () {
        return ["as", "break", "callbot", "continue", "else", "filter", "foreach", "function", "if", "in", "include", "map", "private", "return", "use", "var", "while","do","fail","success","finally"]
    },
    getPluginNames: function () {
        if (xillCoreOverride) {
            return xillCoreOverride.getPluginNames();
        }
        return ["System"];
    },
    getLanguageConstants: function () {
        return ["ATOMIC", "LIST", "NaN", "OBJECT", "argument", "null", "true", "false"]
    },
    getCompletions: function (state, session, pos, prefix, aceCallback) {
        if (xillCoreOverride) {

            xillCoreOverride.getCompletions({
                state: state,
                session: session,
                prefix: prefix,
                column: pos.column,
                row: pos.row,
                currentLine: session.getLine(pos.row),
                callback: function (name, value, score, meta) {
                    aceCallback(null, {name: name, caption: name, snippet: value, score: score, meta: meta});
                }
            });
        }
    }
};
window.initConsole = function () {
    if (xillCoreOverride != null) {
        console.log = function () {
            var output = "[JAVASCRIPT]";
            for (var key in arguments) {
                output += " " + arguments[key];
            }
            xillCoreOverride.info(output);
        };
        console.error = console.log;
        console.warn = console.log;
    }
}
