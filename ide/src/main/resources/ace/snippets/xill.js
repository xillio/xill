ace.define("ace/snippets/xill",["require","exports","module"], function(require, exports, module) {
"use strict";

exports.snippetText =
"# Function\n\
snippet function\n\
	/**\n\
	 * ${1:description}\n\
	 */\n\
	function ${2?:function_name}(${3:argument}) {\n\
		${4:// body...}\n\
	}\n\
# Use\n\
snippet use\n\
	use ${1?:package} as ${2:alias};\n\
# Include\n\
snippet inc\n\
	include ${1?:robotPath};\n\
snippet if\n\
	if (${1:true}) {\n\
		${0}\n\
	}\n\
# if ... else\n\
snippet ife\n\
	if (${1:true}) {\n\
		${2}\n\
	} else {\n\
		${0}\n\
	}\n\
# region\n\
snippet //{\n\
	// ${0:region} {\n\
	//} \n\
# return\n\
snippet ret\n\
	return ${1:result}\n\
# foreach (property in object ) { ... }\n\
snippet foreach\n\
	foreach (${1:value} in ${2:collection}) {\n\
		${0:}\n\
	}\n\
# while (condition) { ... }\n\
snippet while\n\
	while (${1:true}) {\n\
		${0:}\n\
	}\n\
";
exports.scope = "xill";

});
