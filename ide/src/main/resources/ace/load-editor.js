
//Needed libraries
var Range = ace.require('ace/range').Range;
var UndoManager = ace.require("ace/undomanager").UndoManager;

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
        editor.scrollToLine(line);
    }
    editor.clearHighlight = function() {
        var editor = contenttools.getAce();
        editor.$highlights.forEach(function(entry) {
            editor.getSession().removeMarker(entry);
        });
        editor.$highlights = [];
    }

	///////////// SEARCHING /////////////

	// Occurrences.
	editor.$occurrences = [];
	// Search options.
	editor.$savedSearch = {
		needle: "",
		regex: false,
		caseSensitive: false
	};

	// Clear the occurrences.
    editor.clearOccurrences = function() {
		if (editor.$occurrences) {
			editor.$occurrences.forEach(function(entry) {
				editor.getSession().removeMarker(entry);
			});
		}
		editor.$occurrences = [];

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

        // Set the cursor to the beginning of the selection.
        // So that when successive searches are performed which match the current word, the current word stays selected.
        var selection = editor.getSelectionRange();
        editor.moveCursorTo(selection.start.row, selection.start.column);

        // Find all occurrences.
        var result = editor.countOccurrences();

        // Execute the search.
        editor.doFind(false, false);

        return result;
    }

    // Do the actual search.
    editor.doFind = function(backwards, skipCurrent) {
	    var options = {
	        // Given settings.
	        backwards: backwards,
	        skipCurrent: skipCurrent,
	        // Saved search settings.
	        regExp: editor.$savedSearch.regex,
	        caseSensitive: editor.$savedSearch.caseSensitive,
	        // Constant settings.
	        wrap: true,
	        range: null
	    };
        editor.find(editor.$savedSearch.needle, options);

        // Update the highlighting.
        editor.session.highlight(editor.$search.$options.re);
        editor.renderer.updateBackMarkers();
    }

    // Count the occurrences and get the index.
    editor.countOccurrences = function() {
        var result = {};

        // Save the cursor position.
        var pos = editor.getCursorPosition();

		// Find all occurrences.
        var options = {
        	// Saved search settings.
            regExp: editor.$savedSearch.regex,
            caseSensitive: editor.$savedSearch.caseSensitive,
            // Constant settings.
            wrap: true,
            range: null,
            backwards: false,
            skipCurrent: false
        };
        result.amount = editor.findAll(editor.$savedSearch.needle, options);

        // Find occurrences before the cursor, so we know the index.
        options.range = new Range(0, 0, pos.row, pos.column);
        result.index = editor.findAll(editor.$savedSearch.needle, options);

        // Reset the cursor position.
        editor.moveCursorToPosition(pos);
        editor.clearSelection();

        return result;
    }

	// Find the next match.
    editor.findNext = function() {
        editor.doFind(false, true);
    }

	// Find the previous match.
    editor.findPrevious = function() {
        editor.doFind(true, true);
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
	
	editor.getSession().setUndoManager(new UndoManager());
	editor.setOption("dragEnabled", false);
	
	// Add listeners for copy and cut
	var toClipboard = function(range) {
		var editor = contenttools.getAce();
		contenttools.copyToClipboard(editor.getSelectedText());
	}
	editor.on('cut', toClipboard);
	editor.on('copy', toClipboard);
}
