import sqlite3
import csv

moviesfilepath = '/home/lukas/Downloads/ml-latest-small/movies.csv'
ratingsfilepath = '/home/lukas/Downloads/ml-latest-small/ratings.csv'
tagsfilepath = '/home/lukas/Downloads/ml-latest-small/tags.csv'

con = sqlite3.connect('movie.db')
print 'start sqlite process'
c = con.cursor()
c.execute('''DROP TABLE movie''')
c.execute('''CREATE TABLE movie (id text, title text)''')

with open(moviesfilepath, 'r') as csvfile:
    csvreader = csv.reader(csvfile)
    for row in csvreader:
        if row[0] == 'movieId':
            print 'found header'
            continue
        movieid = unicode(row[0])
        title = unicode(row[1],errors='ignore')
        c.execute('INSERT INTO movie VALUES (?,?)',[movieid,title])
        
c.execute('''DROP TABLE like''')
c.execute('''CREATE TABLE like (userid integer, movieid text, timestamp text)''')
with open(ratingsfilepath, 'r') as csvfile:
    csvreader = csv.reader(csvfile)
    for row in csvreader:
        userid = int(row[0])
        movieid = unicode(row[1])
        rating = unicode(row[2])
        timestamp = unicode(row[3])
        c.execute('INSERT INTO like VALUES (?,?,?)',[userid,movieid,timestamp])

c.execute('''DROP TABLE tag''')
c.execute('''CREATE TABLE tag (userid text, movieid text, tag text, timestamp text)''')
with open(tagsfilepath) as csvfile:
    csvreader = csv.reader(csvfile)
    for row in csvreader:
        userid = unicode(row[0])
        movieid = unicode(row[1])
        tag = unicode(row[2],errors='ignore')
        timestamp = unicode(row[3])
        c.execute('INSERT INTO tag VALUES (?,?,?,?)',[userid,movieid,tag,timestamp])
        
con.commit()
con.close()
