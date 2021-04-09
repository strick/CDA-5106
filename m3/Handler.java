import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Handler {
	
	protected int schedulingQueueSize = 0;
	protected int dispatchQueueSize = 0;
	protected  int fetchRate = 0;
	protected String tracefile;
	protected int fetchedInstructions = 0;
	protected int counter = 0;
	protected int rscounter  = 0;
	boolean fileEmpty = false;
	boolean step = true;
	File myObj;
	Scanner myReader;
	int cycle = 0;
	int count = 0;
	
	public Handler(int schedulingQueueSize, int fetchRate, String tracefile) {
		
		this.schedulingQueueSize = schedulingQueueSize;
		this.fetchRate = fetchRate;
		this.dispatchQueueSize = fetchRate * 2;
		this.tracefile = tracefile;
		
		for(int i=0; i<128; i++) {
			this.rf.add(new RFEntry(i, -1));
		}
	}
	
	ArrayList<RFEntry> rf = new ArrayList<RFEntry>();
	ArrayList<ROBEntry> fakeROB = new ArrayList<ROBEntry>();
	ArrayList<RSEntry> reservationStation = new ArrayList<RSEntry>();
	ArrayList<Instruction> dispatchList = new ArrayList<Instruction>();
	ArrayList<Instruction> issueList = new ArrayList<Instruction>();  // Scheduling Queue
	ArrayList<Instruction> executeList = new ArrayList<Instruction>();
	
	public void run() {
		
		try {
			
			this.myObj = new File("src/" + this.tracefile);
			this.myReader = new Scanner(myObj);
			
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
			
		while(this.AdvanceCycle()) {
			this.FakeRetire(); 				
			this.Execute(); 	
			this.Issue();
			this.Dispatch();
			this.Fetch(); 
			this.cycle++;
		}
		
		//System.out.println("DONE)");
	}
		 
		
	public void FakeRetire() {
		
	//	System.out.println("Fake Retire");
		if(this.fakeROB.isEmpty()) {
			return;
		}
		
		// Remove instructions from the head of the fake-ROB
		// until an instruction is reached that is not in the WB state.
		for(int i=0; i<this.fakeROB.size(); i++) {
			
			if(!this.fakeROB.get(i).getInstruction().isWb()) {
		//		System.out.println(this.fakeROB.get(i).getInstruction().state.toString());
				return;
			}
			
			Instruction is = this.fakeROB.get(i).getInstruction();
		//	this.fakeROB.get(i).getInstruction().wbCycle = this.cycle;
			this.fakeROB.remove(i);
			
			System.out.println(this.count + " fu{" + is.operationType + "} src{" + is.getSrc1() + "," + is.getSrc2() + "} dst{" + is.getDest() + "} IF{" + is.fetchCycle + "," + (is.dispatchCycle - is.fetchCycle) + "}"
			
			+  " ID{" + is.dispatchCycle + "," + (is.issueCylce - is.dispatchCycle) + "}"
			+  " IS{" + is.issueCylce + "," + (is.executeCycle - is.issueCylce) + "}"
			+  " EX{" + is.executeCycle + "," + (is.wbCycle - is.executeCycle) + "}"
			+  " WB{" + is.wbCycle + "," + 1 + "}");
		

			this.count++;
			//if(this.count == 8) System.exit(0);	
			// Print out the instruction witht he variables IF{#,#}  IF{when it changed state, how many cycles to move from state one to state two), WB is always 1
		}
		
	}
	
	public void Execute() {

	//	System.out.println("Execute");
		// From the execute_list, 	
		for(int index = 0; index < this.executeList.size(); index++) {
			
			Instruction instruction = this.executeList.get(index);
			
			//check for instructions that are finishing execution this cycle, and:
			if(instruction.isFinishing()) {
				
				// 1) Remove the instruction from the execute_list.
				this.executeList.remove(index);
						
				// 2) Transition from EX state to WB state.
				// Need to track what cycle this is happening when it changes the set.
				instruction.setState(InstructionState.WB);
				instruction.wbCycle = this.cycle;

				// 3) Update the register file state e.g., ready flag) and wakeup
				// dependent instructions (set their operand ready flags).
				for(int i=0;i<this.rf.size();i++) {
					
					RFEntry rf = this.rf.get(i);
					
					if(rf.tag == instruction.tag) {
						rf.inRf = true;
						rf.tag = -1;
						
						for(int j=0; j<this.reservationStation.size();j++) {
							if(this.reservationStation.get(j).s1Value == instruction.tag ) {								
								this.reservationStation.get(j).s1Ready = true;
							}
							if(this.reservationStation.get(j).s2Value == instruction.tag) {
								this.reservationStation.get(j).s2Ready = true;
							}						
						}
					}
				}
			}
			else {
				instruction.decrementCycle();
			}
			 
		}
	}
	
	public void Issue() {
		
		// From the issue_list, construct a temp list of instructions whose
		// operands are ready – these are the READY instructions.
		ArrayList<Instruction> tempList = this.getReadyInstructions();		
		
		// Scan the READY instructions in ascending order of tags and issue up to N+1 of them. 
		Collections.sort(tempList);				
		int max = this.fetchRate + 1;
		for(int i=0; i<tempList.size();i++) {
		
			Instruction is = tempList.get(i);
			
			// To issue an instruction:
			for(int j=0;j<this.issueList.size();j++) {
			
				if(this.issueList.get(j).tag == tempList.get(i).tag) {
					
					// 1) Remove the instruction from the issue_list and add it to the  execute_list.
					// Fetch Rate + 1
					this.executeList.add(is);
					this.issueList.remove(j);
										
					// 2) Transition from the IS state to the EX state.
					is.setState(InstructionState.EX);
					is.executeCycle = this.cycle;
					
					// 3) Free up the scheduling queue entry (e.g., decrement a count
					// of the number of instructions in the scheduling queue)					
					max--;
					
					// 4) Set a timer in the instruction’s data structure that will allow
					// you to model the execution latency.
				}
				
				if(max == 0) break;
			}
		}			
	}
		
	public void Dispatch() {
		
		// From the dispatch_list, construct a temp list of instructions in the ID
		// state (don’t include those in the IF state – you must model the
		// 1 cycle fetch latency).
		ArrayList<Instruction> tempList = new ArrayList<Instruction>();
		tempList = this.getIDInstructions();
				
		//Scan the temp list in ascending order of tags
		Collections.sort(tempList);
		
		for(int i = 0; i<tempList.size(); i++){
			
			Instruction issueIns = null;
			
			//  if the scheduling queue is not full
			if(this.issueList.size() < this.schedulingQueueSize) {

				// 1) Remove the instruction from the dispatch_list and add it to the
				// issue_list. Reserve a schedule queue entry (e.g. increment a
				// count of the number of instructions in the scheduling
				// queue) and free a dispatch queue entry (e.g. decrement a count of
				// the number of instructions in the dispatch queue).
				issueIns = this.switchToIssueState(tempList.get(i));
				
				// 3) Rename source operands by looking up state in the register file;				
				RSEntry rsentry = new RSEntry(issueIns.tag);//this.rscounter);
	
				this.renameSrcOperands(rsentry, issueIns, issueIns.getDest(), issueIns.getSrc1(), 1); 
				this.renameSrcOperands(rsentry, issueIns, issueIns.getDest(), issueIns.getSrc2(), 2);

				//rsentry.tag = issueIns.tag;
				rsentry.ins = issueIns;
				this.reservationStation.add(rsentry);					
	
				// Rename destination by updating state in the register file.				
				if(issueIns.getDest() != -1) {
					RFEntry rfentry = this.getRfBySrc(issueIns.getDest(), this.rf);

					//?????????????????
					if(rfentry.inRf) {
						rfentry.inRf = false;
						rfentry.tag = issueIns.tag;
					}
					else {
						rfentry.inRf = true;
						
					}
			
				//	this.rf.set(rfentry.index, rfentry);
				}
			}
		}
	}
	
	public void Fetch() {
		
		//System.out.println("Fetch"); 
		int fetchedInstructions = 0;
		// Read new instructions from the trace as long as
		// 1) you have not reached the end-of-file,
		// 2) the fetch bandwidth is not exceeded, and
		// 3) the dispatch queue is not full.

		if(!this.fileEmpty) {
		    while (myReader.hasNextLine()) {

			    	
		    	//System.out.println("Fetched vs Rate: " + this.fetchedInstructions + " < " + this.fetchRate);
		    	//System.out.println("Dispatch size vs limit: " + this.dispatchList.size() + " < " + this.dispatchQueueSize);
		    	
		    	if(fetchedInstructions < this.fetchRate && this.dispatchList.size() < this.dispatchQueueSize) {
		        
					// Then, for each incoming instruction:
					// 1) Push the new instruction onto the fake-ROB. Initialize the
					// instruction’s data structure, including setting its state to IF.
					// 2) Add the instruction to the dispatch_list and reserve a
					// dispatch queue entry (e.g., increment a count of the number
					// of instructions in the dispatch queue).
			    	
			    	String data = myReader.nextLine();  		        
			        String[] splited = data.split("\\s+");
			        
			    	Instruction instruction = new Instruction(splited[0], Integer.parseInt(splited[1]),Integer.parseInt(splited[2]),Integer.parseInt(splited[3]),Integer.parseInt(splited[4]), this.fetchedInstructions);
			    	instruction.setState(InstructionState.IF);
			    	instruction.fetchCycle = this.cycle;
			    	
			       	instruction.tag = this.counter;
			    	
			    	
			    	this.fakeROB.add(new ROBEntry(this.counter, instruction.getDest(), 0, splited[0], instruction));		
			    	this.dispatchList.add(instruction);
			    	fetchedInstructions++;
			    	this.counter++;
		    	}
		    	else {
		    		return;
		    	}

		    }
		}
		    
		    this.fileEmpty = true;
		    
		    myReader.close();
		
	}
	
	public boolean AdvanceCycle() {
		// Advance_Cycle performs several functions.
		// It advances the simulator cycle. Also, when it becomes
		// known that the fake-ROB is empty AND the trace is
		// depleted, the function returns “false” to terminate
		// the loop.
		if(this.fakeROB.size() == 0 && this.fileEmpty)
			return false;
		
		return true;
	}
	
	private void renameSrcOperands(RSEntry rsentry, Instruction issueIns, int dest, int src, int srcNum) {

		if(src != -1) {
			
			// Get the rfentry with index of source
			RFEntry rfentry = this.getRfBySrc(src, this.rf);
			
			// If it's in the RF, then it is ready
			if(rfentry.inRf) {
				
				// Set Reservation entry to ready and value to tag of the instruction
				this.setOperand(rsentry, true, issueIns.tag, srcNum);
				
				// Update the RF table state to not in RF and set the ta to the instruction
				//if(dest != -1) {
					rfentry.tag = issueIns.tag;
					rfentry.inRf = false;
				//}
			//	this.rf.set(rfentry.index, rfentry);
			}
			else {
				this.setOperand(rsentry, false, rfentry.tag, srcNum);
			}
		}		
		else {
			this.setOperand(rsentry, true, issueIns.tag, srcNum);			
		}
		
	}
	
	protected void setOperand(RSEntry rsentry, boolean ready, int tag, int src) {
		
		if(src == 1) {
			rsentry.s1Ready = ready;
			rsentry.s1Value = tag;
		}
		else {
			rsentry.s2Ready = ready;
			rsentry.s2Value = tag;
		}
	}


	private RFEntry getRfBySrc(int src1, ArrayList<RFEntry> entries) {
		// TODO Auto-generated method stub
		
		for(RFEntry entry : entries) {
			if(entry.index == src1) {
				return entry;
			}
		}
		return null;
	}
	
	protected ArrayList<Instruction> getIDInstructions(){

		ArrayList<Instruction> tempList = new ArrayList<Instruction>();
		
		for(int i=0; i<this.dispatchList.size();i++) {
			
			Instruction instruction = this.dispatchList.get(i);
			
			if(instruction.state.equals(InstructionState.ID)) {
				//System.out.println("Adding to dispatach");
				tempList.add(instruction);
			}
			
			// For instructions in the dispatch_list that are in the IF state,
			// unconditionally transition to the ID state (models the 1 cycle
			// latency for instruction fetch).
			if(instruction.state.equals(InstructionState.IF)){
				
				//System.out.println("Moving instruction from IF to ID");
				
				// Need to track what cycle this is happening when it changes the set.
				instruction.dispatchCycle = this.cycle;
				instruction.setState(InstructionState.ID);
				//this.dispatchList.set(i, instruction);
			}
			
		}
		
		return tempList;
		
	}
	
	protected Instruction switchToIssueState(Instruction issueIns) {
		
		for(int j = 0; j<this.dispatchList.size(); j++) {
			
			// Check the 
			int dispatchTag = this.dispatchList.get(j).tag;//.getPc();
			int tempListTag = issueIns.tag;//getPc();
			
			if(dispatchTag == tempListTag) {
												
				// 2) Transition from the ID state to the IS state.
				//Instruction issueIns = this.issueList.get(i);
				issueIns.setState(InstructionState.IS);
				issueIns.issueCylce = this.cycle;
				this.issueList.add(issueIns);
				this.dispatchList.remove(j);
				
				// Need to track what cycle this is happening when it changes the set.
				break;
			}
		}
		
		return issueIns;
	}
	
	private ArrayList<Instruction> getReadyInstructions() {
		
		// TODO Auto-generated method stub
		ArrayList<Instruction> tempList = new ArrayList<Instruction>();
		
		// Look through the list of instructions in the IS state
		for(int i=0; i<this.issueList.size();i++) {
			
			Instruction instruction = this.issueList.get(i);
						
			// Check to see if any of the IS instructions have a tag that matches an instruciton in the reservation station
			for(int j=0; j<this.reservationStation.size(); j++) {
				
				RSEntry rsentry = this.reservationStation.get(j);
	
				if(instruction.tag == rsentry.tag && rsentry.s1Ready && rsentry.s2Ready) {
				//if(rsentry.s1Ready && rsentry.s2Ready) {
				//	rsentry.tag
					tempList.add(instruction);
					
				}
			}

		}
		
		return tempList;
	}
}


