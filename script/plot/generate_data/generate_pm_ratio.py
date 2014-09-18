# calculate the ratio of resource usage of every host

import sys, os, glob, csv, string, math
from numexpr.cpuinfo import cpu

if len(sys.argv) < 5:
    print 'No algorithm name, input path, out path, accuracy .'
    print 'python plot_proportion.py algorithm_name input_path output_path accuracy'
    sys.exit()

algorithm_name = sys.argv[1]
inputpath = os.path.abspath(sys.argv[2]) + "/"
outpath = os.path.abspath(sys.argv[3]) + "/"
accuracy = string.atof(sys.argv[4])

print 'algorithmname: ' + algorithm_name
print 'inputpath: ' + inputpath
print 'outpath: ' + outpath
print 'accuracy: ' + str(accuracy)
    
print 'generating....'

result_list = []
for i in range(0, int(1 / accuracy)):
    result_list.append(0)
    
print result_list

host_path = glob.glob(inputpath + '/*' + algorithm_name + '*hostlog')

for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = open(inputfile)
    print 'loading items...'
    
    counter = 0
    for item in reader:
        host_info_list = (item.split(':'))[1].split(',')
        for info in host_info_list:
            counter = counter + 1
            cpu = float((info.split('~'))[0])
            mem = float((info.split('~'))[1])
            ratio = min(cpu, mem) / max(cpu, mem)
            result_list[int(ratio // accuracy)] = result_list[int(ratio // accuracy)] + 1
    print 'loading finish' 

print result_list

csvfile = file(algorithm_name + '_' + str(accuracy) + '_result_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_list)
csvfile.close()

print 'generating finish'
    




