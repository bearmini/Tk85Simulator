package   deb8085;


import   java.util.Vector;



public class   PublicLabelList extends Vector
  {

  //***************************************************************************************************
  // コンストラクタ
  public   PublicLabelList()
    {
    }



  //***************************************************************************************************
  // パブリックラベルをリストに追加
  public void   addPublicLabel( String   name, int   addr ) throws PublicLabelListException
    {
    name = name.toUpperCase();

    if( existPublicLabel( name ) )
      throw   new PublicLabelListException( "Name " + name + " is already defined." );
    else if( existPublicLabel( addr ) )
      throw   new PublicLabelListException( "Public label for address " + util.hex4(addr) + " is already defined." );
    else
      addElement( new PublicLabel8085( name, addr ) );

    }



  //***************************************************************************************************
  // パブリックラベルをリストから削除
  public void   delPublicLabel( String   name ) throws PublicLabelListException
    {
    name = name.toUpperCase();

    try
      {
      removeElementAt( getPublicLabelIndex( name ) );
      }
    catch( PublicLabelListException   e )
      {
      throw e;
      }

    }



  //***************************************************************************************************
  // 指定した名前のパブリックラベルが何番目か
  public int   getPublicLabelIndex( String   name ) throws PublicLabelListException
    {
    for( int   i = 0; i<size(); i++ )
      {
      PublicLabel8085   p = (PublicLabel8085)elementAt(i);
      if( p.name.equals( name ) )
        return   i;
      }
    // 見つからなかったら
    throw   new PublicLabelListException( name + " is not defined." );
    }



  //***************************************************************************************************
  // 指定された名前のパブリックラベルがあるかどうか
  public boolean   existPublicLabel( String   name )
    {
    for( int   i = 0; i<size(); i++ )
      {
      PublicLabel8085   p = (PublicLabel8085)elementAt(i);
      if( p.name.equals( name ) )
        return   true;
      }

    return   false;
    }



  //***************************************************************************************************
  // 指定されたアドレスに対応するパブリックラベルがあるかどうか
  public boolean   existPublicLabel( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      {
      PublicLabel8085   p = (PublicLabel8085)elementAt(i);
      if( p.addr == addr )
        return   true;
      }

    return   false;
    }




  //***************************************************************************************************
  // index 番目のパブリックラベルを返す
  public PublicLabel8085   getPublicLabelAt( int   index )
    {
    return   (PublicLabel8085)elementAt( index );
    }



  //***************************************************************************************************
  // 指定されたラベル名に対応するアドレスを返す
  public int   toPublicLabelAddr( String   name )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((PublicLabel8085)elementAt(i)).name == name )
        return   ((PublicLabel8085)elementAt(i)).addr;

    // パブリックラベルが見つからなかったら、-1 を返す（エラー！）
    // この関数を使うときは、事前に existPublicLabel( name ) で存在を確認しておくべき。
    return   -1;
    }


  //***************************************************************************************************
  // 指定されたアドレスに対応するラベル名を返す
  public String   toPublicLabelName( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((PublicLabel8085)elementAt(i)).addr == addr )
        return   ((PublicLabel8085)elementAt(i)).name;

    // パブリックラベルが見つからなかったら、null を返す（エラー！）
    // この関数を使うときは、事前に existPublicLabel( name ) で存在を確認しておくべき。
    return   null;
    }


  //***************************************************************************************************
  // 指定されたアドレスに対応するラベルを返す
  public PublicLabel8085   toPublicLabel( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((PublicLabel8085)elementAt(i)).addr == addr )
        return   ((PublicLabel8085)elementAt(i));

    // パブリックラベルが見つからなかったら、null を返す（エラー！）
    // この関数を使うときは、事前に existPublicLabel( name ) で存在を確認しておくべき。
    return   null;
    }


  }