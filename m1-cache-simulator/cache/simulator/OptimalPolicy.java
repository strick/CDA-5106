package cache.simulator;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Can extend LRU because the structures can be the same, the eviction just happends differnt
 * @author bjstrick
 *
 */
public class OptimalPolicy extends LRU {

	protected ArrayList<Operation> operationList = null;
	protected int currentOperation = 0;
	
	protected int evictionColumn;

	public OptimalPolicy(ArrayList<Cache> caches, ArrayList<Operation> operations) {
		super(caches);
		
		this.operationList = operations;
		// TODO Auto-generated constructor stub
	}
	
	public void read(String address) {
		super.read(address);
		this.currentOperation++;
	}
	
	public void write(String address) {
		super.write(address);
		this.currentOperation++;
	}
	
	protected void evict() {
		
		//System.out.println("My evict");
		
		this._runEvictionPolicy();
		
		int line = this.getBlockIndex();
		int col = this.getBlockColumn();
		
		//System.out.println("Eviction will be: (" + line + "," + col +")");
		
		//System.out.println(col);
		boolean wb = this.currentCache.evict(line, col, this.currentTag, this.currentAddress);
		//this.waitMe();
		
		if(wb) 
			this.needsWriteBack = true;//this.l1writebacks++;
		
		int column = this.currentCache.getBlockColumn(this.currentTag, this.currentIndex);
		
		if(this.isWrite)
			this.currentCache.getSetRow(this.currentIndex).writeDirty(column);
		
		if(Simulator.debug) this.currentCache.printContents();
		
	}
	
	protected void runEvictionPolicy() {
		
	}

	/**
	 * Instead of the LRU, look ahead in time to see which block will be reference the farthest in the future.
	 */
	protected
	void _runEvictionPolicy() {
		// TODO Auto-generated method stub

		ArrayList<Integer> touched = new ArrayList<Integer>();
		
		// Get a list of the blocks
		Cache c = this.currentCache;
		Set row = c.getSetRow(this.currentIndex);
		this.evictionColumn = 0;
			
		Integer[] counts = new Integer[row.cols.length];
		for(int k = 0; k<counts.length;k++)
			counts[k] = 0;
		//row.__toString();
			
		// Look at all of the remaining operations to come[
		for(int i =  (this.currentOperation+1); i<this.operationList.size(); i++) {

			// Get the future operation tag
			Operation current = this.operationList.get(i);
			String tag = this.currentCache.getTag(current.getAddress()); 
			int index = this.currentCache.getIndex(current.getAddress());
			
		
			// Caputure where in the future an operation with same tag is
			for(int j = 0; j < row.cols.length; j++) {
				if(index == this.currentIndex && tag.equals(row.cols[j][0]) && !touched.contains(j)) {
					
					if(Simulator.debug) System.out.println("Found " + tag + " on operation " + i);
					if(Simulator.debug) System.out.println("J is: " + j);
					counts[j] = i;
					if(Simulator.debug) System.out.println(counts[j]);
					touched.add(j);
				}
				
			}
			
			// If you've already looked at each column, skip
			if(touched.size() == c._assoc) {
				break;
			}
			
			//System.out.println("Done loop");
		}
		
		// Set the eviction column to the block either not used again or latest used
		int max = 0;
		this.evictionColumn = 0;
		for(int k = 0; k<counts.length;k++) {
			
			// Not  used again, so remove it.
			if(counts[k] == 0) {
				this.evictionColumn = k;
				break;
			}
				
			// Find the column that is farest out and set that for eviction.
			if(Simulator.debug) System.out.println("Checking (" + k + "): " + counts[k]);
			if(counts[k] > max) {
				if(Simulator.debug) System.out.println("k is " + k);
				this.evictionColumn = k;
				max = counts[k];
				if(Simulator.debug) System.out.println("Set count to: " + max);
			}
			
			
		}
		if(Simulator.debug) System.out.println("Eviction is " + this.evictionColumn);

	}
	
	private void waitMe() {
		Scanner scanner = new Scanner(System.in);
      
            while (true) {
                System.out.println("Please input a line");
                long then = System.currentTimeMillis();
                String line = scanner.nextLine();
                long now = System.currentTimeMillis();
                System.out.printf("Waited %.3fs for user input%n", (now - then) / 1000d);
                System.out.printf("User input was: %s%n", line);
            }

	}
	
	protected int getBlockColumn() {
		return this.evictionColumn;
	}
	
	protected int getBlockIndex() {
		return this.currentIndex;
	}

}
