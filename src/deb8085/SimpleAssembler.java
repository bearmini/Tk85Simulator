package deb8085;

import java.util.Hashtable;
import java.util.StringTokenizer;

import deb8085.instr.*;

//***************************************************************************************************
//***************************************************************************************************
/* 8085CPU 命令コード */
class InstructionCode8085 {
	int size;
	int b1;
	int b2;
	int b3;

	public InstructionCode8085(int size, int b1, int b2, int b3) {
		this.size = size;
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
	}
}

// ***************************************************************************************************
// ***************************************************************************************************
/* 8085CPU 用 エンコーダ */
class Encoder8085 {
	CPU8085 cpu;

	// エンコードテーブル
	Hashtable<String, Instruction8085> table = new Hashtable<String, Instruction8085>(256);

	// コンストラクタ
	public Encoder8085(CPU8085 cpu) {
		this.cpu = cpu;

		table.put("NOP", new InstructionNOP(cpu, (byte) 0x00, "NOP", (byte) 1));
		table.put("LXI B", new InstructionLXI(cpu, (byte) 0x01, "LXI B",
				(byte) 3));
		table.put("STAX B", new InstructionSTAX(cpu, (byte) 0x02, "STAX B",
				(byte) 1));
		table.put("INX B", new InstructionINX(cpu, (byte) 0x03, "INX B",
				(byte) 1));
		table.put("INR B", new InstructionINR(cpu, (byte) 0x04, "INR B",
				(byte) 1));
		table.put("DCR B", new InstructionDCR(cpu, (byte) 0x05, "DCR B",
				(byte) 1));
		table.put("MVI B", new InstructionMVI(cpu, (byte) 0x06, "MVI B",
				(byte) 2));
		table.put("RLC", new InstructionRLC(cpu, (byte) 0x07, "RLC", (byte) 1));
		table.put("DAD B", new InstructionDAD(cpu, (byte) 0x09, "DAD B",
				(byte) 1));
		table.put("LDAX B", new InstructionLDAX(cpu, (byte) 0x0A, "LDAX B",
				(byte) 1));
		table.put("DCX B", new InstructionDCX(cpu, (byte) 0x0B, "DCX B",
				(byte) 1));
		table.put("INR C", new InstructionINR(cpu, (byte) 0x0C, "INR C",
				(byte) 1));
		table.put("DCR C", new InstructionDCR(cpu, (byte) 0x0D, "DCR C",
				(byte) 1));
		table.put("MVI C", new InstructionMVI(cpu, (byte) 0x0E, "MVI C",
				(byte) 2));
		table.put("RRC", new InstructionRRC(cpu, (byte) 0x0F, "RRC", (byte) 1));
		table.put("LXI D", new InstructionLXI(cpu, (byte) 0x11, "LXI D",
				(byte) 3));
		table.put("STAX D", new InstructionSTAX(cpu, (byte) 0x12, "STAX D",
				(byte) 1));
		table.put("INX D", new InstructionINX(cpu, (byte) 0x13, "INX D",
				(byte) 1));
		table.put("INR D", new InstructionINR(cpu, (byte) 0x14, "INR D",
				(byte) 1));
		table.put("DCR D", new InstructionDCR(cpu, (byte) 0x15, "DCR D",
				(byte) 1));
		table.put("MVI D", new InstructionMVI(cpu, (byte) 0x16, "MVI D",
				(byte) 2));
		table.put("RAL", new InstructionRAL(cpu, (byte) 0x17, "RAL", (byte) 1));
		table.put("DAD D", new InstructionDAD(cpu, (byte) 0x19, "DAD D",
				(byte) 1));
		table.put("LDAX D", new InstructionLDAX(cpu, (byte) 0x1A, "LDAX D",
				(byte) 1));
		table.put("DCX D", new InstructionDCX(cpu, (byte) 0x1B, "DCX D",
				(byte) 1));
		table.put("INR E", new InstructionINR(cpu, (byte) 0x1C, "INR E",
				(byte) 1));
		table.put("DCR E", new InstructionDCR(cpu, (byte) 0x1D, "DCR E",
				(byte) 1));
		table.put("MVI E", new InstructionMVI(cpu, (byte) 0x1E, "MVI E",
				(byte) 2));
		table.put("RAR", new InstructionRAR(cpu, (byte) 0x1F, "RAR", (byte) 1));
		table.put("LXI H", new InstructionLXI(cpu, (byte) 0x21, "LXI H",
				(byte) 1));
		table.put("SHLD", new InstructionSHLD(cpu, (byte) 0x22, "SHLD",
				(byte) 3));
		table.put("INX H", new InstructionINX(cpu, (byte) 0x23, "INX H",
				(byte) 1));
		table.put("INR H", new InstructionINR(cpu, (byte) 0x24, "INR H",
				(byte) 1));
		table.put("DCR H", new InstructionDCR(cpu, (byte) 0x25, "DCR H",
				(byte) 1));
		table.put("MVI H", new InstructionMVI(cpu, (byte) 0x26, "MVI H",
				(byte) 2));
		table.put("DAA", new InstructionDAA(cpu, (byte) 0x27, "DAA", (byte) 1));
		table.put("DAD H", new InstructionDAD(cpu, (byte) 0x29, "DAD H",
				(byte) 1));
		table.put("LHLD", new InstructionLHLD(cpu, (byte) 0x2A, "LHLD",
				(byte) 3));
		table.put("DCX H", new InstructionDCX(cpu, (byte) 0x2B, "DCX H",
				(byte) 1));
		table.put("INR L", new InstructionINR(cpu, (byte) 0x2C, "INR L",
				(byte) 1));
		table.put("DCR L", new InstructionDCR(cpu, (byte) 0x2D, "DCR L",
				(byte) 1));
		table.put("MVI L", new InstructionMVI(cpu, (byte) 0x2E, "MVI L",
				(byte) 2));
		table.put("CMA", new InstructionCMA(cpu, (byte) 0x2F, "CMA", (byte) 1));
		table.put("LXI SP", new InstructionLXI(cpu, (byte) 0x31, "LXI SP",
				(byte) 1));
		table.put("STA", new InstructionSTA(cpu, (byte) 0x32, "STA", (byte) 3));
		table.put("INX SP", new InstructionINX(cpu, (byte) 0x33, "INX SP",
				(byte) 1));
		table.put("INR M", new InstructionINR(cpu, (byte) 0x34, "INR M",
				(byte) 1));
		table.put("DCR M", new InstructionDCR(cpu, (byte) 0x35, "DCR M",
				(byte) 1));
		table.put("MVI M", new InstructionMVI(cpu, (byte) 0x36, "MVI M",
				(byte) 1));
		table.put("STC", new InstructionSTC(cpu, (byte) 0x37, "STC", (byte) 1));
		table.put("DAD SP", new InstructionDAD(cpu, (byte) 0x39, "DAD SP",
				(byte) 1));
		table.put("LDA", new InstructionLDA(cpu, (byte) 0x3A, "LDA", (byte) 3));
		table.put("DCX SP", new InstructionDCX(cpu, (byte) 0x3B, "DCX SP",
				(byte) 1));
		table.put("INR A", new InstructionINR(cpu, (byte) 0x3C, "INR A",
				(byte) 1));
		table.put("DCR A", new InstructionDCR(cpu, (byte) 0x3D, "DCR A",
				(byte) 1));
		table.put("MVI A", new InstructionMVI(cpu, (byte) 0x3E, "MVI A",
				(byte) 2));
		table.put("CMC", new InstructionCMC(cpu, (byte) 0x3F, "CMC", (byte) 1));
		table.put("MOV B,B", new InstructionMOV(cpu, (byte) 0x40, "MOV B,B",
				(byte) 1));
		table.put("MOV B,C", new InstructionMOV(cpu, (byte) 0x41, "MOV B,C",
				(byte) 1));
		table.put("MOV B,D", new InstructionMOV(cpu, (byte) 0x42, "MOV B,D",
				(byte) 1));
		table.put("MOV B,E", new InstructionMOV(cpu, (byte) 0x43, "MOV B,E",
				(byte) 1));
		table.put("MOV B,H", new InstructionMOV(cpu, (byte) 0x44, "MOV B,H",
				(byte) 1));
		table.put("MOV B,L", new InstructionMOV(cpu, (byte) 0x45, "MOV B,L",
				(byte) 1));
		table.put("MOV B,M", new InstructionMOV(cpu, (byte) 0x46, "MOV B,M",
				(byte) 1));
		table.put("MOV B,A", new InstructionMOV(cpu, (byte) 0x47, "MOV B,A",
				(byte) 1));
		table.put("MOV C,B", new InstructionMOV(cpu, (byte) 0x48, "MOV C,B",
				(byte) 1));
		table.put("MOV C,C", new InstructionMOV(cpu, (byte) 0x49, "MOV C,C",
				(byte) 1));
		table.put("MOV C,D", new InstructionMOV(cpu, (byte) 0x4A, "MOV C,D",
				(byte) 1));
		table.put("MOV C,E", new InstructionMOV(cpu, (byte) 0x4B, "MOV C,E",
				(byte) 1));
		table.put("MOV C,H", new InstructionMOV(cpu, (byte) 0x4C, "MOV C,H",
				(byte) 1));
		table.put("MOV C,L", new InstructionMOV(cpu, (byte) 0x4D, "MOV C,L",
				(byte) 1));
		table.put("MOV C,M", new InstructionMOV(cpu, (byte) 0x4E, "MOV C,M",
				(byte) 1));
		table.put("MOV C,A", new InstructionMOV(cpu, (byte) 0x4F, "MOV C,A",
				(byte) 1));
		table.put("MOV D,B", new InstructionMOV(cpu, (byte) 0x50, "MOV D,B",
				(byte) 1));
		table.put("MOV D,C", new InstructionMOV(cpu, (byte) 0x51, "MOV D,C",
				(byte) 1));
		table.put("MOV D,D", new InstructionMOV(cpu, (byte) 0x52, "MOV D,D",
				(byte) 1));
		table.put("MOV D,E", new InstructionMOV(cpu, (byte) 0x53, "MOV D,E",
				(byte) 1));
		table.put("MOV D,H", new InstructionMOV(cpu, (byte) 0x54, "MOV D,H",
				(byte) 1));
		table.put("MOV D,L", new InstructionMOV(cpu, (byte) 0x55, "MOV D,L",
				(byte) 1));
		table.put("MOV D,M", new InstructionMOV(cpu, (byte) 0x56, "MOV D,M",
				(byte) 1));
		table.put("MOV D,A", new InstructionMOV(cpu, (byte) 0x57, "MOV D,A",
				(byte) 1));
		table.put("MOV E,B", new InstructionMOV(cpu, (byte) 0x58, "MOV E,B",
				(byte) 1));
		table.put("MOV E,C", new InstructionMOV(cpu, (byte) 0x59, "MOV E,C",
				(byte) 1));
		table.put("MOV E,D", new InstructionMOV(cpu, (byte) 0x5A, "MOV E,D",
				(byte) 1));
		table.put("MOV E,E", new InstructionMOV(cpu, (byte) 0x5B, "MOV E,E",
				(byte) 1));
		table.put("MOV E,H", new InstructionMOV(cpu, (byte) 0x5C, "MOV E,H",
				(byte) 1));
		table.put("MOV E,L", new InstructionMOV(cpu, (byte) 0x5D, "MOV E,L",
				(byte) 1));
		table.put("MOV E,M", new InstructionMOV(cpu, (byte) 0x5E, "MOV E,M",
				(byte) 1));
		table.put("MOV E,A", new InstructionMOV(cpu, (byte) 0x5F, "MOV E,A",
				(byte) 1));
		table.put("MOV H,B", new InstructionMOV(cpu, (byte) 0x60, "MOV H,B",
				(byte) 1));
		table.put("MOV H,C", new InstructionMOV(cpu, (byte) 0x61, "MOV H,C",
				(byte) 1));
		table.put("MOV H,D", new InstructionMOV(cpu, (byte) 0x62, "MOV H,D",
				(byte) 1));
		table.put("MOV H,E", new InstructionMOV(cpu, (byte) 0x63, "MOV H,E",
				(byte) 1));
		table.put("MOV H,H", new InstructionMOV(cpu, (byte) 0x64, "MOV H,H",
				(byte) 1));
		table.put("MOV H,L", new InstructionMOV(cpu, (byte) 0x65, "MOV H,L",
				(byte) 1));
		table.put("MOV H,M", new InstructionMOV(cpu, (byte) 0x66, "MOV H,M",
				(byte) 1));
		table.put("MOV H,A", new InstructionMOV(cpu, (byte) 0x67, "MOV H,A",
				(byte) 1));
		table.put("MOV L,B", new InstructionMOV(cpu, (byte) 0x68, "MOV L,B",
				(byte) 1));
		table.put("MOV L,C", new InstructionMOV(cpu, (byte) 0x69, "MOV L,C",
				(byte) 1));
		table.put("MOV L,D", new InstructionMOV(cpu, (byte) 0x6A, "MOV L,D",
				(byte) 1));
		table.put("MOV L,E", new InstructionMOV(cpu, (byte) 0x6B, "MOV L,E",
				(byte) 1));
		table.put("MOV L,H", new InstructionMOV(cpu, (byte) 0x6C, "MOV L,H",
				(byte) 1));
		table.put("MOV L,L", new InstructionMOV(cpu, (byte) 0x6D, "MOV L,L",
				(byte) 1));
		table.put("MOV L,M", new InstructionMOV(cpu, (byte) 0x6E, "MOV L,M",
				(byte) 1));
		table.put("MOV L,A", new InstructionMOV(cpu, (byte) 0x6F, "MOV L,A",
				(byte) 1));
		table.put("MOV M,B", new InstructionMOV(cpu, (byte) 0x70, "MOV M,B",
				(byte) 1));
		table.put("MOV M,C", new InstructionMOV(cpu, (byte) 0x71, "MOV M,C",
				(byte) 1));
		table.put("MOV M,D", new InstructionMOV(cpu, (byte) 0x72, "MOV M,D",
				(byte) 1));
		table.put("MOV M,E", new InstructionMOV(cpu, (byte) 0x73, "MOV M,E",
				(byte) 1));
		table.put("MOV M,H", new InstructionMOV(cpu, (byte) 0x74, "MOV M,H",
				(byte) 1));
		table.put("MOV M,L", new InstructionMOV(cpu, (byte) 0x75, "MOV M,L",
				(byte) 1));
		table.put("HLT", new InstructionHLT(cpu, (byte) 0x76, "HLT", (byte) 1));
		table.put("MOV M,A", new InstructionMOV(cpu, (byte) 0x77, "MOV M,A",
				(byte) 1));
		table.put("MOV A,B", new InstructionMOV(cpu, (byte) 0x78, "MOV A,B",
				(byte) 1));
		table.put("MOV A,C", new InstructionMOV(cpu, (byte) 0x79, "MOV A,C",
				(byte) 1));
		table.put("MOV A,D", new InstructionMOV(cpu, (byte) 0x7A, "MOV A,D",
				(byte) 1));
		table.put("MOV A,E", new InstructionMOV(cpu, (byte) 0x7B, "MOV A,E",
				(byte) 1));
		table.put("MOV A,H", new InstructionMOV(cpu, (byte) 0x7C, "MOV A,H",
				(byte) 1));
		table.put("MOV A,L", new InstructionMOV(cpu, (byte) 0x7D, "MOV A,L",
				(byte) 1));
		table.put("MOV A,M", new InstructionMOV(cpu, (byte) 0x7E, "MOV A,M",
				(byte) 1));
		table.put("MOV A,A", new InstructionMOV(cpu, (byte) 0x7F, "MOV A,A",
				(byte) 1));
		table.put("ADD B", new InstructionADD(cpu, (byte) 0x80, "ADD B",
				(byte) 1));
		table.put("ADD C", new InstructionADD(cpu, (byte) 0x81, "ADD C",
				(byte) 1));
		table.put("ADD D", new InstructionADD(cpu, (byte) 0x82, "ADD D",
				(byte) 1));
		table.put("ADD E", new InstructionADD(cpu, (byte) 0x83, "ADD E",
				(byte) 1));
		table.put("ADD H", new InstructionADD(cpu, (byte) 0x84, "ADD H",
				(byte) 1));
		table.put("ADD L", new InstructionADD(cpu, (byte) 0x85, "ADD L",
				(byte) 1));
		table.put("ADD M", new InstructionADD(cpu, (byte) 0x86, "ADD M",
				(byte) 1));
		table.put("ADD A", new InstructionADD(cpu, (byte) 0x87, "ADD A",
				(byte) 1));
		table.put("ADC B", new InstructionADC(cpu, (byte) 0x88, "ADC B",
				(byte) 1));
		table.put("ADC C", new InstructionADC(cpu, (byte) 0x89, "ADC C",
				(byte) 1));
		table.put("ADC D", new InstructionADC(cpu, (byte) 0x8A, "ADC D",
				(byte) 1));
		table.put("ADC E", new InstructionADC(cpu, (byte) 0x8B, "ADC E",
				(byte) 1));
		table.put("ADC H", new InstructionADC(cpu, (byte) 0x8C, "ADC H",
				(byte) 1));
		table.put("ADC L", new InstructionADC(cpu, (byte) 0x8D, "ADC L",
				(byte) 1));
		table.put("ADC M", new InstructionADC(cpu, (byte) 0x8E, "ADC M",
				(byte) 1));
		table.put("ADC A", new InstructionADC(cpu, (byte) 0x8F, "ADC A",
				(byte) 1));
		table.put("SUB B", new InstructionSUB(cpu, (byte) 0x90, "SUB B",
				(byte) 1));
		table.put("SUB C", new InstructionSUB(cpu, (byte) 0x91, "SUB C",
				(byte) 1));
		table.put("SUB D", new InstructionSUB(cpu, (byte) 0x92, "SUB D",
				(byte) 1));
		table.put("SUB E", new InstructionSUB(cpu, (byte) 0x93, "SUB E",
				(byte) 1));
		table.put("SUB H", new InstructionSUB(cpu, (byte) 0x94, "SUB H",
				(byte) 1));
		table.put("SUB L", new InstructionSUB(cpu, (byte) 0x95, "SUB L",
				(byte) 1));
		table.put("SUB M", new InstructionSUB(cpu, (byte) 0x96, "SUB M",
				(byte) 1));
		table.put("SUB A", new InstructionSUB(cpu, (byte) 0x97, "SUB A",
				(byte) 1));
		table.put("SBB B", new InstructionSBB(cpu, (byte) 0x98, "SBB B",
				(byte) 1));
		table.put("SBB C", new InstructionSBB(cpu, (byte) 0x99, "SBB C",
				(byte) 1));
		table.put("SBB D", new InstructionSBB(cpu, (byte) 0x9A, "SBB D",
				(byte) 1));
		table.put("SBB E", new InstructionSBB(cpu, (byte) 0x9B, "SBB E",
				(byte) 1));
		table.put("SBB H", new InstructionSBB(cpu, (byte) 0x9C, "SBB H",
				(byte) 1));
		table.put("SBB L", new InstructionSBB(cpu, (byte) 0x9D, "SBB L",
				(byte) 1));
		table.put("SBB M", new InstructionSBB(cpu, (byte) 0x9E, "SBB M",
				(byte) 1));
		table.put("SBB A", new InstructionSBB(cpu, (byte) 0x9F, "SBB A",
				(byte) 1));
		table.put("ANA B", new InstructionANA(cpu, (byte) 0xA0, "ANA B",
				(byte) 1));
		table.put("ANA C", new InstructionANA(cpu, (byte) 0xA1, "ANA C",
				(byte) 1));
		table.put("ANA D", new InstructionANA(cpu, (byte) 0xA2, "ANA D",
				(byte) 1));
		table.put("ANA E", new InstructionANA(cpu, (byte) 0xA3, "ANA E",
				(byte) 1));
		table.put("ANA H", new InstructionANA(cpu, (byte) 0xA4, "ANA H",
				(byte) 1));
		table.put("ANA L", new InstructionANA(cpu, (byte) 0xA5, "ANA L",
				(byte) 1));
		table.put("ANA M", new InstructionANA(cpu, (byte) 0xA6, "ANA M",
				(byte) 1));
		table.put("ANA A", new InstructionANA(cpu, (byte) 0xA7, "ANA A",
				(byte) 1));
		table.put("XRA B", new InstructionXRA(cpu, (byte) 0xA8, "XRA B",
				(byte) 1));
		table.put("XRA C", new InstructionXRA(cpu, (byte) 0xA9, "XRA C",
				(byte) 1));
		table.put("XRA D", new InstructionXRA(cpu, (byte) 0xAA, "XRA D",
				(byte) 1));
		table.put("XRA E", new InstructionXRA(cpu, (byte) 0xAB, "XRA E",
				(byte) 1));
		table.put("XRA H", new InstructionXRA(cpu, (byte) 0xAC, "XRA H",
				(byte) 1));
		table.put("XRA L", new InstructionXRA(cpu, (byte) 0xAD, "XRA L",
				(byte) 1));
		table.put("XRA M", new InstructionXRA(cpu, (byte) 0xAE, "XRA M",
				(byte) 1));
		table.put("XRA A", new InstructionXRA(cpu, (byte) 0xAF, "XRA A",
				(byte) 1));
		table.put("ORA B", new InstructionORA(cpu, (byte) 0xB0, "ORA B",
				(byte) 1));
		table.put("ORA C", new InstructionORA(cpu, (byte) 0xB1, "ORA C",
				(byte) 1));
		table.put("ORA D", new InstructionORA(cpu, (byte) 0xB2, "ORA D",
				(byte) 1));
		table.put("ORA E", new InstructionORA(cpu, (byte) 0xB3, "ORA E",
				(byte) 1));
		table.put("ORA H", new InstructionORA(cpu, (byte) 0xB4, "ORA H",
				(byte) 1));
		table.put("ORA L", new InstructionORA(cpu, (byte) 0xB5, "ORA L",
				(byte) 1));
		table.put("ORA M", new InstructionORA(cpu, (byte) 0xB6, "ORA M",
				(byte) 1));
		table.put("ORA A", new InstructionORA(cpu, (byte) 0xB7, "ORA A",
				(byte) 1));
		table.put("CMP B", new InstructionCMP(cpu, (byte) 0xB8, "CMP B",
				(byte) 1));
		table.put("CMP C", new InstructionCMP(cpu, (byte) 0xB9, "CMP C",
				(byte) 1));
		table.put("CMP D", new InstructionCMP(cpu, (byte) 0xBA, "CMP D",
				(byte) 1));
		table.put("CMP E", new InstructionCMP(cpu, (byte) 0xBB, "CMP E",
				(byte) 1));
		table.put("CMP H", new InstructionCMP(cpu, (byte) 0xBC, "CMP H",
				(byte) 1));
		table.put("CMP L", new InstructionCMP(cpu, (byte) 0xBD, "CMP L",
				(byte) 1));
		table.put("CMP M", new InstructionCMP(cpu, (byte) 0xBE, "CMP M",
				(byte) 1));
		table.put("CMP A", new InstructionCMP(cpu, (byte) 0xBF, "CMP A",
				(byte) 1));
		table.put("RNZ", new InstructionRET(cpu, (byte) 0xC0, "RNZ", (byte) 1));
		table.put("POP B", new InstructionPOP(cpu, (byte) 0xC1, "POP B",
				(byte) 1));
		table.put("JNZ", new InstructionJMP(cpu, (byte) 0xC2, "JNZ", (byte) 3));
		table.put("JMP", new InstructionJMP(cpu, (byte) 0xC3, "JMP", (byte) 3));
		table
				.put("CNZ", new InstructionCALL(cpu, (byte) 0xC4, "CNZ",
						(byte) 3));
		table.put("PUSH B", new InstructionPUSH(cpu, (byte) 0xC5, "PUSH B",
				(byte) 1));
		table.put("ADI", new InstructionADI(cpu, (byte) 0xC6, "ADI", (byte) 2));
		table.put("RST 0", new InstructionRST(cpu, (byte) 0xC7, "RST 0",
				(byte) 1));
		table.put("RZ", new InstructionRET(cpu, (byte) 0xC8, "RZ", (byte) 1));
		table.put("RET", new InstructionRET(cpu, (byte) 0xC9, "RET", (byte) 1));
		table.put("JZ", new InstructionJMP(cpu, (byte) 0xCA, "JZ", (byte) 3));
		table.put("CZ", new InstructionCALL(cpu, (byte) 0xCC, "CZ", (byte) 3));
		table.put("CALL", new InstructionCALL(cpu, (byte) 0xCD, "CALL",
				(byte) 3));
		table.put("ACI", new InstructionACI(cpu, (byte) 0xCE, "ACI", (byte) 2));
		table.put("RST 1", new InstructionRST(cpu, (byte) 0xCF, "RST 1",
				(byte) 1));
		table.put("RNC", new InstructionRET(cpu, (byte) 0xD0, "RNC", (byte) 1));
		table.put("POP D", new InstructionPOP(cpu, (byte) 0xD1, "POP D",
				(byte) 1));
		table.put("JNC", new InstructionJMP(cpu, (byte) 0xD2, "JNC", (byte) 3));
		table.put("OUT", new InstructionOUT(cpu, (byte) 0xD3, "OUT", (byte) 2));
		table
				.put("CNC", new InstructionCALL(cpu, (byte) 0xD4, "CNC",
						(byte) 3));
		table.put("PUSH D", new InstructionPUSH(cpu, (byte) 0xD5, "PUSH D",
				(byte) 1));
		table.put("SUI", new InstructionSUI(cpu, (byte) 0xD6, "SUI", (byte) 2));
		table.put("RST 2", new InstructionRST(cpu, (byte) 0xD7, "RST 2",
				(byte) 1));
		table.put("RC", new InstructionRET(cpu, (byte) 0xD8, "RC", (byte) 1));
		table.put("JC", new InstructionJMP(cpu, (byte) 0xDA, "JC", (byte) 3));
		table.put("IN", new InstructionIN(cpu, (byte) 0xDB, "IN", (byte) 2));
		table.put("CC", new InstructionCALL(cpu, (byte) 0xDC, "CC", (byte) 3));
		table.put("SBI", new InstructionSBI(cpu, (byte) 0xDE, "SBI", (byte) 2));
		table.put("RST 3", new InstructionRST(cpu, (byte) 0xDF, "RST 3",
				(byte) 1));
		table.put("RPO", new InstructionRET(cpu, (byte) 0xE0, "RPO", (byte) 1));
		table.put("POP H", new InstructionPOP(cpu, (byte) 0xE1, "POP H",
				(byte) 1));
		table.put("JPO", new InstructionJMP(cpu, (byte) 0xE2, "JPO", (byte) 3));
		table.put("XTHL", new InstructionXTHL(cpu, (byte) 0xE3, "XTHL",
				(byte) 1));
		table
				.put("CPO", new InstructionCALL(cpu, (byte) 0xE4, "CPO",
						(byte) 3));
		table.put("PUSH H", new InstructionPUSH(cpu, (byte) 0xE5, "PUSH H",
				(byte) 1));
		table.put("ANI", new InstructionANI(cpu, (byte) 0xE6, "ANI", (byte) 2));
		table.put("RST 4", new InstructionRST(cpu, (byte) 0xE7, "RST 4",
				(byte) 1));
		table.put("RPE", new InstructionRET(cpu, (byte) 0xE8, "RPE", (byte) 1));
		table.put("PCHL", new InstructionPCHL(cpu, (byte) 0xE9, "PCHL",
				(byte) 1));
		table.put("JPE", new InstructionJMP(cpu, (byte) 0xEA, "JPE", (byte) 3));
		table.put("XCHG", new InstructionXCHG(cpu, (byte) 0xEB, "XCHG",
				(byte) 1));
		table
				.put("CPE", new InstructionCALL(cpu, (byte) 0xEC, "CPE",
						(byte) 3));
		table.put("XRI", new InstructionXRI(cpu, (byte) 0xEE, "XRI", (byte) 2));
		table.put("RST 5", new InstructionRST(cpu, (byte) 0xEF, "RST 5",
				(byte) 1));
		table.put("RP", new InstructionRET(cpu, (byte) 0xF0, "RP", (byte) 1));
		table.put("POP PSW", new InstructionPOP(cpu, (byte) 0xF1, "POP PSW",
				(byte) 1));
		table.put("JP", new InstructionJMP(cpu, (byte) 0xF2, "JP", (byte) 3));
		table.put("DI", new InstructionDI(cpu, (byte) 0xF3, "DI", (byte) 1));
		table.put("CP", new InstructionCALL(cpu, (byte) 0xF4, "CP", (byte) 3));
		table.put("PUSH PSW", new InstructionPUSH(cpu, (byte) 0xF5, "PUSH PSW",
				(byte) 1));
		table.put("ORI", new InstructionORI(cpu, (byte) 0xF6, "ORI", (byte) 2));
		table.put("RST 6", new InstructionRST(cpu, (byte) 0xF7, "RST 6",
				(byte) 1));
		table.put("RM", new InstructionRET(cpu, (byte) 0xF8, "RM", (byte) 1));
		table.put("SPHL", new InstructionSPHL(cpu, (byte) 0xF9, "SPHL",
				(byte) 1));
		table.put("JM", new InstructionJMP(cpu, (byte) 0xFA, "JM", (byte) 3));
		table.put("EI", new InstructionEI(cpu, (byte) 0xFB, "EI", (byte) 1));
		table.put("CM", new InstructionCALL(cpu, (byte) 0xFC, "CM", (byte) 3));
		table.put("CPI", new InstructionCPI(cpu, (byte) 0xFE, "CPI", (byte) 2));
		table.put("RST 7", new InstructionRST(cpu, (byte) 0xFF, "RST 7",
				(byte) 1));
	}

