#!/usr/bin/python

import glob, string, os, sys, traceback, csv

bttwo=0
lttwo=0
count=0

try:
    writer=csv.writer(open('result.csv', 'wb'))
    filepath = glob.glob('../task_events/part-000*')
    for pathvalue in filepath:
	print pathvalue
	reader=csv.reader(open(pathvalue, 'rb'))
	for item in reader:
	    if item[9]<>'' and  item[10]<>'':
		if string.atof(item[9])>0 and string.atof(item[10])>0:
		    print item[0]+"-"+item[2]+"-"+item[3]+"-"+item[5]+"-"+item[8]+"-"+item[9]+"-"+item[10]
            	    cpu = string.atof(item[9])
            	    mem = string.atof(item[10])
            	    value = min(cpu,mem)/max(cpu,mem)
            	    if value >= 0.5:
			bttwo = bttwo + 1
		    else:
			lttwo = lttwo + 1
		    writer.writerow([item[0], item[2], item[3], item[5], item[8], item[9], item[10], min(cpu,mem)/max(cpu,mem)])
  		    count = count + 1

    f = file('result.txt', 'w') # open for 'w'riting
    f.write('total: ' + str(count)) # write text to file
    f.write('bigger than two: ' + str(bttwo)) # write text to file
    f.write('less than two : ' + str(lttwo)) # write text to file
    f.close() # close the file
    print 'total: ' + str(count)
    print 'bigger than two: ' + str(bttwo)
    print 'less than two : ' + str(lttwo)

except Exception, e:
    print e
    print traceback.format_exc()
