ace.define("ace/theme/chrome",["require","exports","module","ace/lib/dom"], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-chrome";
exports.cssText = ""; //See ../editor.css file

var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass);
});
