package deb8085;

public class BreakPoint8085 {
	int addr;
	int count;

	public BreakPoint8085(int addr, int count) {
		this.addr = addr;
		this.count = count;
	}

	boolean isAvailable(int addr) {
		if (this.addr == addr && count == 0)
			return true;
		else {
			count--; // ブレークポイントが参照された
			return false;
		}
	}

}