	// 8085インストラクションニーモニック → 命令コード列
	public InstructionCode8085 encode(String opecode, String operand1,
			String operand2) throws OnEncodeException {
		// 大文字化する
		opecode = opecode.toUpperCase();
		if (operand1 != null)
			operand1 = operand1.toUpperCase();
		if (operand2 != null)
			operand2 = operand2.toUpperCase();

		// テーブルからニーモニックに対応する"命令"(Instruction8085クラス)を得る
		Instruction8085 inst = (Instruction8085) table.get(opecode);

		// 得るのを失敗したら、オペランドを足して再挑戦
		if ((inst == null) && (operand1 != null)) {
			inst = (Instruction8085) table.get(opecode + " " + operand1);

			if ((inst == null) && (operand2 != null))
				inst = (Instruction8085) table.get(opecode + " " + operand1
						+ "," + operand2);
		}

		if (inst == null)
			throw new OnEncodeException("不正な命令です.");

		// Instruction8085 クラスの各命令自体に、オペランドを解釈させる
		inst.encode(operand1, operand2);

		// 解釈されたオペランドを基に、命令コード列を生成して返す
		return new InstructionCode8085(inst.getSize(), inst.getOpecode(), inst
				.getB2(), inst.getB3());

	}

}

// ***************************************************************************************************
// ***************************************************************************************************
// 簡易アセンブラ
public class SimpleAssembler {

