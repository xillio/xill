//Needed libraries
var Range = ace.require('ace/range').Range;
var UndoManager = ace.require("ace/undomanager").UndoManager;
ace.require("ace/ext/language_tools");

// Load the editor
var editor = ace.edit("editor");
editor.setTheme("ace/theme/chrome");
editor.getSession().setMode("ace/mode/xill");

// Extend the editor
editor.xillKeywords = "";
editor.xillBuildin = "use|as|include|if|else|while|foreach|var|function|return|continue|break|callbot|args";
editor.addKeyword = function(keyword) {
	if (this.xillKeywords == "")
		this.xillKeywords = keyword;
	else
		this.xillKeywords += "|" + keyword;
}
editor.addBuildin = function(keyword) {
	if (this.xillBuildin == "")
		this.xillBuildin = keyword;
	else
		this.xillBuildin += "|" + keyword;
}
editor.addACKeyword = function(keyword) {
}

editor.$highlights = [];
editor.highlight = function(line, type) {
	hl = editor.getSession().addMarker(new Range(line, 0, line, 1), "ace_" + type, "fullLine");
	editor.$highlights.push(hl);
	editor.scrollToLine(line);
}
editor.clearHighlight = function() {
	editor.$highlights.forEach(function(entry) {
		editor.getSession().removeMarker(entry);
	});
	editor.$highlights = [];
}

editor.clearSearch = function() {
	for ( var key in editor.getSession().getMarkers(false)) {
		entry = editor.getSession().$backMarkers[key];

		if (entry.clazz == "ace_selection")
			editor.getSession().removeMarker(key);
	}
}

editor.getCurrentWord = function() { 
	editor.selection.selectWord();
	return editor.getSelectedText();
}

editor.setOptions({
	enableBasicAutocompletion : true,
	enableSnippets : true,
	enableLiveAutocompletion : true
});

// Configure editor
editor.setScrollSpeed(0.03);

// Remove keybindings
editor.commands.removeCommand('find');
editor.commands.removeCommand('replace');

editor.getSession().setUndoManager(new UndoManager());
editor.setOption("dragEnabled", false);

// Add listeners for copy and cut
var toClipboard = function(range) {
	if (typeof contenttools !== 'undefined')
		contenttools.copyToClipboard(editor.getSelectedText());
}
editor.on('cut', toClipboard);
editor.on('copy', toClipboard);
