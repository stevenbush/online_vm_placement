import sys, os, glob, csv, string, traceback

if len(sys.argv) < 2:
    print 'No input path specified'
    print 'python plot_proportion.py input_path'
    sys.exit()
    
inputpath = os.path.abspath(sys.argv[1]) + "/"

print 'inputpath: ' + inputpath

try:
    cpu_path = glob.glob(inputpath + '/raw_workload-cpu*')
    mem_path = glob.glob(inputpath + '/raw_workload-mem*')
    
    time_unit = 1000000
    end_time = 2506198229513
    raw_cpuload_list = []
    raw_memload_list = []
    for i in range((end_time // time_unit) + 1):
        raw_cpuload_list.append(float(0))
        raw_memload_list.append(float(0))
    print len(raw_cpuload_list)
    print len(raw_memload_list)
        
    for pathvalue in cpu_path:
        name_list = pathvalue.split("/")
        file_name = name_list[len(name_list) - 1]
        print 'processing %s' % (file_name) 
        reader = csv.reader(open(pathvalue, 'rb'))
        print 'loading cpu items...'
        for item in reader:
            for i in range(len(item)):
                raw_cpuload_list[i]=raw_cpuload_list[i]+string.atof(item[i])
        print 'finish loading...'
        
    for pathvalue in mem_path:
        name_list = pathvalue.split("/")
        file_name = name_list[len(name_list) - 1]
        print 'processing %s' % (file_name) 
        reader = csv.reader(open(pathvalue, 'rb'))
        print 'loading mem items...'
        for item in reader:
            for i in range(len(item)):
                raw_memload_list[i]=raw_memload_list[i]+string.atof(item[i])
        print 'finish loading...'
        
    cpu_writer = csv.writer(open(inputpath + 'raw_workload-cpu', 'wb'))
    mem_writer = csv.writer(open(inputpath + 'raw_workload-mem', 'wb'))
    cpu_writer.writerow(raw_cpuload_list)
    mem_writer.writerow(raw_memload_list)
        
except Exception, e:
    print e
    print traceback.format_exc()