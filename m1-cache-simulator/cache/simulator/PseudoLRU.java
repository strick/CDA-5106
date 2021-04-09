package cache.simulator;

import java.util.ArrayList;

public class PseudoLRU extends ReplacementPolicy {

	ArrayList<SetTree> setTrees = null;
	
	public PseudoLRU(ArrayList<Cache> caches) {
		super(caches);

		// TODO Auto-generated constructor stub
	}
	
	@Override
	void runHitPolicy() {
		// TODO Auto-generated method stub
		//this.currentCache.g)

	//	this.currentCache.getSetRow(this.currentIndex);
		
		// Update the list with a new tree
		
		int column = this.currentCache.getBlockColumn(this.currentTag, this.currentIndex);
		
		//Get the tree and flip the numbers because it was accesseed
		SetTree tree = this.setTrees.get(this.currentIndex);
		tree.set(column);
	}

	@Override
	void runEvictionPolicy() {
		// TODO Auto-generated method stub
		
		// Just go down 0 and filp
		

		
	}
	
	protected void evict() {
		
		int column = this.currentCache.getBlockColumn(this.currentTag, this.currentIndex);
		
		//Get the tree and flip the numbers because it was accesseed
		SetTree tree = this.setTrees.get(this.currentIndex);
		tree.setEvict(column);
		
		int line = this.currentIndex;
		int col = column;
		
		if(Simulator.debug) System.out.println("Eviction will be: (" + line + "," + col +")");
		
		//System.out.println(col);
		boolean wb = this.currentCache.evict(line, col, this.currentTag, this.currentAddress);
		
		if(wb) 
			this.needsWriteBack = true;//this.l1writebacks++;
		
		
		if(this.isWrite)
			this.currentCache.getSetRow(this.currentIndex).writeDirty(column);
		
		
	}

	@Override
	void runMissAndAllocatePolicy() {
		// TODO Auto-generated method stub
		
		int column = this.currentCache.getBlockColumn(this.currentTag, this.currentIndex);
		
		//Get the tree and flip the numbers because it was accesseed
		SetTree tree = this.setTrees.get(this.currentIndex);
		tree.set(column);
		

	}

	@Override
	int getBlockIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getBlockColumn() {
		// TODO Auto-generated method stub
		return 0;
	}
}

class SetTree {
	
	protected Set set; 
	Integer[] tree = null;
	protected int treeIndexValue = 0;
	
	SetTree(Set s, int size){
		this.set = s;
		
		// Bits needed = 2^n = cache associtiviy
		// Bits needed = #-assoc -1
		// 2-way:  1 bit
		// 4-way:  3 bits
		// 8-way:  7 bits
		tree = new Integer[size]; 
	
		for(int i=1; i<size;i++) {
			tree[i] = 0;
		}
		
		
	}
	
	public void set(int column){
		
	
		int assoc = this.tree.length;
		for(int i=this.tree.length;i<=0; i=i-2) {
			
			// Go left
			if(column < assoc/2 ) {
				tree[i] = 0;
			}
			// Right
			else {
				tree[i] = 1;
			}
		}
	}
	
	public int getTreeEvictValue() {
		return 0;
	}
	
	public void setEvict(int column){
		
		
		int assoc = this.tree.length;
		for(int i=this.tree.length;i<=0; i=i-2) {
			
			// Go left
			if(column < assoc/2 ) {
				tree[i] = 1;
			}
			// Right
			else {
				tree[i] = 0;
			}
		}
	}
	
}
