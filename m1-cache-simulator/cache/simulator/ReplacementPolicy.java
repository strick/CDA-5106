package cache.simulator;

import java.util.ArrayList;

abstract public class ReplacementPolicy {
	
	protected ArrayList<Cache> caches;
	protected int misses = 0;
	protected int hits = 0;
	protected int l1reads = 0;
	protected int l1writes = 0;
	protected int l1writeMisses = 0;
	protected int l1readMisses = 0;
	protected int l1writebacks = 0;
	protected int l2reads = 0;
	protected int l2writes = 0;
	protected int l2writeMisses = 0;
	protected int l2readMisses = 0;
	protected int l2writebacks = 0;
	protected boolean needsWriteBack = false;
	protected int currentCacheLevel = 0;
	protected int currentIndex = 0;
	protected boolean isWrite = false;
	protected String currentAddress = null;
	protected String currentTag = null;
	protected Cache currentCache = null;
	protected boolean evictOccurred = false;
	protected String evictedAddress = null;
	protected int extraL1Wb = 0;
	protected boolean isL1FromL2Eviction = false;

	
	public ReplacementPolicy(ArrayList<Cache> caches) {
		
		this.caches = caches;
	}
	
	public ReplacementPolicy(Cache cache) {

	}
	
	
	
	/**
	 * LRU processes reads in the following way
	 * 
	 * 1) Check to see what is in the current address
	 * 2) If it's a hit, increase the touches
	 * 3) If it's a miss and a slot is empty, fille it
	 * 4) If it's a miss and no line slots empty, urs LRU policy
	 * @param address
	 */
	public void read(String address) {
		
		this.l1reads++;
		
		Cache l1 = this.getL1();
		
		int index = l1.getIndex(address);
		String tag = l1.getTag(address);
		
		if(!this.runPolicy(l1, address, tag, index, false))
		{	 
			this.l1readMisses++;
		
			if(this.needsWriteBack) {
				this.l1writebacks++;
				this.needsWriteBack = false;
				

				this.l2Write(this.evictedAddress);
				
				if(this.evictOccurred) {
					// If this is inclusive, remove the L1 as well
					if(this.currentCache.getLevel() == 2 && this.currentCache.getInclusiveProperty() == 1) {
						//this.runL1Evict(l1, this.currentAddress, tag, index, false);
						//this.runL1Evict(l1, this.evictedAddress, tag, index, false);
						this.runL1Evict(l1, this.evictedAddress, l1.getTag(this.evictedAddress), l1.getIndex(this.evictedAddress), false);
						this.l2writeMisses--;
						this.l2readMisses++;
						this.l2readMisses++;
						this.l1readMisses++;
						this.l2reads++;
					}
					
				}
			
			}
			 
			this.l2Read(address);
			
		}
	}
	
	protected String createL2Address(String address) {
	
		String newAddress = null;
		
		return newAddress;
	}
	
	private void runL1Evict(Cache l1, String address, String tag, int index, boolean write) {
		// TODO Auto-generated method stub
		
		this.currentCacheLevel = l1.getLevel();
		this.currentIndex = index;
		this.isWrite = write;
		this.currentTag = tag;
		this.currentAddress = address;
		this.currentCache = l1;
		
		this.needsWriteBack = false;
		this.isL1FromL2Eviction = true;

		this.evict();
		
		this.isL1FromL2Eviction = false;
		
		// If htis happened need to track for memory interaction
		if(this.needsWriteBack) {
			this.extraL1Wb++;
		}
		
		this.runEvictionPolicy();	
	}

	public void write(String address) {
		
		this.l1writes++;
		
		Cache l1 = this.getL1();
		
		int index = l1.getIndex(address);
		String tag = l1.getTag(address);
		
		// Determine the tag address
		if(!this.runPolicy(l1, address, tag, index, true)) {
			
			this.l1writeMisses++;
			
			if(this.needsWriteBack) {
				this.l1writebacks++;
				this.needsWriteBack = false;							
		
				
				this.l2Write(this.evictedAddress);
				
				if(this.evictOccurred) {
					// If this is inclusive, remove the L1 as well
					if(this.currentCache.getLevel() == 2 && this.currentCache.getInclusiveProperty() == 1) {
						
						//this.runL1Evict(l1, this.currentAddress, tag, index, false);
						//this.runL1Evict(l1, address, tag, index, true);
						this.runL1Evict(l1, this.evictedAddress, l1.getTag(this.evictedAddress), l1.getIndex(this.evictedAddress), false);
						this.l2writeMisses--;
						this.l1writebacks--;
						this.l2writes--;
					}
					
				}
			
			} 
			this.l2Read(address);
		}
	}
	
	protected void l2Read(String address) {
		
		if(this.hasL2()) {
			
			Cache l2 = this.getL2();
			
			int index = l2.getIndex(address);
			String tag = l2.getTag(address);
			
			this.l2reads++;
			if(!this.runPolicy(l2, address, tag, index, false)) {
				this.l2readMisses++;
			
				if(this.needsWriteBack) {
					this.l2writebacks++;
					this.needsWriteBack = false;
					//this.l2readMisses++;
				}
			}
		}
	}

	
	protected void l2Write(String address) {
		
		if(this.hasL2()) {
				
			Cache l2 = this.getL2();
			
			int index = l2.getIndex(address);
			String tag = l2.getTag(address);
		
			this.l2writes++;
			if(!this.runPolicy(l2, address, tag, index, true))
				this.l2writeMisses++;
				//this.l2readMisses++;
			;
			
			if(this.needsWriteBack) {
				this.l2writebacks++;
				this.needsWriteBack = false;
				//this.l2readMisses++;
			}

		}
	}
	
