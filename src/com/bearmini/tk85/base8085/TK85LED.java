package com.bearmini.tk85.base8085;

import java.awt.FlowLayout;
import java.awt.Panel;

public class TK85LED extends Panel implements DMADevice {
    /**
     *
     */
    private static final long serialVersionUID = 2580635679994682988L;

    private LED7Seg led[];
    private Panel addr = new Panel(); // アドレス表示用 LED のコンテナ
    private Panel data = new Panel(); // データ 表示用 LED のコンテナ

    private int dma_baseaddr;

    // コンストラクタ
    public TK85LED(int dma_baseaddr) {
        // DMA のベースアドレス
        this.dma_baseaddr = dma_baseaddr;

        // LED を8個生成
        led = new LED7Seg[8];
        for (int i = 0; i < 8; i++)
            led[i] = new LED7Seg();

        // アドレス表示用 LED を貼り付ける
        for (int i = 0; i < 4; i++)
            addr.add(led[i]);

        // データ表示用 LED を貼り付ける
        for (int i = 4; i < 8; i++)
            data.add(led[i]);

        // それぞれのコンテナを貼り付ける
        FlowLayout fl = new FlowLayout();
        fl.setHgap(10);
        setLayout(fl);
        add(addr);
        add(data);
    }

    public void onMemoryModified(int addr, short val) {
        led[addr - dma_baseaddr].setSegData(val);
    }

}
