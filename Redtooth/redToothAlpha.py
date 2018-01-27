import daemon
import time
import os
from urllib.request import urlopen
import urllib.request
import sys


print("Flag")

url = 'http://10.209.6.212:8080/redtooth/report/Device1/1/true'
response = urlopen(url)
print(response.read())

print("Flag")


def getURL(device, priority, isPlaying):
    ipAddress = "10.209.6.212"
    url = "http://" + ipAddress + ":8080/redtooth/report/" + device + "/" + priority + "/" + isPlaying
    return url

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
    url = getURL("Device1", "1", "true")
    response = urlopen(url)
    targetMAC = response.read()
    print ("TARGET MAC= " + targetMAC)

    #with urllib.request.urlopen('http://10.209.6.212:8080/redtooth/report/Device1/1/isPlaying') as response:
    #    html = response.read()

    #print(response)

    '''
    if (address == mine and volume == True):
        os.system("blueutil on")
    if (address != mine or volume == False):
        os.system("bluetil off")
    '''
    os.system("blueutil on")
    while True:
        print("Flag")
        with open("/tmp/current_time.txt", "w") as f:
            f.write("The time is now " + time.ctime())
        time.sleep(10)

#url = (getURL("Device1", "1", "true"))
#print(urllib.request.urlopen(getURL("Device1", "1", "true")).read)

#url = 'http://10.209.6.212:8080/redtooth/report/Device1/1/true'
#url = "hi"
#response = urlopen(url)

#print(response.read())
#print(url)
#print("hi")

def run():
    with daemon.DaemonContext():
        #loop call below
        Redtooth()


if __name__ == "__main__":
    run()
