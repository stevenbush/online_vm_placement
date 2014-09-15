import os,sys,string,time

for i in range(100):
    print '\r%d%% :' %(i),
    time.sleep(0.1)
    #print i,
    sys.stdout.flush() 
