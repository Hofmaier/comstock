#!/bin/bash

export MAHOUT_HOME=../apache-mahout-distribution-0.10.1/
export SPARK_HOME=../spark-1.1.1/
export MASTER=spark://case:7077

python updateSolr.py ../data/movie.db http://localhost:8983/solr/movielens

