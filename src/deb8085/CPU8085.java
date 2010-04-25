package   deb8085;



import   java.util.StringTokenizer;

import   deb8085.instr.*;


//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU �G�~�����[�^�N���X */
public class   CPU8085
  {
  public Reg8085   reg;        // ���W�X�^
  public Mem8085   mem;        // �������̈�
  public Decoder8085   dec;    // �f�R�[�_
  public IOPort8085   ioport;  // I/O�|�[�g

  // �p�u���b�N���x��
  public PublicLabelList   publicLabels;

  // �u���[�N�|�C���g
  public BreakPointList   breakPoints;

  private boolean   halted;   // CPU �͒�~���Ă��邩

  short   oldAreg;            // �t���O�ω��̂Ƃ��Q�Ƃ��邽�߂ɕۑ����Ă����A���ߎ��s�O�� A ���W�X�^
  public boolean   subflag;   // DAA �����s����Ƃ��A���Z��10�i�␳�����邩(false)�A���Z��10�i�␳�����邩(true)
  public boolean   interruptEnabled;  // ���荞�݂�������Ă��邩


  // ���荞�܂ꂽ���ǂ����`�F�b�N����t���O
  boolean   interruptedTRAP  = false;
  boolean   interruptedRST75 = false;
  boolean   interruptedRST65 = false;
  boolean   interruptedRST55 = false;
  boolean   interruptedINTR  = false;

  // ���荞�݃}�X�N
  public boolean   interruptMaskedRST75 = false;
  public boolean   interruptMaskedRST65 = false;
  public boolean   interruptMaskedRST55 = false;



  //***************************************************************************************************
  // �R���X�g���N�^
  public   CPU8085( Mem8085   mem, PublicLabelList   publicLabels, BreakPointList   breakPoints )
    {
    // CPU���p�[�c�i���W�X�^�ƃf�R�[�_��I/O�|�[�g�j�𐶐�
    reg = new Reg8085( this );
    dec = new Decoder8085( this );
    ioport = new IOPort8085( this );

    // �������ւ̎Q�Ƃ��擾
    this.mem = mem;

    // �p�u���b�N���x�����X�g�ւ̎Q�Ƃ��擾
    this.publicLabels = publicLabels;

    // �u���[�N�|�C���g���X�g�ւ̎Q�Ƃ��擾
    this.breakPoints  = breakPoints;
    }



  //***************************************************************************************************
  // �P���ߎ擾    ���݂� PC �l�̈ʒu���疽�߂��t�F�b�`
  public short  fetch()
    {
    return   mem.getValue( reg.getReg(reg.PC) );
    }



  //***************************************************************************************************
  // 1���߃f�R�[�h
  public Instruction8085   decode( short   code )
    {
    Instruction8085   inst = dec.decode( code );
    if( inst.getSize() == 2 )
      inst.setB2( (short)mem.getValue( reg.getReg(reg.PC)+1 ) );
    else if( inst.getSize() == 3 )
      {
      inst.setB2( (short)mem.getValue( reg.getReg(reg.PC)+1 ) );
      inst.setB3( (short)mem.getValue( reg.getReg(reg.PC)+2 ) );
      }
    return   inst;
    }



  //***************************************************************************************************
  // �P���ߎ��s
  public void  execute( Instruction8085   inst ) throws OnBreakPointException
    {
    if( isHalted() )
      return;

    // ���ߎ��s����
    StringTokenizer   st = new StringTokenizer( inst.getMnemonic(), " " );
    String   opecode = st.nextToken();
    String   operands;

    if( st.hasMoreTokens() )
      operands = st.nextToken();
    else
      operands = "";

    inst.setOperands( operands );

    saveAreg();

    // ���ߎ��s
    synchronized( this )
      {
      inst.execute();
      }

    // �u���[�N�|�C���g�̃`�F�b�N
    if( breakPoints.existBreakPoint( reg.getReg(reg.PC) ) )
      {
      halted = true;
      throw new OnBreakPointException( "Break point at address "+util.hex4(reg.getReg(reg.PC)) + "." );
      }

    //���荞�݃t���O�̃��Z�b�g
    resetInterruptFlag();

    }






  //***************************************************************************************************
  // ���荞�݃t���O�̃��Z�b�g
  public void   resetInterruptFlag()
    {
    // ���荞�݃t���O��߂�
    // �Ă������A�D�揇�ʂ̒Ⴂ���荞�݂́A�󗝂����܂Ŗ߂��Ȃ��H
    interruptedTRAP  = false;
    interruptedRST75 = false;
    interruptedRST65 = false;
    interruptedRST55 = false;
    interruptedINTR  = false;
    }




  //***************************************************************************************************
  // ���荞��
  //***************************************************************************************************


  //***************************************************************************************************
  // ���荞�݌�̃��^�[���A�h���X���X�^�b�N�ɕۑ�
  void   saveReturnAddr()
    {
    // ���荞�݂��֎~�I�I
    interruptEnabled = false;

    int     sp  = reg.getReg( Reg8085.SP );
    int     pc  = reg.getReg( Reg8085.PC );
    short   pcH = (short)((pc&0xFF00)>>>8);
    short   pcL = (short)( pc&0x00FF);

    mem.setValue( sp-1, pcH );
    mem.setValue( sp-2, pcL );
    reg.setReg( Reg8085.SP, sp-2 );
    }


  //***************************************************************************************************
  // TRAP ���荞��
  public void   interruptTRAP()
    {
    interruptedTRAP  = true;
    reg.setReg( Reg8085.PC, 0x0024 );
    restart();
    }

  //***************************************************************************************************
  // RST7.5 ���荞��
  public void   interruptRST75()
    {
    if( interruptEnabled && !interruptMaskedRST75 && !interruptedTRAP )
      {
      saveReturnAddr();
      interruptedRST75 = true;
      reg.setReg( Reg8085.PC, 0x003C );
      restart();
      }
    }

  //***************************************************************************************************
  // RST6.5 ���荞��
  public void   interruptRST65()
    {
    if( interruptEnabled && !interruptMaskedRST65 && !interruptedTRAP && !interruptedRST75 )
      {
      saveReturnAddr();
      interruptedRST65 = true;
      reg.setReg( Reg8085.PC, 0x0034 );
      restart();
      }
    }

  //***************************************************************************************************
  // RST5.5 ���荞��
  public void   interruptRST55()
    {
    if( interruptEnabled && !interruptMaskedRST55 && !interruptedTRAP && !interruptedRST75 && !interruptedRST65 )
      {
      saveReturnAddr();
      interruptedRST55 = true;
      reg.setReg( Reg8085.PC, 0x002c );
      restart();
      }
    }

  //***************************************************************************************************
  // INTR ���荞��
  public void   interruptINTR( int   addr )
    {
    if( interruptEnabled && !interruptedTRAP && !interruptedRST75 && !interruptedRST65 && !interruptedRST55 )
      {
      saveReturnAddr();
      interruptedINTR  = true;
      reg.setReg( Reg8085.PC, addr );
      restart();
      }
    }


  //***************************************************************************************************
  // CPU �� Reset ����
  public void   reset()
    {
    reg.setReg( Reg8085.PC, 0x0000 );
    interruptEnabled = false;
    restart();
    }




  //***************************************************************************************************
  // CPU �� Halt ����Ă��邩
  public boolean  isHalted()
    {
    return   halted;
    }


  //***************************************************************************************************
  // CPU �� Halt ����
  public void   halt()
    {
    halted = true;
    }


  //***************************************************************************************************
  // CPU �� Halt ����������
  public void   restart()
    {
    halted = false;
    }


  //***************************************************************************************************
  // �v���O�����J�E���^�� d ���₷
  public void   incPC( int   d )
    {
    reg.setReg( Reg8085.PC, reg.getReg(reg.PC)+d );
    }



  //***************************************************************************************************
  // �t���O�X�V�萔
  public final static int   FLAGUPDATE_ALL        = 0x01;
  public final static int   FLAGUPDATE_NO_CARRY   = 0x02;
  public final static int   FLAGUPDATE_CARRY_ONLY = 0x04;
  public final static int   FLAGUPDATE_CARRY_0    = 0x08;

  public final static int   FLAGUPDATE_INRDCR     = 0x10;

  public final static int   FLAGUPDATE_SET_SUB    = 0x100;
  public final static int   FLAGUPDATE_RESET_SUB  = 0x200;

  // �t���O�̍X�V
  public void   updateFlags( int   status )
    {
    if( ( status & FLAGUPDATE_ALL ) != 0 )
      {
      reg.setFlag( Reg8085.Zf,  reg.getReg( Reg8085.A ) == 0 );
      reg.setFlag( Reg8085.Cf,  ( reg.getReg( Reg8085.A ) & 0x80 ) != ( oldAreg & 0x80 ) );
      reg.setFlag( Reg8085.Sf,  reg.getReg( Reg8085.A ) > 0x7F );
      reg.setFlag( Reg8085.Pf,  parityCheck( reg.getReg( Reg8085.A ) ) );
      reg.setFlag( Reg8085.ACf, ( reg.getReg( Reg8085.A ) & 0x08 ) != ( oldAreg & 0x08 ) );
      }
    else if( ( status & FLAGUPDATE_NO_CARRY ) != 0 )
      {
      reg.setFlag( Reg8085.Zf,  reg.getReg( Reg8085.A ) == 0 );
//    reg.setFlag( Reg8085.Cf,  ( reg.getReg( Reg8085.A ) & 0x80 ) != oldAreg & 0x80 );
      reg.setFlag( Reg8085.Sf,  reg.getReg( Reg8085.A ) > 0x7F );
      reg.setFlag( Reg8085.Pf,  parityCheck( reg.getReg( Reg8085.A ) ) );
      reg.setFlag( Reg8085.ACf, ( reg.getReg( Reg8085.A ) & 0x08 ) != ( oldAreg & 0x08 ) );
      }
    else if( ( status & FLAGUPDATE_CARRY_ONLY ) != 0 )
      {
      reg.setFlag( Reg8085.Cf,( reg.getReg( Reg8085.A ) & 0x80 ) != ( oldAreg & 0x80  ) );
      }
    else if( ( status & FLAGUPDATE_CARRY_0 ) != 0 )
      {
      reg.setFlag( Reg8085.Zf,  reg.getReg( Reg8085.A ) == 0 );
      reg.setFlag( Reg8085.Cf,  false );
      reg.setFlag( Reg8085.Sf,  reg.getReg( Reg8085.A ) > 0x7F );
      reg.setFlag( Reg8085.Pf,  parityCheck( reg.getReg( Reg8085.A ) ) );
      reg.setFlag( Reg8085.ACf, ( reg.getReg( Reg8085.A ) & 0x08 ) != ( oldAreg & 0x08 ) );
      }

    if( ( status & FLAGUPDATE_SET_SUB ) != 0 )
      {
      subflag = true;
      }
    else if( ( status & FLAGUPDATE_RESET_SUB ) != 0 )
      {
      subflag = false;
      }


    }



  // �t���O�̍X�V(INR/DCR�p)
  public void   updateFlags( int   status, byte   r )
    {
    short   regVal = reg.getReg( r );

    if( ( status & FLAGUPDATE_INRDCR ) != 0 )
      {
      reg.setFlag( Reg8085.Zf,  regVal == 0 );
      reg.setFlag( Reg8085.Sf,  regVal > 0x7F );
      reg.setFlag( Reg8085.Pf,  parityCheck( regVal ) );

      /* AC �t���O�̋����͕s�� */
      reg.setFlag( Reg8085.ACf, ( reg.getReg( Reg8085.A ) & 0x08 ) != ( oldAreg & 0x08 ) );
      }
    }


  //***************************************************************************************************
  // �p���e�B�`�F�b�N  �����̂Ƃ��� 1(true)
  public boolean   parityCheck( short   regA )
    {
    int   cnt;
    for( cnt = 0; regA != 0; regA >>>= 1 )
      if( ( regA & 0x01 ) == 1 )
        cnt++;
    return   ( cnt % 2 )==0;
    }



  //***************************************************************************************************
  // A ���W�X�^�ۑ�
  public void   saveAreg()
    {
    oldAreg = reg.getReg( Reg8085.A );
    }


  // A ���W�X�^���A
  public void   restoreAreg()
    {
    reg.setReg( Reg8085.A, oldAreg );
    }


  }









