import sys, os, glob, csv, string

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
start_time = 1987200000000
end_time = 2332800000000
# bestfit_result_average_migrations_list = []
# bestfit_result_cumulative_migrations_list = []
# bestfit_counter_list = []
ARP_result_average_migrations_list = []
ARP_result_cumulative_migrations_list = []
ARP_counter_list = []
ORA_result_average_migrations_list = []
ORA_result_cumulative_migrations_list = []
ORA_counter_list = []
ROBP_result_average_migrations_list = []
ROBP_result_cumulative_migrations_list = []
ROBP_counter_list = []
    
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ARP_counter_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ARP_result_average_migrations_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ARP_result_cumulative_migrations_list.append(0.0)

for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ROBP_counter_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ROBP_result_average_migrations_list.append(0.0)
for i in range((((end_time - start_time) // time_unit) // period_seconds) + 1):
    ROBP_result_cumulative_migrations_list.append(0.0)
    
# print 'len(bestfit_counter_list): ' + str(len(bestfit_counter_list))
# print 'len(bestfit_result_average_migrations_list): ' + str(len(bestfit_result_average_migrations_list))
# print 'len(bestfit_result_cumulative_migrations_list): ' + str(len(bestfit_result_cumulative_migrations_list))
print 'len(ARP_counter_list): ' + str(len(ARP_counter_list))
print 'len(ARP_result_average_migrations_list): ' + str(len(ARP_result_average_migrations_list))
print 'len(ARP_result_cumulative_migrations_list): ' + str(len(ARP_result_cumulative_migrations_list))
print 'len(ROBP_counter_list): ' + str(len(ROBP_counter_list))
print 'len(ROBP_result_average_migrations_list): ' + str(len(ROBP_result_average_migrations_list))
print 'len(ROBP_result_cumulative_migrations_list): ' + str(len(ROBP_result_cumulative_migrations_list))

        
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
        migrations_num = string.atoi(item[2])
        if migrations_num > 0:
            index = timestamp // period_seconds
            ARP_counter_list[index] = ARP_counter_list[index] + 1.0
            ARP_result_average_migrations_list[index] = ARP_result_average_migrations_list[index] + migrations_num
            ARP_result_cumulative_migrations_list[index] = ARP_result_cumulative_migrations_list[index] + migrations_num
    print 'loading finish'  
    
    
    for i in range(len(ARP_result_average_migrations_list)):
        if ARP_result_average_migrations_list[i] > 0:
            ARP_result_average_migrations_list[i] = ARP_result_average_migrations_list[i] / ARP_counter_list[i]
        
    previous_value = 0.0
    for i in range(len(ARP_result_cumulative_migrations_list)):
        ARP_result_cumulative_migrations_list[i] = ARP_result_cumulative_migrations_list[i] + previous_value  
        previous_value = ARP_result_cumulative_migrations_list[i]  
        
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
        migrations_num = string.atoi(item[2])
        if migrations_num > 0:
            index = timestamp // period_seconds
            ROBP_counter_list[index] = ROBP_counter_list[index] + 1.0
            ROBP_result_average_migrations_list[index] = ROBP_result_average_migrations_list[index] + migrations_num
            ROBP_result_cumulative_migrations_list[index] = ROBP_result_cumulative_migrations_list[index] + migrations_num
    print 'loading finish'  
    
    
    for i in range(len(ROBP_result_average_migrations_list)):
        if ROBP_result_average_migrations_list[i]:
            ROBP_result_average_migrations_list[i] = ROBP_result_average_migrations_list[i] / ROBP_counter_list[i]
        
    previous_value = 0.0
    for i in range(len(ROBP_result_cumulative_migrations_list)):
        ROBP_result_cumulative_migrations_list[i] = ROBP_result_cumulative_migrations_list[i] + previous_value  
        previous_value = ROBP_result_cumulative_migrations_list[i] 

csvfile = file('two_ARP_result_average_migrations_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(ARP_result_average_migrations_list)
csvfile.close()

csvfile = file('two_ROBP_result_average_migrations_list.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")
writer.writerow(ROBP_result_average_migrations_list)
csvfile.close()
     
