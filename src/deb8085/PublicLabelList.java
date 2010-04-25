package   deb8085;


import   java.util.Vector;



public class   PublicLabelList extends Vector
  {

  //***************************************************************************************************
  // �R���X�g���N�^
  public   PublicLabelList()
    {
    }



  //***************************************************************************************************
  // �p�u���b�N���x�������X�g�ɒǉ�
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
  // �p�u���b�N���x�������X�g����폜
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
  // �w�肵�����O�̃p�u���b�N���x�������Ԗڂ�
  public int   getPublicLabelIndex( String   name ) throws PublicLabelListException
    {
    for( int   i = 0; i<size(); i++ )
      {
      PublicLabel8085   p = (PublicLabel8085)elementAt(i);
      if( p.name.equals( name ) )
        return   i;
      }
    // ������Ȃ�������
    throw   new PublicLabelListException( name + " is not defined." );
    }



  //***************************************************************************************************
  // �w�肳�ꂽ���O�̃p�u���b�N���x�������邩�ǂ���
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
  // �w�肳�ꂽ�A�h���X�ɑΉ�����p�u���b�N���x�������邩�ǂ���
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
  // index �Ԗڂ̃p�u���b�N���x����Ԃ�
  public PublicLabel8085   getPublicLabelAt( int   index )
    {
    return   (PublicLabel8085)elementAt( index );
    }



  //***************************************************************************************************
  // �w�肳�ꂽ���x�����ɑΉ�����A�h���X��Ԃ�
  public int   toPublicLabelAddr( String   name )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((PublicLabel8085)elementAt(i)).name == name )
        return   ((PublicLabel8085)elementAt(i)).addr;

    // �p�u���b�N���x����������Ȃ�������A-1 ��Ԃ��i�G���[�I�j
    // ���̊֐����g���Ƃ��́A���O�� existPublicLabel( name ) �ő��݂��m�F���Ă����ׂ��B
    return   -1;
    }


  //***************************************************************************************************
  // �w�肳�ꂽ�A�h���X�ɑΉ����郉�x������Ԃ�
  public String   toPublicLabelName( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((PublicLabel8085)elementAt(i)).addr == addr )
        return   ((PublicLabel8085)elementAt(i)).name;

    // �p�u���b�N���x����������Ȃ�������Anull ��Ԃ��i�G���[�I�j
    // ���̊֐����g���Ƃ��́A���O�� existPublicLabel( name ) �ő��݂��m�F���Ă����ׂ��B
    return   null;
    }


  //***************************************************************************************************
  // �w�肳�ꂽ�A�h���X�ɑΉ����郉�x����Ԃ�
  public PublicLabel8085   toPublicLabel( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((PublicLabel8085)elementAt(i)).addr == addr )
        return   ((PublicLabel8085)elementAt(i));

    // �p�u���b�N���x����������Ȃ�������Anull ��Ԃ��i�G���[�I�j
    // ���̊֐����g���Ƃ��́A���O�� existPublicLabel( name ) �ő��݂��m�F���Ă����ׂ��B
    return   null;
    }


  }