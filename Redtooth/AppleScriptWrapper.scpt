--APPLESCRIPT
--bluetooth_powerstate(0) -- off
bluetooth_powerstate(1) -- on
--bluetooth_powerstate(-1) -- toggle
--bluetooth_powerstate(9) -- query

on bluetooth_powerstate(m)
    (*
        integer m : operation mode
            0  = off
            1  = on
            -1 = toggle
            9  = query (print current state)
        return integer : resulting state
    *)
    if m = 9 then set m to ""
    do shell script "/usr/bin/python <<'EOF' - " & m & "
# coding: utf-8
# 
#   file:
#       bluetooth_powerstate.py
# 
#   function:
#       get or set bluetooth power state
# 
#   usaeg:
#       ./bluetooth_powerstate [mode]
#           mode :
#               0  = off
#               1  = on
#               -1 = toggle
#                  = query (print current state)
#   version:
#       0.10
# 
import sys, objc
import time
from CoreFoundation import *

IOBT_BRIDGESUPPORT = '''<?xml version=\"1.0\" standalone=\"yes\"?>
<!DOCTYPE signatures SYSTEM \"file://localhost/System/Library/DTDs/BridgeSupport.dtd\">
<signatures version=\"0.9\">
    <function name=\"IOBluetoothPreferenceGetControllerPowerState\">
        <retval type=\"i\"></retval>
    </function>
    <function name=\"IOBluetoothPreferenceSetControllerPowerState\">
        <arg type=\"i\"></arg>
        <retval type=\"i\"></retval>
    </function>
</signatures>'''

objc.initFrameworkWrapper(
    frameworkName=\"IOBluetooth\",
    frameworkIdentifier=\"com.apple.Bluetooth\",
    frameworkPath=objc.pathForFramework('/System/Library/Frameworks/IOBluetooth.framework'),
    globals=globals()
)
objc.parseBridgeSupport(
    IOBT_BRIDGESUPPORT, 
    globals(), 
    objc.pathForFramework('/System/Library/Frameworks/IOBluetooth.framework')
)

def set_ioblpstate(s):
    #   int s : 0 = off, 1 = on
    IOBluetoothPreferenceSetControllerPowerState(s)
    s1 = -1
    for i in range(50):
        s1 = get_ioblpstate()
        if s1 == s:
            break
        time.sleep(0.1)
    if s1 != s:
        sys.stderr.write('Unable to set bluetooth power state to %s\\n' % ('off' if s == 0 else 'on').encode('utf-8'))
        sys.exit(1)
    return s1

def get_ioblpstate():
    return IOBluetoothPreferenceGetControllerPowerState()

def main():
    m = [ a.decode('utf-8') for a in sys.argv[1:] ]
    if m == []:
        print '%d' % get_ioblpstate()
    elif m == ['0']:
        print '%d' % set_ioblpstate(0)
    elif m == ['1']:
        print '%d' % set_ioblpstate(1)
    elif m == ['-1']:
        print '%d' % set_ioblpstate(1 if get_ioblpstate() == 0 else 0)
    else:
        sys.stderr.write('Usage: %s [mode]\\n\\t%s, %s, %s, %s\\n' % (
            sys.argv[0], 'mode: 0 = off', '1 = on', '-1 = toggle', '(void) = query')
        )
        sys.exit(1)
    sys.exit(0)

main()
EOF"
    result + 0
end bluetooth_powerstate
--END OF APPLESCRIPT