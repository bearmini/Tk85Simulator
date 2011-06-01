package deb8085;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU メモリ領域クラス */
public class Mem8085 {
	final int MEMORYSIZE = 65536;

	short[] area = new short[MEMORYSIZE];
	boolean[] readonly = new boolean[MEMORYSIZE];

	// コンストラクタ
	public Mem8085() {
		for (int i = 0; i < MEMORYSIZE; i++) {
			area[i] = 0;
			readonly[i] = false;
		}
	}

	// 値をセット
	public void setValue(int addr, int value) {
		if (outOfAddressRange(addr)) {
			return;
		}
		
		if (readonly[addr])
			return;

		area[addr] = (short) (value & 0xFF);
	}

	// 値を読み出す
	public short getValue(int addr) {
		if (outOfAddressRange(addr)) {
			return 0xaa;// Error
		}

		return area[addr];
	}

	// リードオンリーかどうかをセット
	public void setReadOnly(int addr, boolean readonly) {
		if (outOfAddressRange(addr)) {
			return;
		}

		this.readonly[addr] = readonly;
	}

	public void setRange(int addr, short[] values) {
		if (outOfAddressRange(addr)) {
			return;
		}
		
		if (addr + values.length > 0xFFFF) {
			return;
		}
		
		for (int i = 0; i < values.length; ++i) {
			area[addr + i] = values[i];
		}
	}
	
	private boolean outOfAddressRange(int addr) {
		if ((addr < 0) || (0xFFFF < addr)) {
			return true;
		}
		return false;
	}
}