//***************************************************************************************************
//***************************************************************************************************
/* 8085CPU �p �f�R�[�_ */
class   Decoder8085
  {
  CPU8085   cpu;

  // �f�R�[�h�e�[�u��
  Instruction8085   table[] = new Instruction8085[256];


  // �R���X�g���N�^
  public   Decoder8085( CPU8085   cpu )
    {
    this.cpu = cpu;

    table[0x00] = new InstructionNOP(  cpu, (byte)0x00, "NOP",     (byte)1 );
    table[0x01] = new InstructionLXI(  cpu, (byte)0x01, "LXI B",   (byte)3 );
    table[0x02] = new InstructionSTAX( cpu, (byte)0x02, "STAX B",  (byte)1 );
    table[0x03] = new InstructionINX(  cpu, (byte)0x03, "INX B",   (byte)1 );
    table[0x04] = new InstructionINR(  cpu, (byte)0x04, "INR B",   (byte)1 );
    table[0x05] = new InstructionDCR(  cpu, (byte)0x05, "DCR B",   (byte)1 );
    table[0x06] = new InstructionMVI(  cpu, (byte)0x06, "MVI B",   (byte)2 );
    table[0x07] = new InstructionRLC(  cpu, (byte)0x07, "RLC",     (byte)1 );
    table[0x08] = new InstructionNOP(  cpu, (byte)0x08, "???",     (byte)1 );
    table[0x09] = new InstructionDAD(  cpu, (byte)0x09, "DAD B",   (byte)1 );
    table[0x0A] = new InstructionLDAX( cpu, (byte)0x0A, "LDAX B",  (byte)1 );
    table[0x0B] = new InstructionDCX(  cpu, (byte)0x0B, "DCX B",   (byte)1 );
    table[0x0C] = new InstructionINR(  cpu, (byte)0x0C, "INR C",   (byte)1 );
    table[0x0D] = new InstructionDCR(  cpu, (byte)0x0D, "DCR C",   (byte)1 );
    table[0x0E] = new InstructionMVI(  cpu, (byte)0x0E, "MVI C",   (byte)2 );
    table[0x0F] = new InstructionRRC(  cpu, (byte)0x0F, "RRC",     (byte)1 );
    table[0x10] = new InstructionNOP(  cpu, (byte)0x10, "???",     (byte)1 );
    table[0x11] = new InstructionLXI(  cpu, (byte)0x11, "LXI D",   (byte)3 );
    table[0x12] = new InstructionSTAX( cpu, (byte)0x12, "STAX D",  (byte)1 );
    table[0x13] = new InstructionINX(  cpu, (byte)0x13, "INX D",   (byte)1 );
    table[0x14] = new InstructionINR(  cpu, (byte)0x14, "INR D",   (byte)1 );
    table[0x15] = new InstructionDCR(  cpu, (byte)0x15, "DCR D",   (byte)1 );
    table[0x16] = new InstructionMVI(  cpu, (byte)0x16, "MVI D",   (byte)2 );
    table[0x17] = new InstructionRAL(  cpu, (byte)0x17, "RAL",     (byte)1 );
    table[0x18] = new InstructionNOP(  cpu, (byte)0x18, "???",     (byte)1 );
    table[0x19] = new InstructionDAD(  cpu, (byte)0x19, "DAD D",   (byte)1 );
    table[0x1A] = new InstructionLDAX( cpu, (byte)0x1A, "LDAX D",  (byte)1 );
    table[0x1B] = new InstructionDCX(  cpu, (byte)0x1B, "DCX D",   (byte)1 );
    table[0x1C] = new InstructionINR(  cpu, (byte)0x1C, "INR E",   (byte)1 );
    table[0x1D] = new InstructionDCR(  cpu, (byte)0x1D, "DCR E",   (byte)1 );
    table[0x1E] = new InstructionMVI(  cpu, (byte)0x1E, "MVI E",   (byte)2 );
    table[0x1F] = new InstructionRAR(  cpu, (byte)0x1F, "RAR",     (byte)1 );
    table[0x20] = new InstructionRIM(  cpu, (byte)0x20, "RIM",     (byte)1 );
    table[0x21] = new InstructionLXI(  cpu, (byte)0x21, "LXI H",   (byte)3 );
    table[0x22] = new InstructionSHLD( cpu, (byte)0x22, "SHLD",    (byte)3 );
    table[0x23] = new InstructionINX(  cpu, (byte)0x23, "INX H",   (byte)1 );
    table[0x24] = new InstructionINR(  cpu, (byte)0x24, "INR H",   (byte)1 );
    table[0x25] = new InstructionDCR(  cpu, (byte)0x25, "DCR H",   (byte)1 );
    table[0x26] = new InstructionMVI(  cpu, (byte)0x26, "MVI H",   (byte)2 );
    table[0x27] = new InstructionDAA(  cpu, (byte)0x27, "DAA",     (byte)1 );
    table[0x28] = new InstructionNOP(  cpu, (byte)0x28, "???",     (byte)1 );
    table[0x29] = new InstructionDAD(  cpu, (byte)0x29, "DAD H",   (byte)1 );
    table[0x2A] = new InstructionLHLD( cpu, (byte)0x2A, "LHLD",    (byte)3 );
    table[0x2B] = new InstructionDCX(  cpu, (byte)0x2B, "DCX H",   (byte)1 );
    table[0x2C] = new InstructionINR(  cpu, (byte)0x2C, "INR L",   (byte)1 );
    table[0x2D] = new InstructionDCR(  cpu, (byte)0x2D, "DCR L",   (byte)1 );
    table[0x2E] = new InstructionMVI(  cpu, (byte)0x2E, "MVI L",   (byte)2 );
    table[0x2F] = new InstructionCMA(  cpu, (byte)0x2F, "CMA",     (byte)1 );
    table[0x30] = new InstructionSIM(  cpu, (byte)0x30, "SIM",     (byte)1 );
    table[0x31] = new InstructionLXI(  cpu, (byte)0x31, "LXI SP",  (byte)3 );
    table[0x32] = new InstructionSTA(  cpu, (byte)0x32, "STA",     (byte)3 );
    table[0x33] = new InstructionINX(  cpu, (byte)0x33, "INX SP",  (byte)1 );
    table[0x34] = new InstructionINR(  cpu, (byte)0x34, "INR M",   (byte)1 );
    table[0x35] = new InstructionDCR(  cpu, (byte)0x35, "DCR M",   (byte)1 );
    table[0x36] = new InstructionMVI(  cpu, (byte)0x36, "MVI M",   (byte)1 );
    table[0x37] = new InstructionSTC(  cpu, (byte)0x37, "STC",     (byte)1 );
    table[0x38] = new InstructionNOP(  cpu, (byte)0x38, "???",     (byte)1 );
    table[0x39] = new InstructionDAD(  cpu, (byte)0x39, "DAD SP",  (byte)1 );
    table[0x3A] = new InstructionLDA(  cpu, (byte)0x3A, "LDA",     (byte)3 );
    table[0x3B] = new InstructionDCX(  cpu, (byte)0x3B, "DCX SP",  (byte)1 );
    table[0x3C] = new InstructionINR(  cpu, (byte)0x3C, "INR A",   (byte)1 );
    table[0x3D] = new InstructionDCR(  cpu, (byte)0x3D, "DCR A",   (byte)1 );
    table[0x3E] = new InstructionMVI(  cpu, (byte)0x3E, "MVI A",   (byte)2 );
    table[0x3F] = new InstructionCMC(  cpu, (byte)0x3F, "CMC",     (byte)1 );
    table[0x40] = new InstructionMOV(  cpu, (byte)0x40, "MOV B,B", (byte)1 );
    table[0x41] = new InstructionMOV(  cpu, (byte)0x41, "MOV B,C", (byte)1 );
    table[0x42] = new InstructionMOV(  cpu, (byte)0x42, "MOV B,D", (byte)1 );
    table[0x43] = new InstructionMOV(  cpu, (byte)0x43, "MOV B,E", (byte)1 );
    table[0x44] = new InstructionMOV(  cpu, (byte)0x44, "MOV B,H", (byte)1 );
    table[0x45] = new InstructionMOV(  cpu, (byte)0x45, "MOV B,L", (byte)1 );
    table[0x46] = new InstructionMOV(  cpu, (byte)0x46, "MOV B,M", (byte)1 );
    table[0x47] = new InstructionMOV(  cpu, (byte)0x47, "MOV B,A", (byte)1 );
    table[0x48] = new InstructionMOV(  cpu, (byte)0x48, "MOV C,B", (byte)1 );
    table[0x49] = new InstructionMOV(  cpu, (byte)0x49, "MOV C,C", (byte)1 );
    table[0x4A] = new InstructionMOV(  cpu, (byte)0x4A, "MOV C,D", (byte)1 );
    table[0x4B] = new InstructionMOV(  cpu, (byte)0x4B, "MOV C,E", (byte)1 );
    table[0x4C] = new InstructionMOV(  cpu, (byte)0x4C, "MOV C,H", (byte)1 );
    table[0x4D] = new InstructionMOV(  cpu, (byte)0x4D, "MOV C,L", (byte)1 );
    table[0x4E] = new InstructionMOV(  cpu, (byte)0x4E, "MOV C,M", (byte)1 );
    table[0x4F] = new InstructionMOV(  cpu, (byte)0x4F, "MOV C,A", (byte)1 );
    table[0x50] = new InstructionMOV(  cpu, (byte)0x50, "MOV D,B", (byte)1 );
    table[0x51] = new InstructionMOV(  cpu, (byte)0x51, "MOV D,C", (byte)1 );
    table[0x52] = new InstructionMOV(  cpu, (byte)0x52, "MOV D,D", (byte)1 );
    table[0x53] = new InstructionMOV(  cpu, (byte)0x53, "MOV D,E", (byte)1 );
    table[0x54] = new InstructionMOV(  cpu, (byte)0x54, "MOV D,H", (byte)1 );
    table[0x55] = new InstructionMOV(  cpu, (byte)0x55, "MOV D,L", (byte)1 );
    table[0x56] = new InstructionMOV(  cpu, (byte)0x56, "MOV D,M", (byte)1 );
    table[0x57] = new InstructionMOV(  cpu, (byte)0x57, "MOV D,A", (byte)1 );
    table[0x58] = new InstructionMOV(  cpu, (byte)0x58, "MOV E,B", (byte)1 );
    table[0x59] = new InstructionMOV(  cpu, (byte)0x59, "MOV E,C", (byte)1 );
    table[0x5A] = new InstructionMOV(  cpu, (byte)0x5A, "MOV E,D", (byte)1 );
    table[0x5B] = new InstructionMOV(  cpu, (byte)0x5B, "MOV E,E", (byte)1 );
    table[0x5C] = new InstructionMOV(  cpu, (byte)0x5C, "MOV E,H", (byte)1 );
    table[0x5D] = new InstructionMOV(  cpu, (byte)0x5D, "MOV E,L", (byte)1 );
    table[0x5E] = new InstructionMOV(  cpu, (byte)0x5E, "MOV E,M", (byte)1 );
    table[0x5F] = new InstructionMOV(  cpu, (byte)0x5F, "MOV E,A", (byte)1 );
    table[0x60] = new InstructionMOV(  cpu, (byte)0x60, "MOV H,B", (byte)1 );
    table[0x61] = new InstructionMOV(  cpu, (byte)0x61, "MOV H,C", (byte)1 );
    table[0x62] = new InstructionMOV(  cpu, (byte)0x62, "MOV H,D", (byte)1 );
    table[0x63] = new InstructionMOV(  cpu, (byte)0x63, "MOV H,E", (byte)1 );
    table[0x64] = new InstructionMOV(  cpu, (byte)0x64, "MOV H,H", (byte)1 );
    table[0x65] = new InstructionMOV(  cpu, (byte)0x65, "MOV H,L", (byte)1 );
    table[0x66] = new InstructionMOV(  cpu, (byte)0x66, "MOV H,M", (byte)1 );
    table[0x67] = new InstructionMOV(  cpu, (byte)0x67, "MOV H,A", (byte)1 );
    table[0x68] = new InstructionMOV(  cpu, (byte)0x68, "MOV L,B", (byte)1 );
    table[0x69] = new InstructionMOV(  cpu, (byte)0x69, "MOV L,C", (byte)1 );
    table[0x6A] = new InstructionMOV(  cpu, (byte)0x6A, "MOV L,D", (byte)1 );
    table[0x6B] = new InstructionMOV(  cpu, (byte)0x6B, "MOV L,E", (byte)1 );
    table[0x6C] = new InstructionMOV(  cpu, (byte)0x6C, "MOV L,H", (byte)1 );
    table[0x6D] = new InstructionMOV(  cpu, (byte)0x6D, "MOV L,L", (byte)1 );
    table[0x6E] = new InstructionMOV(  cpu, (byte)0x6E, "MOV L,M", (byte)1 );
    table[0x6F] = new InstructionMOV(  cpu, (byte)0x6F, "MOV L,A", (byte)1 );
    table[0x70] = new InstructionMOV(  cpu, (byte)0x70, "MOV M,B", (byte)1 );
    table[0x71] = new InstructionMOV(  cpu, (byte)0x71, "MOV M,C", (byte)1 );
    table[0x72] = new InstructionMOV(  cpu, (byte)0x72, "MOV M,D", (byte)1 );
    table[0x73] = new InstructionMOV(  cpu, (byte)0x73, "MOV M,E", (byte)1 );
    table[0x74] = new InstructionMOV(  cpu, (byte)0x74, "MOV M,H", (byte)1 );
    table[0x75] = new InstructionMOV(  cpu, (byte)0x75, "MOV M,L", (byte)1 );
    table[0x76] = new InstructionHLT(  cpu, (byte)0x76, "HLT",     (byte)1 );
    table[0x77] = new InstructionMOV(  cpu, (byte)0x77, "MOV M,A", (byte)1 );
    table[0x78] = new InstructionMOV(  cpu, (byte)0x78, "MOV A,B", (byte)1 );
    table[0x79] = new InstructionMOV(  cpu, (byte)0x79, "MOV A,C", (byte)1 );
    table[0x7A] = new InstructionMOV(  cpu, (byte)0x7A, "MOV A,D", (byte)1 );
    table[0x7B] = new InstructionMOV(  cpu, (byte)0x7B, "MOV A,E", (byte)1 );
    table[0x7C] = new InstructionMOV(  cpu, (byte)0x7C, "MOV A,H", (byte)1 );
    table[0x7D] = new InstructionMOV(  cpu, (byte)0x7D, "MOV A,L", (byte)1 );
    table[0x7E] = new InstructionMOV(  cpu, (byte)0x7E, "MOV A,M", (byte)1 );
    table[0x7F] = new InstructionMOV(  cpu, (byte)0x7F, "MOV A,A", (byte)1 );
    table[0x80] = new InstructionADD(  cpu, (byte)0x80, "ADD B",   (byte)1 );
    table[0x81] = new InstructionADD(  cpu, (byte)0x81, "ADD C",   (byte)1 );
    table[0x82] = new InstructionADD(  cpu, (byte)0x82, "ADD D",   (byte)1 );
    table[0x83] = new InstructionADD(  cpu, (byte)0x83, "ADD E",   (byte)1 );
    table[0x84] = new InstructionADD(  cpu, (byte)0x84, "ADD H",   (byte)1 );
    table[0x85] = new InstructionADD(  cpu, (byte)0x85, "ADD L",   (byte)1 );
    table[0x86] = new InstructionADD(  cpu, (byte)0x86, "ADD M",   (byte)1 );
    table[0x87] = new InstructionADD(  cpu, (byte)0x87, "ADD A",   (byte)1 );
    table[0x88] = new InstructionADC(  cpu, (byte)0x88, "ADC B",   (byte)1 );
    table[0x89] = new InstructionADC(  cpu, (byte)0x89, "ADC C",   (byte)1 );
    table[0x8A] = new InstructionADC(  cpu, (byte)0x8A, "ADC D",   (byte)1 );
    table[0x8B] = new InstructionADC(  cpu, (byte)0x8B, "ADC E",   (byte)1 );
    table[0x8C] = new InstructionADC(  cpu, (byte)0x8C, "ADC H",   (byte)1 );
    table[0x8D] = new InstructionADC(  cpu, (byte)0x8D, "ADC L",   (byte)1 );
    table[0x8E] = new InstructionADC(  cpu, (byte)0x8E, "ADC M",   (byte)1 );
    table[0x8F] = new InstructionADC(  cpu, (byte)0x8F, "ADC A",   (byte)1 );
    table[0x90] = new InstructionSUB(  cpu, (byte)0x90, "SUB B",   (byte)1 );
    table[0x91] = new InstructionSUB(  cpu, (byte)0x91, "SUB C",   (byte)1 );
    table[0x92] = new InstructionSUB(  cpu, (byte)0x92, "SUB D",   (byte)1 );
    table[0x93] = new InstructionSUB(  cpu, (byte)0x93, "SUB E",   (byte)1 );
    table[0x94] = new InstructionSUB(  cpu, (byte)0x94, "SUB H",   (byte)1 );
    table[0x95] = new InstructionSUB(  cpu, (byte)0x95, "SUB L",   (byte)1 );
    table[0x96] = new InstructionSUB(  cpu, (byte)0x96, "SUB M",   (byte)1 );
    table[0x97] = new InstructionSUB(  cpu, (byte)0x97, "SUB A",   (byte)1 );
    table[0x98] = new InstructionSBB(  cpu, (byte)0x98, "SBB B",   (byte)1 );
    table[0x99] = new InstructionSBB(  cpu, (byte)0x99, "SBB C",   (byte)1 );
    table[0x9A] = new InstructionSBB(  cpu, (byte)0x9A, "SBB D",   (byte)1 );
    table[0x9B] = new InstructionSBB(  cpu, (byte)0x9B, "SBB E",   (byte)1 );
    table[0x9C] = new InstructionSBB(  cpu, (byte)0x9C, "SBB H",   (byte)1 );
    table[0x9D] = new InstructionSBB(  cpu, (byte)0x9D, "SBB L",   (byte)1 );
    table[0x9E] = new InstructionSBB(  cpu, (byte)0x9E, "SBB M",   (byte)1 );
    table[0x9F] = new InstructionSBB(  cpu, (byte)0x9F, "SBB A",   (byte)1 );
    table[0xA0] = new InstructionANA(  cpu, (byte)0xA0, "ANA B",   (byte)1 );
    table[0xA1] = new InstructionANA(  cpu, (byte)0xA1, "ANA C",   (byte)1 );
    table[0xA2] = new InstructionANA(  cpu, (byte)0xA2, "ANA D",   (byte)1 );
    table[0xA3] = new InstructionANA(  cpu, (byte)0xA3, "ANA E",   (byte)1 );
    table[0xA4] = new InstructionANA(  cpu, (byte)0xA4, "ANA H",   (byte)1 );
    table[0xA5] = new InstructionANA(  cpu, (byte)0xA5, "ANA L",   (byte)1 );
    table[0xA6] = new InstructionANA(  cpu, (byte)0xA6, "ANA M",   (byte)1 );
    table[0xA7] = new InstructionANA(  cpu, (byte)0xA7, "ANA A",   (byte)1 );
    table[0xA8] = new InstructionXRA(  cpu, (byte)0xA8, "XRA B",   (byte)1 );
    table[0xA9] = new InstructionXRA(  cpu, (byte)0xA9, "XRA C",   (byte)1 );
    table[0xAA] = new InstructionXRA(  cpu, (byte)0xAA, "XRA D",   (byte)1 );
    table[0xAB] = new InstructionXRA(  cpu, (byte)0xAB, "XRA E",   (byte)1 );
    table[0xAC] = new InstructionXRA(  cpu, (byte)0xAC, "XRA H",   (byte)1 );
    table[0xAD] = new InstructionXRA(  cpu, (byte)0xAD, "XRA L",   (byte)1 );
    table[0xAE] = new InstructionXRA(  cpu, (byte)0xAE, "XRA M",   (byte)1 );
    table[0xAF] = new InstructionXRA(  cpu, (byte)0xAF, "XRA A",   (byte)1 );
    table[0xB0] = new InstructionORA(  cpu, (byte)0xB0, "ORA B",   (byte)1 );
    table[0xB1] = new InstructionORA(  cpu, (byte)0xB1, "ORA C",   (byte)1 );
    table[0xB2] = new InstructionORA(  cpu, (byte)0xB2, "ORA D",   (byte)1 );
    table[0xB3] = new InstructionORA(  cpu, (byte)0xB3, "ORA E",   (byte)1 );
    table[0xB4] = new InstructionORA(  cpu, (byte)0xB4, "ORA H",   (byte)1 );
    table[0xB5] = new InstructionORA(  cpu, (byte)0xB5, "ORA L",   (byte)1 );
    table[0xB6] = new InstructionORA(  cpu, (byte)0xB6, "ORA M",   (byte)1 );
    table[0xB7] = new InstructionORA(  cpu, (byte)0xB7, "ORA A",   (byte)1 );
    table[0xB8] = new InstructionCMP(  cpu, (byte)0xB8, "CMP B",   (byte)1 );
    table[0xB9] = new InstructionCMP(  cpu, (byte)0xB9, "CMP C",   (byte)1 );
    table[0xBA] = new InstructionCMP(  cpu, (byte)0xBA, "CMP D",   (byte)1 );
    table[0xBB] = new InstructionCMP(  cpu, (byte)0xBB, "CMP E",   (byte)1 );
    table[0xBC] = new InstructionCMP(  cpu, (byte)0xBC, "CMP H",   (byte)1 );
    table[0xBD] = new InstructionCMP(  cpu, (byte)0xBD, "CMP L",   (byte)1 );
    table[0xBE] = new InstructionCMP(  cpu, (byte)0xBE, "CMP M",   (byte)1 );
    table[0xBF] = new InstructionCMP(  cpu, (byte)0xBF, "CMP A",   (byte)1 );
    table[0xC0] = new InstructionRET(  cpu, (byte)0xC0, "RNZ",     (byte)1 );
    table[0xC1] = new InstructionPOP(  cpu, (byte)0xC1, "POP B",   (byte)1 );
    table[0xC2] = new InstructionJMP(  cpu, (byte)0xC2, "JNZ",     (byte)3 );
    table[0xC3] = new InstructionJMP(  cpu, (byte)0xC3, "JMP",     (byte)3 );
    table[0xC4] = new InstructionCALL( cpu, (byte)0xC4, "CNZ",     (byte)3 );
    table[0xC5] = new InstructionPUSH( cpu, (byte)0xC5, "PUSH B",  (byte)1 );
    table[0xC6] = new InstructionADI(  cpu, (byte)0xC6, "ADI",     (byte)2 );
    table[0xC7] = new InstructionRST(  cpu, (byte)0xC7, "RST 0",   (byte)1 );
    table[0xC8] = new InstructionRET(  cpu, (byte)0xC8, "RZ",      (byte)1 );
    table[0xC9] = new InstructionRET(  cpu, (byte)0xC9, "RET",     (byte)1 );
    table[0xCA] = new InstructionJMP(  cpu, (byte)0xCA, "JZ",      (byte)3 );
    table[0xCB] = new InstructionNOP(  cpu, (byte)0xCB, "???",     (byte)1 );
    table[0xCC] = new InstructionCALL( cpu, (byte)0xCC, "CZ",      (byte)3 );
    table[0xCD] = new InstructionCALL( cpu, (byte)0xCD, "CALL",    (byte)3 );
    table[0xCE] = new InstructionACI(  cpu, (byte)0xCE, "ACI",     (byte)2 );
    table[0xCF] = new InstructionRST(  cpu, (byte)0xCF, "RST 1",   (byte)1 );
    table[0xD0] = new InstructionRET(  cpu, (byte)0xD0, "RNC",     (byte)1 );
    table[0xD1] = new InstructionPOP(  cpu, (byte)0xD1, "POP D",   (byte)1 );
    table[0xD2] = new InstructionJMP(  cpu, (byte)0xD2, "JNC",     (byte)3 );
    table[0xD3] = new InstructionOUT(  cpu, (byte)0xD3, "OUT",     (byte)2 );
    table[0xD4] = new InstructionCALL( cpu, (byte)0xD4, "CNC",     (byte)3 );
    table[0xD5] = new InstructionPUSH( cpu, (byte)0xD5, "PUSH D",  (byte)1 );
    table[0xD6] = new InstructionSUI(  cpu, (byte)0xD6, "SUI",     (byte)2 );
    table[0xD7] = new InstructionRST(  cpu, (byte)0xD7, "RST 2",   (byte)1 );
    table[0xD8] = new InstructionRET(  cpu, (byte)0xD8, "RC",      (byte)1 );
    table[0xD9] = new InstructionNOP(  cpu, (byte)0xD9, "???",     (byte)1 );
    table[0xDA] = new InstructionJMP(  cpu, (byte)0xDA, "JC",      (byte)3 );
    table[0xDB] = new InstructionIN(   cpu, (byte)0xDB, "IN",      (byte)2 );
    table[0xDC] = new InstructionCALL( cpu, (byte)0xDC, "CC",      (byte)3 );
    table[0xDD] = new InstructionNOP(  cpu, (byte)0xDD, "???",     (byte)1 );
    table[0xDE] = new InstructionSBI(  cpu, (byte)0xDE, "SBI",     (byte)2 );
    table[0xDF] = new InstructionRST(  cpu, (byte)0xDF, "RST 3",   (byte)1 );
    table[0xE0] = new InstructionRET(  cpu, (byte)0xE0, "RPO",     (byte)1 );
    table[0xE1] = new InstructionPOP(  cpu, (byte)0xE1, "POP H",   (byte)1 );
    table[0xE2] = new InstructionJMP(  cpu, (byte)0xE2, "JPO",     (byte)3 );
    table[0xE3] = new InstructionXTHL( cpu, (byte)0xE3, "XTHL",    (byte)1 );
    table[0xE4] = new InstructionCALL( cpu, (byte)0xE4, "CPO",     (byte)3 );
    table[0xE5] = new InstructionPUSH( cpu, (byte)0xE5, "PUSH H",  (byte)1 );
    table[0xE6] = new InstructionANI(  cpu, (byte)0xE6, "ANI",     (byte)2 );
    table[0xE7] = new InstructionRST(  cpu, (byte)0xE7, "RST 4",   (byte)1 );
    table[0xE8] = new InstructionRET(  cpu, (byte)0xE8, "RPE",     (byte)1 );
    table[0xE9] = new InstructionPCHL( cpu, (byte)0xE9, "PCHL",    (byte)1 );
    table[0xEA] = new InstructionJMP(  cpu, (byte)0xEA, "JPE",     (byte)3 );
    table[0xEB] = new InstructionXCHG( cpu, (byte)0xEB, "XCHG",    (byte)1 );
    table[0xEC] = new InstructionCALL( cpu, (byte)0xEC, "CPE",     (byte)3 );
    table[0xED] = new InstructionNOP(  cpu, (byte)0xED, "???",     (byte)1 );
    table[0xEE] = new InstructionXRI(  cpu, (byte)0xEE, "XRI",     (byte)2 );
    table[0xEF] = new InstructionRST(  cpu, (byte)0xEF, "RST 5",   (byte)1 );
    table[0xF0] = new InstructionRET(  cpu, (byte)0xF0, "RP",      (byte)1 );
    table[0xF1] = new InstructionPOP(  cpu, (byte)0xF1, "POP PSW", (byte)1 );
    table[0xF2] = new InstructionJMP(  cpu, (byte)0xF2, "JP",      (byte)3 );
    table[0xF3] = new InstructionDI(   cpu, (byte)0xF3, "DI",      (byte)1 );
    table[0xF4] = new InstructionCALL( cpu, (byte)0xF4, "CP",      (byte)3 );
    table[0xF5] = new InstructionPUSH( cpu, (byte)0xF5, "PUSH PSW",(byte)1 );
    table[0xF6] = new InstructionORI(  cpu, (byte)0xF6, "ORI",     (byte)2 );
    table[0xF7] = new InstructionRST(  cpu, (byte)0xF7, "RST 6",   (byte)1 );
    table[0xF8] = new InstructionRET(  cpu, (byte)0xF8, "RM",      (byte)1 );
    table[0xF9] = new InstructionSPHL( cpu, (byte)0xF9, "SPHL" ,   (byte)1 );
    table[0xFA] = new InstructionJMP(  cpu, (byte)0xFA, "JM",      (byte)3 );
    table[0xFB] = new InstructionEI(   cpu, (byte)0xFB, "EI",      (byte)1 );
    table[0xFC] = new InstructionCALL( cpu, (byte)0xFC, "CM",      (byte)3 );
    table[0xFD] = new InstructionNOP(  cpu, (byte)0xFD, "???",     (byte)1 );
    table[0xFE] = new InstructionCPI(  cpu, (byte)0xFE, "CPI",     (byte)2 );
    table[0xFF] = new InstructionRST(  cpu, (byte)0xFF, "RST 7",   (byte)1 );

    }




  // ���߃R�[�h �� 8085�C���X�g���N�V�����j�[���j�b�N
  public Instruction8085   decode( short   opecode )
    {
    return   table[ opecode ];
    }


  }




