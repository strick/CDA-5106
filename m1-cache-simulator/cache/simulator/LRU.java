package cache.simulator;

import java.util.ArrayList;

public class LRU extends ReplacementPolicy {
	
	protected Cache cache;
	protected int misses = 0;
	protected int hits = 0;
	protected int[] touches;
	protected ArrayList<String> lruList = new ArrayList<String>();
	protected ArrayList<String> lruList2 = new ArrayList<String>();
	
	public LRU(ArrayList<Cache> caches) {
		
		
		super(caches);
		
	}
	
	protected ArrayList<String> getCurrentList(){
		
		ArrayList<String> list = null;
		
		if(this.currentCacheLevel == 1) {
			list = this.lruList;
		}
		else if(this.currentCacheLevel == 2) {
			list = this.lruList2;
		}
		
		return list;
	}

	protected void runHitPolicy() {

		String lruIndex = this.createLruTag(this.currentIndex, this.currentCache.getBlockColumn(this.currentTag, this.currentIndex));
		
		this.updateLruPosition(lruIndex, this.getCurrentList());
		
	}

	protected void runMissAndAllocatePolicy() {
	
		int column = this.currentCache.getBlockColumn(this.currentTag, this.currentIndex);
		String lruIndex = this.createLruTag(this.currentIndex, column);
		
		if(!this.getCurrentList().contains(lruIndex)) {
			
			this.getCurrentList().add(lruIndex);
		}
		else {
			this.updateLruPosition(lruIndex, this.getCurrentList());
		}
	}
	
	protected void runEvictionPolicy() {
		
		int evictIndex = this.findEvictionIndex(this.currentIndex);
		String evictionBlock = this.getEvictionBlock(this.currentIndex);
		
		ArrayList<String> cacheLruList = this.getCurrentList();
		
		cacheLruList.remove(evictIndex);
		cacheLruList.add(evictionBlock);
	
	}
	
	protected String getEvictionBlock(int index) {
		
		int evictIndex = this.findEvictionIndex(index);
		String evictionBlock = this.getEvictionBlock(evictIndex, this.getCurrentList());
		
		return evictionBlock;
	}
	
	protected int getBlockColumn() {
		return this.getBlockColumn(this.getEvictionBlock(this.currentIndex));
	}
	
	protected int getBlockIndex() {
		return this.getBlockIndex(this.getEvictionBlock(this.currentIndex));
	}
	
	protected int getBlockIndex(String lruIndex) {
		
		return Integer.parseInt(lruIndex.split("-")[0]);
	}
	
	protected int getBlockColumn(String lruIndex) {
		//System.out.println(lruIndex);
		return Integer.parseInt(lruIndex.split("-")[1]);
	}
	
	protected int findEvictionIndex(int line) {
		
		ArrayList<String> cacheLruList = this.getCurrentList();
		
		// Look at all of the LRU until you find the first lin entry
		for(int i=0; i < cacheLruList.size(); i++) {
			if(this.getBlockIndex(cacheLruList.get(i)) == line) {
				//System.out.println("Size: " + this.lruList.size());
				//System.out.println("Returning " + this.lruList.get(i));
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Moves the last used line to the end of the LRU list
	 * 
	 * @param index
	 */
	protected void updateLruPosition(String lruIndex, ArrayList<String> cacheLruList) {

		//System.out.println(lruIndex);
		cacheLruList.remove(cacheLruList.indexOf(lruIndex));
		cacheLruList.add(lruIndex);
	}
	
	protected String createLruTag(int index, int column) {
		
		return Integer.toString(index).concat("-").concat(Integer.toString(column));
	}
	
	protected String getEvictionBlock(int index, ArrayList<String> cacheLruList) {
		
		return cacheLruList.get(index);	
	}

}
