import java.util.ArrayList;

public class Instruction implements Comparable<Instruction>{

	protected InstructionState state;
	protected int operationType;
	protected int operandState;
	protected int sequenceNumberTag;
	protected int cyclesRemaining;
	protected int destReg;
	protected int src1Reg;
	protected int src2Reg;
	protected int s1Ready;
	protected int s2Ready;
	protected String pc;
	protected int tag;
	public int fetchCycle;
	public int dispatchCycle;
	public int issueCylce;
	public int executeCycle;
	public int wbCycle;
	public String text;
	public ArrayList<Integer> s1Dependencies = new ArrayList<Integer>();
	public ArrayList<Integer> s2Dependencies = new ArrayList<Integer>();
	
	
	//protected int 
	
	Instruction(String pc, int operationType, int destReg, int src1, int src2, int tag){
		
		this.pc = pc;
		this.operationType = operationType;
		this.destReg = destReg; 
		this.src1Reg = src1;
		this.src2Reg = src2;
		this.tag = tag;
		this.operandState = 0;
		
		if(src1 == -1) this.s1Ready = 1;
		else this.s1Ready = 0;
		
		if(src2 == -1) this.s1Ready = 1;
		else this.s2Ready = 0;
		
		if(operationType == 0) {
			this.cyclesRemaining = 1;
		}
		else if(operationType == 1) {
			this.cyclesRemaining = 2;
		}
		else if(operationType == 2) {
			this.cyclesRemaining = 5;
		}
		
		this.state = InstructionState.IF;
		
		this.text = "Instruction: " + this.pc + " " + this.operationType + " " + this.destReg + " " + this.src1Reg + " " + this.src2Reg + " " + this.state.toString();
	}
	
	public void print() {
		System.out.println(this.text);
	}
	
	public boolean isWb() {
		
		return this.state == InstructionState.WB ? true : false;
	}
	
	public void decrementCycle() {
		this.cyclesRemaining--;
	}

	public boolean isFinishing() {
		// TODO Auto-generated method stub
		
		if(this.cyclesRemaining == 1) {
			return true;
		}
		//System.out.println("NO: " + this.cyclesRemaining + " cycles remaining for " + this.pc);
		return false;
	}
	
	public String getPc() {
		return this.pc;
	}
	
	public int getTag() {
		return this.tag;
	}

	public void setState(InstructionState newState) {
		// TODO Auto-generated method stub
		this.state = newState;
	}

	@Override
	public int compareTo(Instruction arg0) {
		// TODO Auto-generated method stub
		int compareValue = ((Instruction) arg0).getTag();
		
		return this.getTag() - compareValue;
	}
	
	public int getSrc1() {
		return this.src1Reg;
	}
	
	public int getSrc2() {
		return this.src2Reg;
	}
	
	public int setSrc1(int src) {
		return this.src1Reg;
	}
	
	public int setSrc2(int src) {
		return this.src2Reg = src;
	}
	
	public int getDest() {
		return this.destReg;
	}
	
}
