import csv
inputfilepath = '/home/lukas/Downloads/ml-latest-small/tags.csv'
outputfilepath = '/home/lukas/comstock/data/tagid-itemid.csv'

with open(inputfilepath, 'r') as csvfile:
    useritemtag = csv.reader(csvfile)
    s = set()
    nroflines = 0
    tagitemid = []
    for row in useritemtag:
        if row[1] == 'movieId':
            continue
        nroflines = nroflines + 1
        s.update([row[2]])
        tagstritemid = [row[2], row[1]]
        tagitemid.append(tagstritemid)
    print 'nroflines: ' + str(nroflines)
    print 'nroftags ' + str(len(s))
    tag2id = dict([(tag,i) for i, tag in enumerate(s)])
        
    print 'tag adventure has tag ' + str(tag2id['adventure'])
    outputfile = open(outputfilepath, 'w')
    
    csvwriter = csv.writer(outputfile)

    for row in tagitemid:
        tagid = tag2id[row[0]]
        itemid = row[1]
        newrow = [tagid, itemid, 1]
        print str(newrow)
        csvwriter.writerow(newrow)

    outputfile.close()
        
