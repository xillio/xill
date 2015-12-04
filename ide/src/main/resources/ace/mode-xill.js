/**
 * This block defines the cstyle folding behavior.
 */
ace.define("ace/mode/folding/cstyle", ["require", "exports", "module", "ace/lib/oop", "ace/range", "ace/mode/folding/fold_mode"], function (require, exports, module) {
    "use strict";

    var oop = require("../../lib/oop");
    var Range = require("../../range").Range;
    var BaseFoldMode = require("./fold_mode").FoldMode;

    var FoldMode = exports.FoldMode = function (commentRegex) {
        if (commentRegex) {
            this.foldingStartMarker = new RegExp(
                this.foldingStartMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.start)
            );
            this.foldingStopMarker = new RegExp(
                this.foldingStopMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.end)
            );
        }
    };
    oop.inherits(FoldMode, BaseFoldMode);

    (function () {

        this.foldingStartMarker = /(\{|\[)[^\}\]]*$|^\s*(\/\*)/;
        this.foldingStopMarker = /^[^\[\{]*(\}|\])|^[\s\*]*(\*\/)/;
        this.singleLineBlockCommentRe = /^\s*(\/\*).*\*\/\s*$/;
        this.tripleStarBlockCommentRe = /^\s*(\/\*\*\*).*\*\/\s*$/;
        this.startRegionRe = /^\s*(\/\*|\/\/)#?region\b/;
        this._getFoldWidgetBase = this.getFoldWidget;
        this.getFoldWidget = function (session, foldStyle, row) {
            var line = session.getLine(row);

            if (this.singleLineBlockCommentRe.test(line)) {
                if (!this.startRegionRe.test(line) && !this.tripleStarBlockCommentRe.test(line))
                    return "";
            }

            var fw = this._getFoldWidgetBase(session, foldStyle, row);

            if (!fw && this.startRegionRe.test(line))
                return "start"; // lineCommentRegionStart

            return fw;
        };

        this.getFoldWidgetRange = function (session, foldStyle, row, forceMultiline) {
            var line = session.getLine(row);

            if (this.startRegionRe.test(line))
                return this.getCommentRegionBlock(session, line, row);

            var match = line.match(this.foldingStartMarker);
            if (match) {
                var i = match.index;

                if (match[1])
                    return this.openingBracketBlock(session, match[1], row, i);

                var range = session.getCommentFoldRange(row, i + match[0].length, 1);

                if (range && !range.isMultiLine()) {
                    if (forceMultiline) {
                        range = this.getSectionRange(session, row);
                    } else if (foldStyle != "all")
                        range = null;
                }

                return range;
            }

            if (foldStyle === "markbegin")
                return;

            var match = line.match(this.foldingStopMarker);
            if (match) {
                var i = match.index + match[0].length;

                if (match[1])
                    return this.closingBracketBlock(session, match[1], row, i);

                return session.getCommentFoldRange(row, i, -1);
            }
        };

        this.getSectionRange = function (session, row) {
            var line = session.getLine(row);
            var startIndent = line.search(/\S/);
            var startRow = row;
            var startColumn = line.length;
            row = row + 1;
            var endRow = row;
            var maxRow = session.getLength();
            while (++row < maxRow) {
                line = session.getLine(row);
                var indent = line.search(/\S/);
                if (indent === -1)
                    continue;
                if (startIndent > indent)
                    break;
                var subRange = this.getFoldWidgetRange(session, "all", row);

                if (subRange) {
                    if (subRange.start.row <= startRow) {
                        break;
                    } else if (subRange.isMultiLine()) {
                        row = subRange.end.row;
                    } else if (startIndent == indent) {
                        break;
                    }
                }
                endRow = row;
            }

            return new Range(startRow, startColumn, endRow, session.getLine(endRow).length);
        };
        this.getCommentRegionBlock = function (session, line, row) {
            var startColumn = line.search(/\s*$/);
            var maxRow = session.getLength();
            var startRow = row;

            var re = /^\s*(?:\/\*|\/\/|--)#?(end)?region\b/;
            var depth = 1;
            while (++row < maxRow) {
                line = session.getLine(row);
                var m = re.exec(line);
                if (!m) continue;
                if (m[1]) depth--;
                else depth++;

                if (!depth) break;
            }

            var endRow = row;
            if (endRow > startRow) {
                return new Range(startRow, startColumn, endRow, line.length);
            }
        };

    }).call(FoldMode.prototype);

});

/**
 * This block defines the syntax highlighting of the xill language.
 */
ace.define("ace/mode/xill_highlight_rules", function (require, exports) {
    var oop = require("../lib/oop");

    var HighlightRules = function () {
        var keywords = xillCore.getKeywords().join("|");
        var plugins = xillCore.getPluginNames().join("|");
        var languageConstants = xillCore.getLanguageConstants().join("|");
        var escapedRe = "\\\\(?:x[0-9a-fA-F]{2}|" + // hex
            "u[0-9a-fA-F]{4}|" + // unicode
            "[0-2][0-7]{0,2}|" + // oct
            "3[0-6][0-7]?|" + // oct
            "37[0-7]?|" + // oct
            "[4-7][0-7]?|" + // oct
            ".)";


        this.$rules = {
            "start": [
                {token: "comment", regex: "\\/\\/.*$"},
                {token: "comment", regex: "\\/\\*", next: "comment"},
                {token: "string", regex: '"', next: "qqString"},
                {token: "string", regex: "'", next: "gString"},
                {token: "constant.numeric", regex: "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"},
                {token: "lparen", regex: "[\\[({]"},
                {token: "rparen", regex: "[\\])}]"},
                {token: "keyword", regex: "\\b(?:" + keywords + ")\\b"},
                {token: "plugin", regex: "\\b(?:" + plugins + ")\\b"},
                {token: "constant.language", regex: "\\b(?:" + languageConstants + ")\\b"}

            ],
            "comment": [
                {token: "comment", regex: ".*?\\*\\/", next: "start"},
                {token: "comment", regex: ".+"}
            ],
            "qqString": [
                {token: "constant.language.escape", regex: escapedRe},
                {token: "string", regex: "\\\\$", next: "qqString"},
                {token: "string", regex: '"', next: "start"},
                {defaultToken: "string"}
            ]
        };
    };

    oop.inherits(HighlightRules, require("./text_highlight_rules").TextHighlightRules);

    exports.HighlightRules = HighlightRules;
});

/**
 * This block defines the xill mode.
 */
ace.define("ace/mode/xill", function (require, exports) {
    var oop = require("../lib/oop");

    // Get dependencies
    var HighlightRules = require("./xill_highlight_rules").HighlightRules;
    var CStyleFoldMode = require("./folding/cstyle").FoldMode;

    var Mode = function () {
        this.HighlightRules = HighlightRules;
        this.foldingRules = new CStyleFoldMode();
    };

    oop.inherits(Mode, require("./text").Mode);

    exports.Mode = Mode;
});

