package   deb8085;


import   java.util.Vector;


public class   BreakPointList extends Vector
  {

  //***************************************************************************************************
  // コンストラクタ
  public   BreakPointList()
    {
    }



  //***************************************************************************************************
  // ブレークポイントをリストに追加
  public void   addBreakPoint( int   addr, int   count ) throws BreakPointListException
    {
    if( existBreakPoint( addr ) )
      throw   new BreakPointListException( "Public label for address " + util.hex4(addr) + " is already defined." );
    else
      addElement( new BreakPoint8085( addr, count ) );

    }



  //***************************************************************************************************
  // ブレークポイントをリストから削除
  public void   delBreakPoint( int   addr ) throws BreakPointListException
    {
    try
      {
      removeElementAt( getBreakPointIndex( addr ) );
      }
    catch( BreakPointListException   e )
      {
      throw e;
      }
    }



  //***************************************************************************************************
  // 指定したアドレスのブレークポイントが何番目か
  public int   getBreakPointIndex( int   addr ) throws BreakPointListException
    {
    for( int   i = 0; i<size(); i++ )
      {
      BreakPoint8085   b = (BreakPoint8085)elementAt(i);
      if( b.addr == addr )
        return   i;
      }
    // 見つからなかったら
    throw   new BreakPointListException( "Break point at address " + util.hex4(addr) + " is not defined." );
    }



  //***************************************************************************************************
  // 指定されたアドレスに対応するブレークポイントがあるかどうか
  public boolean   existBreakPoint( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      {
      BreakPoint8085   p = (BreakPoint8085)elementAt(i);
      if( p.addr == addr )
        return   true;
      }

    return   false;
    }




  //***************************************************************************************************
  // index 番目のブレークポイントを返す
  public BreakPoint8085   getBreakPointAt( int   index )
    {
    return   (BreakPoint8085)elementAt( index );
    }



  //***************************************************************************************************
  // 指定されたアドレスに対応するブレークポイントを返す
  public BreakPoint8085   toBreakPoint( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((BreakPoint8085)elementAt(i)).addr == addr )
        return   (BreakPoint8085)elementAt(i);

    // ブレークポイントが見つからなかったら、仕方がないから nil を返す
    return   null;
    }




  }