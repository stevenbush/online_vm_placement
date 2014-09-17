import sys, os, glob, csv, string, traceback
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

period_seconds = string.atoi(time_period)

print 'plotting....'
    
period_seconds = string.atoi(time_period)
time_unit = 1000000
end_time = 2506198229513
result_cpuload_list = []
raw_cpuload_list = []
result_memload_list = []
raw_memload_list = []
    
print 'len(raw_cpuload_list): ' + str(len(raw_cpuload_list))
print 'len(result_cpuload_list): ' + str(len(result_cpuload_list))
print 'len(raw_memload_list): ' + str(len(raw_cpuload_list))
print 'len(result_memload_list): ' + str(len(result_cpuload_list))
    
host_path = glob.glob(inputpath + '/*one*ORA*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'

    cpu = file('result_cpuload_list.csv','w')
    mem = file('result_memload_list.csv','w')
    counter = 0
    for item in reader:
        print counter
        cpu.write(item[6]+'\n')
        mem.write(item[7]+'\n')
        counter = counter + 1
    print 'loading finish'
    cpu.close()
    mem.close()
    
csvfile = file('result_cpuload_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_cpuload_list)
csvfile.close()

csvfile = file('result_memload_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_memload_list)
csvfile.close()

##fig, ax = plt.subplots()
##
##plt.plot(result_cpuload_list, label='CPU Workload Demand')   
##
##plt.xlabel('Time(day)')
##plt.ylabel('CPU Resource Demand (every ' + str(string.atoi(time_period) // 3600) + ' hours)')
##plt.legend(loc='best', prop={'size':10})
##     
##plt.grid(True)    
##ax.set_xticks(range(0, len(result_cpuload_list), 2 * (24 * 3600) / period_seconds))
##ax.set_xticklabels(range(0, 1 + len(result_cpuload_list) / ((24 * 3600) / period_seconds), 2))
## 
### plt.ylim(0, 1000)
##     
##plt.savefig(outpath + 'CPU_' + plotname, dpi=300)
##plt.show()
##
##fig, ax = plt.subplots()
##
##plt.plot(result_memload_list, label='Memory Workload Demand')
##
##plt.xlabel('Time(day)')
##plt.ylabel('Memory Resource Demand (every ' + str(string.atoi(time_period) // 3600) + ' hours)')
##plt.legend(loc='best', prop={'size':10})
##     
##plt.grid(True)    
##ax.set_xticks(range(0, len(result_cpuload_list), 2 * (24 * 3600) / period_seconds))
##ax.set_xticklabels(range(0, 1 + len(result_cpuload_list) / ((24 * 3600) / period_seconds), 2))
## 
### plt.ylim(0, 1000)
##     
##plt.savefig(outpath + 'MEM_' + plotname, dpi=300)
##plt.show()
    
