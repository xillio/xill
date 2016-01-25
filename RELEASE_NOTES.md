# Xill IDE - Release Notes

##[3.0-Aristotle]

###About this release
What you see before you was the product of years of effort. At first the brain child of a single brilliant mind and now 
a proudly finished piece of software. All finished? No there is still a tiny piece here or there that we would want to 
improve, some functionality that we are eager to deliver in the next release... There is always a next release, but 
first there is this release. And what a release it is! These are just some of the main features:

- a complete rewrite of the core
- a rewrite of the language (Xill 3), improving on the principles we have set before
- plugin package support
- Universal Data Model (UDM) supportd through the Document Package
- direct calling of webservices through the REST package
- new Web engine with better javascript support
- code completion
- compile error highlighting
- a searchable help system

What we are dedicated to is delivering an easy and fast way for consultants and developers to script all of their 
Content ETL needs. We support complex migrations and data transformations, in way fewer lines of code than any other 
scripting language. Our scripts are compiled, so they run fast and we offer connectivity to DB, Web, File systems and 
REST Webservices. The architecture is highly modular and you can create your own connectors for systems or even extension 
plugins for the language.

We believe that this feature packed release delivers on that promise and shows how far we have come since that first 
line of code was compiled a few years ago. By using this completely overhauled version of the Xill scripting language 
you commit yourself to a future of ever increasing productivity and possibilities in the Content ETL space.

###Documentation
see [support.xillio.com](http://support.xillio.com)

###Changes
see [CHANGELOG.md](CHANGELOG.md)

###Known Issues
- Xpath support in Web plugin is partly broken (when querying for attributes). As a workaround, try to split up complex 
xpath queries into multiple consecutive queries.
- New File and Open File buttons are disabled afer creating a new robot. Click on a robot in the project pane to enable
them.
- Robots may continue running for a short period after clicking stop or pause.
- You cannot currently use the comparison operators <, >, <= and >= on strings.
- Scrolling through a code example in the help does not work. Instead hold your mouse over the scroll bar.
- The debugger is slow when rendering large amounts of list or JSON object data for the preview.
- When editing code during debugging, variables may disappear from the debug pane.
- Breakpoints may move when pressing ENTER or DELETE on the same line
- Currently only one instance of Xill IDE can be running. Older instances would lose the ability to open robots.
- When the samples folder is missing an error might occur on startup, giving a stack trace.
- When you try to declare a variable inside a function, which already exists as a parameter, no error is thrown.
- Some help examples may not work without modification.
- When previewing huge XML files (>120MB) you may run out of memory.
- After a compile error the run button is still greyed out. Click Stop to enable it.
- On a Mac, some keyboard-shortcuts may not work, because they are used to type special characters.
- When using an extra graphics adapter, the notification dialog for changing files outside of the IDE may fail.
- Math.random() does not work without a parameter, call Math.random(null) instead.

###Credits
Copyright (C) 2010-2016 Xillio B.V. All Rights Reserved

####Architect
Ernst van Rheenen
####Head of development
Paul van der Zandt
####Product owner
Sjoerd Alkema
####Release Manager
Titus Nachbauer
####UI Designer
Ruben Pape
####Tester
Hatice Figge
####Programmers
Thomas Biesaart
Zbynek Hochmann
Xavier Pardonnet
Anwar Rahimbaks
Luca Scalzotto
Daan Knoope
Pieter Soels
Geert Konijnendijk
Ivor van der Hoog
Edward van Egdom
Folkert van Verseveld
####Consultants
Remco Steelink
Marijn van der Zaag
Mark de Wit
Tatiana Mamaliga
Jeroen Rombouts
Andrew Sutjahjo
