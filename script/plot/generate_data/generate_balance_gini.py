# calculate the Gini coefficient in every moment

import sys, os, glob, csv, string, math

def gini1(values):
    raw_sum = sum(values)
    average = raw_sum * 1.0 / len(values)
    diff_sum = 0
    for a in values:
        for b in values:
            diff_sum = diff_sum + abs(a - b)
    gini = (diff_sum * 1.0) / (2.0 * average * len(values) * len(values))
    return gini

def gini2(values):
    '''
    Calculate Gini index, Gini coefficient, Robin Hood index, and points of 
    Lorenz curve based on the instructions given in 
    www.peterrosenmai.com/lorenz-curve-graphing-tool-and-gini-coefficient-calculator
    Lorenz curve values as given as lists of x & y points [[x1, x2], [y1, y2]]
    @param values: List of values
    @return: [Gini index, Gini coefficient, Robin Hood index, [Lorenz curve]] 
    '''
    n = len(values)
    assert(n > 0), 'Empty list of values'
    sortedValues = sorted(values)  # Sort smallest to largest
    
    # Find cumulative totals
    cumm = [0]
    for i in range(n):
        cumm.append(sum(sortedValues[0:(i + 1)]))
    # Calculate Lorenz points
    LorenzPoints = [[], []]
    sumYs = 0  # Some of all y values
    robinHoodIdx = -1  # Robin Hood index max(x_i, y_i)
    for i in range(1, n + 2):
        x = 100.0 * (i - 1) / n
        y = 100.0 * (cumm[i - 1] / float(cumm[n]))
        LorenzPoints[0].append(x)
        LorenzPoints[1].append(y)
        sumYs += y
        maxX_Y = x - y
        if maxX_Y > robinHoodIdx: robinHoodIdx = maxX_Y   
    
    giniIdx = 100 + (100 - 2 * sumYs) / n  # Gini index 
    
    return giniIdx / 100    

if len(sys.argv) < 4:
    print 'No algorithm name, input path, out path.'
    print 'python plot_proportion.py algorithm_name input_path output_path'
    sys.exit()

algorithm_name = sys.argv[1]
inputpath = os.path.abspath(sys.argv[2]) + "/"
outpath = os.path.abspath(sys.argv[3]) + "/"

print 'algorithmname: ' + algorithm_name
print 'inputpath: ' + inputpath
print 'outpath: ' + outpath

time_unit = 1000000
start_time = 1987200000000
# start_time = 23 * 24 * 3600 * 1000000
end_time = 2332800000000
# end_time = 26 * 24 * 3600 * 1000000

print 'generating....'
cpu_gini_result_list = []
mem_gini_result_list = []

print 'len(cpu_gini_result_list): ' + str(len(cpu_gini_result_list))
print 'len(mem_gini_result_list): ' + str(len(mem_gini_result_list))

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
        timestamp = (string.atoi((item.split(':'))[0]) - start_time) // time_unit
        print 'timestamp: ' + str(timestamp)
        host_info_list = (item.split(':'))[1].split(',')
        cpu_raw_data = []
        mem_raw_data = []
        for info in host_info_list:
            counter = counter + 1
            cpu = float((info.split('~'))[0])
            mem = float((info.split('~'))[1])           
            cpu_raw_data.append(cpu)            
            mem_raw_data.append(mem)           
        print 'cal cpu gini: ' + str(len(cpu_raw_data))
        cpu_gini = gini2(cpu_raw_data)
        print 'end cal cpu gini'
        print 'cal mem gini: ' + str(len(mem_raw_data))
        mem_gini = gini2(mem_raw_data)
        print 'cal cpu gini'
        cpu_gini_result_list.append(cpu_gini)
        mem_gini_result_list.append(mem_gini)
    
    print 'loading finish'
            
print 'len(cpu_gini_result_list): ' + str(len(cpu_gini_result_list))
print 'len(mem_gini_result_list): ' + str(len(mem_gini_result_list))

csvfile = file(algorithm_name + '_cpu_gini_result_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(cpu_gini_result_list)
csvfile.close()

csvfile = file(algorithm_name + '_mem_gini_result_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(mem_gini_result_list)
csvfile.close()
            
            
            
            
            
            
            
            
            
            
            
            
        
