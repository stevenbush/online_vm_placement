import sys, os, glob, csv, string
import numpy as np
import matplotlib.pyplot as plt

if len(sys.argv) < 6:
    print 'No input path, plot name, out path, time period and algorithm name.'
    print 'python plot_proportion.py input_path plot_name output_path time_period algorithm_name'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1]) + "/"
plotname = sys.argv[2]
outpath = os.path.abspath(sys.argv[3]) + "/"
time_period = sys.argv[4]
algorithm_name = sys.argv[5]

print 'inputpath: ' + inputpath
print 'plotname: ' + plotname
print 'outpath: ' + outpath
print 'timeperiod: ' + time_period
print 'algorithm_name: ' + algorithm_name

period_seconds = string.atoi(time_period)

print 'plotting....'
    
period_seconds = string.atoi(time_period)
time_unit = 1000000
end_time = 2506198229513
result_average_migrations_list = []
result_cumulative_migrations_list = []
counter_list = []

for i in range(((end_time // time_unit) // period_seconds) + 1):
    counter_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    result_average_migrations_list.append(0.0)
for i in range(((end_time // time_unit) // period_seconds) + 1):
    result_cumulative_migrations_list.append(0.0)
    
print 'len(counter_list): ' + str(len(counter_list))
print 'len(result_average_migrations_list): ' + str(len(result_average_migrations_list))
print 'len(result_cumulative_migrations_list): ' + str(len(result_cumulative_migrations_list))

host_path = glob.glob(inputpath + '/*one*' + algorithm_name + '*resultlog')
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
        migrations_num = string.atoi(item[3])
        index = timestamp // period_seconds
        counter_list[index] = counter_list[index] + 1.0
        result_average_migrations_list[index] = result_average_migrations_list[index] + migrations_num
        result_cumulative_migrations_list[index] = result_cumulative_migrations_list[index] + migrations_num
    print 'loading finish'  
    
    
    for i in range(len(result_average_migrations_list)):
        result_average_migrations_list[i] = result_average_migrations_list[i] / counter_list[i]
        
    previous_value = 0.0
    for i in range(len(result_cumulative_migrations_list)):
        result_cumulative_migrations_list[i] = result_cumulative_migrations_list[i] + previous_value  
        previous_value = result_cumulative_migrations_list[i]
        
     
fig, ax = plt.subplots()
     
plt.plot(result_average_migrations_list, label=algorithm_name + ' Average Migrations Number')
    
# plt.tight_layout()
     
plt.xlabel('Time(day)')
plt.ylabel('Migrations Number')
plt.legend(loc='best', prop={'size':12})
     
plt.grid(True)    
ax.set_xticks(range(0, len(result_average_migrations_list), 2 * (24 * 3600) / period_seconds))
ax.set_xticklabels(range(0, 1 + len(result_average_migrations_list) / ((24 * 3600) / period_seconds), 2))
 
# plt.ylim(0, 1000)
     
plt.savefig(outpath + plotname+ '_average', dpi=300)
plt.show()

fig, ax = plt.subplots()
     
plt.plot(result_cumulative_migrations_list, label=algorithm_name + ' Cumulative Migrations Number')
     
# plt.tight_layout()
     
plt.xlabel('Time(day)')
plt.ylabel('Migrations Number')
plt.legend(loc='best', prop={'size':12})
     
plt.grid(True)    
ax.set_xticks(range(0, len(result_cumulative_migrations_list), 2 * (24 * 3600) / period_seconds))
ax.set_xticklabels(range(0, 1 + len(result_cumulative_migrations_list) / ((24 * 3600) / period_seconds), 2))
 
# plt.ylim(0, 1000)
     
plt.savefig(outpath + plotname+'_cumulative', dpi=300)
plt.show()
