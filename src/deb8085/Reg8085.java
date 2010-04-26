package deb8085;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU レジスタクラス */
public class Reg8085 {
	CPU8085 cpu; // 自分を所有している CPU

	// レジスタ値を記憶しているプライベートな変数
	short a, b, c, d, e, h, l, m, flags;
	int pc, sp;

	// レジスタを識別するためのレジスタ名の定数
	public final static byte A = 0;
	public final static byte B = 1;
	public final static byte C = 2;
	public final static byte D = 3;
	public final static byte E = 4;
	public final static byte H = 5;
	public final static byte L = 6;
	public final static byte M = 7;
	public final static byte FLAGS = 8;
	public final static short AF = 9;
	public final static short BC = 10;
	public final static short DE = 11;
	public final static short HL = 12;
	public final static short PC = 13;
	public final static short SP = 14;

	// フラグを識別するためのフラグ名の定数
	public final static byte Zf = 100;
	public final static byte Cf = 101;
	public final static byte ACf = 102;
	public final static byte Sf = 103;
	public final static byte Pf = 104;

	// ***************************************************************************************************
	// コンストラクタ
	public Reg8085(CPU8085 parent) {
		cpu = parent;

		a = b = c = d = e = h = l = m = 0;
		flags = 0x02; // bit1 is '1' fixed
		pc = sp = 0;
	}

	// ***************************************************************************************************
	// レジスタ値の取得・設定
	public void setReg(int kind, int value) {
		if (Reg8085.A <= kind && kind <= Reg8085.FLAGS)
			value = value & 0x00FF;
		else if (Reg8085.AF <= kind && kind <= Reg8085.SP)
			value = value & 0xFFFF;

		switch (kind) {
		case A:
			a = (short) value;
			break;
		case B:
			b = (short) value;
			break;
		case C:
			c = (short) value;
			break;
		case D:
			d = (short) value;
			break;
		case E:
			e = (short) value;
			break;
		case H:
			h = (short) value;
			break;
		case L:
			l = (short) value;
			break;
		case M:
			m = (short) value;
			cpu.mem.setValue(h * 0x100 + l, (short) value);
			break;
		case FLAGS:
			flags = (short) (value | 0x02); // bit1 is '1' fixed
			break;

		case AF:
			a = (short) ((value & 0xFF00) >> 8);
			flags = (short) ((value & 0x00FF) | 0x02); // bit1 is '1' fixed
			break;
		case BC:
			b = (short) ((value & 0xFF00) >> 8);
			c = (short) ((value & 0x00FF));
			break;
		case DE:
			d = (short) ((value & 0xFF00) >> 8);
			e = (short) ((value & 0x00FF));
			break;
		case HL:
			h = (short) ((value & 0xFF00) >> 8);
			l = (short) ((value & 0x00FF));
			break;
		case SP:
			sp = value;
			break;
		case PC:
			pc = value;
			break;
		}
	}

	public short getReg(byte kind) {
		switch (kind) {
		case A:
			return a;
		case B:
			return b;
		case C:
			return c;
		case D:
			return d;
		case E:
			return e;
		case H:
			return h;
		case L:
			return l;
		case M:
			m = cpu.mem.getValue(h * 0x100 + l);
			return m;
		case FLAGS:
			return flags;
		}

		return 0;
	}

	public int getReg(short kind) {
		switch (kind) {
		case AF:
			return ((a & 0xFF) << 8) | (flags & 0xFF);
		case BC:
			return ((b & 0xFF) << 8) | (c & 0xFF);
		case DE:
			return ((d & 0xFF) << 8) | (e & 0xFF);
		case HL:
			return ((h & 0xFF) << 8) | (l & 0xFF);
		case SP:
			return sp;
		case PC:
			return pc;
		}

		return 0;
	}

	// ***************************************************************************************************
	// フラグの設定・取得
	public void setFlag(byte kind, boolean value) {
		switch (kind) {
		case Zf:
			flags = (byte) ((value) ? flags | 0x40 : flags & 0xBF);
			break;
		case Cf:
			flags = (byte) ((value) ? flags | 0x01 : flags & 0xFE);
			break;
		case ACf:
			flags = (byte) ((value) ? flags | 0x10 : flags & 0xEF);
			break;
		case Sf:
			flags = (byte) ((value) ? flags | 0x80 : flags & 0x7F);
			break;
		case Pf:
			flags = (byte) ((value) ? flags | 0x04 : flags & 0xFB);
			break;
		}
	}

	public boolean getFlag(byte kind) {
		switch (kind) {
		case Zf:
			return (flags & 0x40) != 0;
		case Cf:
			return (flags & 0x01) != 0;
		case ACf:
			return (flags & 0x10) != 0;
		case Sf:
			return (flags & 0x80) != 0;
		case Pf:
			return (flags & 0x04) != 0;
		}

		return false;
	}

}
