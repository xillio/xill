ace.define("ace/mode/behaviour/cstyle", [ "require", "exports", "module", "ace/lib/oop", "ace/mode/behaviour", "ace/token_iterator", "ace/lib/lang" ], function(require, exports, module) {
	"use strict";

	var oop = require("../../lib/oop");
	var Behaviour = require("../behaviour").Behaviour;
	var TokenIterator = require("../../token_iterator").TokenIterator;
	var lang = require("../../lib/lang");

	var SAFE_INSERT_IN_TOKENS = [ "text", "paren.rparen", "punctuation.operator" ];
	var SAFE_INSERT_BEFORE_TOKENS = [ "text", "paren.rparen", "punctuation.operator", "comment" ];

	var context;
	var contextCache = {};
	var initContext = function(editor) {
		var id = -1;
		if (editor.multiSelect) {
			id = editor.selection.index;
			if (contextCache.rangeCount != editor.multiSelect.rangeCount)
				contextCache = {
					rangeCount : editor.multiSelect.rangeCount
				};
		}
		if (contextCache[id])
			return context = contextCache[id];
		context = contextCache[id] = {
			autoInsertedBrackets : 0,
			autoInsertedRow : -1,
			autoInsertedLineEnd : "",
			maybeInsertedBrackets : 0,
			maybeInsertedRow : -1,
			maybeInsertedLineStart : "",
			maybeInsertedLineEnd : ""
		};
	};

	var CstyleBehaviour = function() {
		this.add("braces", "insertion", function(state, action, editor, session, text) {
			var cursor = editor.getCursorPosition();
			var line = session.doc.getLine(cursor.row);
			if (text == '{') {
				initContext(editor);
				var selection = editor.getSelectionRange();
				var selected = session.doc.getTextRange(selection);
				if (selected !== "" && selected !== "{" && editor.getWrapBehavioursEnabled()) {
					return {
						text : '{' + selected + '}',
						selection : false
					};
				} else if (CstyleBehaviour.isSaneInsertion(editor, session)) {
					if (/[\]\}\)]/.test(line[cursor.column]) || editor.inMultiSelectMode) {
						CstyleBehaviour.recordAutoInsert(editor, session, "}");
						return {
							text : '{}',
							selection : [ 1, 1 ]
						};
					} else {
						CstyleBehaviour.recordMaybeInsert(editor, session, "{");
						return {
							text : '{',
							selection : [ 1, 1 ]
						};
					}
				}
			} else if (text == '}') {
				initContext(editor);
				var rightChar = line.substring(cursor.column, cursor.column + 1);
				if (rightChar == '}') {
					var matching = session.$findOpeningBracket('}', {
						column : cursor.column + 1,
						row : cursor.row
					});
					if (matching !== null && CstyleBehaviour.isAutoInsertedClosing(cursor, line, text)) {
						CstyleBehaviour.popAutoInsertedClosing();
						return {
							text : '',
							selection : [ 1, 1 ]
						};
					}
				}
			} else if (text == "\n" || text == "\r\n") {
				initContext(editor);
				var closing = "";
				if (CstyleBehaviour.isMaybeInsertedClosing(cursor, line)) {
					closing = lang.stringRepeat("}", context.maybeInsertedBrackets);
					CstyleBehaviour.clearMaybeInsertedClosing();
				}
				var rightChar = line.substring(cursor.column, cursor.column + 1);
				if (rightChar === '}') {
					var openBracePos = session.findMatchingBracket({
						row : cursor.row,
						column : cursor.column + 1
					}, '}');
					if (!openBracePos)
						return null;
					var next_indent = this.$getIndent(session.getLine(openBracePos.row));
				} else if (closing) {
					var next_indent = this.$getIndent(line);
				} else {
					CstyleBehaviour.clearMaybeInsertedClosing();
					return;
				}
				var indent = next_indent + session.getTabString();

				return {
					text : '\n' + indent + '\n' + next_indent + closing,
					selection : [ 1, indent.length, 1, indent.length ]
				};
			} else {
				CstyleBehaviour.clearMaybeInsertedClosing();
			}
		});

		this.add("braces", "deletion", function(state, action, editor, session, range) {
			var selected = session.doc.getTextRange(range);
			if (!range.isMultiLine() && selected == '{') {
				initContext(editor);
				var line = session.doc.getLine(range.start.row);
				var rightChar = line.substring(range.end.column, range.end.column + 1);
				if (rightChar == '}') {
					range.end.column++;
					return range;
				} else {
					context.maybeInsertedBrackets--;
				}
			}
		});

		this.add("parens", "insertion", function(state, action, editor, session, text) {
			if (text == '(') {
				initContext(editor);
				var selection = editor.getSelectionRange();
				var selected = session.doc.getTextRange(selection);
				if (selected !== "" && editor.getWrapBehavioursEnabled()) {
					return {
						text : '(' + selected + ')',
						selection : false
					};
				} else if (CstyleBehaviour.isSaneInsertion(editor, session)) {
					CstyleBehaviour.recordAutoInsert(editor, session, ")");
					return {
						text : '()',
						selection : [ 1, 1 ]
					};
				}
			} else if (text == ')') {
				initContext(editor);
				var cursor = editor.getCursorPosition();
				var line = session.doc.getLine(cursor.row);
				var rightChar = line.substring(cursor.column, cursor.column + 1);
				if (rightChar == ')') {
					var matching = session.$findOpeningBracket(')', {
						column : cursor.column + 1,
						row : cursor.row
					});
					if (matching !== null && CstyleBehaviour.isAutoInsertedClosing(cursor, line, text)) {
						CstyleBehaviour.popAutoInsertedClosing();
						return {
							text : '',
							selection : [ 1, 1 ]
						};
					}
				}
			}
		});

		this.add("parens", "deletion", function(state, action, editor, session, range) {
			var selected = session.doc.getTextRange(range);
			if (!range.isMultiLine() && selected == '(') {
				initContext(editor);
				var line = session.doc.getLine(range.start.row);
				var rightChar = line.substring(range.start.column + 1, range.start.column + 2);
				if (rightChar == ')') {
					range.end.column++;
					return range;
				}
			}
		});

		this.add("brackets", "insertion", function(state, action, editor, session, text) {
			if (text == '[') {
				initContext(editor);
				var selection = editor.getSelectionRange();
				var selected = session.doc.getTextRange(selection);
				if (selected !== "" && editor.getWrapBehavioursEnabled()) {
					return {
						text : '[' + selected + ']',
						selection : false
					};
				} else if (CstyleBehaviour.isSaneInsertion(editor, session)) {
					CstyleBehaviour.recordAutoInsert(editor, session, "]");
					return {
						text : '[]',
						selection : [ 1, 1 ]
					};
				}
			} else if (text == ']') {
				initContext(editor);
				var cursor = editor.getCursorPosition();
				var line = session.doc.getLine(cursor.row);
				var rightChar = line.substring(cursor.column, cursor.column + 1);
				if (rightChar == ']') {
					var matching = session.$findOpeningBracket(']', {
						column : cursor.column + 1,
						row : cursor.row
					});
					if (matching !== null && CstyleBehaviour.isAutoInsertedClosing(cursor, line, text)) {
						CstyleBehaviour.popAutoInsertedClosing();
						return {
							text : '',
							selection : [ 1, 1 ]
						};
					}
				}
			}
		});

		this.add("brackets", "deletion", function(state, action, editor, session, range) {
			var selected = session.doc.getTextRange(range);
			if (!range.isMultiLine() && selected == '[') {
				initContext(editor);
				var line = session.doc.getLine(range.start.row);
				var rightChar = line.substring(range.start.column + 1, range.start.column + 2);
				if (rightChar == ']') {
					range.end.column++;
					return range;
				}
			}
		});

		this.add("string_dquotes", "insertion", function(state, action, editor, session, text) {
			if (text == '"' || text == "'") {
				initContext(editor);
				var quote = text;
				var selection = editor.getSelectionRange();
				var selected = session.doc.getTextRange(selection);
				if (selected !== "" && selected !== "'" && selected != '"' && editor.getWrapBehavioursEnabled()) {
					return {
						text : quote + selected + quote,
						selection : false
					};
				} else if (!selected) {
					var cursor = editor.getCursorPosition();
					var line = session.doc.getLine(cursor.row);
					var leftChar = line.substring(cursor.column - 1, cursor.column);
					var rightChar = line.substring(cursor.column, cursor.column + 1);

					var token = session.getTokenAt(cursor.row, cursor.column);
					var rightToken = session.getTokenAt(cursor.row, cursor.column + 1);
					if (leftChar == "\\" && token && /escape/.test(token.type))
						return null;

					var stringBefore = token && /string/.test(token.type);
					var stringAfter = !rightToken || /string/.test(rightToken.type);

					var pair;
					if (rightChar == quote) {
						pair = stringBefore !== stringAfter;
					} else {
						if (stringBefore && !stringAfter)
							return null; // wrap string with different quote
						if (stringBefore && stringAfter)
							return null; // do not pair quotes inside strings
						var wordRe = session.$mode.tokenRe;
						wordRe.lastIndex = 0;
						var isWordBefore = wordRe.test(leftChar);
						wordRe.lastIndex = 0;
						var isWordAfter = wordRe.test(leftChar);
						if (isWordBefore || isWordAfter)
							return null; // before or after alphanumeric
						if (rightChar && !/[\s;,.})\]\\]/.test(rightChar))
							return null; // there is rightChar and it isn't closing
						pair = true;
					}
					return {
						text : pair ? quote + quote : "",
						selection : [ 1, 1 ]
					};
				}
			}
		});

		this.add("string_dquotes", "deletion", function(state, action, editor, session, range) {
			var selected = session.doc.getTextRange(range);
			if (!range.isMultiLine() && (selected == '"' || selected == "'")) {
				initContext(editor);
				var line = session.doc.getLine(range.start.row);
				var rightChar = line.substring(range.start.column + 1, range.start.column + 2);
				if (rightChar == selected) {
					range.end.column++;
					return range;
				}
			}
		});

	};

	CstyleBehaviour.isSaneInsertion = function(editor, session) {
		var cursor = editor.getCursorPosition();
		var iterator = new TokenIterator(session, cursor.row, cursor.column);
		if (!this.$matchTokenType(iterator.getCurrentToken() || "text", SAFE_INSERT_IN_TOKENS)) {
			var iterator2 = new TokenIterator(session, cursor.row, cursor.column + 1);
			if (!this.$matchTokenType(iterator2.getCurrentToken() || "text", SAFE_INSERT_IN_TOKENS))
				return false;
		}
		iterator.stepForward();
		return iterator.getCurrentTokenRow() !== cursor.row || this.$matchTokenType(iterator.getCurrentToken() || "text", SAFE_INSERT_BEFORE_TOKENS);
	};

	CstyleBehaviour.$matchTokenType = function(token, types) {
		return types.indexOf(token.type || token) > -1;
	};

	CstyleBehaviour.recordAutoInsert = function(editor, session, bracket) {
		var cursor = editor.getCursorPosition();
		var line = session.doc.getLine(cursor.row);
		if (!this.isAutoInsertedClosing(cursor, line, context.autoInsertedLineEnd[0]))
			context.autoInsertedBrackets = 0;
		context.autoInsertedRow = cursor.row;
		context.autoInsertedLineEnd = bracket + line.substr(cursor.column);
		context.autoInsertedBrackets++;
	};

	CstyleBehaviour.recordMaybeInsert = function(editor, session, bracket) {
		var cursor = editor.getCursorPosition();
		var line = session.doc.getLine(cursor.row);
		if (!this.isMaybeInsertedClosing(cursor, line))
			context.maybeInsertedBrackets = 0;
		context.maybeInsertedRow = cursor.row;
		context.maybeInsertedLineStart = line.substr(0, cursor.column) + bracket;
		context.maybeInsertedLineEnd = line.substr(cursor.column);
		context.maybeInsertedBrackets++;
	};

	CstyleBehaviour.isAutoInsertedClosing = function(cursor, line, bracket) {
		return context.autoInsertedBrackets > 0 && cursor.row === context.autoInsertedRow && bracket === context.autoInsertedLineEnd[0] && line.substr(cursor.column) === context.autoInsertedLineEnd;
	};

	CstyleBehaviour.isMaybeInsertedClosing = function(cursor, line) {
		return context.maybeInsertedBrackets > 0 && cursor.row === context.maybeInsertedRow && line.substr(cursor.column) === context.maybeInsertedLineEnd && line.substr(0, cursor.column) == context.maybeInsertedLineStart;
	};

	CstyleBehaviour.popAutoInsertedClosing = function() {
		context.autoInsertedLineEnd = context.autoInsertedLineEnd.substr(1);
		context.autoInsertedBrackets--;
	};

	CstyleBehaviour.clearMaybeInsertedClosing = function() {
		if (context) {
			context.maybeInsertedBrackets = 0;
			context.maybeInsertedRow = -1;
		}
	};

	oop.inherits(CstyleBehaviour, Behaviour);

	exports.CstyleBehaviour = CstyleBehaviour;
});

