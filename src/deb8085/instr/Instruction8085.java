package   deb8085.instr;

import   deb8085.*;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 命令コード */
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


  // コンストラクタ
  public   Instruction8085( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    this.cpu = cpu;
    opecode  = p1;
    mnemonic = p2;
    size     = p3;
    setB2( (short)0 );
    setB3( (short)0 );
    }


  // 抽象メソッド  このクラスを継承するクラスが必ずオーバーライドしなくてはならない。
  public abstract void   execute();


/*
言葉の定義


ニーモニック, mnemonic
  MVI A,01H  → MVI A
  HLT        → HLT
  CALL 01C0H → CALL
  RST 6      → RST 6
  など、デコーダに定義されている、命令コード１バイトで区別されるもの

オペランド, operand
  MVI A,01H  → A
  HLT        → (なし)
  CALL 01C0H → (なし)
  RST 6      → 6
  など、主にレジスタの指定。各命令の中で動作に影響する


命令コード
  32 F4 83 など、ある一つの命令をあらわすために必要なバイトの集まり

opecode
  命令コードの中で、1バイトめにあり、命令の動作をあらわすバイト


*/

  // エンコード
  public void   encode( String   operand1, String   operand2 ) throws OnEncodeException
    {
    // デフォルトでは何もしない
    }



  // 文字列に変換
  public String   toString()
    {
    // デフォルト動作
    return   mnemonic;
    }


  // opecode を取得
  public byte   getOpecode()
    {
    return   opecode;
    }

  // ニーモニックを取得
  public String   getMnemonic()
    {
    return   mnemonic;
    }

  // オペランドを設定
  public void   setOperands( String   newOperands )
    {
    operands = newOperands;
    }


  // オペランドを取得
  public String   getOperands()
    {
    return   operands;
    }


  // 命令サイズを取得
  public byte   getSize()
    {
    return   size;
    }

  // 2バイトめを設定
  public void  setB2( short   newB2 )
    {
    b2 = newB2;
    b3b2 = (b3<<8)+b2;
    }

  // 3バイトめを設定
  public void  setB3( short   newB3 )
    {
    b3 = newB3;
    b3b2 = (b3<<8)+b2;
    }

  // 2バイトめ、3バイトめを設定
  public void  setB3B2( int   newB3B2 )
    {
    b3b2 = newB3B2;
    b2 = (short)(newB3B2 & 0xFF );
    b3 = (short)(newB3B2 >>> 8 );
    }

  // 2バイトめを取得
  public short   getB2()
    {
    return   b2;
    }

  // 3バイトめを取得
  public short   getB3()
    {
    return   b3;
    }

  // 2バイトめ、3バイトめを取得
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
  
  
  // 文字列をレジスタ定数にする
  // "A" なら Reg8085.A など
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





