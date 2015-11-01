import sqlite3
import csv
import os
import shutil
import pysolr
import json
import argparse
import re

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
sparkoutputdir = './outfile'
likeindicatorfield = 'likeindicator'
tagindicatorfield = 'tagindicator'

mahouthome = os.getenv('MAHOUT_HOME')
mahoutshellpath = mahouthome + 'bin/mahout spark-itemsimilarity'

def line2indicators(line):
    idsims = line.split('\t')
    if len(idsims) > 1:
        mid = idsims[0]
        similarities = idsims[1]
        p = re.compile('\d*:\d*.\d*')
        allsims = p.findall(line)
        indicators = ''
        for idsim in allsims:
            idsiml = idsim.split(':')
            mid = idsiml[0]
            payloadstr = idsiml[0] + '|' + idsiml[1] + ' '
            indicators = indicators + payloadstr
        return (idsims[0],indicators)
    return (idsims[0].strip(),'')

def writeinputfile(table, con):
    cursor = con.cursor()
    sqlstmnt = '''SELECT * FROM {0}'''
    cursor.execute(sqlstmnt.format(table))
    with open(rawdatafilepath, 'w') as rawdatafile:
        csvwriter = csv.writer(rawdatafile)
        for r in cursor:
            csvwriter.writerow([r[0],r[1],'like'])

def startItemsimilarity():
    print 'start update solr process'
    executellrcmd = mahoutshellpath + ' --input ' + rawdatafilepath + ' --output ' + sparkoutputdir
    if os.path.isdir(sparkoutputdir):
        shutil.rmtree(sparkoutputdir)
    os.system(executellrcmd)

con = sqlite3.connect(dbfilepath)
writeinputfile('like',con)
startItemsimilarity()

solr = pysolr.Solr(solrinstance)
sparkoutputfile = sparkoutputdir + '/similarity-matrix/part-00000'
solrdocs = []
movies = {}
cursor = con.cursor()
cursor.execute('''SELECT * FROM movie''')
for m in cursor:
    titlestr = m[1]
    movies[m[0]]= {
        'id':m[0],
        'title':titlestr
        }

print sparkoutputfile
with open(sparkoutputfile, 'r') as similarityfile:
    lines = list(similarityfile)
    for line in lines:
        midindicator = line2indicators(line)
        movie = movies[midindicator[0]]
        movie[likeindicatorfield] = midindicator[1]

writeinputfile('tag',con)
startItemsimilarity()
with open(sparkoutputfile, 'r') as similarityfile:
    lines = list(similarityfile)
    for line in lines:
        midindicator = line2indicators(line)
        if midindicator[0] == 'movieId':
            pass
        else:
            movie = movies[midindicator[0]]
            movie[tagindicatorfield] = midindicator[1]
      
solr.delete(q='*:*')
l = [movie for k, movie in movies.iteritems()]
print str(len(l))
solr.add(l)

con.close()
