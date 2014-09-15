import glob, string, os, sys, traceback, csv

if len(sys.argv) < 3:
    print 'No input file and result location specified.'
    print 'please enter \"csv_transform.py input_file result location\"'
    sys.exit()

input_file = os.path.abspath(sys.argv[1])
result_location = os.path.abspath(sys.argv[2])
print input_file
print result_location
reader=csv.reader(open(input_file,'rb'))
name_path = input_file.split("/")
file_name = name_path[len(name_path)-1]
writer=csv.writer(open(result_location+'/transformed_'+file_name, 'wb'))
bttwo=0
lttwo=0
count=0
SCHEDULE=0
FINISH=0
UPDATE_RUNNING=0
for item in reader:
    if item[9]<>'' and  item[10]<>'': 
        if string.atof(item[9])>0 and string.atof(item[10])>0 and (string.atoi(item[5])==1 or string.atoi(item[5])==4 or string.atoi(item[5])==8):
        #if string.atof(item[9])>0 and string.atof(item[10])>0:
            #     "time"    "job-id"    "task-id"  "event type" "scheduling-class""priority" "CPU"    "mem"        "disk"
            #print item[0]+"-"+item[2]+"-"+item[3]+"-"+item[5]+"-"+item[7]+"-"+item[8]+"-"+item[9]+"-"+item[10]+"-"+item[11]
            print "processing %s" %(count)
            cpu = string.atof(item[9])
            mem = string.atof(item[10])
            value = min(cpu,mem)/max(cpu,mem)
            if string.atoi(item[5])==1:
                SCHEDULE = SCHEDULE + 1
            if string.atoi(item[5])==4:
                FINISH = FINISH + 1
            if string.atoi(item[5])==8:
                UPDATE_RUNNING = UPDATE_RUNNING + 1
            if value >= 0.5:
                bttwo = bttwo + 1
            else:
                lttwo = lttwo + 1
            #     "time"    "job-id"    "task-id"  "event type" "scheduling-class""priority" "CPU"    "mem"   "disk"
            writer.writerow([item[0], item[2], item[3], item[5], item[7], item[8], item[9], item[10], item[11]])
            count = count + 1

f = file(result_location+'/result_'+file_name, 'w') # open for 'w'riting
f.write('total: ' + str(count)+'\n') # write text to file
f.write('bigger than two: ' + str(bttwo)+'\n') # write text to file
f.write('less than two : ' + str(lttwo)+'\n') # write text to file
f.write('SCHEDULE EVENTS: ' + str(SCHEDULE)+'\n') # write text to file
f.write('FINISH EVENTS: ' + str(FINISH)+'\n') # write text to file
f.write('UPDATE_RUNNING EVENTS: ' + str(UPDATE_RUNNING)+'\n') # write text to file
f.close() # close the file
print 'total: ' + str(count)
print 'bigger than 0.5: ' + str(bttwo)
print 'less than 0.5: ' + str(lttwo)
print 'SCHEDULE EVENTS: ' + str(SCHEDULE)
print 'FINISH EVENTS: ' + str(FINISH)
print 'UPDATE_RUNNING EVENTS: ' + str(UPDATE_RUNNING)
