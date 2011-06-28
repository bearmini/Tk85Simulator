package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* PCHL 命令 */
public class InstructionPCHL extends Instruction8085 {
    public InstructionPCHL(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        int valPC = cpu.reg.getReg(Reg8085.PC);
        int valHL = cpu.reg.getReg(Reg8085.HL);

        cpu.reg.setReg(Reg8085.HL, valPC);
        cpu.reg.setReg(Reg8085.PC, valHL);

        // この命令自体によって PC の値が変わってしまうので
        // 次の行は要らない
        // cpu.incPC( getSize() );
    }

}
