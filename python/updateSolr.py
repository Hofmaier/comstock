import sqlite3
import csv
import os
import pysolr
import json
import argparse

parser = argparse.ArgumentParser(
    description='Solr update utility',
    epilog="""Reads likes from a sqlite database
    and runs the spark-itemsimilarity job of mahout.
    Then it updates a Solr instance with the computed
    similiarities. The following environment variables
    must be set:
    MAHOUT_HOME: directory of the mahout installation.
    JAVA_HOME: directory of jdk
    SPARK_HOME: directory of Spark installation
    MASTER: url of spark master node(e.g. spark://localhost:7077""")

parser.add_argument("db", help='filepath to sqlite db')
parser.add_argument("solr", help='url of solr instance')
args = parser.parse_args()

dbfilepath = args.db
solrinstance = args.solr
rawdatafilepath = './inputfile'
sparkoutputdir = '/home/lukas/Documents/outfile'
mahouthome = os.getenv('MAHOUT_HOME')
print mahouthome
mahoutshellpath = '/bin/mahout spark-itemsimilarity'

#'/home/lukas/Downloads/apache-mahout-distribution-0.10.1/bin/mahout spark-itemsimilarity'

con = sqlite3.connect(dbfilepath)
print 'start update solr process'
cursor = con.cursor()
cursor.execute('''SELECT * FROM like''')
with open(rawdatafilepath, 'w') as rawdatafile:
    csvwriter = csv.writer(rawdatafile)
    for r in cursor:
        csvwriter.writerow([r[0],r[1],'like'])

#os.putenv('JAVA_HOME','/usr/lib/jvm/java-1.8.0-openjdk-amd64')
#os.putenv('MAHOUT_HOME','/home/lukas/Downloads/mahout-distribution-0.10.1/')
#os.putenv('SPARK_HOME','/home/lukas/Downloads/spark-1.3.1/')
#os.putenv('MASTER','spark://case:7077')

executellrcmd = mahoutshellpath + ' --input ' + rawdatafilepath + ' --output ' + sparkoutputdir
#os.system(executellrcmd)

solr = pysolr.Solr(solrinstance)
sparkoutputfile = sparkoutputdir + '/part0'
solrdocs = []
movies = {}
cursor.execute('''SELECT * FROM movie''')
for m in cursor:
    movies[m[0]]= {
        'id':m[0],
        'title':m[1],
        'payloads':''
        }
           
with open(sparkoutputfile, 'r') as similarityfile:
    lines = list(similarityfile)
    for line in lines:
        idsim = line.split('\\')
        mid = idsim[0]
        otheridsim = idsim[1].split(':')
        payloadstr = otheridsim[0] + '|' + otheridsim[1] + ' '
        if mid in movies:
            m = movies[mid]
            m['payloads'] = m['payloads'] + payloadstr
      
solr.delete(q='*:*')
l = [movie for k, movie in movies.iteritems()]
solr.add(l)


        
