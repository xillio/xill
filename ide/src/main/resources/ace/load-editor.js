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
editor.xillBuildin = "";
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

editor.$occurences = [];
editor.clearOccurences = function() {
  if (editor.$occurences) {
    editor.$occurences.forEach(function(entry) {
    		editor.getSession().removeMarker(entry);
   	});
  }
  editor.$occurences = [];
} 
editor.findOccurences = function(needle, options) {
  options = options || {};
  options.needle = needle || options.needle;
  if (options.needle == undefined) {
      var range = editor.selection.isEmpty()
          ? editor.selection.getWordRange()
          : editor.selection.getRange();
      options.needle = editor.session.getTextRange(range);
  }    
  editor.$search.set(options);
  var occurence = (options.occurence == undefined ? 0 : options.occurence);
  
  var ranges = editor.$search.findAll(editor.session);
  if (!ranges.length)
      return 0;
  
  editor.$blockScrolling += 1;
  
  editor.clearOccurences();
  
  var scrollRange = null;
  for (var i = 0; i<ranges.length; i++ ) {
    if (i == occurence) {
      scrollRange = ranges[i];
    }
    hl = editor.getSession().addMarker(ranges[i],"ace_highlight","text");
    editor.$occurences.push(hl);
  }
  editor.$blockScrolling -= 1;
  
  if (scrollRange) {
    editor.navigateTo(scrollRange.end.row, scrollRange.end.column);
    editor.navigateTo(scrollRange.start.row, scrollRange.start.column);
  }
  
  return ranges.length;
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
