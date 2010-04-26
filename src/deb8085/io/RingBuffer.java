package deb8085.io;

// �����O�E�o�b�t�@  �N���X
public class RingBuffer {

	private int buf[];
	private int nextputpos = 0;
	private int nextgetpos = 0;

	// �R���X�g���N�^
	public RingBuffer(int n) {
		buf = new int[n];
	}

	// �o�b�t�@�ɂP�v�f����
	public void put(int val) {
		// ���^����������p��
		if (isFull())
			return;

		buf[nextputpos] = val;
		nextputpos++;
		if (nextputpos >= buf.length)
			nextputpos = 0;
	}

	// �o�b�t�@����P�v�f�擾
	public int get() {
		// ����ۂ�������p��
		if (isEmpty())
			return -1;

		int result = buf[nextgetpos];
		nextgetpos++;
		if (nextgetpos >= buf.length)
			nextgetpos = 0;
		return result;
	}

	// �o�b�t�@������߂�
	public void back() {
		if (!isEmpty())
			nextputpos--;

		if (nextputpos < 0)
			nextputpos = buf.length - 1;
	}

	// �o�b�t�@������ۂɂ���
	public void flush() {
		// buf =
		nextgetpos = 0;
		nextputpos = 0;
	}

	// �o�b�t�@�͋���ۂ��H
	public boolean isEmpty() {
		return nextputpos == nextgetpos;
	}

	// �o�b�t�@�͖��^�����H
	public boolean isFull() {
		if (nextputpos < buf.length - 1)
			return nextputpos + 1 == nextgetpos;
		else
			return nextgetpos == 0;
	}

}