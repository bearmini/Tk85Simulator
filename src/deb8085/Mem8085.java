package deb8085;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU �������̈�N���X */
public class Mem8085 {
	final int MEMORYSIZE = 65536;

	short[] area = new short[MEMORYSIZE];
	boolean[] readonly = new boolean[MEMORYSIZE];

	// �R���X�g���N�^
	public Mem8085() {
		for (int i = 0; i < MEMORYSIZE; i++) {
			area[i] = 0;
			readonly[i] = false;
		}
	}

	// �l���Z�b�g
	public void setValue(int addr, int value) {
		if (outOfAddressRange(addr)) {
			return;
		}
		
		if (readonly[addr])
			return;

		area[addr] = (short) (value & 0xFF);
	}

	// �l��ǂݏo��
	public short getValue(int addr) {
		if (outOfAddressRange(addr)) {
			return 0xaa;// Error
		}

		return area[addr];
	}

	// ���[�h�I�����[���ǂ������Z�b�g
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
