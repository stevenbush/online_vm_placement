# calculate the number of hosts in different utilization at each time period

import sys, os, glob, csv, string, math

if len(sys.argv) < 5:
    print 'No algorithm name, input path, out path and time period.'
    print 'python plot_proportion.py algorithm_name input_path output_path time_period'
    sys.exit()

algorithm_name = sys.argv[1]
inputpath = os.path.abspath(sys.argv[2]) + "/"
outpath = os.path.abspath(sys.argv[3]) + "/"
time_period = sys.argv[4]

print 'algorithmname: ' + algorithm_name
print 'inputpath: ' + inputpath
print 'outpath: ' + outpath
print 'timeperiod: ' + time_period
    
period_seconds = string.atoi(time_period)
time_unit = 1000000
start_time = 1987200000000
# start_time = 23 * 24 * 3600 * 1000000
end_time = 2332800000000
# end_time = 26 * 24 * 3600 * 1000000

print 'generating....'

raw_0_list = []
raw_25_list = []
raw_50_list = []
raw_75_list = []

result_0_list = []
result_25_list = []
result_50_list = []
result_75_list = []

final_result_0_list = []
final_result_25_list = []
final_result_50_list = []
final_result_75_list = []

for i in range(((end_time - start_time) // time_unit) + 1):
    raw_0_list.append(0)
    raw_25_list.append(0)
    raw_50_list.append(0)
    raw_75_list.append(0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    result_0_list.append(0)
    result_25_list.append(0)
    result_50_list.append(0)
    result_75_list.append(0)
    final_result_0_list.append(0)
    final_result_25_list.append(0)
    final_result_50_list.append(0)
    final_result_75_list.append(0)
    
print 'len(raw_0_list): ' + str(len(raw_0_list))
print 'len(raw_25_list): ' + str(len(raw_25_list))
print 'len(raw_50_list): ' + str(len(raw_50_list))
print 'len(raw_75_list): ' + str(len(raw_75_list))
print 'len(result_0_list): ' + str(len(result_0_list))
print 'len(result_25_list): ' + str(len(result_0_list))
print 'len(result_50_list): ' + str(len(result_0_list))
print 'len(result_75_list): ' + str(len(result_0_list))

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
        for info in host_info_list:
            counter = counter + 1
            cpu = float((info.split('~'))[0])
            utilization = cpu / 2.0
            if utilization < 0.65:
                raw_0_list[timestamp] = raw_0_list[timestamp] + 1
            elif utilization >= 0.65 and utilization < 0.75:
                raw_25_list[timestamp] = raw_25_list[timestamp] + 1
            elif utilization >= 0.75 and utilization < 0.85:
                raw_50_list[timestamp] = raw_50_list[timestamp] + 1   
            else:
                raw_75_list[timestamp] = raw_75_list[timestamp] + 1    
    print 'loading finish' 
    
    for i in range(len(raw_0_list)):
        index = i // period_seconds
        result_0_list[index] = result_0_list[index] + raw_0_list[i]
        result_25_list[index] = result_25_list[index] + raw_25_list[i]
        result_50_list[index] = result_50_list[index] + raw_50_list[i]
        result_75_list[index] = result_75_list[index] + raw_75_list[i]
#         
#     for i in range(len(result_0_list)):
#         if result_0_list[i] > 0:
#             final_result_0_list[i] = (result_0_list[i] * 1.0) / (result_0_list[i] + result_25_list[i] + result_50_list[i] + result_75_list[i])
#         if result_25_list[i] > 0:
#             final_result_25_list[i] = (result_25_list[i] * 1.0) / (result_0_list[i] + result_25_list[i] + result_50_list[i] + result_75_list[i])
#         if result_50_list[i] > 0:
#             final_result_50_list[i] = (result_50_list[i] * 1.0) / (result_0_list[i] + result_25_list[i] + result_50_list[i] + result_75_list[i])
#         if result_75_list[i] > 0:
#             final_result_75_list[i] = (result_75_list[i] * 1.0) / (result_0_list[i] + result_25_list[i] + result_50_list[i] + result_75_list[i])
    
csvfile = file(algorithm_name + '_' + time_period + '_utilization_0_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_0_list)
csvfile.close()

csvfile = file(algorithm_name + '_' + time_period + '_utilization_25_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_25_list)
csvfile.close()

csvfile = file(algorithm_name + '_' + time_period + '_utilization_50_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_50_list)
csvfile.close()

csvfile = file(algorithm_name + '_' + time_period + '_utilization_75_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_75_list)
csvfile.close()    

# csvfile = file(algorithm_name + '_' + time_period + 'final_result_0_list.csv', 'wb')
# writer = csv.writer(csvfile, delimiter="\n")
# writer.writerow(final_result_0_list)
# csvfile.close()
# 
# csvfile = file(algorithm_name + '_' + time_period + 'final_result_25_list.csv', 'wb')
# writer = csv.writer(csvfile, delimiter="\n")
# writer.writerow(final_result_25_list)
# csvfile.close()
# 
# csvfile = file(algorithm_name + '_' + time_period + 'final_result_50_list.csv', 'wb')
# writer = csv.writer(csvfile, delimiter="\n")
# writer.writerow(final_result_50_list)
# csvfile.close()
# 
# csvfile = file(algorithm_name + '_' + time_period + 'final_result_75_list.csv', 'wb')
# writer = csv.writer(csvfile, delimiter="\n")
# writer.writerow(final_result_75_list)
# csvfile.close()    
    
print 'generating finish'
    
print result_0_list
print result_25_list
print result_50_list
print result_75_list





