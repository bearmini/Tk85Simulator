package   deb8085.io;

import   java.awt.*;
import   java.awt.event.KeyEvent;
import   java.awt.event.KeyListener;
import   java.io.InputStream;
import   java.lang.String;


/* �L�[�{�[�h���̓X�g���[�� */

public class   KeyboardInputStream extends InputStream implements KeyListener
  {
  TextArea   console;  // �e�L�X�g�G���A�����ͥ�o�͂̃R���\�[���̖������ʂ���

  final static int   MAX_OF_INPUTBUFFER = 100;
  RingBuffer   inputBuffer;

  boolean   consumed = false;
  boolean   enterPressed = false;
  boolean   echo = true;
  boolean   enabled = true;


  //***************************************************************************************************
  // �R���X�g���N�^
  public   KeyboardInputStream( TextArea   console )
    {
    this.console = console;
    inputBuffer = new RingBuffer( MAX_OF_INPUTBUFFER );
    console.addKeyListener( this );
    }


  //***************************************************************************************************
  // �L�[�G�R�[ �Ȃ�
  public void   echoOff()
    {
    echo = false;
    }


  //***************************************************************************************************
  // �L�[�G�R�[ ����
  public void   echoOn()
    {
    echo = true;
    }


  //***************************************************************************************************
  // �L�[�{�[�h����ꕶ�������
  public int   read()
    {
    while( inputBuffer.isEmpty() );  // ���̋󃋁[�v�ŃL�[���͂�҂�
    return   inputBuffer.get();
    }


  //***************************************************************************************************
  // �L�[�{�[�h���當��������
  public String   readln()
    {
    while( !enterPressed );  // ���^�[���L�[���������܂ő҂�

    enterPressed = false;
    String   result = "";

    while( !inputBuffer.isEmpty() )
      result += (char)inputBuffer.get();

    // ���̓o�b�t�@�N���A
    inputBuffer.flush();

    return result;
    }




  //***************************************************************************************************
  // �L�[�{�[�h����̓��͂�  �L��  �ɂ���
  public void   enable()
    {
    enabled = true;
    }





  //***************************************************************************************************
  // �L�[�{�[�h����̓��͂�  ����  �ɂ���
  public void   disable()
    {
    enabled = false;
    }





  //***************************************************************************************************
  // �L�[��������ĕ����ꂽ��
  public void   keyTyped( KeyEvent   e )
    {
    char   k;

    // �L�[�������ꂽ�Ƃ��ɁA�s�v�ȃL�[�Ɣ��f���ꂽ�Ȃ� �������Ȃ��������̂悤�Ƀ��^�[��
    if( consumed || e.isConsumed() )
      return;

    // �����ꂽ�L�[���擾
    k = e.getKeyChar();

    switch( k )
      {
      // �o�b�N�X�y�[�X��������Ă�����A���̓o�b�t�@��߂�
      case '\b':
        inputBuffer.back();
        break;

      // ���ʂ̃L�[�Ȃ�o�b�t�@�ɑ���
      default:
        inputBuffer.put( k );
        break;

      }

    // �G�R�[���Ȃ��Ƃ�
    if( !echo )
      e.consume();

    }



  //***************************************************************************************************
  // �L�[�������ꂽ��
  public void   keyPressed( KeyEvent   e )
    {
    consumed = false;
    enterPressed = false;

    // Enable �łȂ���Γ��삵�Ȃ�
    if( !enabled )
      {
      e.consume();
      consumed = true;
      return;
      }

    // �L�[�R�[�h��ǂݍ���
    int   keycode = e.getKeyCode();

    // ���^�[���L�[�������ꂽ��
    if( keycode == KeyEvent.VK_ENTER )
      {
      enterPressed = true;
      if( !echo )
        e.consume();
      consumed = true;
      }

    // �o�b�t�@�̌��E�ɒB�����Ƃ�  �o�b�N�X�y�[�X�L�[�ȊO�̃L�[��������
    if( inputBuffer.isFull() && keycode != KeyEvent.VK_BACK_SPACE )
      {
      e.consume();
      consumed = true;
      }

    // �o�b�N�X�y�[�X�Ŗ߂肷����̂�h��
    if( inputBuffer.isEmpty() && keycode == KeyEvent.VK_BACK_SPACE )
      {
      e.consume();
      consumed = true;
      }

    // ���L�[�Ȃǂŕςȕ����ɍs���Ă��܂��̂�h��
    if( keycode == KeyEvent.VK_UP || keycode == KeyEvent.VK_DOWN || keycode == KeyEvent.VK_LEFT || keycode == KeyEvent.VK_RIGHT || keycode == KeyEvent.VK_PAGE_DOWN || keycode == KeyEvent.VK_PAGE_UP || keycode == KeyEvent.VK_HOME || keycode == KeyEvent.VK_END )
      {
      e.consume();
      consumed = true;
      }

    }



  //***************************************************************************************************
  // �L�[�������ꂽ��
  public void   keyReleased( KeyEvent   e )
    {
    }


  }