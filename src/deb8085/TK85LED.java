package deb8085;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Panel;

public class TK85LED extends Panel implements DMADevice {
	LED7Seg led[];
	Panel addr = new Panel(); // �A�h���X�\���p LED �̃R���e�i
	Panel data = new Panel(); // �f�[�^ �\���p LED �̃R���e�i

	int dma_baseaddr;

	// �R���X�g���N�^
	public TK85LED(int dma_baseaddr) {
		// DMA �̃x�[�X�A�h���X
		this.dma_baseaddr = dma_baseaddr;

		// LED ��8����
		led = new LED7Seg[8];
		for (int i = 0; i < 8; i++)
			led[i] = new LED7Seg();

		// �A�h���X�\���p LED ��\��t����
		for (int i = 0; i < 4; i++)
			addr.add(led[i]);

		// �f�[�^�\���p LED ��\��t����
		for (int i = 4; i < 8; i++)
			data.add(led[i]);

		// ���ꂼ��̃R���e�i��\��t����
		FlowLayout fl = new FlowLayout();
		fl.setHgap(10);
		setLayout(fl);
		add(addr);
		add(data);
	}

	/*
	 * public TK85LED( LayoutManager layout ) { // �w�肳�ꂽ���C�A�E�g�}�l�[�W���𖳎� setLayout(
	 * new FlowLayout() );
	 * 
	 * this.TK85LED(); // ���ꂪ�ł��邩? }
	 */
	public void onMemoryModified(int addr, short val) {
		led[addr - dma_baseaddr].setSegData(val);
	}

}