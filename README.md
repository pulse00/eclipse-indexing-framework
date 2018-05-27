Eclipse Indexing Framework
==========================

[![Build Status](https://secure.travis-ci.org/pulse00/eclipse-indexing-framework.png)](http://travis-ci.org/pulse00/eclipse-indexing-framework)

This eclipse plugin provides an indexing infrastructure for other plugins based on the 
[apache lucene](http://lucene.apache.org/core/) search engine.

It works by adding an IncrementalProjectBuilder to projects with the nature of bundles implementing
the `com.dubture.indexing.core.buildParticipant` extension point.

The implementing extension point will receive an `IResource` during the build and can then access an 
`IndexingRequestor` which can be used to store ReferenceInformation into a lucene index.

Using the `SearchEngine` utility class, this index can be queried later on.

The plugin is still alpha and its API will most likely change.