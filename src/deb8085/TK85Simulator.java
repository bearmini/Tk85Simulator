package   deb8085;


import   java.awt.Frame;


//***************************************************************************************************
//***************************************************************************************************
/* TK85シミュレータクラス */
public class   TK85Simulator extends Thread
  {
  SimulatorParent   parent;

  Frame      frame;

  TK85Keyboard   keyboard;
  TK85LED        led;

  public CPU8085   cpu;   // CPU
  public Mem8085   mem;   // メモリ
  Parallel8255   parallelio;  // パラレルIC 8255
  DMAController8085   dmac;

  boolean   isWait = false;

  int   instructionCount = -1;
  public boolean   step=false;  // ステップ実行するかどうかのフラグ

  // パラレルICのポート番号
  static final int   PORT_A = 0xf8;
  static final int   PORT_B = 0xf9;
  static final int   PORT_C = 0xfa;
  static final int   CTRLW  = 0xfb;


  // モニタプログラム ROM データ
  static final short  ROMDATA[] =
    {
//               0,   1,   2,   3,   4,   5,   6,   7,   8,   9,   a,   b,   c,   d,   e,   f,
/*0x0000*/    0x3e,0x92,0xd3,0xfb,0xc3,0x5f,0x00,0x00,0xc3,0xb1,0x83,0x00,0x00,0x00,0x00,0x00,
/*0x0010*/    0xc3,0xb4,0x83,0x00,0x00,0x00,0x00,0x00,0xc3,0xb7,0x83,0x00,0x00,0x00,0x00,0x00,
/*0x0020*/    0xc3,0xba,0x83,0x00,0xc3,0xd3,0x01,0x00,0xc3,0xbd,0x83,0x00,0xc3,0xc0,0x83,0x00,
/*0x0030*/    0xc3,0xc3,0x83,0x00,0xc3,0x25,0xf1,0x00,0xc3,0xc6,0x83,0x00,0xc3,0x19,0x02,0x21,
/*0x0040*/    0xc9,0x83,0xaf,0x77,0x23,0x05,0xc2,0x43,0x00,0x32,0xdc,0x83,0x3e,0xc9,0x32,0xce,
/*0x0050*/    0x83,0x32,0xcb,0x83,0x3e,0xd3,0x32,0xcc,0x83,0x3e,0xdb,0x32,0xc9,0x83,0xc9,0x21,
/*0x0060*/    0x91,0x83,0x7e,0x2f,0x77,0xbe,0xca,0x6a,0x00,0x76,0x2c,0xc2,0x62,0x00,0x31,0xb1,
/*0x0070*/    0x83,0x06,0x2f,0xcd,0x3f,0x00,0x21,0x91,0x83,0x22,0xe2,0x83,0xcd,0x6c,0x05,0xcd,
/*0x0080*/    0x84,0x06,0xe6,0x10,0xca,0xbb,0x00,0x3a,0xdf,0x83,0x0f,0xda,0x95,0x00,0x3a,0xde,
/*0x0090*/    0x83,0x0f,0xd2,0x9b,0x00,0x78,0xfe,0x16,0xda,0x7c,0x00,0x78,0xe6,0x0f,0x06,0x00,
/*0x00a0*/    0x87,0x4f,0x21,0xab,0x00,0x09,0x7e,0x23,0x66,0x6f,0xe9,0x97,0x01,0xa4,0x01,0x66,
/*0x00b0*/    0x01,0x8d,0x01,0x83,0x01,0x79,0x02,0xda,0x00,0xe9,0x00,0x3a,0xde,0x83,0x0f,0xda,
/*0x00c0*/    0x12,0x01,0x3a,0xdf,0x83,0x0f,0xda,0xf2,0x00,0xcd,0xd2,0x05,0x3a,0xec,0x83,0xb0,
/*0x00d0*/    0x32,0xec,0x83,0x78,0x32,0xcf,0x83,0xc3,0x7c,0x00,205,233,  5, 50,223,131,
/*0x00e0*/    205, 41,  6,205, 57,  6,195,124,  0,205,233,  5, 50,222,131,195,
/*0x00f0*/    224,  0,120,254, 10,218,124,  0,214,  8,205,245,  5, 50,223,131,
/*0x0100*/    230,192,202,124,  0, 23, 23, 50,214,131, 62, 29, 50,213,131,195,
/*0x0110*/    124,  0,120,254,  7,210,124,  0,205,245,  5,  7, 50,222,131,120,
/*0x0120*/      6,  0,135, 79, 33, 74,  1,  9,126, 35,102,111, 34,213,131, 33,
/*0x0130*/     88,  1,  9,126, 35,102,111,126, 35,102,111, 34,236,131, 33, 29,
/*0x0140*/     29, 34,211,131,205,100,  6,195,124,  0, 15, 10, 12, 11, 14, 13,
/*0x0150*/     18, 16, 21,  5, 21, 11, 13, 11,234,131,232,131,230,131,228,131,
/*0x0160*/    226,131,240,131,242,131,205,124,  1,126, 50,236,131,205, 81,  6,
/*0x0170*/    205, 67,  6,205,109,  6,205,233,  5,195,124,  0, 42,236,131, 34,
/*0x0180*/    238,131,201, 42,238,131, 35, 34,238,131,195,105,  1, 42,238,131,
/*0x0190*/     43, 34,238,131,195,105,  1,0x2a,0xee,0x83,0x22,0xe0,0x83,0xc3,0xa4,0x01,
/*0x01a0*/    0x00,0xc3,0xcf,0x07,0x2a,0xe2,0x83,0xf9,0x2a,0xe0,0x83,0xe5,0x2a,0xea,0x83,0xe5,
/*0x01b0*/    0x2a,0xe4,0x83,0xe5,0x2a,0xe8,0x83,0xe5,0xc1,0x2a,0xe6,0x83,0xeb,0xc3,0xc3,0x01,
/*0x01c0*/    0xc3,0xdf,0x07,0xe1,0xaf,0xd3,0xfa,0x3d,0x32,0xdc,0x83,0x3e,0x98,0x30,0xd3,0xfa,
/*0x01d0*/    0xf1,0xfb,0xc9,245, 58,220,131,167,194,228,  1, 49,177,131,205,  2,
/*0x01e0*/      2,195,124,  0, 34,228,131, 33,  4,  0, 57, 34,226,131,241,225,
/*0x01f0*/     34,224,131, 49,236,131,245,197,213, 49,177,131,205,  2,  2,195,
/*0x0200*/     93,  2, 62,146,211,251,  6, 23,205, 63,  0, 33,236,131,  6, 12,
/*0x0210*/    195, 66,  0,  0,  0,  0,195,132,  6,227, 34,224,131,245,195, 38,
/*0x0220*/      2,  0,  0,195,144,  6, 33,  4,  0, 57, 34,226,131,241,225, 49,
/*0x0230*/    236,131,245,197,213,229, 49,177,131,175, 50,220,131,205,111,  2,
/*0x0240*/    202, 93,  2, 42,240,131,235, 42,224,131,205,255,  5,194,164,  1,
/*0x0250*/     42,242,131, 43, 34,242,131,205,111,  2,194,164,  1, 42,224,131,
/*0x0260*/     34,238,131, 42,234,131, 34,236,131,205, 78,  6,195,124,  0, 58,
/*0x0270*/    242,131,167,192, 58,243,131,167,201, 58,223,131,167,194,191,  2,
/*0x0280*/     58,222,131,167,194,149,  2, 42,238,131, 58,236,131,119,190,194,
/*0x0290*/     23,  6,195,131,  1, 33, 88,  1,  6,  1, 58,222,131, 15, 15,218,
/*0x02a0*/    168,  2, 35, 35,  4,195,158,  2,126, 35,102,111, 58,236,131,119,
/*0x02b0*/     35, 58,237,131,119,120,238,  7,194, 18,  1, 71,195, 18,  1,  7,
/*0x02c0*/    218,214,  2,  7,218,255,  2,  7,218,162,  3,  7,218, 34,  3,  7,
/*0x02d0*/    218,220,  4,195, 37,  4, 58,219,131,167,194,237,  2, 60, 50,219,
/*0x02e0*/    131, 58,236,131, 50,202,131,205,119,  6, 34,211,131,205,201,131,
/*0x02f0*/     50,236,131,205,119,  6, 34,207,131,205, 67,  6,195,124,  0, 58,
/*0x0300*/    219,131,167,194, 25,  3, 60, 50,219,131, 58,236,131, 50,205,131,
/*0x0310*/    205,119,  6, 34,211,131,195,227,  0, 58,236,131,205,204,131,195,
/*0x0320*/    249,  2, 58,219,131,167,194, 57,  3, 60, 50,219,131,205,124,  1,
/*0x0330*/     34,215,131,205, 81,  6,195,227,  0,175, 79, 42,236,131,235, 42,
/*0x0340*/    215,131,205, 18,  6, 65, 43, 35,112,  4,205,255,  5,194, 71,  3,
/*0x0350*/     65, 42,215,131, 43, 35,120,190,194,123,  3,  4,205,255,  5,194,
/*0x0360*/     85,  3, 12,194, 63,  3,205, 41,  6, 33, 20,  9, 34,209,131, 33,
/*0x0370*/     13, 20, 34,207,131,205,233,  5,195,124,  0, 34,238,131, 96,110,
/*0x0380*/     34,236,131,197,213,229,205, 78,  6,205,108,  5,205,132,  6,225,
/*0x0390*/    209,193,254, 21, 42,238,131,202, 91,  3, 71,205,233,  5,120,195,
/*0x03a0*/    130,  0, 58,219,131,167,194,182,  3, 60, 50,219,131,205,124,  1,
/*0x03b0*/    205, 81,  6,195,227,  0, 15,210,210,  3, 62,  2, 50,219,131, 42,
/*0x03c0*/    236,131, 34,215,131,235, 42,238,131, 34,217,131,205, 18,  6,195,
/*0x03d0*/    224,  0,205,124,  1,229,205, 81,  6,205, 57,  6,205,108,  5, 42,
/*0x03e0*/    215,131,229,193,209, 42,217,131,205,255,  5,210,  7,  4, 43, 27,
/*0x03f0*/     35, 19,126, 18,205, 12,  6,194,240,  3,235, 34,236,131,205,100,
/*0x0400*/      6,205,233,  5,195,124,  0,197,197,229,193,225, 25,125,145, 95,
/*0x0410*/    124,152, 87,225,213, 35, 19, 43, 27,126, 18,205, 12,  6,194, 23,
/*0x0420*/      4,209,195,250,  3, 58,219,131,167,194, 57,  4, 60, 50,219,131,
/*0x0430*/     58,236,131, 50,215,131,195,224,  0, 15,210, 75,  4, 62,  2, 50,
/*0x0440*/    219,131,205,124,  1,205, 81,  6,195,227,  0, 42,236,131,235, 42,
/*0x0450*/    238,131,205, 18,  6,213,229, 33, 29,  5, 34,213,131, 58,215,131,
/*0x0460*/    205,119,  6, 34,211,131,205, 57,  6,205,108,  5, 14,  0,205,231,
/*0x0470*/      6,190, 62, 85,  6, 55,205,  1,  7, 35, 43,190, 58,215,131,  6,
/*0x0480*/     53,205,210,  4,  0,  0,225,209,124,  6, 53,205,210,  4,125,  6,
/*0x0490*/     55,205,210,  4,122,  6, 55,205,210,  4,123,  6, 55,205,210,  4,
/*0x04a0*/     52, 53,121, 47, 60,  6, 53,205,210,  4, 58,236,131,  6, 53, 43,
/*0x04b0*/     35,126,205,210,  4,219,248,  6, 49,205,255,  5,194,176,  4,  0,
/*0x04c0*/    190,121, 47, 60,  6, 48,205,210,  4,205, 57,  6,205,233,  5,195,
/*0x04d0*/    124,  0,245,129, 79,241, 50,255,131,195,  1,  7, 58,236,131,245,
/*0x04e0*/    205,119,  6, 34,211,131, 33, 29,  5, 34,213,131,205, 57,  6,205,
/*0x04f0*/    108,  5, 14,  0,205,101,  7,205,131,  7,254, 85,194,239,  4,205,
/*0x0500*/     97,  5, 87,241,186,202, 19,  5,245,122,205,119,  6, 34,207,131,
/*0x0510*/    195,239,  4,175, 50,254,131, 62,113, 50,248,131,205, 97,  5,103,
/*0x0520*/    205, 97,  5,111,205, 97,  5, 87,205, 97,  5, 95,205, 97,  5,194,
/*0x0530*/     86,  5,229, 43, 35,205, 97,  5,119,205,255,  5,194, 52,  5,205,
/*0x0540*/     97,  5,225,194, 86,  5, 34,238,131,235, 34,236,131,205, 78,  6,
/*0x0550*/    205,233,  5,195,124,  0, 62, 14, 50,214,131,205,233,  5,195,227,
/*0x0560*/      0,205,131,  7, 50,255,131, 71,129, 79,120,201,0x21,0xd6,0x83,0x11,
/*0x0570*/    0xf8,0x83,0x01,0xb3,0x05,0x3a,0xdf,0x83,0xe6,0x3c,0x07,0x07,0xf5,0x7e,0xe5,0x26,
/*0x0580*/    0x00,0x6f,0x09,0x7e,0x12,0xe1,0xf1,0x07,0xf5,0xdc,0xae,0x05,0x2b,0x1c,0xc2,0x7d,
/*0x0590*/    0x05,0xf1,0x3a,0xdf,0x83,0x0f,0xd2,0xa6,0x05,0x06,0x04,0x11,0xf8,0x83,0xcd,0xae,
/*0x05a0*/    0x05,0x13,0x05,0xc2,0x9e,0x05,0x3a,0xde,0x83,0x0f,0xd0,0x11,0xfc,0x83,0x1a,0xf6,
/*0x05b0*/    0x80,0x12,0xc9,0x3f,0x06,0x5b,0x4f,0x66,0x6d,0x7d,0x27,0x7f,0x6f,0x77,0x7c,0x39,
/*0x05c0*/    0x5e,0x79,0x71,0x76,0x1e,0x38,0x54,0x5c,0x73,0x67,0x50,0x3e,0x1c,0x6e,0x74,0x00,
/*0x05d0*/    0x40,0x80, 42,208,131, 34,209,131, 58,207,131, 50,208,131, 42,236,
/*0x05e0*/    131, 41, 41, 41, 41, 34,236,131,201, 33,  0,  0, 34,219,131, 34,
/*0x05f0*/    222,131, 62,  1,201, 60, 79, 62,128,  7, 13,194,249,  5,201,122,
/*0x0600*/    188,194,  8,  6,  0,123,189,201,204,  0,  0,201,120,188,192,121,
/*0x0610*/    189,201,205,255,  5,208,225, 33, 14, 28, 34,213,131, 33, 23, 23,
/*0x0620*/     34,211,131,205,233,  5,195,227,  0, 33, 28, 28, 34,213,131, 34,
/*0x0630*/    211,131, 33,  0,  0, 34,238,131,201, 33, 28, 28, 34,207,131,175,
/*0x0640*/     50,236,131, 33, 28, 28, 34,209,131,175, 50,237,131,201,205,100,
/*0x0650*/      6, 58,239,131,205,119,  6, 34,213,131, 58,238,131,205,119,  6,
/*0x0660*/     34,211,131,201, 58,237,131,205,119,  6, 34,209,131, 58,236,131,
/*0x0670*/    205,119,  6, 34,207,131,201, 71,230, 15,111,120, 15, 15, 15, 15,
/*0x0680*/    230, 15,103,201,205,144,  6, 58,221,131,167,202,132,  6,120,201,
/*0x0690*/    205,188,  6, 60,202,183,  6, 22, 14, 30,  0,205,189,  7, 21,194,
/*0x06a0*/    153,  6,205,188,  6, 71, 60,202,183,  6, 58,221,131,167,194,151,
/*0x06b0*/      6, 61, 50,221,131,120,201,  6,255,195,178,  6,175, 87, 71, 62,
/*0x06c0*/    239,205,212,  6,  6,  8, 62,223,205,212,  6,  6, 16, 62,191,205,
/*0x06d0*/    212,  6, 61,201,211,250,219,248, 47,167,200,225, 15,218,228,  6,
/*0x06e0*/     20,195,220,  6,122,176,201,197,229, 33,112, 23,205, 59,  7, 70,
/*0x06f0*/      6, 28, 45,194,236,  6,219,248,  6, 26, 37,194,236,  6,225,193,
/*0x0700*/    201,197,213,229, 79,205, 74,  7, 17, 62, 25,126, 46,  8,121, 31,
/*0x0710*/     79,218, 30,  7, 67,219,248,126,205, 74,  7,195, 40,  7, 66,219,
/*0x0720*/    248,219,248,205, 59,  7,219,248, 45,194, 14,  7,  6, 28,205, 59,
/*0x0730*/      7,126,  6, 29,205, 59,  7,225,209,193,201, 62,216,205, 89,  7,
/*0x0740*/    205, 87,  7,205, 87,  7,205, 87,  7,201, 62,216,205, 89,  7,  0,
/*0x0750*/      0,  6, 68,205, 89,  7,201,  6, 32,  5,194, 89,  7,  3, 11,  0,
/*0x0760*/     23, 63, 31, 48,201,197,213, 32, 71, 22,  0, 30,  0,205,198,  7,
/*0x0770*/     62, 31,187,218,105,  7, 62,  8,187,210,105,  7, 20,194,107,  7,
/*0x0780*/    209,193,201,197,213,229, 32, 71, 30,  0,205,198,  7, 62, 32,187,
/*0x0790*/    210,136,  7, 62, 74,205,191,  7, 46,  8,205,172,  7,124, 31,103,
/*0x07a0*/    205,189,  7, 45,194,154,  7,124,225,209,193,201,205,198,  7, 62,
/*0x07b0*/     53, 30,  0,205,191,  7,205,198,  7, 62, 55,187,201,0xc9, 71, 28,
/*0x07c0*/      0,187,210,191,  7,201, 28, 32,168,202,198,  7,168, 71,201, 33,
/*0x07d0*/    239,131, 17,244,131,  6,  4,126, 18, 43, 19,  5,194,215,  7,0x06,
/*0x07e0*/    0x04,0x21,0xf4,0x83,0x11,0xd6,0x83,0x7e,0xcd,0xf7,0x07,0x7e,0xcd,0xfb,0x07,0x23,
/*0x07f0*/    0x05,0xc2,0xe7,0x07,0xc3,0x6c,0x05,0x0f,0x0f,0x0f,0x0f,0xe6,0x0f,0x12,0x1b,0xc9,
    };


  //***************************************************************************************************
  // コンストラクタ
  public   TK85Simulator( SimulatorParent   parent, Frame   frame, TK85LED   led, TK85Keyboard   keyboard )
    {
    this.parent = parent;
    this.frame  = frame;

    // CPU・メモリ領域を作成
    mem = new Mem8085();
    cpu = new CPU8085( mem, new PublicLabelList(), new BreakPointList() );

    // ROM データをロード
    for( int   i = 0; i<0x800; i++ )
      {
      mem.setValue( i, ROMDATA[i] );
      mem.setReadOnly( i, true );
      }

    // キーボードを接続
    parallelio = new Parallel8255();

    cpu.ioport.assignInputDevice(  PORT_A, parallelio );
    cpu.ioport.assignInputDevice(  PORT_B, parallelio );
    cpu.ioport.assignInputDevice(  PORT_C, parallelio );
    cpu.ioport.assignInputDevice(  CTRLW,  parallelio );
    cpu.ioport.assignOutputDevice( PORT_A, parallelio );
    cpu.ioport.assignOutputDevice( PORT_B, parallelio );
    cpu.ioport.assignOutputDevice( PORT_C, parallelio );
    cpu.ioport.assignOutputDevice( CTRLW,  parallelio );

    this.keyboard = keyboard;
    this.keyboard.simulator = this;
    parallelio.assignInputDevice(  Parallel8255.A, this.keyboard );
    parallelio.assignOutputDevice( Parallel8255.C, this.keyboard );

    // LED を接続
    this.led = led;
    dmac = new DMAController8085( cpu );
    dmac.assignDMADevice( led, 0x83f8, 8 );
    }






  // ***************************************************************************************************
  // CPU の状態をしらべ、もし Halt 状態ならシステムを停止する。
  synchronized void   checkCPU_and_halt()
    {
    if( cpu.isHalted() )
      {
      try
        {
        isWait = true;
        this.wait();
        }
      catch( InterruptedException   e )
        {
        }
      }
    }


  // ***************************************************************************************************
  // システムを再開する。
  synchronized void   restart()
    {
    this.notify();
    isWait = false;
    }




  // ***************************************************************************************************
  // シミュレーションする
  public void   run()
    {
    while( true )
      {
      try
        {
        cpu.execute( cpu.decode( cpu.fetch() ) );
        dmac.memoryModifyCheck();

        checkInstructionCount();
        checkCPU_and_halt();

        }
      catch( OnBreakPointException   e )
        {
        }
      }

    }




  //***************************************************************************************************
  // 命令数を数え始める
  public void   startCountInstruction()
    {
    instructionCount = 5;
    }

  //***************************************************************************************************
  // 命令数を数えるのをやめる
  public void   stopCountInstruction()
    {
    instructionCount = -1;
    }

  //***************************************************************************************************
  // 命令数を数え、チェックする
  public void   checkInstructionCount()
    {
    // カウントダウン
    if( instructionCount > 0 )
      instructionCount--;

    // ０になって、STEP 動作だったら、RST7.5 割り込み
    if( instructionCount == 0 )
      {
      instructionCount = -1;
      if( step )
        cpu.interruptRST75();
      }
    }


  }






