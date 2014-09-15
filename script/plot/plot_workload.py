import sys, os, glob, csv, string
import numpy as np
import matplotlib.pyplot as plt

if len(sys.argv) < 4:
    print 'No input path, plot name and out path.'
    print 'python plot_proportion.py input_path plot_name output_path'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1]) + "/"
plotname = sys.argv[2]
outpath = os.path.abspath(sys.argv[3]) + "/"

print 'inputpath: ' + inputpath
print 'plotname: ' + plotname
print 'outpath: ' + outpath

cpu_path = glob.glob(inputpath + '/workload-cpu*')
mem_path = glob.glob(inputpath + '/workload-mem*')

result_cpuload_list = []
result_memload_list = []

for i in range(4177):
    result_cpuload_list.append(0.0)
    result_memload_list.append(0.0)

for pathvalue in cpu_path:
    print pathvalue
    cpu_reader = csv.reader(open(pathvalue, 'rb'))
    print 'loading items...'
    for item in cpu_reader:
        for j in range(len(item)):
            result_cpuload_list[j] = result_cpuload_list[j] + string.atof(item[j])
    print 'finish loading...'
    
for pathvalue in mem_path:
    print pathvalue
    mem_reader = csv.reader(open(pathvalue, 'rb'))
    print 'loading items...'
    for item in mem_reader:
        for j in range(len(item)):
            result_memload_list[j] = result_memload_list[j] + string.atof(item[j])
    print 'finish loading...'
     
fig, ax = plt.subplots()
     
plt.plot(result_cpuload_list, label='CPU Demand')
plt.plot(result_memload_list, label='MEM Demand')
     
# plt.tight_layout()
     
plt.xlabel('Time(day)')
plt.ylabel('Resource Demand')
plt.legend(loc='best', prop={'size':12})
     
plt.grid(True)    
ax.set_xticks(range(0, 5000, 144 * 4))
ax.set_xticklabels(range(0, 33, 4))
 
# plt.ylim(0, 1000)
     
plt.savefig(outpath + plotname, dpi=300)
plt.show()