	protected boolean runPolicy(Cache c, String address, String tag, int index, boolean write) {
		
		this.currentCacheLevel = c.getLevel();
		this.currentIndex = index;
		this.isWrite = write;
		this.currentTag = tag;
		this.currentAddress = address;
		this.currentCache = c;

		boolean isHit = this.isAHit();
		boolean isMissAllocate = this.isMissAllocate();
		this.evictOccurred = false;
			
		if(isHit) {			
			
			this.hit();
			this.runHitPolicy();
	
		}
		else if(isMissAllocate){
			
			this.missAndAllocate();
			this.runMissAndAllocatePolicy();
		}
			// No space, evict based on LRU
		else {
			this.evict();
			this.runEvictionPolicy();			
			this.evictOccurred = true;
		}

		return isHit;
	}
	
	
	abstract void runEvictionPolicy();
	abstract void runMissAndAllocatePolicy();
	abstract void runHitPolicy();
	abstract int getBlockIndex();
	abstract int getBlockColumn();
	//abstract void evict(Cache c, String tag, int index);
	
	protected void evict() {
		
		int line = this.getBlockIndex();
		int col = this.getBlockColumn();
		
		if(Simulator.debug) System.out.println("Eviction will be: (" + line + "," + col +")");
		
		//System.out.println(col);
		this.evictedAddress = this.currentCache.getSetRow(line).getEvictedAddress(line, col, this.currentTag);
		//this.evictedAddressL1 = this.getL1().getSetRow(line).getEvictedAddress(line, col, currentTag)
		//System.out.println("Evicted address: " + this.evictedAddress);
		
		boolean wb = false;
		if(this.isL1FromL2Eviction) {
			wb = this.currentCache.evictL1FromL2(line, col, this.currentTag, this.currentAddress);
		}
		else {
			wb = this.currentCache.evict(line, col, this.currentTag, this.currentAddress);
			if(wb) 
				this.needsWriteBack = true;//this.l1writebacks++;
			
			int column = this.currentCache.getBlockColumn(this.currentTag, this.currentIndex);
			
			if(this.isWrite)
				this.currentCache.getSetRow(this.currentIndex).writeDirty(column);
		
		}
		
		
		
	}
	 
	protected void missAndAllocate() {
		
		Cache c = this.currentCache;
		String tag = this.currentTag;
		int index = this.currentIndex;
		boolean write = this.isWrite;
		
		c.allocate(index, tag, this.currentAddress);
		int column = c.getBlockColumn(tag, index);
		
		if(write)
			c.getSetRow(index).writeDirty(column);
	}
	
	protected int hit() {
		
		
		// Move this block to the end of the list.
		int column = this.currentCache.getBlockColumn(this.currentTag, this.currentIndex);
		
		// Mark as dirty if it's a hit and write
		if(this.isWrite)
			this.currentCache.getSetRow(this.currentIndex).writeDirty(column);
		
		
		// Return the 
		return column;

	}
	
	protected Cache getL2() {
		return this.caches.get(1);
	}
	
	protected Cache getL1() {
		return this.caches.get(0);
	}
	
	protected boolean hasL2() {
		if(this.caches.size() > 1)
			return true;
		return false;
	}
	
	protected int miss() {
		
		System.out.println("Miss");
		this.misses++;
		
		return this.misses;
	}
	
	public int getL1Reads() {
		return this.l1reads;
	}
	
	public int getL1Writes() {
		return this.l1writes;
	}
	
	public int getL1WriteMisses() {
		return this.l1writeMisses;
	}
	
	public int getL1ReadMisses() {
		return this.l1readMisses;
	}
	
	public int getL1WriteBacks() {
		return this.l1writebacks;
	}
	
	public int getL2Reads() {
		return this.l2reads;
	}
	
	public int getL2Writes() {
		return this.l2writes;
	}
	
	public int getL2WriteMisses() {
		return this.l2writeMisses;
	}
	
	public int getL2ReadMisses() {
		return this.l2readMisses;
	}
	
	public int getL2WriteBacks() {
		return this.l2writebacks;
	}
	
	public String getMemoryTraffic() {
	
		String L1 = Integer.toString(this.getL1ReadMisses() + this.getL1WriteMisses() + this.getL1WriteBacks());

		if(!this.hasL2()) {
			return L1;
		}
		else {
			if(this.getL1().getInclusiveProperty() != 1) {
				return Integer.toString(this.getL2ReadMisses() + this.getL2WriteMisses() + this.getL2WriteBacks());
			}
			else {
				return Integer.toString(this.getL2ReadMisses() + this.getL2WriteMisses() + this.getL2WriteBacks() + this.extraL1Wb); // Plus L1 writebacks to memory due to the includsioh
				
			}
				
		}

	}
	
	public double getL1MissRate() {
		
		return (double) (this.l1writeMisses + this.l1readMisses) / (double) (this.l1writes + this.l1reads);
	}
	
	public double getL2MissRate() {
		if(this.l2writes == 0 && this.l2reads == 0) return 0;
		//return (double) (this.l2writeMisses + this.l2readMisses) / (double) (this.l2writes + this.l2reads);
		return (double) (this.l2readMisses / this.l2reads);
		
	}
	
	protected boolean isAHit() {
		return this.currentCache.doesTagExist(this.currentTag, this.currentIndex);	
	}
	
	protected boolean isMissAllocate() {
		return this.currentCache.hasSpace(this.currentIndex);			
	}	

}
