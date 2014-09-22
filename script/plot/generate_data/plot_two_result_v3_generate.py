import sys, os, glob, csv, string, math

if len(sys.argv) < 7:
    print 'No input path, plot name, out path, time period, start day, end day.'
    print 'python plot_proportion.py input_path plot_name output_path time_period start_day end_day'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1]) + "/"
plotname = sys.argv[2]
outpath = os.path.abspath(sys.argv[3]) + "/"
time_period = sys.argv[4]
start_day = string.atoi(sys.argv[5])
end_day = string.atoi(sys.argv[6])

print 'inputpath: ' + inputpath
print 'plotname: ' + plotname
print 'outpath: ' + outpath
print 'timeperiod: ' + time_period
print 'startday: ' + str(start_day)
print 'endday: ' + str(end_day)

period_seconds = string.atoi(time_period)

print 'plotting....'
    
period_seconds = string.atoi(time_period)
time_unit = 1000000
# start_time = 1987200000000
# end_time = 2332800000000
start_time = start_day * 24 * 3600 * 1000000
end_time = end_day * 24 * 3600 * 1000000


ORA_result_host_list = []
ORA_raw_host_list = []
ARP_result_host_list = []
ARP_raw_host_list = []
ROBP_result_host_list = []
ROBP_raw_host_list = []
result_maxload_list = []
raw_maxload_list = []

    
for i in range(((end_time - start_time) // time_unit) + 1):
    ORA_raw_host_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ORA_result_host_list.append(0.0)
    
for i in range(((end_time - start_time) // time_unit) + 1):
    ARP_raw_host_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ARP_result_host_list.append(0.0)
    
for i in range(((end_time - start_time) // time_unit) + 1):
    ROBP_raw_host_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ROBP_result_host_list.append(0.0)

for i in range(((end_time - start_time) // time_unit) + 1):
    raw_maxload_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    result_maxload_list.append(0.0)
    
# print 'len(bestfit_raw_host_list): ' + str(len(bestfit_raw_host_list))
# print 'len(bestfit_result_host_list): ' + str(len(bestfit_result_host_list))
print 'len(ORA_raw_host_list): ' + str(len(ORA_raw_host_list))
print 'len(ORA_result_host_list): ' + str(len(ORA_result_host_list))
print 'len(ARP_raw_host_list): ' + str(len(ARP_raw_host_list))
print 'len(ARP_result_host_list): ' + str(len(ARP_result_host_list))
print 'len(ROBP_raw_host_list): ' + str(len(ROBP_raw_host_list))
print 'len(ROBP_result_host_list): ' + str(len(ROBP_result_host_list))
print 'len(raw_maxload_list): ' + str(len(raw_maxload_list))
print 'len(result_maxload_list): ' + str(len(result_maxload_list))

host_path = glob.glob(inputpath + '/*two*ARP*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    
    counter = 0
    for item in reader:
        timestamp = (string.atoi(item[0]) - start_time) // time_unit
        if timestamp >= 0 and timestamp < len(ARP_raw_host_list):
            host_num = string.atoi(item[3])
            if ORA_raw_host_list[timestamp] == 0.0:
                counter = 1
            else:
                counter = counter + 1
            ORA_raw_host_list[timestamp] = (ORA_raw_host_list[timestamp] * (counter - 1) + host_num) * 1.0 / counter  
            raw_maxload_list[timestamp] = max(string.atof(item[6]), string.atof(item[7]))
    print 'loading finish'   
    
    previous_value = ORA_raw_host_list[0]
    for i in range(len(ORA_raw_host_list)):
        if ORA_raw_host_list[i] == 0.0:
            ORA_raw_host_list[i] = previous_value
        else:
            previous_value = ORA_raw_host_list[i]        
            
    for i in range(len(ORA_raw_host_list)):
        index = i // period_seconds
        ORA_result_host_list[index] = ORA_result_host_list[index] + ORA_raw_host_list[i]  
        
    for i in range(len(ORA_result_host_list)):
        ORA_result_host_list[i] = ORA_result_host_list[i] / period_seconds
        
    for i in range(len(raw_maxload_list)):
        index = i // period_seconds
        result_maxload_list[index] = result_maxload_list[index] + raw_maxload_list[i]  
        
    for i in range(len(result_maxload_list)):
        result_maxload_list[i] = result_maxload_list[i] / period_seconds
        
host_path = glob.glob(inputpath + '/*two*ARP*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    
    counter = 0
    for item in reader:
        timestamp = (string.atoi(item[0]) - start_time) // time_unit
        if timestamp >= 0 and timestamp < len(ARP_raw_host_list):
            host_num = string.atoi(item[3])
            if ARP_raw_host_list[timestamp] == 0.0:
                counter = 1
            else:
                counter = counter + 1
            ARP_raw_host_list[timestamp] = (ARP_raw_host_list[timestamp] * (counter - 1) + host_num) * 1.0 / counter   
    print 'loading finish'   
    
    previous_value = ARP_raw_host_list[0]
    for i in range(len(ARP_raw_host_list)):
        if ARP_raw_host_list[i] == 0.0:
            ARP_raw_host_list[i] = previous_value
        else:
            previous_value = ARP_raw_host_list[i]        
            
    for i in range(len(ARP_raw_host_list)):
        index = i // period_seconds
        ARP_result_host_list[index] = ARP_result_host_list[index] + ARP_raw_host_list[i]  
        
    for i in range(len(ARP_result_host_list)):
        ARP_result_host_list[i] = ARP_result_host_list[i] / period_seconds
        
host_path = glob.glob(inputpath + '/*two*ROBP*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    
    counter = 0
    for item in reader:
        timestamp = (string.atoi(item[0]) - start_time) // time_unit
        if timestamp >= 0 and timestamp < len(ROBP_raw_host_list):
            host_num = string.atoi(item[3])
            if ROBP_raw_host_list[timestamp] == 0.0:
                counter = 1
            else:
                counter = counter + 1
            ROBP_raw_host_list[timestamp] = (ROBP_raw_host_list[timestamp] * (counter - 1) + host_num) * 1.0 / counter   
    print 'loading finish'   
    
    previous_value = ROBP_raw_host_list[0]
    for i in range(len(ROBP_raw_host_list)):
        if ROBP_raw_host_list[i] == 0.0:
            ROBP_raw_host_list[i] = previous_value
        else:
            previous_value = ROBP_raw_host_list[i]        
            
    for i in range(len(ROBP_raw_host_list)):
        index = i // period_seconds
        ROBP_result_host_list[index] = ROBP_result_host_list[index] + ROBP_raw_host_list[i]  
        
    for i in range(len(ARP_result_host_list)):
        ROBP_result_host_list[i] = ROBP_result_host_list[i] / period_seconds

csvfile = file(plotname + '_two_result_maxload_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(result_maxload_list)
csvfile.close()

csvfile = file(plotname + '_two_ORA_result_host_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(ORA_result_host_list)
csvfile.close()

csvfile = file(plotname + '_two_ARP_result_host_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(ARP_result_host_list)
csvfile.close()

csvfile = file(plotname + '_two_ROBP_result_host_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(ROBP_result_host_list)
csvfile.close()		
