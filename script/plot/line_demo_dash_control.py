import numpy as np
import matplotlib.pyplot as plt
import random

period_seconds = 600
time_unit = 1000000
end_time = 2506198229513
result_cpuload_list = []
result_memload_list = []

for i in range(((end_time // time_unit) // period_seconds) + 1):
    result_cpuload_list.append(random.uniform(100, 500))
    result_memload_list.append(random.uniform(100, 500))
    
fig, ax = plt.subplots()
    
plt.plot(result_cpuload_list, label='CPU Demand')
plt.plot(result_memload_list, label='MEM Demand')
    
# plt.tight_layout()
    
plt.xlabel('Time(day)')
plt.ylabel('Resource Demand')
plt.legend(loc='best', prop={'size':12})
    
plt.grid(True)    
ax.set_xticks(range(0, 5000, 144 * 4))
ax.set_xticklabels(range(0, 33, 4))

plt.ylim(0,1000)
    
plt.savefig("test.png", dpi=300)
plt.show()
