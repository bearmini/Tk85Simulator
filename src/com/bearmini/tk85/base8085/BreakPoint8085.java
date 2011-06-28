package com.bearmini.tk85.base8085;

/**
 */
public class BreakPoint8085 {
    public int addr;
    public int count;

    public BreakPoint8085(final int addr, final int count) {
        this.addr = addr;
        this.count = count;
    }

    private boolean isAvailable(final int addr) {
        if (this.addr == addr && count == 0) {
            return true;
        } else {
            count--; // ブレークポイントが参照された
            return false;
        }
    }

}
