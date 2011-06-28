package com.bearmini.tk85;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import com.bearmini.tk85.base8085.TK85Keyboard;
import com.bearmini.tk85.base8085.TK85LED;
import com.bearmini.tk85.base8085.TK85Simulator;

public class Tk85SimulatorApplet extends Applet {
    /**
     *
     */
    private static final long serialVersionUID = 8112550200725052568L;

    private TK85Simulator simulator; // TK85シミュレータ
    private TK85LED led;
    private TK85Keyboard kb;
    private static final int DMA_BASEADDR_LED = 0x83f8;

    public final void init() {
        setBackground(Color.green.darker().darker());
        setLayout(new BorderLayout());
        setFont(new Font("SansSerif", Font.PLAIN, 16));

        // LED を生成、貼り付け
        led = new TK85LED(DMA_BASEADDR_LED);
        add("North", led);

        // キーボードを生成、貼り付け
        kb = new TK85Keyboard();
        add("Center", kb);

        simulator = new TK85Simulator(led, kb);
        simulator.start();

    }
}
