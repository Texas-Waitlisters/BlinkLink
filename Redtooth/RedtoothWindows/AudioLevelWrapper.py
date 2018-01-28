import ctypes


def checkaudio():
    audiovalue = ctypes.DLL('Redtooth\CScore\CSCore.dll')
    print(audiovalue)
    #return audiovalue


print("hi")

