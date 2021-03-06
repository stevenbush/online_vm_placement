import sys, os, glob, csv, string
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
# bestfit_result_host_list = []
# bestfit_raw_host_list = []
ORA_result_host_list = []
ORA_raw_host_list = []
ARP_result_host_list = []
ARP_raw_host_list = []
ROBP_result_host_list = []
ROBP_raw_host_list = []
result_cpuload_list = []
raw_cpuload_list = []

# for i in range((end_time // time_unit) + 1):
#     bestfit_raw_host_list.append(0.0)
# for i in range(((end_time // time_unit) // period_seconds) + 1):
#     bestfit_result_host_list.append(0.0)
    
for i in range((end_time // time_unit) + 1):
    ORA_raw_host_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ORA_result_host_list.append(0.0)
    
for i in range((end_time // time_unit) + 1):
    ARP_raw_host_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ARP_result_host_list.append(0.0)
    
for i in range((end_time // time_unit) + 1):
    ROBP_raw_host_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ROBP_result_host_list.append(0.0)

for i in range((end_time // time_unit) + 1):
    raw_cpuload_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    result_cpuload_list.append(0.0)
    
# print 'len(bestfit_raw_host_list): ' + str(len(bestfit_raw_host_list))
# print 'len(bestfit_result_host_list): ' + str(len(bestfit_result_host_list))
print 'len(ORA_raw_host_list): ' + str(len(ORA_raw_host_list))
print 'len(ORA_result_host_list): ' + str(len(ORA_result_host_list))
print 'len(ARP_raw_host_list): ' + str(len(ARP_raw_host_list))
print 'len(ARP_result_host_list): ' + str(len(ARP_result_host_list))
print 'len(ROBP_raw_host_list): ' + str(len(ROBP_raw_host_list))
print 'len(ROBP_result_host_list): ' + str(len(ROBP_result_host_list))
print 'len(raw_cpuload_list): ' + str(len(raw_cpuload_list))
print 'len(result_cpuload_list): ' + str(len(result_cpuload_list))

# host_path = glob.glob(inputpath + '/*one*bestfit*resultlog')
# for inputfile in host_path:
#     print inputfile
#     name_list = inputfile.split("/")
#     file_name = name_list[len(name_list) - 1]
#     print 'processing %s' % (file_name) 
#     reader = csv.reader(open(inputfile, 'rb'))
#     print 'loading items...'
#     
#     counter = 0
#     for item in reader:
#         timestamp = string.atoi(item[0]) // time_unit
#         host_num = string.atoi(item[3])
#         if bestfit_raw_host_list[timestamp] == 0.0:
#             counter = 1
#         else:
#             counter = counter + 1
#         bestfit_raw_host_list[timestamp] = (bestfit_raw_host_list[timestamp] * (counter - 1) + host_num) * 1.0 / counter 
#         raw_cpuload_list[timestamp] = string.atof(item[6])
#     print 'loading finish'  
#     
#     previous_value = bestfit_raw_host_list[0]
#     for i in range(len(bestfit_raw_host_list)):
#         if bestfit_raw_host_list[i] == 0.0:
#             bestfit_raw_host_list[i] = previous_value
#         else:
#             previous_value = bestfit_raw_host_list[i]        
#             
#     for i in range(len(bestfit_raw_host_list)):
#         index = i // period_seconds
#         bestfit_result_host_list[index] = bestfit_result_host_list[index] + bestfit_raw_host_list[i]  
#         
#     for i in range(len(bestfit_result_host_list)):
#         bestfit_result_host_list[i] = bestfit_result_host_list[i] / period_seconds

host_path = glob.glob(inputpath + '/*one*ORA*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    
    counter = 0
    for item in reader:
        timestamp = string.atoi(item[0]) // time_unit
        host_num = string.atoi(item[3])
        if ORA_raw_host_list[timestamp] == 0.0:
            counter = 1
        else:
            counter = counter + 1
        ORA_raw_host_list[timestamp] = (ORA_raw_host_list[timestamp] * (counter - 1) + host_num) * 1.0 / counter   
        raw_cpuload_list[timestamp] = string.atof(item[6])
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
    
    for i in range(len(raw_cpuload_list)):
        index = i // period_seconds
        result_cpuload_list[index] = result_cpuload_list[index] + raw_cpuload_list[i]  
         
    for i in range(len(result_cpuload_list)):
        result_cpuload_list[i] = result_cpuload_list[i] / period_seconds
        
host_path = glob.glob(inputpath + '/*one*ARP*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    
    counter = 0
    for item in reader:
        timestamp = string.atoi(item[0]) // time_unit
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
        
host_path = glob.glob(inputpath + '/*one*ROBP*resultlog')
for inputfile in host_path:
    print inputfile
    name_list = inputfile.split("/")
    file_name = name_list[len(name_list) - 1]
    print 'processing %s' % (file_name) 
    reader = csv.reader(open(inputfile, 'rb'))
    print 'loading items...'
    
    counter = 0
    for item in reader:
        timestamp = string.atoi(item[0]) // time_unit
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
     
fig, ax = plt.subplots()
     
plt.plot(result_cpuload_list, 'b', label='Actual Workload Demands')

# plt.plot(bestfit_result_host_list, label='BestFit Host Demand')

plt.plot(ORA_result_host_list, 'r', label='ORA Host Demands')

plt.plot(ARP_result_host_list, 'g', label='ARP Host Demands')

plt.plot(ROBP_result_host_list, 'm', label='AOBP Host Demands')
     
# plt.tight_layout()
     
plt.xlabel('Time(day)')
plt.ylabel('Average Resource Demands (every ' + str(string.atoi(time_period) // 3600) + ' hours)')
plt.legend(loc='best', prop={'size':10})
     
plt.grid(True)    
ax.set_xticks(range(0, len(result_cpuload_list), 2 * (24 * 3600) / period_seconds))
ax.set_xticklabels(range(0, 1 + len(result_cpuload_list) / ((24 * 3600) / period_seconds), 2))
 
# plt.ylim(0, 1000)
     
plt.savefig(outpath + plotname, dpi=300)
plt.show()