	Hashtable<String, Instruction8085> table = new Hashtable<String, Instruction8085>(256);

	CPU8085 cpu;
	Encoder8085 encoder;

	// ***************************************************************************************************
	// コンストラクタ
	public SimpleAssembler(CPU8085 cpu) {
		this.cpu = cpu;
		encoder = new Encoder8085(cpu);
	}

	// ***************************************************************************************************
	// 一行アセンブル
	public int assemble(int codeaddr, String s) throws OnEncodeException {
		StringTokenizer st = new StringTokenizer(s, " \t"); // 空白、タブを区切りとみなす
		String opecode = null;
		String operand = null;
		String operand1 = null;
		String operand2 = null;

		// 文字列を命令とオペランドに大きく分解
		if (st.hasMoreTokens()) {
			opecode = st.nextToken();
			if (st.hasMoreTokens())
				operand = st.nextToken();
		}

		if (operand != null) {
			st = new StringTokenizer(operand, ","); // コンマを区切りとみなす

			// オペランドを二つに分解
			if (st.hasMoreTokens()) {
				operand1 = st.nextToken();
				if (st.hasMoreTokens())
					operand2 = st.nextToken();
			}
		}

		// オペランドを正規化（ラベルをアドレスに変換、数式を解析、2進数、8進数、10進数をすべて16進数に統一)
		normalize(operand1);
		normalize(operand2);

		// エンコード
		InstructionCode8085 inst;
		try {
			inst = encoder.encode(opecode, operand1, operand2);
		} catch (OnEncodeException e) {
			throw e;
		}

		// 命令コードを書き込む
		cpu.mem.setValue(codeaddr, inst.b1);

		// 命令サイズによって、さらにもう一バイトか二バイト書き込む
		if (inst.size == 2)
			cpu.mem.setValue(codeaddr + 1, inst.b2);
		else if (inst.size == 3) {
			cpu.mem.setValue(codeaddr + 1, inst.b2);
			cpu.mem.setValue(codeaddr + 2, inst.b3);
		}

		// 命令サイズを返す
		return inst.size;
	}

	// ***************************************************************************************************
	// オペランドを正規化（ラベルをアドレスに変換、数式を解析、2進数、8進数、10進数をすべて16進数に統一)
	void normalize(String operand) {
	}

}
