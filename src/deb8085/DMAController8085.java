package deb8085;

//***************************************************************************************************
//***************************************************************************************************
/* 8085 CPU �p DMA�R���g���[�� �N���X */
public class DMAController8085 {
	CPU8085 cpu;
	DMADevice dev;

	int startaddr;
	int size;

	// �������Ď�����r�p
	short memoryOld[];

	// ***************************************************************************************************
	// �R���X�g���N�^
	public DMAController8085(CPU8085 cpu) {
		this.cpu = cpu;
	}

	// ***************************************************************************************************
	// DMA �f�o�C�X�����蓖�Ă�
	public void assignDMADevice(DMADevice dev, int startaddr, int size) {
		this.dev = dev;
		this.startaddr = startaddr;
		this.size = size;

		// �Ď�����r�p�f�[�^���擾
		memoryOld = new short[size];
		for (int addr = startaddr; addr < startaddr + size; addr++)
			memoryOld[addr - startaddr] = cpu.mem.getValue(addr);
	}

	// ***************************************************************************************************
	// �Ď��������̈悪�ύX���ꂽ��
	public boolean isMemoryModified(int addr) {
		return memoryOld[addr - startaddr] != cpu.mem.getValue(addr);
	}

	// ***************************************************************************************************
	// �Ď��������̈悪�ύX���ꂽ��
	public void memoryModifyCheck() {
		if (dev == null)
			return;

		for (int addr = startaddr; addr < startaddr + size; addr++)
			if (isMemoryModified(addr)) {
				short newval = cpu.mem.getValue(addr);
				memoryOld[addr - startaddr] = newval;
				dev.onMemoryModified(addr, newval);
				// System.out.println(
				// "DMA Controller : memory modifing is detected : at " +
				// util.hex4(addr) + "  new value:" + util.hex2(newval) );
			}

	}

}
