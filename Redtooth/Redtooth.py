#pip install python-daemon
import os
import time
from daemon import runner


class Redtooth():
    def __init__(self):
        self.stdin_path = '/dev/null'
        self.stdout_path = '/dev/tty'
        self.stderr_path = 'dev/tty/'
        self.pidfile_path = '/tmp/foo.pid'
        self.pidfile_timeout = 5

    def run(self):
        while True:
            print("Flag")
            #os.system("rfkill block bluetooth")
            os.system("blueutil on")
            time.sleep(10)


app = Redtooth()
daemon_runner = runner.DaemonRunner(app)
daemon_runner.do_action()
