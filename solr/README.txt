Solr example
------------

This directory contains a Solr instance. The configuration creates a movielens core in order to save movies with their indicator.

To run this example:

  java -jar start.jar

in this example directory, and when Solr is started connect to 

  http://localhost:8983/solr/

To add documents to the index, use the script updateSolr.py in
the python/ subdirectory.
