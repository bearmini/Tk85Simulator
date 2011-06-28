package com.bearmini.tk85.base8085;

public class Parallel8255 implements InputDevice, OutputDevice {

    private ParallelInputDevice input_dev[] = new ParallelInputDevice[3];
    private ParallelOutputDevice output_dev[] = new ParallelOutputDevice[3];

    public static final int A = 0;
    public static final int B = 1;
    public static final int C = 2;

    private static final int PORT_A = 0xF8;
    private static final int PORT_B = 0xF9;
    private static final int PORT_C = 0xFA;
    private static final int CTRLWD = 0xFB;

    private static final int RESET = 0x00;

    // コントロールワードに指定する値

    private static final int MODE_SET_MODE = 0x80;
    private static final int MODE_SET_BIT = 0x00;

    private static final int GA_MODE_0 = 0x00;
    private static final int GA_MODE_1 = 0x20;
    private static final int GA_MODE_2 = 0x60;
    private static final int GB_MODE_0 = 0x00;
    private static final int GB_MODE_1 = 0x04;

    private int pa_mode = GA_MODE_0;
    private int pb_mode = GB_MODE_0;
    private int pcu_mode = GA_MODE_0;
    private int pcl_mode = GB_MODE_0;

    private static final int PORT_A_INPUT = 0x10;
    private static final int PORT_B_INPUT = 0x02;
    private static final int PORT_CU_INPUT = 0x08;
    private static final int PORT_CL_INPUT = 0x01;

    private static final int PORT_A_OUTPUT = 0x00;
    private static final int PORT_B_OUTPUT = 0x00;
    private static final int PORT_CU_OUTPUT = 0x00;
    private static final int PORT_CL_OUTPUT = 0x00;

    private int pa_io = PORT_A_INPUT;
    private int pb_io = PORT_B_INPUT;
    private int pcu_io = PORT_CU_OUTPUT;
    private int pcl_io = PORT_CL_OUTPUT;

    // コンストラクタ
    public Parallel8255() {
    }

    // デバイスを接続
    public void assignInputDevice(int port, ParallelInputDevice input_dev) {
        this.input_dev[port] = input_dev;
    }

    public void assignOutputDevice(int port, ParallelOutputDevice output_dev) {
        this.output_dev[port] = output_dev;
    }

    // ポートに接続されたデバイスから入力
    public int in(int portnum) {
        switch (portnum) {
        case PORT_A: {
            if (input_dev[A] == null)
                break;

            if (pa_io == PORT_A_INPUT)
                return input_dev[A].in();
        }
            break;
        case PORT_B: {
        }
            break;
        case PORT_C: {
        }
            break;
        case CTRLWD: {
            // if( val == RESET )
        }
            break;
        }
        return -1; // Error
    }

    public void out(int portnum, int val) {

        switch (portnum) {
        case PORT_A: {
        }
            break;
        case PORT_B: {
        }
            break;
        case PORT_C: {
            if (output_dev[C] == null)
                break;

            if (pcu_io == PORT_CU_OUTPUT && pcl_io == PORT_CL_OUTPUT)
                output_dev[C].out(val);
        }
            break;
        case CTRLWD: {
            // if( val == RESET )
        }
            break;

        }

    }

}
