
//Needed libraries
var Range = ace.require('ace/range').Range;
var UndoManager = ace.require("ace/undomanager").UndoManager;
var Search = ace.require("ace/search").Search;

function loadEditor(){
	ace.require("ace/ext/language_tools");
	
	// Load the editor
	var editor = ace.edit("editor");
	contenttools.setAce(editor);
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
        var editor = contenttools.getAce();
        hl = editor.getSession().addMarker(new Range(line, 0, line, 1), "ace_" + type, "fullLine");
        editor.$highlights.push(hl);
        editor.scrollToLine(line, true);
    }
    editor.clearHighlight = function() {
        var editor = contenttools.getAce();
        editor.$highlights.forEach(function(entry) {
            editor.getSession().removeMarker(entry);
        });
        editor.$highlights = [];
    }

	///////////// SEARCHING /////////////

	// Search options.
	editor.$savedSearch = {
		needle: "",
		regex: false,
		caseSensitive: false
	};

	// Clear the occurrences.
    editor.clearSearch = function() {
		// Clear the highlighting.
		editor.session.highlight(null);
		editor.renderer.updateBackMarkers();
		// Remove the selection.
		var selection = editor.getSelectionRange();
		editor.moveCursorTo(selection.start.row, selection.start.column);
		editor.clearSelection();
    }

	// Save and execute the search.
    editor.findOccurrences = function(needle, regex, caseSensitive) {
        // Save the search settings.
        editor.$savedSearch = {
            needle: needle,
            regex: regex,
            caseSensitive: caseSensitive
        };

        // Execute the search.
        return editor.doFind(false, false);
    }

    // Do the actual search.
    editor.doFind = function(backwards, skipCurrent) {
	    var options = {
	        // Given settings.
	        backwards: backwards,
	        skipCurrent: skipCurrent,
	        // Saved search settings.
	        needle: editor.$savedSearch.needle,
	        regExp: editor.$savedSearch.regex,
	        caseSensitive: editor.$savedSearch.caseSensitive,
	        // Constant settings.
	        wrap: true,
	        range: null
	    };

	    // Create a search object and find all ranges.
	    var s = new Search();
	    s.setOptions(options);
	    var ranges = s.findAll(editor.session);

		// Build the result.
        var result = { amount: ranges.length, index: 0 };

	    // Find, save the current hit.
        var f = editor.find(editor.$savedSearch.needle, options);
        if (f == null)
            return result;
        var current = f.start;

        for (var key in ranges) {
            var check = ranges[key].start;

            // Check if the highlight is the same as the current.
            if (current.row == check.row && current.column == check.column)
                result.index = parseInt(key);
        }

        return result;
    }

	///////////// END OF SEARCHING /////////////
	
	editor.getCurrentWord = function() { 
		var editor = contenttools.getAce();
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
	editor.commands.removeCommand('removeline');
	
	editor.getSession().setUndoManager(new UndoManager());
	editor.setOption("dragEnabled", false);
	
	// Add listeners for copy and cut
	var toClipboard = function(range) {
		var editor = contenttools.getAce();
		contenttools.copy();
	}
	editor.on('cut', toClipboard);
	editor.on('copy', toClipboard);

	// Duplicate the selected lines.
	editor.duplicateCurrentLines = function() {
		var range = editor.selection.getRange();
		editor.session.duplicateLines(range.start.row, range.end.row);
	};
}
