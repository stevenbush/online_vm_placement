#!/usr/bin/python
# Filename: cal_information.py

import glob, string, os, sys, traceback

if len(sys.argv) < 2:
    print 'No input path specified.'
    print 'please enter \"submit_job.py inputpath\"'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1])

print "inputpath: %s" %inputpath

bttwo=0
lttwo=0
total=0
SCHEDULE=0
FINISH=0
UPDATE_RUNNING=0
count = 0

try:
    filepath = glob.glob(inputpath + '/result_part-*')
    for pathvalue in filepath:
        #print pathvalue
        f = file(pathvalue)
        while True:
            line = f.readline()
            valuelist = line.split(":")
            key = valuelist[0].strip()
            value = valuelist[len(valuelist)-1].strip()
            #print key + '-' + value
            if key=='total':
                total = total + string.atoi(value) 
            if key=='bigger than two':
                bttwo = bttwo + string.atoi(value) 
            if key=='less than two':
                lttwo = lttwo + string.atoi(value) 
            if key=='SCHEDULE EVENTS':
                SCHEDULE = SCHEDULE + string.atoi(value) 
            if key=='FINISH EVENTS':
                FINISH = FINISH + string.atoi(value) 
            if key=='UPDATE_RUNNING EVENTS':
                UPDATE_RUNNING = UPDATE_RUNNING + string.atoi(value) 
            if len(line) == 0: # Zero length indicates EOF
                break
        f.close() # close the file
        count = count + 1

    print 'total files: '+str(count)
    print 'total events: '+str(total)
    print 'total bigger than two: '+str(bttwo)
    print 'total less than two: '+str(lttwo)
    print 'total SCHEDULE EVENTS: '+str(SCHEDULE)
    print 'total FINISH EVENTS: '+str(FINISH)
    print 'total UPDATE_RUNNING EVENTS: '+str(UPDATE_RUNNING)

except Exception, e:
    print e
    print traceback.format_exc()
