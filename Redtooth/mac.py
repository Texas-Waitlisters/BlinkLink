from uuid import getnode as get_mac


mac = hex(get_mac())
print(mac)