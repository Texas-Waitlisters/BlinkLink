#import daemon
import time
import os
import sys
from urllib.request import urlopen
import urllib.request
import sys
import subprocess
from uuid import getnode as get_mac

ipAddress =  sys.argv[1]
mac = str(hex(get_mac()))
print(mac)

cmd = [ 'pmset', '-g']
#cmd = [ 'pmset -g' ]
#cmd = os.system("pmset -g")

#Gets volume
def getVolumeStatus():
    output = subprocess.Popen( cmd, stdout=subprocess.PIPE ).communicate()[0]
    print (output)
    print ("flag12")
    volumePlaying = False
    if ('coreaudiod' in str(output)):
        volumePlaying = True
        print("Volume found")
    return volumePlaying



#Creates custom URL string
def getURL(device, isPlaying):
    boolean = "false"
    if (isPlaying):
        boolean = "true"
    url = "http://" + ipAddress + ":8080/redtooth/report/" + device + "/" + boolean
    print(url)
    return url

#Ping database
def pingDatabase(isPlaying):
    url = getURL(mac, isPlaying)
    response = urlopen(url).read()
    desiredMAC = response
    print(desiredMAC)
    return desiredMAC

#os.system("blueutil on")


def Redtooth():
    #sys.stdout.write("flag")
    #print("Hello")
    #last 3 are parameters
    #isPlaying = true #dependent on output of sound
    priority = 2
    #iffy
    #if (isPlaying):
    #    priority = 1
    #device = mac address
    #response = urllib2.urlopen(http://10.209.6.212:8080/redtooth/report/Device1/1/isPlaying)
    '''
    url = getURL("Device1", True)
    response = urlopen(url)
    targetMAC = response.read()
    print ("TARGET MAC= " + targetMAC)
    '''
    #with urllib.request.urlopen('http://10.209.6.212:8080/redtooth/report/Device1/1/isPlaying') as response:
    #    html = response.read()

    #print(response)

    print("Running Core")
    #print(mac)
    #print(desiredMac)
    while True:
        isPlaying = getVolumeStatus()
        deviceToPlay = pingDatabase(isPlaying)
        if ((str(mac) in str(deviceToPlay)) and isPlaying):
            os.system("blueutil on")
            print("Redtooth Activated")
        if (not(str(mac) in str(deviceToPlay)) or not isPlaying):
            os.system("blueutil off")
            print("Redtooth De-activated")
        time.sleep(1)

    #os.system("blueutil on")
    '''
    while True:
        print("Flag")
        with open("/tmp/current_time.txt", "w") as f:
            f.write("The time is now " + time.ctime())
        time.sleep(10)
    '''
#url = (getURL("Device1", "1", "true"))
#print(urllib.request.urlopen(getURL("Device1", "1", "true")).read)

#url = 'http://10.209.6.212:8080/redtooth/report/Device1/1/true'
#url = "hi"
#response = urlopen(url)

#print(response.read())
#print(url)
#print("hi")


Redtooth()


#Start the daemon
#def run():
#    with daemon.DaemonContext():
#        Redtooth()


if __name__ == "__main__":
    #run()
    Redtooth()
