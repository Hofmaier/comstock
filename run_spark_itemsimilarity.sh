export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-amd64"
export MAHOUT_HOME=~/Downloads/mahout-distribution-0.10.1/
export SPARK_HOME=~/Downloads/spark-1.4.0/
export MASTER=spark://case:7077

infile="/home/lukas/Documents/infile"
outfile="/home/lukas/Documents/outfile/"

/home/lukas/Downloads/apache-mahout-distribution-0.10.1/bin/mahout spark-itemsimilarity --input $infile --output $outfile
