import os, glob, string, sys, traceback, csv

if len(sys.argv) < 4:
    print 'No input job file, out path and time period specified.'
    print 'python generate_workload.py input_job_file output_path time_period'
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
    result_cpuload_list = []
    result_memload_list = []
    raw_cpuload_list = []
    raw_memload_list = []
    for i in range((end_time // time_unit) + 1):
        raw_cpuload_list.append(float(0))
        raw_memload_list.append(float(0))
    for i in range(((end_time // time_unit) // period_seconds) + 1):
        result_cpuload_list.append(float(0))
        result_memload_list.append(float(0))
    # print 'len(raw_cpuload_list): ' + str(len(raw_cpuload_list))
    # print 'len(result_cpuload_list): ' + str(len(result_cpuload_list))
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    for item in reader:
        events_list = []
        for event in item:
            value_list = event.split('~')
            event_info = []
            event_info.append(string.atoi(value_list[0]) // time_unit)  # time
            event_info.append(string.atof(value_list[4]))  # CPU
            event_info.append(string.atof(value_list[5]))  # MEM
            events_list.append(event_info)
        # print events_list
            
        for i in range(len(events_list) - 1):
            for j in range(events_list[i][0], events_list[i + 1][0]):
                raw_cpuload_list[j] = raw_cpuload_list[j] + events_list[i][1]      
                raw_memload_list[j] = raw_memload_list[j] + events_list[i][2]    
            
    print 'loading finish'
    
    # print len(raw_cpuload_list)
    for i in range(len(raw_cpuload_list)):
        index = i // period_seconds
        result_cpuload_list[index] = result_cpuload_list[index] + raw_cpuload_list[i]
    for i in range(len(raw_memload_list)):
        index = i // period_seconds
        result_memload_list[index] = result_memload_list[index] + raw_memload_list[i]
        
    for i in range(len(result_cpuload_list)):
        result_cpuload_list[i] = result_cpuload_list[i] / period_seconds
        
    for i in range(len(result_memload_list)):
        result_memload_list[i] = result_memload_list[i] / period_seconds
        
    cpu_writer = csv.writer(open(outpath + 'workload-cpu-' + file_name, 'wb'))
    mem_writer = csv.writer(open(outpath + 'workload-mem-' + file_name, 'wb'))
    cpu_writer.writerow(result_cpuload_list)
    mem_writer.writerow(result_memload_list)
    
                   
except Exception, e:
    print e
    print traceback.format_exc()
