import csv, random

ORA_result_host_list = []
ARP_result_host_list = []
ROBP_result_host_list = []

for i in range(5000):
    ORA_VAL = random.randint(10, 100)
    ORA_result_host_list.append(ORA_VAL)
    ARP_VAL = random.randint(10, 100)
    ARP_result_host_list.append(ARP_VAL)
    ROBP_VAL = random.randint(10, 100)
    ROBP_result_host_list.append(ROBP_VAL)
    
    
print ROBP_result_host_list

csvfile = file('csv_test.csv', 'wb')
writer = csv.writer(csvfile, delimiter="\n")

writer.writerow(ORA_result_host_list)
writer.writerow(ARP_result_host_list)
writer.writerow(ROBP_result_host_list)

csvfile.close()
