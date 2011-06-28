package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.OnEncodeException;
import com.bearmini.tk85.base8085.Reg8085;
import com.bearmini.tk85.base8085.util;

//***************************************************************************************************
/* IN 命令 */
public class InstructionIN extends Instruction8085 {
    public InstructionIN(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        cpu.reg.setReg(Reg8085.A, cpu.ioport.in(getB2()));
        cpu.incPC(getSize());
    }

    public String toString() {
        return getMnemonic() + " " + util.hex2(getB2());
    }

    public void encode(String operand1, String operand2)
            throws OnEncodeException {
        if (operand1 == null)
            throw new OnEncodeException("不正なオペランド（１つめ）");
        setB2((short) util.unhex(operand1));
    }

}
