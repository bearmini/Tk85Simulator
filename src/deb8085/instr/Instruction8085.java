package   deb8085.instr;

import   deb8085.*;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 ���߃R�[�h */
public abstract class   Instruction8085
  {
  CPU8085   cpu;

  private byte     opecode;
  private String   mnemonic;
  private String   operands;
  private byte     size;

  private short    b2;
  private short    b3;

  private int      b3b2;


  // �R���X�g���N�^
  public   Instruction8085( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    this.cpu = cpu;
    opecode  = p1;
    mnemonic = p2;
    size     = p3;
    setB2( (short)0 );
    setB3( (short)0 );
    }


  // ���ۃ��\�b�h  ���̃N���X���p������N���X���K���I�[�o�[���C�h���Ȃ��Ă͂Ȃ�Ȃ��B
  public abstract void   execute();


/*
���t�̒�`


�j�[���j�b�N, mnemonic
  MVI A,01H  �� MVI A
  HLT        �� HLT
  CALL 01C0H �� CALL
  RST 6      �� RST 6
  �ȂǁA�f�R�[�_�ɒ�`����Ă���A���߃R�[�h�P�o�C�g�ŋ�ʂ�������

�I�y�����h, operand
  MVI A,01H  �� A
  HLT        �� (�Ȃ�)
  CALL 01C0H �� (�Ȃ�)
  RST 6      �� 6
  �ȂǁA��Ƀ��W�X�^�̎w��B�e���߂̒��œ���ɉe������


���߃R�[�h
  32 F4 83 �ȂǁA�����̖��߂�����킷���߂ɕK�v�ȃo�C�g�̏W�܂�

opecode
  ���߃R�[�h�̒��ŁA1�o�C�g�߂ɂ���A���߂̓��������킷�o�C�g


*/

  // �G���R�[�h
  public void   encode( String   operand1, String   operand2 ) throws OnEncodeException
    {
    // �f�t�H���g�ł͉������Ȃ�
    }



  // ������ɕϊ�
  public String   toString()
    {
    // �f�t�H���g����
    return   mnemonic;
    }


  // opecode ���擾
  public byte   getOpecode()
    {
    return   opecode;
    }

  // �j�[���j�b�N���擾
  public String   getMnemonic()
    {
    return   mnemonic;
    }

  // �I�y�����h��ݒ�
  public void   setOperands( String   newOperands )
    {
    operands = newOperands;
    }


  // �I�y�����h���擾
  public String   getOperands()
    {
    return   operands;
    }


  // ���߃T�C�Y���擾
  public byte   getSize()
    {
    return   size;
    }

  // 2�o�C�g�߂�ݒ�
  public void  setB2( short   newB2 )
    {
    b2 = newB2;
    b3b2 = (b3<<8)+b2;
    }

  // 3�o�C�g�߂�ݒ�
  public void  setB3( short   newB3 )
    {
    b3 = newB3;
    b3b2 = (b3<<8)+b2;
    }

  // 2�o�C�g�߁A3�o�C�g�߂�ݒ�
  public void  setB3B2( int   newB3B2 )
    {
    b3b2 = newB3B2;
    b2 = (short)(newB3B2 & 0xFF );
    b3 = (short)(newB3B2 >>> 8 );
    }

  // 2�o�C�g�߂��擾
  public short   getB2()
    {
    return   b2;
    }

  // 3�o�C�g�߂��擾
  public short   getB3()
    {
    return   b3;
    }

  // 2�o�C�g�߁A3�o�C�g�߂��擾
  public int   getB3B2()
    {
    return   b3b2;
    }


  public boolean calcAddCarry(int val1, int val2) {
	  return (val1 + val2) > 0xFF;
  }

  public boolean calcAddHalfCarry(int val1, int val2) {
	  return ((val1 & 0x0F) + (val2 & 0x0F)) > 0x0F;
  }
  
  public boolean calcSubCarry(int val1, int val2) {
	  return val2 > val1;	  
  }

  public boolean calcSubHalfCarry(int val1, int val2) {
	  return (val2 & 0x0F) > (val1 & 0x0F);
  }
  
  
  // ����������W�X�^�萔�ɂ���
  // "A" �Ȃ� Reg8085.A �Ȃ�
  static byte   StringToReg( String   reg )
    {
    if( reg.equals( "A" ) )
      return   Reg8085.A;
    else if( reg.equals( "B" ) )
      return   Reg8085.B;
    else if( reg.equals( "C" ) )
      return   Reg8085.C;
    else if( reg.equals( "D" ) )
      return   Reg8085.D;
    else if( reg.equals( "E" ) )
      return   Reg8085.E;
    else if( reg.equals( "H" ) )
      return   Reg8085.H;
    else if( reg.equals( "L" ) )
      return   Reg8085.L;
    else if( reg.equals( "M" ) )
      return   Reg8085.M;
    else
      return 0;
    }

  static short   StringToPairReg( String   reg )
    {
    if( reg.equals( "PSW" ) )
      return   Reg8085.AF;
    else if( reg.equals( "B" ) )
      return   Reg8085.BC;
    else if( reg.equals( "D" ) )
      return   Reg8085.DE;
    else if( reg.equals( "H" ) )
      return   Reg8085.HL;
    else if( reg.equals( "SP" ) )
      return   Reg8085.SP;
    else
      return 0;
    }

  }





