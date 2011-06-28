package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.OnEncodeException;
import com.bearmini.tk85.base8085.Reg8085;
import com.bearmini.tk85.base8085.util;

//***************************************************************************************************
/* LDA 命令 */
public class InstructionLDA extends Instruction8085 {
    public InstructionLDA(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        int addr = getB3B2();
        short val = cpu.mem.getValue(addr);

        cpu.reg.setReg(Reg8085.A, val);
        cpu.incPC(getSize());
    }

    public String toString() {
        int addr = getB3B2();
        if (!cpu.publicLabels.existPublicLabel(addr))
            return getMnemonic() + " " + util.hex4(addr);
        else
            return getMnemonic() + " " + cpu.publicLabels.toPublicLabel(addr);
    }

    public void encode(String operand1, String operand2)
            throws OnEncodeException {
        if (operand1 == null)
            throw new OnEncodeException("不正なオペランド（１つめ）");
        int addr = util.unhex(operand1);
        setB2((short) (addr % 0x100));
        setB3((short) (addr / 0x100));
    }

}
