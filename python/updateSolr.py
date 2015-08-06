import sqlite3
import csv

dbfilepath = './movie.db'
rawdatafilepath = './inputfile'
con = sqlite3.connect(dbfilepath)
print 'start update solr process'
cursor = con.cursor()
cursor.execute('''SELECT * FROM like LIMIT 5''')
with open(rawdatafilepath, 'w') as rawdatafile:
    csvwriter = csv.writer(rawdatafile)
    for r in cursor:
        csvwriter.writerow([r[0],r[1],'like'])
    

    
