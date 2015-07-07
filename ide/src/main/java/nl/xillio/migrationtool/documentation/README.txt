README.

How the classes work:

FunctionDocument:
is the actual documentation which contains the data and has one conversion method:
+ HTML toHTML() which creates and returns a HTML page.

StringTuple:
Java does not have tuples

XMLparser:
is a class which contains one public method:
+ FunctionDocument parseXML(string url) which parses the xml at the given url and returns a functiondocument.

DocumentSearcher:
is a class which can query a given client with the following method:
+ string[] search(String query, Client c) which gets a query and a client to search and returns an array with the unique names of the functionDocuments.
+ IndexResponse index(FunctionDocument func) which indexes a functiondocument. Usually cast into the void.

XML_Format_Handler:
is a class contained in XML parser.