import daemon
import time

def Redtooth():
    while True:
        with open("/tmp/current_time.txt", "w") as f:
            f.write("The time is now " + time.ctime())
            os.system("blueutil on")
        time.sleep(10)

def run():
    with daemon.DaemonContext():
        Redtooth()

if __name__ == "__main__":
    run()
