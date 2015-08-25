#!/bin/bash

mvn clean compile assembly:single
java -jar target/evaluator-0.0.1-jar-with-dependencies.jar -precisionat 10 -connectionString http://localhost:8983/solr/movielens -ratings ../data/ml-latest-small/ratings.csv -tags ../data/tagid-itemid.csv
