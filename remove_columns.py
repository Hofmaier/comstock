f = open('/home/lukas/Downloads/ml-100k/u1.base', 'r')
output = open('/home/lukas/Documents/infile','w')
lines = list(f)
writelines = []
for line in lines[0:4]:
    words = line.split()
    writelines.append("\t".join(words[0:2]))

for w in writelines:
    output.write("%s\n" % w)

output.close()
