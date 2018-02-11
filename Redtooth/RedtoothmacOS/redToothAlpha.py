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
connected = false

cmd = [ 'pmset', '-g']

#Gets volume
def getVolumeStatus():
    output = subprocess.Popen( cmd, stdout=subprocess.PIPE ).communicate()[0]
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

def pair(connected):
    global connected
    while (not connected):
        os.system("blueutil on")
        print("Redtooth Activated")
        cmd2 = [ '/usr/bin/osascript',  'connect.scpt']
        output2 = subprocess.Popen( cmd2, stdout=subprocess.PIPE ).communicate()[0]
        print (output2)
        expect = "Connect menu was not found, are you already connected?\n"
        if (output2 == expect.encode("ASCII")):
            return True
        time.sleep(1)


def Redtooth():
    global connected
    priority = 2
    connected = False
    print("Running Core")
    while True:
        isPlaying = getVolumeStatus()
        deviceToPlay = pingDatabase(isPlaying)
        if ((str(mac) in str(deviceToPlay)) and isPlaying):
            if (not connected):
                connected = pair(connected)
        if (not(str(mac) in str(deviceToPlay))):# or not isPlaying):
            os.system("blueutil off")
            connected = False
            print("Redtooth De-activated")
        time.sleep(1)

Redtooth()

if __name__ == "__main__":
    Redtooth()
