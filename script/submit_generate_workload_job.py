#!/usr/bin/python
# Filename: submit_generate_workload_job.py

import glob, string, os, sys, traceback

if len(sys.argv) < 5:
    print 'No input path, resultpath, log path and time_period specified.'
    print 'please enter \"submit_generate_workload_job.py inputpath resultpath logpath time_period\"'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1])
resultpath = os.path.abspath(sys.argv[2])
logpath = os.path.abspath(sys.argv[3])
time_period = sys.argv[4]

print "inputpath: %s" % inputpath
print "resultpath: %s" % resultpath
print "logpath: %s" % logpath
print "time_period: %s" % time_period

try:
    filepath = glob.glob(inputpath + '/*jobs-info*')
    for pathvalue in filepath:
        print pathvalue
        jobname = pathvalue.split("/")
        jobname = jobname[len(jobname) - 1]
        cmd = "bsub -q 8nm -e %s -o %s \'cd /afs/cern.ch/work/j/jishi/private/experiment/googleclusterdata/ && python /afs/cern.ch/work/j/jishi/private/experiment/googleclusterdata/script/plot/generate_workload.py %s %s %s > %s 2>&1\'" % (logpath + '/' + jobname + '.elog', logpath + '/' + jobname + '.olog', pathvalue, resultpath, time_period, logpath + '/' + jobname + '.log')
        #print cmd
        # os.system(cmd)

except Exception, e:
    print e
    print traceback.format_exc()