ace.define("ace/mode/folding/cstyle", [ "require", "exports", "module", "ace/lib/oop", "ace/range", "ace/mode/folding/fold_mode" ], function(require, exports, module) {
	"use strict";

	var oop = require("../../lib/oop");
	var Range = require("../../range").Range;
	var BaseFoldMode = require("./fold_mode").FoldMode;

	var FoldMode = exports.FoldMode = function(commentRegex) {
		if (commentRegex) {
			this.foldingStartMarker = new RegExp(this.foldingStartMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.start));
			this.foldingStopMarker = new RegExp(this.foldingStopMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.end));
		}
	};
	oop.inherits(FoldMode, BaseFoldMode);

	(function() {

		this.foldingStartMarker = /(\{|\[)[^\}\]]*$|^\s*(\/\*)/;
		this.foldingStopMarker = /^[^\[\{]*(\}|\])|^[\s\*]*(\*\/)/;
		this.singleLineBlockCommentRe = /^\s*(\/\*).*\*\/\s*$/;
		this.tripleStarBlockCommentRe = /^\s*(\/\*\*\*).*\*\/\s*$/;
		this.startRegionRe = /^\s*(\/\*|\/\/)#region\b/;
		this._getFoldWidgetBase = this.getFoldWidget;
		this.getFoldWidget = function(session, foldStyle, row) {
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

		this.getFoldWidgetRange = function(session, foldStyle, row, forceMultiline) {
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

		this.getSectionRange = function(session, row) {
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

		this.getCommentRegionBlock = function(session, line, row) {
			var startColumn = line.search(/\s*$/);
			var maxRow = session.getLength();
			var startRow = row;

			var re = /^\s*(?:\/\*|\/\/)#(end)?region\b/;
			var depth = 1;
			while (++row < maxRow) {
				line = session.getLine(row);
				var m = re.exec(line);
				if (!m)
					continue;
				if (m[1])
					depth--;
				else
					depth++;

				if (!depth)
					break;
			}

			var endRow = row;
			if (endRow > startRow) {
				return new Range(startRow, startColumn, endRow, line.length);
			}
		};

	}).call(FoldMode.prototype);

});

ace.define('ace/mode/xill', [ "ace/mode/folding/cstyle", "ace/mode/behaviour/cstyle" ], function(require, exports, module) {

	var oop = require("ace/lib/oop");
	var TextMode = require("ace/mode/text").Mode;
	var Tokenizer = require("ace/tokenizer").Tokenizer;
	var XillHighlightRules = require("ace/mode/xill_highlight_rules").XillHighlightRules;
	var CstyleBehaviour = require("./behaviour/cstyle").CstyleBehaviour;
	var CStyleFoldMode = require("./folding/cstyle").FoldMode;

	var Mode = function() {
		this.$highlightRules = new XillHighlightRules();
		this.$behaviour = new CstyleBehaviour();
		this.$tokenizer = new Tokenizer(new XillHighlightRules().getRules());
		this.foldingRules = new CStyleFoldMode();
	};
	oop.inherits(Mode, TextMode);

	(function() {
		// Extra logic goes here. (see below)
	}).call(Mode.prototype);

	exports.Mode = Mode;
});

ace.define('ace/mode/xill_highlight_rules', function(require, exports, module) {

	var oop = require("ace/lib/oop");
	var TextHighlightRules = require("ace/mode/text_highlight_rules").TextHighlightRules;

	var escapedRe = "\\\\(?:x[0-9a-fA-F]{2}|" + // hex
	"u[0-9a-fA-F]{4}|" + // unicode
	"[0-2][0-7]{0,2}|" + // oct
	"3[0-6][0-7]?|" + // oct
	"37[0-7]?|" + // oct
	"[4-7][0-7]?|" + // oct
	".)";

	var identifierRe = "[a-zA-Z\\$_\u00a1-\uffff][a-zA-Z\\d\\$_\u00a1-\uffff]*\\b";

	var XillHighlightRules = function() {
		var keywordMapper = this.createKeywordMapper({
			"keyword" : highlightSettings.getKeywords(),
			"buildin" : highlightSettings.getBuiltins(),
			"language.constant": "true|false|null|ATOMIC|LIST|OBJECT"
		}, "identifier");

		this.$rules = {
			"no_regex" : [ {
				token : "comment",
				regex : "\\/\\/",
				next : "line_comment"
			}, {
				token : "comment", // multi line comment
				regex : /\/\*/,
				next : "comment"
			}, {
				token : "string",
				regex : "'",
				next : "qstring"
			}, {
				token : "string",
				regex : '"',
				next : "qqstring"
			}, {
				token : "constant.numeric", // hex
				regex : /0[xX][0-9a-fA-F]+\b/
			}, {
				token : "constant.numeric", // float
				regex : /[+-]?\d+(?:(?:\.\d*)?(?:[eE][+-]?\d+)?)?\b/
			}, {
				token : [ "support.constant" ],
				regex : /that\b/
			}, {
				token : [ "storage.type", "punctuation.operator", "support.function.firebug" ],
				regex : /(console)(\.)(warn|info|log|error|time|trace|timeEnd|assert)\b/
			}, {
				token : "keyword.operator",
				regex : /--|\+\+|===|==|=|!=|!==|<=|>=|<<=|>>=|>>>=|<>|<|>|!|&&|\|\||\?\:|[!$%&*+\-~\/^]=?/,
				next : "start"
			}, {
				token : "punctuation.operator",
				regex : /[?:,;.]/,
				next : "start"
			}, {
				token : "paren.lparen",
				regex : /[\[({]/,
				next : "start"
			}, {
				token : "paren.rparen",
				regex : /[\])}]/
			}, {
				token : "comment",
				regex : /^#!.*$/
			}, {
				token : keywordMapper,
				regex : identifierRe
			} ],
			"start" : [ {
				token : "comment", // multi line comment
				regex : "\\/\\*",
				next : "comment_regex_allowed"
			}, {
				token : "comment",
				regex : "\\/\\/",
				next : "line_comment_regex_allowed"
			}, {
				token : "text",
				regex : "\\s+|^$",
				next : "start"
			}, {
				token : "empty",
				regex : "",
				next : "no_regex"
			} ],
			"comment_regex_allowed" : [ {
				token : "comment",
				regex : "\\*\\/",
				next : "start"
			}, {
				defaultToken : "comment",
				caseInsensitive : true
			} ],
			"comment" : [ {
				token : "comment",
				regex : "\\*\\/",
				next : "no_regex"
			}, {
				defaultToken : "comment",
				caseInsensitive : true
			} ],
			"line_comment_regex_allowed" : [ {
				token : "comment",
				regex : "$|^",
				next : "start"
			}, {
				defaultToken : "comment",
				caseInsensitive : true
			} ],
			"line_comment" : [ {
				token : "comment",
				regex : "$|^",
				next : "no_regex"
			}, {
				defaultToken : "comment",
				caseInsensitive : true
			} ],
			"qqstring" : [ {
				token : "constant.language.escape",
				regex : escapedRe
			}, {
				token : "string",
				regex : "\\\\$",
				next : "qqstring"
			}, {
				token : "string",
				regex : '"',
				next : "no_regex"
			}, {
				defaultToken : "string"
			} ],
			"qstring" : [ {
				token : "constant.language.escape",
				regex : escapedRe
			}, {
				token : "string",
				regex : "\\\\$",
				next : "qstring"
			}, {
				token : "string",
				regex : "'",
				next : "no_regex"
			}, {
				defaultToken : "string"
			} ]
		};

	}

	oop.inherits(XillHighlightRules, TextHighlightRules);

	exports.XillHighlightRules = XillHighlightRules;
});

function SmartBreakpoints() {
	this.setBreakpointsAtRows = function(rows) {
		this.$breakpoints = [];
		for (var i = 0; i < rows.length; i++) {
			this.$breakpoints[rows[i]] = true;
		}
		this.onChangeBreakPoint();
	};
	this.setBreakpoints = function(bp) {
		this.$breakpoints = bp;
		this.onChangeBreakPoint();
	};
	this.clearBreakpoints = function() {
		this.$breakpoints = [];
		this.onChangeBreakPoint();
	};

	this.setBreakpoint = function(row, val) {
		this.$breakpoints[row] = val || true;
		this.onChangeBreakPoint();
	};

	this.clearBreakpoint = function(row) {
		delete this.$breakpoints[row];
		this.onChangeBreakPoint();
	};

	this.getBreakpoints = function() {
		return this.$breakpoints;
	};

	this._delayedDispatchEvent = function(eventName, e, delay) {
		if (this[eventName + '_Timeout'] != null || !this._eventRegistry)
			return;

		var listeners = this._eventRegistry[eventName];
		if (!listeners || !listeners.length)
			return;

		var self = this;
		this[eventName + '_Timeout'] = setTimeout(function() {
			self[eventName + '_Timeout'] = null
			self._dispatchEvent(eventName, e)
		}, delay || 20)
	};

	this.updateDataOnDocChange = function(e) {
		//Push change to contenttools (The old name of Xill IDE)
		contenttools.codeChanged(contenttools.getAce().getValue());
		
		var delta = e.data;
		var range = delta.range;
		var len, firstRow, f1;

		if (range.end.row == range.start.row)
			return;

		if (delta.action == "insertText") {
			len = range.end.row - range.start.row
			firstRow = range.start.column == 0 ? range.start.row : range.start.row + 1;
		} else if (delta.action == "insertLines") {
			len = range.end.row - range.start.row;
			firstRow = range.start.row;
		} else if (delta.action == "removeText") {
			len = range.start.row - range.end.row;
			firstRow = range.start.row;
		} else if (delta.action == "removeLines") {
			len = range.start.row - range.end.row
			firstRow = range.start.row;
		}

		if (len > 0) {
			args = Array(len);
			args.unshift(firstRow, 0)
			this.$breakpoints.splice.apply(this.$breakpoints, args);
		} else if (len < 0) {
			var rem = this.$breakpoints.splice(firstRow + 1, -len);
			if (!this.$breakpoints[firstRow]) {
				for ( var oldBP in rem) {
					if (rem[oldBP]) {
						this.$breakpoints[firstRow] = rem[oldBP]
						break
					}
				}
			}
		}
		
		//Push new breakpoints to contenttools (old name of Xill IDE)
		if (typeof contenttools !== 'undefined') {
			contenttools.breakpointsChanged(this.$breakpoints);
		}
		
		
	}

	this.onGutterClick = function(e) {
		var className = e.domEvent.target.className
		if (className.indexOf('ace_fold-widget') < 0) {
			if (className.indexOf("ace_gutter-cell") != -1 && contenttools.getAce().isFocused()) {
				var row = e.getDocumentPosition().row;
				if (this.$breakpoints[row])
					this.clearBreakpoint(row, true);
				else
					this.setBreakpoint(row, true);
				e.stop()
			}
		}
	}
	
	this.onChangeBreakPoint = function() {
		//Push new breakpoints to contenttools (the old name of Xill IDE)
		if (typeof contenttools !== 'undefined') {
			contenttools.breakpointsChanged(this.$breakpoints);
		}
		
		this._dispatchEvent("changeBreakpoint", {});
	}

	contenttools.getAce().on('change', this.updateDataOnDocChange.bind(this));
	contenttools.getAce().on('gutterclick', this.onGutterClick.bind(this));
	this.clearBreakpoints();
};

EditSession = ace.require("ace/edit_session").EditSession;
SmartBreakpoints.call(contenttools.getAce().getSession());
