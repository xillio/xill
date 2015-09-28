
function loadEditors() {
	//Needed libraries
	var Range = ace.require('ace/range').Range;
	ace.require("ace/ext/language_tools");
	
	var counter = 0;
	var nodes = document.getElementsByClassName("code");
	
	for (var i = 0; i < nodes.length; i++) {
	    var node = nodes.item(i);
	    if (!node.id) {
	        node.id = "editor" + counter++;
	    }
	    loadEditor(node.id);
	}
}


function loadEditor(id) {
    console.log("Loading editor: " + id);
    // Create editor
    var editor = ace.edit(id);

    // Load syntax
    editor.setTheme("ace/theme/chrome");
    editor.getSession().setMode("ace/mode/xill");

    // Extend the editor
    editor.xillKeywords = "";
    editor.xillBuildin = "";

    // Auto height
    editor.setOptions({
        maxLines: Infinity
    });
    // Read Only
    editor.setReadOnly(true)
    editor.getSession().setUseWorker(false);
}