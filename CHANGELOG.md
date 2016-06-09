# Xill Core, Codec, Collection, Date, File, Math, Stream, String, System plugins - Change Log
All notable changes to this project will be documented in this file

## [3.3.21] - 09-06-2016
### Fix
- Sometimes included libraries do not initialize variables [CTC-1631]

## [3.3.20] - 07-06-2016
### Change
- Split System.exec() output by line again [CTC-1466]

### Fix
- PhantomJS gives error after starting robot with library ten times [CTC-1543]
- `File.getText()` keeps stream open [CTC-1609]

## [3.3.19] - 26-05-2016
### Change
- Make error messages more descriptive by providing a template [CTC-1420]

### Fix
- `System.exec()` can make robots unstoppable [CTC-1582]
- Debugger variable view must show relevant scope in recursive function [CTC-1584]

### Add
- Configurable logging [CTC-1535]

## [3.3.18] - 23-05-2016
### Change
- Change File.getMimeType() to make use of internal mime type list [CTC-1477]

## [3.3.17] - 13-05-2016
### Fix
- Fix break of backward compatibility of API change in [CTC-1492]
 
## [3.3.16] - 09-05-2016
### Change
- Do not split System.exec() output by line [CTC-1466]

## [3.3.15] - 06-05-2016
### Fix
- Find all referenced libraries before initializing or closing them [CTC-1493]
- Fix `stream closed too early` [CTC-1502]
- Fix timeout on regex and make it threadsafe [CTC-1545]
- Do-fail block does not work on errors that are thrown inside an object declaration [CTC-1566]
- MySQL.connect fails from inside runBulk with "Unknown Internal Error" [CTC-1568] [CTC-1492]

### Add
- Add floor and ceiling construct [CTC-1530]

## [3.3.14] - 19-04-2016
### Fix
- `Date` package help files [CTC-1474]

## [3.3.13] - 14-04-2016
### Add
- `String.regexEscape()` construct to escape strings that will be part of a regular expression [CTC-1406]

### Change
- Move Concurrency plugin package to separate repository [CTC-1519] 

## [3.3.12] - 12-04-2016
### Change
- Errors from external programs run using `System.exec()` are not logged as Xill errors anymore [CTC-1465]

### Other
- Various maintenance tasks [CTC-1496][CTC-1414][CTC-1470]

## [3.3.11] - 30-03-2016
### Fix
- Variables are not disposed leading to write lock on files [CTC-1503]

## [3.3.10] - 30-03-2016
### Fix
- Foreach loop fails with inline `Stream.iterate()` [CTC-1438]
- Memory leak when using MySQL JDBC plugin [CTC-1500]

## [3.3.9] - 24-03-2016
### Add
- Concurrency package and pipeline processing [CTC-1454][CTC-1455][CTC-1456][CTC-1458][CTC-1459][CTC-1460]

## [3.3.8] - 23-03-2016
### Fix
- Line with a breakpoint is executed on stop [CTC-1409]

## [3.3.7] - 23-03-2016
### Change
- Check naming conventions [CTC-1396]
- Update xill language definition to version 3.1.3

## [3.3.6] - 22-3-2016
### Fix
- Remove BOM characters [CTC-1365]

## [3.3.5] - 22-3-2016
### Add
- `String.byteLength()` construct to determine length of a string in bytes [CTC-1279]

## [3.3.4] - 3-3-2016
### Add
- possibility to deprecate constructs [CTC-1297]

## [3.3.3] - 25-02-2016
### Fix
- List append operator not working [CTC-1417]
- Collection size wrong in debugger pane [CTC-1418]
- Unclear error message when variable used in function is declared after function call [CTC-1388]

## [3.3.2] - 24-02-2016
### Change
- For ATOMIC variables for all the basic operators (+, -, *, /, ^, %, ++, --, +=, -=, *=, /=):
if one of them (or both) is NOT a number return NaN. [CTC-1368]
- LIST and OBJECT variables also return NaN [CTC-1411]

## [3.1.2] - 24-02-2016
### Fix
- Issue where File.getLastModifiedDate returns the creation date

## [3.1.1] 11-02-2016

## [3.3.1] - 19-02-2016
### Add
- Hash.toSHA1 and Hash.toSHA256 constructs [CTC-1332]
- Collection.containsKey construct [CTC-1230]
- Extensions for xill bots and xill templates [CTC-380]

### Change
- Add a new stream api for data streaming [CTC-1373]
- Refactor Encode.toBase64, Decode.fromBase6, Hash.toMD5 to work with streams [CTC-1332]
- Refactor logging [CTC-1196]


## [3.1.1] - 11-02-2016
### Fix
- Disappearing variables in variable pane when debugging [CTC-1292]
- Implement the new file api with stream support [CTC-1327]

## [3.1.0] - 08-02-2016

### Add
- Error handling construction
- The runBulk expression to run parallel robots

### Fix
- Issue where lists would always evaluate to false [CTC-1315]
- Issue where circular references would cause a robot to crash [CTC-1166]

### Change
- Update xill_language to 3.1.1

## [3.0.0] - 28-01-2016
 - Initial release
