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

try:
    cpu_path = glob.glob(inputpath + '/raw_workload-cpu')
    mem_path = glob.glob(inputpath + '/raw_workload-mem')

    raw_cpuload_list = []
    raw_memload_list = []
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
    
    for pathvalue in mem_path:
        print pathvalue
        mem_reader = csv.reader(open(pathvalue, 'rb'))
        print 'loading mem items...'
        print len(item)
        for item in mem_reader:
            for value in item:
                raw_memload_list.append(string.atof(value))
        print 'finish loading...'
        
    print len(raw_cpuload_list)
    print len(raw_memload_list)
    print 'len(raw_cpuload_list): ' + str(len(raw_cpuload_list))
    print 'len(raw_memload_list): ' + str(len(raw_memload_list))
    
    result_cpuload_list = []
    result_memload_list = []
    for i in range((len(raw_cpuload_list) // period_seconds) + 1):
        result_cpuload_list.append(0.0)
    for i in range((len(raw_memload_list) // period_seconds) + 1):
        result_memload_list.append(0.0)
    print 'len(result_cpuload_list): ' + str(len(result_cpuload_list))
    print 'len(result_memload_list): ' + str(len(result_memload_list))
    
    print 'plotting....'
    for i in range(len(raw_cpuload_list)):
        index = i // period_seconds
        result_cpuload_list[index] = result_cpuload_list[index] + raw_cpuload_list[i]  
        
    for i in range(len(result_cpuload_list)):
        result_cpuload_list[i] = result_cpuload_list[i] / period_seconds
        
    for i in range(len(raw_memload_list)):
        index = i // period_seconds
        result_memload_list[index] = result_memload_list[index] + raw_memload_list[i]  
        
    for i in range(len(result_memload_list)):
        result_memload_list[i] = result_memload_list[i] / period_seconds
    
    fig, ax = plt.subplots()

    plt.plot(result_cpuload_list, label='CPU Workload Demand')
    plt.plot(result_memload_list, label='MEM Workload Demand')
     
    # plt.tight_layout()
     
    plt.xlabel('Time(day)')
    plt.ylabel('Resource Demand')
    plt.legend(loc='best', prop={'size':12})
     
    plt.grid(True)    
    ax.set_xticks(range(0, len(result_memload_list), 2 * (24 * 3600) / period_seconds))
    ax.set_xticklabels(range(0, 1 + len(result_memload_list) / ((24 * 3600) / period_seconds), 2))
 
    # plt.ylim(0, 1000)
     
    plt.savefig(outpath + plotname, dpi=300)
    plt.show()
    
except Exception, e:
    print e
    print traceback.format_exc()
