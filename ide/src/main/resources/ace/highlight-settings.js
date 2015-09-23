
/**
 * Global settings for the highlighting of keywords and built-in plugins
 */ 
 
function HighlightSettings() {
	this.xillKeywords = "";
	this.xillBuildin = "";
	
	this.addKeyword = function(keyword) {
	if (this.xillKeywords == "")
		this.xillKeywords = keyword;
	else
		this.xillKeywords += "|" + keyword;
	};
	this.addBuildin = function(keyword) {
		if (this.xillBuildin == "")
			this.xillBuildin = keyword;
		else
			this.xillBuildin += "|" + keyword;
	};
}

var highlightSettings = new HighlightSettings();