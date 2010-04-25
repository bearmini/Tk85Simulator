package   deb8085.io;

import   java.awt.*;
import   java.awt.event.KeyEvent;
import   java.awt.event.KeyListener;
import   java.io.InputStream;
import   java.lang.String;


/* キーボード入力ストリーム */

public class   KeyboardInputStream extends InputStream implements KeyListener
  {
  TextArea   console;  // テキストエリアが入力･出力のコンソールの役割を果たす

  final static int   MAX_OF_INPUTBUFFER = 100;
  RingBuffer   inputBuffer;

  boolean   consumed = false;
  boolean   enterPressed = false;
  boolean   echo = true;
  boolean   enabled = true;


  //***************************************************************************************************
  // コンストラクタ
  public   KeyboardInputStream( TextArea   console )
    {
    this.console = console;
    inputBuffer = new RingBuffer( MAX_OF_INPUTBUFFER );
    console.addKeyListener( this );
    }


  //***************************************************************************************************
  // キーエコー なし
  public void   echoOff()
    {
    echo = false;
    }


  //***************************************************************************************************
  // キーエコー あり
  public void   echoOn()
    {
    echo = true;
    }


  //***************************************************************************************************
  // キーボードから一文字を入力
  public int   read()
    {
    while( inputBuffer.isEmpty() );  // この空ループでキー入力を待つ
    return   inputBuffer.get();
    }


  //***************************************************************************************************
  // キーボードから文字列を入力
  public String   readln()
    {
    while( !enterPressed );  // リターンキーが押されるまで待つ

    enterPressed = false;
    String   result = "";

    while( !inputBuffer.isEmpty() )
      result += (char)inputBuffer.get();

    // 入力バッファクリア
    inputBuffer.flush();

    return result;
    }




  //***************************************************************************************************
  // キーボードからの入力を  有効  にする
  public void   enable()
    {
    enabled = true;
    }





  //***************************************************************************************************
  // キーボードからの入力を  無効  にする
  public void   disable()
    {
    enabled = false;
    }





  //***************************************************************************************************
  // キーが押されて放されたら
  public void   keyTyped( KeyEvent   e )
    {
    char   k;

    // キーが押されたときに、不要なキーと判断されたなら 何事もなかったかのようにリターン
    if( consumed || e.isConsumed() )
      return;

    // 押されたキーを取得
    k = e.getKeyChar();

    switch( k )
      {
      // バックスペースが押されていたら、入力バッファを戻る
      case '\b':
        inputBuffer.back();
        break;

      // 普通のキーならバッファに足す
      default:
        inputBuffer.put( k );
        break;

      }

    // エコーしないとき
    if( !echo )
      e.consume();

    }



  //***************************************************************************************************
  // キーが押されたら
  public void   keyPressed( KeyEvent   e )
    {
    consumed = false;
    enterPressed = false;

    // Enable でなければ動作しない
    if( !enabled )
      {
      e.consume();
      consumed = true;
      return;
      }

    // キーコードを読み込む
    int   keycode = e.getKeyCode();

    // リターンキーが押されたら
    if( keycode == KeyEvent.VK_ENTER )
      {
      enterPressed = true;
      if( !echo )
        e.consume();
      consumed = true;
      }

    // バッファの限界に達したとき  バックスペースキー以外のキーだったら
    if( inputBuffer.isFull() && keycode != KeyEvent.VK_BACK_SPACE )
      {
      e.consume();
      consumed = true;
      }

    // バックスペースで戻りすぎるのを防ぐ
    if( inputBuffer.isEmpty() && keycode == KeyEvent.VK_BACK_SPACE )
      {
      e.consume();
      consumed = true;
      }

    // 矢印キーなどで変な方向に行ってしまうのを防ぐ
    if( keycode == KeyEvent.VK_UP || keycode == KeyEvent.VK_DOWN || keycode == KeyEvent.VK_LEFT || keycode == KeyEvent.VK_RIGHT || keycode == KeyEvent.VK_PAGE_DOWN || keycode == KeyEvent.VK_PAGE_UP || keycode == KeyEvent.VK_HOME || keycode == KeyEvent.VK_END )
      {
      e.consume();
      consumed = true;
      }

    }



  //***************************************************************************************************
  // キーが放されたら
  public void   keyReleased( KeyEvent   e )
    {
    }


  }