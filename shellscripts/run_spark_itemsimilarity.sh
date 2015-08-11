export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-amd64"
export MAHOUT_HOME=~/Downloads/mahout-distribution-0.10.1/
export SPARK_HOME=~/Downloads/spark-1.1.1/
export MASTER=spark://case:7077

infile="/home/lukas/Documents/infile/inputfile"
outfile="/home/lukas/Documents/outfile/"

/home/lukas/Downloads/mahout-distribution-0.10.0/bin/mahout spark-itemsimilarity --input $infile --output $outfile

#spark-itemsimilarity --input $infile --output $outfile
