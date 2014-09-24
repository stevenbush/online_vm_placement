import os, glob, string, sys, traceback, csv

if len(sys.argv) < 4:
    print 'No input path and plot name specified.'
    print 'python plot_proportion.py input_path plot_name output_path'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1]) + "/"
plotname = sys.argv[2]
outpath = os.path.abspath(sys.argv[3]) + "/"

print 'inputpath: ' + inputpath
print 'plotname: ' + plotname
print 'outpath: ' + outpath

try:
    filepath = glob.glob(inputpath + '/*transformed_part*')
    ratio_list = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    simple_ratio_list = [0, 0]
    for pathvalue in filepath:
        name_path = pathvalue.split("/")
        file_name = name_path[len(name_path) - 1]
        print 'processing %s' % (file_name) 
        reader = csv.reader(open(pathvalue, 'rb'))
        print 'loading items...'
        for item in reader:
            cpu = string.atof(item[6])
            mem = string.atof(item[7])
            ratio = min(cpu, mem) / max(cpu, mem)
            # print item[6] + '-' + item[7] + '-ration: ' + str(ratio)
            index = min(int(ratio / 0.1), 9)
            # print index
            ratio_list[index] = ratio_list[index] + 1
            simple_index = min(int(ratio / 0.5), 1)
            simple_ratio_list[simple_index] = simple_ratio_list[simple_index] + 1
        print 'loading finish'
    print ratio_list
    print simple_ratio_list
    ratio_sum = 0
    for value in ratio_list:
        ratio_sum = ratio_sum + value
    print ratio_sum
    for i in range(len(ratio_list)):
        ratio_list[i] = (ratio_list[i] * 1.0) / ratio_sum
    print ratio_list
    simple_ratio_sum = simple_ratio_list[0] + simple_ratio_list[1]
    simple_ratio_list[0] = (simple_ratio_list[0] * 1.0) / simple_ratio_sum
    simple_ratio_list[1] = (simple_ratio_list[1] * 1.0) / simple_ratio_sum
    
    print simple_ratio_list
    print ratio_list
    
    csvfile = file('simple_workload_ration_percentage.csv', 'wb')
    writer = csv.writer(csvfile, delimiter="\n")
    writer.writerow(simple_ratio_list)
    csvfile.close()
 
    csvfile = file('workload_ration_percentage.csv', 'wb')
    writer = csv.writer(csvfile, delimiter="\n")
    writer.writerow(ratio_list)
    csvfile.close() 
    
except Exception, e:
    print e
    print traceback.format_exc()
