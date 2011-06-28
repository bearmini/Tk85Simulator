package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* DAA 命令 */
public class InstructionDAA extends Instruction8085 {
    public InstructionDAA(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short regAorg = cpu.reg.getReg(Reg8085.A);
        short bcd;

        // 直前の命令によって、加算補正もしくは減算補正を行う
        // （μPD8085AC は 8085 と違って、減算後の BCD 補正もできる）
        if (!cpu.subtractedFlag) {
            bcd = bcdAdd(regAorg);
        } else {
            bcd = bcdSub(regAorg);
        }
        cpu.reg.setReg(Reg8085.A, bcd);
        cpu.updateFlags(CPU8085.FLAGUPDATE_ALL);
        cpu.reg.setFlag(Reg8085.Cf, calcBcdCarry(regAorg, bcd));
        cpu.reg.setFlag(Reg8085.ACf, calcBcdHalfCarry(regAorg, bcd));
        cpu.incPC(getSize());
    }

    private short bcdAdd(short val) {
        int l = (val & 0x0F);
        boolean carry_l = false;

        if ((l > 9) || cpu.reg.getFlag(Reg8085.ACf)) {
            val += 6;
            carry_l = (val > 0xFF);
        }

        int h = (val & 0xF0) >>> 4;

        if ((h > 9) || cpu.reg.getFlag(Reg8085.Cf) || carry_l) {
            val += 0x60;
        }

        return val;
    }

    private short bcdSub(short val) {
        int l = (val & 0x0F);
        boolean carry_l = false;

        if ((l > 9) || cpu.reg.getFlag(Reg8085.ACf)) {
            val -= 6;
            carry_l = (val < 0);
        }

        int h = (val & 0xF0) >>> 4;

        if ((h > 9) || cpu.reg.getFlag(Reg8085.Cf) || carry_l) {
            val -= 0x60;
        }

        return val;
    }

    public boolean calcBcdCarry(int val, int bcd) {
        if (!cpu.subtractedFlag) {
            return bcdAdd((short) val) > 0xFF;
        } else {
            return bcdSub((short) val) < 0;
        }
    }

    public boolean calcBcdHalfCarry(int val, int bcd) {
        if (!cpu.subtractedFlag) {
            int org_l = (val & 0x0F);
            int bcd_l = (bcd & 0x0F);
            if (bcd_l < org_l) {
                return true;
            }
            return false;
        } else {
            return false;
        }

    }

}
