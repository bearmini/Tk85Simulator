package   deb8085;


import   java.util.Vector;


public class   BreakPointList extends Vector
  {

  //***************************************************************************************************
  // �R���X�g���N�^
  public   BreakPointList()
    {
    }



  //***************************************************************************************************
  // �u���[�N�|�C���g�����X�g�ɒǉ�
  public void   addBreakPoint( int   addr, int   count ) throws BreakPointListException
    {
    if( existBreakPoint( addr ) )
      throw   new BreakPointListException( "Public label for address " + util.hex4(addr) + " is already defined." );
    else
      addElement( new BreakPoint8085( addr, count ) );

    }



  //***************************************************************************************************
  // �u���[�N�|�C���g�����X�g����폜
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
  // �w�肵���A�h���X�̃u���[�N�|�C���g�����Ԗڂ�
  public int   getBreakPointIndex( int   addr ) throws BreakPointListException
    {
    for( int   i = 0; i<size(); i++ )
      {
      BreakPoint8085   b = (BreakPoint8085)elementAt(i);
      if( b.addr == addr )
        return   i;
      }
    // ������Ȃ�������
    throw   new BreakPointListException( "Break point at address " + util.hex4(addr) + " is not defined." );
    }



  //***************************************************************************************************
  // �w�肳�ꂽ�A�h���X�ɑΉ�����u���[�N�|�C���g�����邩�ǂ���
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
  // index �Ԗڂ̃u���[�N�|�C���g��Ԃ�
  public BreakPoint8085   getBreakPointAt( int   index )
    {
    return   (BreakPoint8085)elementAt( index );
    }



  //***************************************************************************************************
  // �w�肳�ꂽ�A�h���X�ɑΉ�����u���[�N�|�C���g��Ԃ�
  public BreakPoint8085   toBreakPoint( int   addr )
    {
    for( int   i = 0; i<size(); i++ )
      if( ((BreakPoint8085)elementAt(i)).addr == addr )
        return   (BreakPoint8085)elementAt(i);

    // �u���[�N�|�C���g��������Ȃ�������A�d�����Ȃ����� nil ��Ԃ�
    return   null;
    }




  }