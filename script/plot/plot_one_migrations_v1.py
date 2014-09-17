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

# for i in range(((end_time // time_unit) // period_seconds) + 1):
#     bestfit_counter_list.append(0.0)
# for i in range(((end_time // time_unit) // period_seconds) + 1):
#     bestfit_result_average_migrations_list.append(0.0)
# for i in range(((end_time // time_unit) // period_seconds) + 1):
#     bestfit_result_cumulative_migrations_list.append(0.0)
    
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ARP_counter_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ARP_result_average_migrations_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ARP_result_cumulative_migrations_list.append(0.0)

for i in range(((end_time // time_unit) // period_seconds) + 1):
    ORA_counter_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ORA_result_average_migrations_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ORA_result_cumulative_migrations_list.append(0.0)

for i in range(((end_time // time_unit) // period_seconds) + 1):
    ROBP_counter_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ROBP_result_average_migrations_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    ROBP_result_cumulative_migrations_list.append(0.0)
    
# print 'len(bestfit_counter_list): ' + str(len(bestfit_counter_list))
# print 'len(bestfit_result_average_migrations_list): ' + str(len(bestfit_result_average_migrations_list))
# print 'len(bestfit_result_cumulative_migrations_list): ' + str(len(bestfit_result_cumulative_migrations_list))
print 'len(ARP_counter_list): ' + str(len(ARP_counter_list))
print 'len(ARP_result_average_migrations_list): ' + str(len(ARP_result_average_migrations_list))
print 'len(ARP_result_cumulative_migrations_list): ' + str(len(ARP_result_cumulative_migrations_list))
print 'len(ORA_counter_list): ' + str(len(ORA_counter_list))
print 'len(ORA_result_average_migrations_list): ' + str(len(ORA_result_average_migrations_list))
print 'len(ORA_result_cumulative_migrations_list): ' + str(len(ORA_result_cumulative_migrations_list))
print 'len(ROBP_counter_list): ' + str(len(ROBP_counter_list))
print 'len(ROBP_result_average_migrations_list): ' + str(len(ROBP_result_average_migrations_list))
print 'len(ROBP_result_cumulative_migrations_list): ' + str(len(ROBP_result_cumulative_migrations_list))

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
#         migrations_num = string.atoi(item[2])
#         if migrations_num > 0:
#             index = timestamp // period_seconds
#             bestfit_counter_list[index] = bestfit_counter_list[index] + 1.0
#             bestfit_result_average_migrations_list[index] = bestfit_result_average_migrations_list[index] + migrations_num
#             bestfit_result_cumulative_migrations_list[index] = bestfit_result_cumulative_migrations_list[index] + migrations_num
#     print 'loading finish'  
#     
#     
#     for i in range(len(bestfit_result_average_migrations_list)):
#         if bestfit_result_average_migrations_list[i] > 0:
#             bestfit_result_average_migrations_list[i] = bestfit_result_average_migrations_list[i] / bestfit_counter_list[i]
#         
#     previous_value = 0.0
#     for i in range(len(bestfit_result_cumulative_migrations_list)):
#         bestfit_result_cumulative_migrations_list[i] = bestfit_result_cumulative_migrations_list[i] + previous_value  
#         previous_value = bestfit_result_cumulative_migrations_list[i]
        
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
        migrations_num = string.atoi(item[2])
        if migrations_num > 0:
            index = timestamp // period_seconds
            ORA_counter_list[index] = ORA_counter_list[index] + 1.0
            ORA_result_average_migrations_list[index] = ORA_result_average_migrations_list[index] + migrations_num
            ORA_result_cumulative_migrations_list[index] = ORA_result_cumulative_migrations_list[index] + migrations_num
    print 'loading finish'  
    
    
    for i in range(len(ORA_result_average_migrations_list)):
        if ORA_result_average_migrations_list[i] > 0:
            ORA_result_average_migrations_list[i] = ORA_result_average_migrations_list[i] / ORA_counter_list[i]
        
    previous_value = 0.0
    for i in range(len(ORA_result_cumulative_migrations_list)):
        ORA_result_cumulative_migrations_list[i] = ORA_result_cumulative_migrations_list[i] + previous_value  
        previous_value = ORA_result_cumulative_migrations_list[i]   
        
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
     
fig, ax = plt.subplots()
     
# plt.plot(bestfit_result_average_migrations_list, label='bestfit Average Migrations Number')
plt.plot(ARP_result_average_migrations_list, 'r', label='ARP Average Number of Migrations')
plt.plot(ORA_result_average_migrations_list, 'g', label='ORA Average Number of Migrations')
plt.plot(ROBP_result_average_migrations_list, 'b', label='AOBP Average Number of Migrations')
    
# plt.tight_layout()
     
plt.xlabel('Time(day)')
plt.ylabel('Average Number of Migrations (every ' + str(string.atoi(time_period) // 3600) + ' hours)')
plt.legend(loc='best', prop={'size':10})
     
plt.grid(True)    
ax.set_xticks(range(0, len(ARP_result_average_migrations_list), 2 * (24 * 3600) / period_seconds))
ax.set_xticklabels(range(0, 1 + len(ARP_result_average_migrations_list) / ((24 * 3600) / period_seconds), 2))

plt.ylim(0.5, 2.0)
     
plt.savefig(outpath + plotname + '_average', dpi=300)
plt.show()

# fig, ax = plt.subplots()
#      
# # plt.plot(bestfit_result_cumulative_migrations_list, label='bestfit Cumulative Migrations Number')
# plt.plot(ARP_result_cumulative_migrations_list, label='ARP Cumulative Migrations Number')
# plt.plot(ORA_result_cumulative_migrations_list, label='ORA Cumulative Migrations Number')
# plt.plot(ROBP_result_cumulative_migrations_list, label='ROBP Cumulative Migrations Number')
#      
# # plt.tight_layout()
#      
# plt.xlabel('Time(day)')
# plt.ylabel('Migrations Number')
# plt.legend(loc='best', prop={'size':12})
#      
# plt.grid(True)    
# ax.set_xticks(range(0, len(ARP_result_cumulative_migrations_list), 2 * (24 * 3600) / period_seconds))
# ax.set_xticklabels(range(0, 1 + len(ARP_result_cumulative_migrations_list) / ((24 * 3600) / period_seconds), 2))
#  
# plt.ylim(0.8, 2.0)
#      
# plt.savefig(outpath + plotname + '_cumulative', dpi=300)
# plt.show()
