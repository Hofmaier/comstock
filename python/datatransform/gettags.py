import csv
inputfilepath = '/home/lukas/Downloads/ml-latest-small/tags.csv'
outputfilepath = '/home/lukas/comstock/data/tagid-itemid.csv'
outputfilepath2 = '/home/lukas/comstock/data/userid-tagid-timestamp.csv'
outputfilepath3 = '/home/lukas/comstock/data/tag-tagid.csv'

with open(inputfilepath, 'r') as csvfile:
    useritemtag = csv.reader(csvfile)
    s = set()
    nroflines = 0
    tagitemid = []
    useriditemidtagtimestamp = []
    for row in useritemtag:
        if row[1] == 'movieId':
            continue
        nroflines = nroflines + 1
        userid = row[0]
        itemid = row[1]
        tag = row[2]
        timestamp = row[3]
            
        s.update([tag])
        tagstritemid = [tag, itemid]
        useriditemidtagtimestamp.append([userid, itemid, tag, timestamp])
        tagitemid.append(tagstritemid)
        
    print 'nroflines: ' + str(nroflines)
    print 'nroftags ' + str(len(s))
    tag2id = dict([(tag,i) for i, tag in enumerate(s)])
        
    print 'tag adventure has tag ' + str(tag2id['adventure'])
    outputfile = open(outputfilepath, 'w')
    
    csvwriter = csv.writer(outputfile)

    outputfile3 = open(outputfilepath3, 'w')
    tagtagidwriter = csv.writer(outputfile3)
    for i, tag in enumerate(s):
        tagtagidwriter.writerow([tag,i])
    
    for row in tagitemid:
        tagid = tag2id[row[0]]
        itemid = row[1]
        newrow = [tagid, itemid, 1]

        csvwriter.writerow(newrow)

    outputfile2 = open(outputfilepath2, 'w')
    useridtagidwriter = csv.writer(outputfile2)
    
    for row in useriditemidtagtimestamp:
        if row[1] == 'movieId':
            continue
        userid = row[0]
        itemid = row[1]
        tag = row[2]
        timestamp = row[3]
        useridtagidwriter.writerow([userid, itemid, tag2id[tag], timestamp])
        
    outputfile.close()
    outputfile2.close()
