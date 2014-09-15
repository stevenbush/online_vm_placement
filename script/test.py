import glob, string, os, sys, traceback, csv

if len(sys.argv) < 3:
    print 'No input path and resultpath.'
    print 'please enter \"submit_job.py inputpath resultpath\"'
    sys.exit()

inputpath = os.path.abspath(sys.argv[1])
resultpath = os.path.abspath(sys.argv[2])

print "inputpath: %s" %inputpath
print "resultpath: %s" %resultpath

try:
    filepaths = glob.glob(inputpath + '/transformed_part-*')
    file_counter = 0
    filepaths.sort()
    for file_path in filepaths:
        print file_path

except Exception, e:
    print e
    print traceback.format_exc()
