import sys, os, glob, csv, string
import numpy as np
import matplotlib.pyplot as plt

if len(sys.argv) < 5:
    print 'No input path, plot name, out path and time period.'
    print 'python plot_proportion.py input_path plot_name output_path time_period'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1]) + "/"
plotname = sys.argv[2]
outpath = os.path.abspath(sys.argv[3]) + "/"
time_period = sys.argv[4]

print 'inputpath: ' + inputpath
print 'plotname: ' + plotname
print 'outpath: ' + outpath
print 'timeperiod: ' + time_period

cpu_path = glob.glob(inputpath + '/*raw_workload-cpu')
raw_cpuload_list = []
period_seconds = string.atoi(time_period)
    
for pathvalue in cpu_path:
    print pathvalue
    cpu_reader = csv.reader(open(pathvalue, 'rb'))
    print 'loading cpu items...'
    for item in cpu_reader:
        print len(item)
        for value in item:
            raw_cpuload_list.append(string.atof(value))
    print 'finish loading...'
    
print len(raw_cpuload_list)
print 'len(raw_cpuload_list): ' + str(len(raw_cpuload_list))

result_cpuload_list = []
for i in range((len(raw_cpuload_list) // period_seconds) + 1):
        result_cpuload_list.append(0.0)
print 'len(result_cpuload_list): ' + str(len(result_cpuload_list))

print 'plotting....'
for i in range(len(raw_cpuload_list)):
    index = i // period_seconds
    result_cpuload_list[index] = result_cpuload_list[index] + raw_cpuload_list[i]  
        
for i in range(len(result_cpuload_list)):
    result_cpuload_list[i] = result_cpuload_list[i] / period_seconds
    
period_seconds = string.atoi(time_period)
time_unit = 1000000
end_time = 2506198229513
result_host_list = []
raw_host_list = []

for i in range((end_time // time_unit) + 1):
    raw_host_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    result_host_list.append(0.0)
    
print 'len(raw_host_list): ' + str(len(raw_host_list))
print 'len(result_host_list): ' + str(len(result_host_list))

host_path = glob.glob(inputpath + '/*one*ORA*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    
    counter = 0
    for item in reader:
        timestamp = string.atoi(item[0]) // time_unit
        host_num = string.atoi(item[3])
        if raw_host_list[timestamp] == 0.0:
            counter = 1
        else:
            counter = counter + 1
        raw_host_list[timestamp] = (raw_host_list[timestamp] * (counter - 1) + host_num) * 1.0 / counter   
    print 'loading finish'
    
    
    previous_value = raw_host_list[0]
    for i in range(len(raw_host_list)):
        if raw_host_list[i] == 0.0:
            raw_host_list[i] = previous_value
        else:
            previous_value = raw_host_list[i]        
            
    for i in range(len(raw_host_list)):
        index = i // period_seconds
        result_host_list[index] = result_host_list[index] + raw_host_list[i]  
        
    for i in range(len(result_host_list)):
        result_host_list[i] = result_host_list[i] / period_seconds

bestfit_result_host_list = result_host_list
     
fig, ax = plt.subplots()
     
plt.plot(result_cpuload_list, label='Workload Demand')

plt.plot(bestfit_result_host_list, label='BestFit Host Demand')
     
# plt.tight_layout()
     
plt.xlabel('Time(day)')
plt.ylabel('Resource Demand')
plt.legend(loc='best', prop={'size':12})
     
plt.grid(True)    
ax.set_xticks(range(0, len(result_cpuload_list), 2 * (24 * 3600) / period_seconds))
ax.set_xticklabels(range(0, 1 + len(result_cpuload_list) / ((24 * 3600) / period_seconds), 2))
 
# plt.ylim(0, 1000)
     
plt.savefig(outpath + plotname, dpi=300)
plt.show()
