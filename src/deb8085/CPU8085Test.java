package deb8085;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CPU8085Test {
	Mem8085 mem = null;
	CPU8085 cpu = null;

	@Before
	public void setUp() {
		this.mem = new Mem8085();
		this.cpu = new CPU8085(mem, null, null);
	}

	@Test
	public void testExecute_DAA() {
		// Regression testings rather than unit testings
		runProgram(0x8000, new short[] {0x3E, 0x74, 0xC6, 0x88, 0x27, 0x76});
		assertEquals(0x62, cpu.reg.getReg(Reg8085.A));

		runProgram(0x8000, new short[] {0x3E, 0x45, 0xC6, 0x45, 0x27, 0x76});
		assertEquals(0x90, cpu.reg.getReg(Reg8085.A));

		runProgram(0x8000, new short[] {0x3E, 0x22, 0xC6, 0x88, 0x27, 0x76});
		assertEquals(0x10, cpu.reg.getReg(Reg8085.A));

		runProgram(0x8000, new short[] {0x3E, 0x99, 0xC6, 0x99, 0x27, 0x76});
		assertEquals(0x98, cpu.reg.getReg(Reg8085.A));
	}

	private void runProgram(int addr, short[] program) {
		mem.setRange(addr, program);
		cpu.restart();
		cpu.reg.setReg(Reg8085.PC, addr);
		
		while (true) {
			try {
				cpu.execute(cpu.decode(cpu.fetch()));

				if (cpu.isHalted()) {
					return;
				}

			} catch (OnBreakPointException e) {
			}
		}
	}
}
