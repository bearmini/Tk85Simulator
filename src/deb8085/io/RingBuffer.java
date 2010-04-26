package deb8085.io;

// リング・バッファ  クラス
public class RingBuffer {

	private int buf[];
	private int nextputpos = 0;
	private int nextgetpos = 0;

	// コンストラクタ
	public RingBuffer(int n) {
		buf = new int[n];
	}

	// バッファに１要素投入
	public void put(int val) {
		// 満タンだったら却下
		if (isFull())
			return;

		buf[nextputpos] = val;
		nextputpos++;
		if (nextputpos >= buf.length)
			nextputpos = 0;
	}

	// バッファから１要素取得
	public int get() {
		// 空っぽだったら却下
		if (isEmpty())
			return -1;

		int result = buf[nextgetpos];
		nextgetpos++;
		if (nextgetpos >= buf.length)
			nextgetpos = 0;
		return result;
	}

	// バッファを一歩戻る
	public void back() {
		if (!isEmpty())
			nextputpos--;

		if (nextputpos < 0)
			nextputpos = buf.length - 1;
	}

	// バッファを空っぽにする
	public void flush() {
		// buf =
		nextgetpos = 0;
		nextputpos = 0;
	}

	// バッファは空っぽか？
	public boolean isEmpty() {
		return nextputpos == nextgetpos;
	}

	// バッファは満タンか？
	public boolean isFull() {
		if (nextputpos < buf.length - 1)
			return nextputpos + 1 == nextgetpos;
		else
			return nextgetpos == 0;
	}

}