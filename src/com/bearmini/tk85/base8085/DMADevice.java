package com.bearmini.tk85.base8085;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU 用 DMAデバイス インターフェース */
public interface DMADevice {

    public abstract void onMemoryModified(int addr, short val);

}
