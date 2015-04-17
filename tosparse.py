import sys

line1 = "1	1	5	874965758"
line2 = "1	2	3	876893171"
line3 = "2	1	4	888550871"
lines = [line1, line2, line3]

item_userrating = {}

if len(sys.argv) > 1:
    lines = list(open('/home/lukas/Downloads/ml-100k/u1.base', 'r'))

for l in lines:
    ll = l.split()
    if ll[1] in item_userrating:
        item_userrating[ll[1]].append((ll[0], ll[2]))
    else:
        item_userrating[ll[1]] = [(ll[0], ll[2])]        

writelines = []
for i, users in item_userrating.iteritems():
    itemidstr = str(i) + "\t"
    l = []
    for u in users:
        l.append(u[0] + ":" + u[1])
    ratings = " ".join(l)
    row = itemidstr + ratings
    writelines.append(row)
    

output = open('/home/lukas/Documents/sparse','w')
for w in writelines:
    output.write("%s\n" % w)

output.close()
