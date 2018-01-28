import subprocess
import time

audiovalue = subprocess.Popen("ConsoleApplication1.exe", shell=False, stdout=subprocess.PIPE)

val = audiovalue.communicate()[0].strip() == b'True'
print(val)
time.sleep(.15)
audiovalue.kill()
