# Xill IDE - Change Log
All notable changes to this project will be documented in this file

##[3.1]
###Added
- Insecure option to the REST call constructs
- (Un)escape XML constructs
###Fixed
- Issue where focus would be lost on the project view [CTC-1254](https://xillio.atlassian.net/browse/CTC-1254)

##[3.0-Aristotle]
###Added

###Changed

###Fixed


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
