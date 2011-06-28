package com.bearmini.tk85.io;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class RingBufferTest extends Frame implements ActionListener,
        WindowListener {
    /**
     * 
     */
    private static final long serialVersionUID = -6284291413734614955L;

    RingBuffer buf;
    TextArea console;

    public RingBufferTest() {
        buf = new RingBuffer(10);
        // ボーダーレイアウトでコンポーネントを配置
        setLayout(new BorderLayout());

        Button buttonTest = new Button("TEST");
        buttonTest.addActionListener(this);
        this.add("South", buttonTest);
        this.console = new TextArea("", 20, 30);
        this.add("Center", console);

        this.addWindowListener(this);
    }

    public static void main(String args[]) {
        RingBufferTest appli = new RingBufferTest();

        appli.setTitle("リングバッファ動作確認");
        appli.pack();
        appli.setVisible(true);
    }

    void test() {
        for (int i = 0; i < 50; i++) {
            buf.put(i);
            console.append("put:" + i + "\n");
            if (i % 3 == 0)
                console.append("get: " + buf.get() + "\n");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        test();
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        this.dispose();
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }

}
