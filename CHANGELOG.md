# Xill IDE - Change Log
All notable changes to this project will be documented in this file

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
