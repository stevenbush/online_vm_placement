#!/usr/bin/python
# Filename: using_file.py

import os

comd = "bjobs | (awk \'{print $1}\')>bkill"
os.system(comd)
f = file('bkill')
while True:
    line = f.readline().strip()
    if len(line) == 0: # Zero length indicates EOF
        break
    cmd = "bkill -r %s" %(line)
    print cmd
    os.system(cmd)
    # Notice comma to avoid automatic newline added by Python
f.close() # close the file
