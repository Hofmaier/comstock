import sqlite3
import csv

moviesfilepath = '/home/lukas/Downloads/ml-latest-small/movies.csv'

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
        print row[0]
        print row[1]
        movieid = unicode(row[0])
        title = unicode(row[1],errors='ignore')
        c.execute('INSERT INTO movie VALUES (?,?)',[movieid,title])


con.commit()
con.close()
