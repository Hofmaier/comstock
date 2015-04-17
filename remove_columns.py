import argparse

line1 = "1	1	5	874965758"
line2 = "1	2	3	876893171"
lines = [line1, line2]

parser = argparse.ArgumentParser()
parser.add_argument("task", help="set task remove_colum, tosparse")
args = parser.parse_args()
if args.task is "remove_c":
f = open('/home/lukas/Downloads/ml-100k/u1.base', 'r')
output = open('/home/lukas/Documents/infile','w')
lines = list(f)
writelines = []
for line in lines:
    words = line.split()
    writelines.append("\t".join(words[0:2]))

for w in writelines:
    output.write("%s\n" % w)

output.close()
