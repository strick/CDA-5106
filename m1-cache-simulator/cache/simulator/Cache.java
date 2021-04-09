package cache.simulator;

import java.util.ArrayList;

public class Cache {

	protected int _size;
	protected int _assoc;
	protected int _blocksize;  // Power of 2
	protected int _sets;
	protected int _indexBits;
	protected int _offsetBits;
	protected int _tagBits;
	protected int[] setTouches;

	
	/**
	 * @_replacementPolicy LRU, MRU, etc
	 */
	protected String _replacementPolicy;
	
	/**
	 * @_inclusionProperty Inclusive, Non-Inclusive, Exclusivsed
	 */
	protected int _inclusionProperty;
	
	/**
	 * @_level L1, L2, etc
	 */
	protected int level;
	
	protected ArrayList<Set> sets = new ArrayList<Set>(); 
	
	
	
	public int getInclusiveProperty() {
		return this._inclusionProperty;
	}
	
	public int getLevel() {
		return this.level;
	}
	/**
	 * 
	 * @param size
	 * @param assoc
	 * @param blocksize
	 */
	public Cache(int size, int assoc, int blocksize, int level, int inclusionProperty) {
		
		this._assoc = assoc;
		this._size = size;
		this._blocksize	= blocksize;
		this.level = level;
		this._inclusionProperty = inclusionProperty;
		
		this._sets = size/(assoc*blocksize);
		//System.out.println("Sets: " + this._sets);
		
		this.setTouches = new int[this._sets];

		
		for(int i = 0; i<this._sets; i++) {
			this.sets.add(new Set(this._assoc));
			this.setTouches[i] = 0;
		}
		
		this.setIndexBits();
		this.setOffsetBits();
		this.setTagBits();
		
	}
	
	public Set assignSet(String[][] data, int line) {
	
		for(int i = 0; i<data.length; i++) {
		
			this.getSetRow(line).setData(data[i][0], i, data[i][1]);
		}
			
		return this.getSetRow(line);
		
	}
	
	public boolean evictL1FromL2(int index, int column, String tag, String address) {
		
		return this.getSetRow(index).allocateL1FromL2Block(index, column, tag, address);
	}
	
	/**
	 * Return if it was a dirty writeback
	 * @param index
	 * @param column
	 * @param tag
	 * @return
	 */
	public boolean evict(int index, int column, String tag, String address) {
		
		// Find teh set with the least amount of touches and evict it
		//this.getSetRow(index).empty();
		
		//this.allocate(index, tag);
		return this.getSetRow(index).allocateBlock(index, column, tag, address);
		
	}
	

	public int getBlockColumn(String tag, int index) {
		
		Set s = this.getSetRow(index);
		
		String[] block = s.findTag(tag);
		
		return Integer.parseInt(block[1]);
	}
	
	public boolean doesTagExist(String tag, int index) {
		
		// Look at each way in the given line for the tag and if it exists return true;
		Set s = this.getSetRow(index);
		
		String[] block = s.findTag(tag);
		
		if(block != null)
			return true;
		
		return false;
	}
	
	public void allocate(int index, String tag, String address) {
		this.getSetRow(index).allocate(tag, address);
	}
	
	public boolean hasSpace(int index) {
		
		Set s = this.getSetRow(index);
		//s.__toString();
		
		return !s.isLineFull();
	}
	

	protected int getIndex(String address) {
				
		// Convert the hex to binary
		String binary = CacheUtilities.converHexToBinary(address);
		
		// What bits do you need?
		int amountToTrim = this.getIndexBits() + this.getOffsetBits();
		
		// Grab the index bits
		String indexBits = binary.substring(binary.length() - amountToTrim, binary.length() - this.getOffsetBits());
		//System.out.println(indexBits);
		
		// Return the the int value
		return Integer.parseInt(indexBits, 2);

	}
	
	public void printContents() {
		
		for(int i=0; i<this.sets.size(); i++) {
			
			Set s = this.getSetRow(i);
			System.out.print("Set     " + i + ":\t");

			String line = null;

			//if(this._assoc <= 2) {
				
				for(int j=0; j<s.cols.length;j++) {
					
					String dirty = s.cols[j][1] == "D" ? " D" : "  ";
					if(s.cols[j][0].equals("empty")) {
						line = "empty";
					}
					else if(this._assoc == 1)
						line = Long.toHexString(Integer.parseInt(s.cols[j][0], 16)/4) + dirty;  // was /4
					else if(this._assoc == 2)
						line = Long.toHexString(Integer.parseInt(s.cols[j][0], 16)/2) + dirty; // /2 
					else
						line = Long.toHexString(Integer.parseInt(s.cols[j][0], 16)/8) + dirty; // /8
				//	if(j+1 < s.cols.length || s.cols.length == 1) {
						if(dirty.equals(" D"))
							line = line.concat(" ");
						if(line.length() == 6)
							line = line.concat("\t");
						System.out.print(line + "\t");
				//	}
				//	else 
					//	System.out.print(line.trim());
				//		System.out.print(line);
				}
		
			System.out.println();
		}
	}
	
	protected String getTag(String address) {
	
		// Since it is always 8 Hex, just do a simple calculation of used bits times / 4 and return the left over hex string
		int amountToTrim = (this.getIndexBits() + this.getOffsetBits()) / 4;
		
		String hexTag = address.substring(0, address.length() - amountToTrim);
		
		return hexTag;
	}
	
	
	protected int setOffsetBits() {
		
		this._offsetBits = this.setBits(this.getBlocksize());
		
		return this._offsetBits;
	}
	
	protected int setIndexBits() {
		
		this._indexBits = this.setBits(this.getSets());
		
		return this._indexBits;
	}
	
	protected int setTagBits() {
		
		this._tagBits = 32 - this._indexBits - this._offsetBits;
		
		return this._tagBits;
	}
	
	public int getTagBits() {
		
		return this._tagBits;
	}
	
	public int getOffsetBits() {
		
		return this._offsetBits;
	}
	
	public int getIndexBits() {
		
		return this._indexBits;
	}
	
	
	private int setBits(int number) {
		
		int bits = (int)(Math.log(number) / Math.log(2));
		
		return bits;
		
	}
	
	public Set getSetRow(int row) {
		//System.out.println("Trying to get row " + row);
		return this.sets.get(row);
	}
	
	public int getSets() {
		return this._sets;
	}
	
	public int setSize(int size) {
		return this._size = size;
	}
	
	public int setAssoc(int assoc) {
		return this._assoc = assoc;
	}
	
	public int setBlocksize(int size) {
		return this._blocksize = size;
	}
	
	public int getAssoc() {
		return this._assoc;
	}
	
	public int getSize() {
		return this._size;
	}
	
	public int getBlocksize() {
		return this._blocksize;
	}
	
	public void __toString() {
		
		System.out.println("Cache");
		System.out.println("Blocksize: " + this.getBlocksize());
		System.out.println("Size: " + this.getSize());
		System.out.println("Assoc: " + this.getAssoc());
		
		System.out.println("IndexBits: " + this.getIndexBits());
		System.out.println("OffsetBits: " + this.getOffsetBits());
		System.out.println("TagBits: " + this.getTagBits());
	}
	
}
