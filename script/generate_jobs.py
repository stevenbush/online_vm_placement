import glob, string, os, sys, traceback, csv

if len(sys.argv) < 3:
    print 'No input path and resultpath specified.'
    print 'please enter \"generate_jobs.py inputpath resultpath\"'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1])
resultpath = os.path.abspath(sys.argv[2])

print "inputpath: %s" %inputpath
print "resultpath: %s" %resultpath

try:
    filepath = glob.glob(inputpath + '/transformed_part-*')
    filepath.sort(reverse = True)
    job_dict = {}
    submit_counter = 0
    update_counter = 0
    finish_counter = 0
    for pathvalue in filepath:
        print pathvalue
        items_list = []
        event_buffer_list = []
        job_buffer_list = []
        name_path = pathvalue.split("/")
        file_name = name_path[len(name_path)-1]
        jobs_writer=csv.writer(open(resultpath+'/jobs-info-'+file_name, 'wb'))
        events_writer=csv.writer(open(resultpath+'/events-info-'+file_name, 'wb'))
        reader=csv.reader(open(pathvalue,'rb'))
        print 'loading items...'
        for item in reader:
            items_list.append(item)
        print 'reversing items...'
        items_list.reverse()
        count = 0
        list_size = len(items_list)
        for item in items_list:
            count = count + 1
            print '\r%d%% :' %((count*100)/list_size),
            sys.stdout.flush()
            if string.atoi(item[3])==4:
                key = item[1]+"-"+item[2]
                #           time      job-id   task-id   event type  "CPU"    "mem"   "disk"
                key_value = [item[0]+'~'+item[1]+'~'+item[2]+'~'+item[3]+'~'+item[6]+'~'+item[7]+'~'+item[8]]
                job_dict[key] = key_value
                event_buffer_list.append(item)
                finish_counter = finish_counter + 1
            if string.atoi(item[3])==8:
                key = item[1]+"-"+item[2]
                if job_dict.has_key(key):
                    key_value = job_dict.get(key)
                    #            time       job-id   task-id   event type  "CPU"    "mem"   "disk"
                    insert_value = item[0]+'~'+item[1]+'~'+item[2]+'~'+item[3]+'~'+item[6]+'~'+item[7]+'~'+item[8]
                    key_value.append(insert_value)
                    job_dict[key] = key_value
                    event_buffer_list.append(item)
                    update_counter = update_counter + 1
            if string.atoi(item[3])==1:
                key = item[1]+"-"+item[2]
                if job_dict.has_key(key):
                    key_value = job_dict.pop(key)
                    #            time        job-id   task-id   event type  "CPU"    "mem"   "disk"
                    insert_value = item[0]+'~'+item[1]+'~'+item[2]+'~'+item[3]+'~'+item[6]+'~'+item[7]+'~'+item[8]
                    key_value.append(insert_value)
                    event_buffer_list.append(item)
                    key_value.reverse()
                    job_buffer_list.append(key_value)
                    submit_counter = submit_counter + 1

        print '\nreversing event buffer list...'
        event_buffer_list.reverse()
        print 'writting event buffer to csv file..'
        count = 0
        list_size = len(event_buffer_list)
        for item in event_buffer_list:
            count = count + 1
            print '\r%d%%' %((count*100)/list_size),
            sys.stdout.flush()
            #print item
            events_writer.writerow(item)
        print '\nwritting event buffer to csv file completed'

        print 'reversing job buffer list...'
        job_buffer_list.reverse()
        print 'writting job buffer to csv file..'
        count = 0
        list_size = len(job_buffer_list)
        for item in job_buffer_list:
            count = count + 1
            print '\r%d%%' %((count*100)/list_size),
            sys.stdout.flush()
            #print item
            jobs_writer.writerow(item)
        print '\nwritting job buffer to csv file completed'

    print 'writting total event information.....'
    total_events_writer=file(resultpath+'/total_events', 'wb')
    total_events_writer.write('submit_events: ' + str(submit_counter)+'\n') # write text to file
    total_events_writer.write('update_events: ' + str(update_counter)+'\n') # write text to file
    total_events_writer.write('finish_events: ' + str(finish_counter)+'\n') # write text to file
    total_events_writer.close()
                    

except Exception, e:
    print e
    print traceback.format_exc()
