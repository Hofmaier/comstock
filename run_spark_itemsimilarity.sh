export MAHOUT_HOME=~/Downloads/mahout-distribution-0.10.0/
export SPARK_HOME=~/Downloads/spark-1.3.0/
export MASTER=spark://case:7077

/home/lukas/Downloads/mahout-distribution-0.10.0/bin/mahout spark-itemsimilarity --input $infile --output $outfile
