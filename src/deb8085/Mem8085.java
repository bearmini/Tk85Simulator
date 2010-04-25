package   deb8085;


//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU メモリ領域クラス */
public class   Mem8085
  {
  final int   MEMORYSIZE = 65536;

  short[]     area     = new short[MEMORYSIZE];
  boolean[]   readonly = new boolean[MEMORYSIZE];


  // コンストラクタ
  public  Mem8085()
    {
    for( int   i=0; i<MEMORYSIZE; i++ )
      {
      area[i] = 0;
      readonly[i] = false;
      }
    }


  // 値をセット
  public void   setValue( int   addr, int   value )
    {
    if( addr < 0 || addr > 0xFFFF )
      return;

    if( readonly[addr] )
      return;

    area[addr] = (short)(value&0xFF);
//    System.out.println("Memory : set value : at " + util.hex4(addr) + "  value:"+util.hex2(value)  );
    }


  // 値を読み出す
  public short   getValue( int   addr )
    {
    if( addr >= 0 && addr <= 0xFFFF )
      {
//      System.out.println("Memory : get value : at " + util.hex4(addr) + "  value:"+util.hex2(area[addr])  );
      return area[addr];
      }
    else
      return 0xaa;//Error
    }


  // リードオンリーかどうかをセット
  public void   setReadOnly( int   addr, boolean   readonly )
    {
    if( addr < 0 || addr > 0xFFFF )
      return;

    this.readonly[addr] = readonly;
    }


  }



