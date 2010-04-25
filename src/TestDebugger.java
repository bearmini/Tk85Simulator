import  java.awt.*;
import  java.awt.event.*;

import  deb8085.*;

//***************************************************************************************************
//***************************************************************************************************
/* �f�o�b�K�p�̃t���[���N���X */
public class   TestDebugger extends Frame implements DebuggerParent, ActionListener, WindowListener
  {
  Debugger   debugger;  // �f�o�b�K

  public TextArea    output;

  MenuItem   mniStart;
  MenuItem   mniStop;

  boolean     debuggingNow = false;


  //***************************************************************************************************
  // �R���X�g���N�^
  public   TestDebugger()
    {
    addWindowListener( this );

    // ���j���[���쐬
    Menu   mfile  = new Menu( "File" );
    MenuItem   mniQuit = new MenuItem( "Quit" );
    mniQuit.addActionListener( this );
    mfile.add( mniQuit );

    Menu   mdeb = new Menu( "Debug" );
    mniStart = new MenuItem( "Start" );
    mniStop  = new MenuItem( "Stop" );
    mniStart.addActionListener( this );
    mniStop.addActionListener( this );
    mdeb.add( mniStart );
    mdeb.add( mniStop );
    mniStop.setEnabled( false );

    MenuBar   mb = new MenuBar();
    mb.add( mfile );
    mb.add( mdeb );
    setMenuBar( mb );

    // �{�[�_�[���C�A�E�g�ŃR���|�[�l���g��z�u
    setLayout( new BorderLayout() );

    // Center�����ɏo�͗̈��ǉ�
    output = new TextArea( "", 25, 80 );
    add( "Center", output );
    output.setFont( new Font( "SansSerif", Font.PLAIN, 20 ) );
    }


  //***************************************************************************************************
  // �C�x���g�n���h��
  public void   windowOpened( WindowEvent   e )
    {
    }

  public void   windowClosing( WindowEvent   e )
    {
    if( e.getID() == Event.WINDOW_DESTROY )
      System.exit(0);
    }

  public void   windowClosed( WindowEvent   e )
    {
    }

  public void   windowIconified( WindowEvent   e )
    {
    }

  public void   windowDeiconified( WindowEvent   e )
    {
    }

  public void   windowActivated( WindowEvent   e )
    {
    }

  public void   windowDeactivated( WindowEvent   e )
    {
    }

  public void   actionPerformed( ActionEvent   e )
    {
    if( e.getActionCommand().equals( "Quit" ) )
      System.exit(0);
    else if( e.getActionCommand().equals( "Start" ) )
      startDebug();
    else if( e.getActionCommand().equals( "Stop" ) )
      stopDebug();

    }


  //***************************************************************************************************
  // �A�v���P�[�V�����̃��C��
  public static void   main( String   args[] )
    {
    TestDebugger   window = new TestDebugger();

    window.setTitle( "�f�o�b�K����m�F" );
    window.pack();
    window.show();

    window.startDebug();

    }



  //***************************************************************************************************
  // �f�o�b�O���J�n
  public void   startDebug()
    {
    if( debuggingNow )
      return;

    debuggingNow = true;
    mniStart.disable();
    mniStop.enable();

    debugger = new Debugger( this, this, output );
    debugger.start();

    }



  //***************************************************************************************************
  // �f�o�b�O�������I��
  public void   stopDebug()
    {
    if( !debuggingNow )
      return;

    onEndDebug();
    debugger.stop();
    }






  //***************************************************************************************************
  // �f�o�b�O�J�n��
  public void   onBeginDebug()
    {
    }




  //***************************************************************************************************
  // �f�o�b�O�I����
  public void   onEndDebug()
    {
    debuggingNow = false;
    mniStart.enable();
    mniStop.disable();
    }




  }
