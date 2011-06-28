package com.bearmini.tk85.base8085;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU 用 I/Oポートクラス */
public class IOPort8085 {
    private CPU8085 parent;

    private InputDevice[] inputDevices;
    private OutputDevice[] outputDevices;

    // ***************************************************************************************************
    public IOPort8085(CPU8085 parent) {
        inputDevices = new InputDevice[256];
        outputDevices = new OutputDevice[256];
    }

    // ***************************************************************************************************
    public boolean assignInputDevice(int portnum, InputDevice dev) {
        if (inputDevices[portnum] == null) {
            inputDevices[portnum] = dev;
            return true;
        } else {
            return false;
        }
    }

    // ***************************************************************************************************
    public boolean assignOutputDevice(int portnum, OutputDevice dev) {
        if (outputDevices[portnum] == null) {
            outputDevices[portnum] = dev;
            return true;
        } else {
            return false;
        }
    }

    // ***************************************************************************************************
    public void removeInputDevice(int portnum) {
        inputDevices[portnum] = null;
    }

    public void removeOutputDevice(int portnum) {
        outputDevices[portnum] = null;
    }

    // ***************************************************************************************************
    public int in(int portnum) {
        if (inputDevices[portnum] == null)
            return 0xFF;
        else
            return inputDevices[portnum].in(portnum);
    }

    // ***************************************************************************************************
    public void out(int portnum, int val) {
        if (outputDevices[portnum] != null)
            outputDevices[portnum].out(portnum, val);
    }

}
