import os, glob, string, sys, traceback, csv
import numpy as np
import matplotlib.pyplot as plt

if len(sys.argv) < 4:
    print 'No input file, out path and time period specified.'
    print 'python generate_workload.py input_file output_path time_period'
    sys.exit()

inputfile = os.path.abspath(sys.argv[1]) 
outpath = os.path.abspath(sys.argv[2]) + "/"
time_period = sys.argv[3]

print 'inputfile: ' + inputfile
print 'outpath: ' + outpath
print 'timeperiod: ' + time_period

try:
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
        
    fig, ax = plt.subplots()
     
    plt.plot(result_host_list, label='Host Demand')
          
    plt.xlabel('Time(day)')
    plt.ylabel('Resource Demand')
    plt.legend(loc='best', prop={'size':12})
     
    plt.grid(True)    
    ax.set_xticks(range(0, 5000, 144 * 4))
    ax.set_xticklabels(range(0, 33, 4))
 
    # plt.ylim(0, 1000)
     
    # plt.savefig(outpath + plotname, dpi=300)
    plt.show()             
#     raw_writer = csv.writer(open(outpath + 'raw-host-' + file_name, 'wb'))
#     result_writer = csv.writer(open(outpath + 'result-host-' + file_name, 'wb'))
#     raw_writer.writerow(raw_host_list)
#     result_writer.writerow(result_host_list)
    
                   
except Exception, e:
    print e
    print traceback.format_exc()
