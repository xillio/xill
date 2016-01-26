# Xill IDE - Change Log
All notable changes to this project will be documented in this file

##[3.0-Aristotle]
###Added

###Changed

###Fixed


##[3.0.RC4]
###Added
- Insecure option in the REST constructs
- (Un)escape XML constructs (formerly known as ampersandEncode)
- Auto-save in GUI

###Changed
- More informative messages in robot status bar
- Ctrl-F no longer closes the search bar, but moves focus to it

###Fixed
- Issue where focus would be lost on the project view [CTC-1254](https://xillio.atlassian.net/browse/CTC-1254)
- Stack trace on startup when project folder is missing [CTC-1295](https://xillio.atlassian.net/browse/CTC-1295)
- Random date corruption in database objects with multiple running robots [CTC-1317](https://xillio.atlassian.net/browse/CTC-1317)


##[3.0.RC3]
###Added
- Decode.fromPercent construct
- Add flag to support formulas in Excel.setCell()
- A list of keywords to the top of the help section

###Changed
- Links to external resources in help panel open in external web browser
- Error message for non-windows system using PhantomJS [CTC-714](https://xillio.atlassian.net/browse/CTC-714)
- Improved error message when trying to add key:value pair to list [CTC-1226](https://xillio.atlassian.net/browse/CTC-1226)

###Fixed
- Incorrect behavior of File.save() [CTC-1277](https://xillio.atlassian.net/browse/CTC-1277)
- Variable stack should not show shadowed variables [CTC-1224](https://xillio.atlassian.net/browse/CTC-1224)
- No compile exception on var+keyword in function [CTC-1268](https://xillio.atlassian.net/browse/CTC-1268)
