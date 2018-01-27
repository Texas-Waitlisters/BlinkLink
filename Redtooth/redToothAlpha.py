import daemon
import time
import os


def Redtooth():
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


def run():
    with daemon.DaemonContext():
        #loop call below
        Redtooth()


if __name__ == "__main__":
    run()
