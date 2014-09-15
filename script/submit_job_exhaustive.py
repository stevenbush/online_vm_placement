#!/usr/bin/python
# Filename: submit_job.py

import glob, string, os, sys, traceback

if len(sys.argv) < 4:
    print 'No input path, resultpath and log path specified.'
    print 'please enter \"submit_job.py inputpath resultpath logpath\"'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1])
resultpath = os.path.abspath(sys.argv[2])
logpath = os.path.abspath(sys.argv[3])

print "inputpath: %s" %inputpath
print "resultpath: %s" %resultpath
print "logpath: %s" %logpath

try:
    filepath = glob.glob(inputpath + '/part-*')
    for pathvalue in filepath:
        jobname = pathvalue.split("/")
        jobname = jobname[len(jobname) - 1]
        cmd = "bsub -q 8nh -e %s -o %s \'cd /afs/cern.ch/work/j/jishi/private/experiment/googleclusterdata/ && python /afs/cern.ch/work/j/jishi/private/experiment/googleclusterdata/script/csv_transform_exhaustive.py %s %s > %s 2>&1\'" % (logpath + '/' +jobname + '.elog', logpath + '/' +jobname + '.olog', pathvalue, resultpath, logpath + '/' +jobname + '.log')
        #print cmd
        os.system(cmd)

except Exception, e:
    print e
    print traceback.format_exc()
