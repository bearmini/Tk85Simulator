package com.bearmini.tk85.base8085;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU 用 DMAコントローラ クラス */
public class DMAController8085 {
    private CPU8085 cpu;
    private DMADevice dev;

    private int startaddr;
    private int size;

    // メモリ監視時比較用
    private short memoryOld[];

    // ***************************************************************************************************
    // コンストラクタ
    public DMAController8085(CPU8085 cpu) {
        this.cpu = cpu;
    }

    // ***************************************************************************************************
    // DMA デバイスを割り当てる
    public void assignDMADevice(DMADevice dev, int startaddr, int size) {
        this.dev = dev;
        this.startaddr = startaddr;
        this.size = size;

        // 監視時比較用データを取得
        memoryOld = new short[size];
        for (int addr = startaddr; addr < startaddr + size; addr++)
            memoryOld[addr - startaddr] = cpu.mem.getValue(addr);
    }

    // ***************************************************************************************************
    // 監視メモリ領域が変更されたか
    public boolean isMemoryModified(int addr) {
        return memoryOld[addr - startaddr] != cpu.mem.getValue(addr);
    }

    // ***************************************************************************************************
    // 監視メモリ領域が変更されたら
    public void memoryModifyCheck() {
        if (dev == null)
            return;

        for (int addr = startaddr; addr < startaddr + size; addr++)
            if (isMemoryModified(addr)) {
                short newval = cpu.mem.getValue(addr);
                memoryOld[addr - startaddr] = newval;
                dev.onMemoryModified(addr, newval);
                // System.out.println(
                // "DMA Controller : memory modifing is detected : at " +
                // util.hex4(addr) + "  new value:" + util.hex2(newval) );
            }

    }

}
