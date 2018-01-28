import time
import os
from urllib.request import urlopen
import subprocess
from uuid import getnode as get_mac


ipAddress = "10.209.6.212"
mac = str(hex(get_mac()))
print(mac)


#audio state
def getVolumeStatus():
    audiovalue = subprocess.Popen("ConsoleApplication1.exe", shell=False, stdout=subprocess.PIPE)

    val = audiovalue.communicate()[0].strip() == b'True'
    print(val)
    time.sleep(.15)
    audiovalue.kill()
    return val


#Creates custom URL string
def getURL(device, isPlaying):
    boolean = "false"
    if isPlaying:
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


def Redtooth():
    print("Running Core")

    while True:
        isPlaying = getVolumeStatus()
        deviceToPlay = pingDatabase(isPlaying)

        if (str(mac) in str(deviceToPlay)) and isPlaying:
            #bt on and pair
            print("Redtooth Activated")

        elif not(str(mac) in str(deviceToPlay)):
            #windows bt cmds off
            print("Redtooth De-activated")
        time.sleep(1)


Redtooth()

