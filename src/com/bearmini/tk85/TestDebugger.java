package com.bearmini.tk85;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.bearmini.tk85.base8085.Debugger;
import com.bearmini.tk85.base8085.DebuggerParent;

/* デバッガ用のフレームクラス */
public class TestDebugger extends Frame implements DebuggerParent,
        ActionListener, WindowListener {
    /**
     *
     */
    private static final long serialVersionUID = -2444040100756005797L;

    private Debugger debugger; // デバッガ

    public TextArea output;

    private MenuItem mniStart;
    private MenuItem mniStop;

    private boolean debuggingNow = false;

    // コンストラクタ
    public TestDebugger() {
        addWindowListener(this);

        // メニューを作成
        Menu mfile = new Menu("File");
        MenuItem mniQuit = new MenuItem("Quit");
        mniQuit.addActionListener(this);
        mfile.add(mniQuit);

        Menu mdeb = new Menu("Debug");
        mniStart = new MenuItem("Start");
        mniStop = new MenuItem("Stop");
        mniStart.addActionListener(this);
        mniStop.addActionListener(this);
        mdeb.add(mniStart);
        mdeb.add(mniStop);
        mniStop.setEnabled(false);

        MenuBar mb = new MenuBar();
        mb.add(mfile);
        mb.add(mdeb);
        setMenuBar(mb);

        // ボーダーレイアウトでコンポーネントを配置
        setLayout(new BorderLayout());

        // Center部分に出力領域を追加
        output = new TextArea("", 25, 80);
        add("Center", output);
        output.setFont(new Font("SansSerif", Font.PLAIN, 20));
    }

    // イベントハンドラ
    public void windowOpened(final WindowEvent e) {
    }

    public final void windowClosing(final WindowEvent e) {
        if (e.getID() == Event.WINDOW_DESTROY) {
            System.exit(0);
        }
    }

    public void windowClosed(final WindowEvent e) {
    }

    public void windowIconified(final WindowEvent e) {
    }

    public void windowDeiconified(final WindowEvent e) {
    }

    public void windowActivated(final WindowEvent e) {
    }

    public void windowDeactivated(final WindowEvent e) {
    }

    public final void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("Quit")) {
            System.exit(0);
        } else if (e.getActionCommand().equals("Start")) {
            startDebug();
        } else if (e.getActionCommand().equals("Stop")) {
            stopDebug();
        }
    }

    // アプリケーションのメイン
    public static void main(final String[] args) {
        TestDebugger window = new TestDebugger();

        window.setTitle("デバッガ動作確認");
        window.pack();
        window.setVisible(true);

        window.startDebug();

    }

    // デバッグを開始
    public final void startDebug() {
        if (debuggingNow) {
            return;
        }

        debuggingNow = true;
        mniStart.setEnabled(false);
        mniStop.setEnabled(false);

        debugger = new Debugger(this, this, output);
        debugger.start();

    }

    // デバッグを強制終了
    public final void stopDebug() {
        if (!debuggingNow) {
            return;
        }

        onEndDebug();
        debugger.requestStop();
    }

    // デバッグ開始時
    public final void onBeginDebug() {
    }

    // デバッグ終了時
    public final void onEndDebug() {
        debuggingNow = false;
        mniStart.setEnabled(true);
        mniStop.setEnabled(false);
    }

}
