import daemon
import time
import os

def Redtooth():
    os.system("blueutil on")
    while True:
        print("Flag")
        with open("/tmp/current_time.txt", "w") as f:
            f.write("The time is now " + time.ctime())
        time.sleep(10)

def run():
    with daemon.DaemonContext():
        Redtooth()

if __name__ == "__main__":
    run()
