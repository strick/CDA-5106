
public class ROBEntry {

	protected int entry;
	protected int dest;
	protected int result;
	protected String pc;
	protected Instruction instruction;
	
	ROBEntry(int entry, int dest, int result, String pc, Instruction instruction){
		this.entry = entry;
		this.dest = dest;
		this.result = result;
		this.pc = pc;
		this.instruction = instruction;
	}
	
	public Instruction getInstruction() {
		return this.instruction;
	}
	
}
