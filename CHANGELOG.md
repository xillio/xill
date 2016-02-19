# Xill IDE - Change Log
All notable changes to this project will be documented in this file

## [3.3.1] - unreleased

## [3.3.0] - 19-02-2016
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
