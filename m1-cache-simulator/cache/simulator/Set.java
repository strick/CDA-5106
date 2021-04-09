package cache.simulator;

public class Set {

	protected String _tag = null;
	protected String _index = null;
	protected int touches = 0;  // Track for LRU replacement policy
	protected String[][] cols;
	protected int _assoc; 
	
	
	public Set(int assoc) {
		  
		this._assoc = assoc;
		// Create number of assoc columns with room for a tag and dirty bit
		this.cols = new String[assoc][3];
		
		for(int i = 0; i<this.cols.length; i++) {
			this.cols[i][0] = "empty";
			this.cols[i][1] = "0";
		}
	}
	
	public void writeDirty(int column) {
		for(int i = 0; i<this.cols.length; i++) {
			if(i == column)
				this.cols[i][1] = "D";
		}
	}
	
	public void empty() {
		for(int i = 0; i<this.cols.length; i++) {
			this.cols[i][0] = "empty";
			this.cols[i][1] = "0";
		}
	}
	
	public void allocate(String tag, String address) {

	for(int i = 0; i<this.cols.length; i++) {
			if(this.cols[i][0] == "empty") {
				this.cols[i][0] = tag;
				this.cols[i][2] = address;
				this.addTouch();
				return;
			}
		}
		
	}
	
public boolean allocateL1FromL2Block(int row, int col, String tag, String address) {
		
		
		//System.out.println("Current Block");
		//this.__toString();
		if(Simulator.debug) System.out.println("Replacing " + this.cols[col][0] + " with " + tag + " at (" + row + "," + col + ")");
		this.cols[col][0] = "empty";
		this.cols[col][2] = address;
		
		//System.out.println("Now Block");
		//this.__toString();
		
		if(this.cols[col][1].equals("D")) {
			this.cols[col][1] = "0";
			//this.__toString();
			
			return true;
		}
		if(Simulator.debug) this.__toString();
		
		
		return false;
	}
	
	public boolean allocateBlock(int row, int col, String tag, String address) {
		
		
		//System.out.println("Current Block");
		//this.__toString();
		if(Simulator.debug) System.out.println("Replacing " + this.cols[col][0] + " with " + tag + " at (" + row + "," + col + ")");
		this.cols[col][0] = tag;
		this.cols[col][2] = address;
		
		//System.out.println("Now Block");
		//this.__toString();
		
		if(this.cols[col][1].equals("D")) {
			this.cols[col][1] = "0";
			//this.__toString();
			
			return true;
		}
		if(Simulator.debug) this.__toString();
		
		
		return false;
	}
	
	public boolean isLineFull() {
		
		for(int i = 0; i<this.cols.length; i++) {
			if(this.cols[i][0] == "empty")
				return false;
		}
		
		return true;
	}
	
	/**
	 * lookf for the tag in any of the ways
	 * @param tag
	 * @return
	 */
	public String[] findTag(String tag) {
		
		for(int i = 0; i<this.cols.length; i++) {
			if(this.cols[i][0].equals(tag)) {
				
				String[] s = new String[2];
				s[0] = tag;
				s[1] = Integer.toString(i);  // Need to know column as well for replacment policy.
				
				return s;
			}
			
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param tag - actual tag
	 * @param assoc - the column this gets stored at
	 * @param dirty - states wether this is dirty
	 * @return
	 */
	public Set setData(String tag, int assoc, String dirty) {
		
		this.cols[assoc][0] = tag;
		this.cols[assoc][1] = dirty;
		return this;
	}
	
	public int addTouch() {
		this.touches++;
		
		return this.touches;
	}
	
	public int getTouches() {
		return this.touches;
	}
	
	public Set(String tag, String index) {
		
		this._tag = tag;
		this._index = index;
	}
	
	public Set setTag(String tag) {
		this._tag = tag;
		return this;
	}
	
	public Set setIndex(String index) {
		this._index = index;
		return this;
	}
	
	public String getTag() {
		return this._tag;
	}
	
	public void __toString() {
		System.out.print("|");
		
		for(int i = 0; i<this.cols.length; i++) {
			System.out.print(this.cols[i][0] + "|" + this.cols[i][1] + "|");
		
		}
		System.out.println("");
	}

	public String getEvictedAddress(int line, int col, String currentTag) {
		// TODO Auto-generated method stub
		return this.cols[col][2];
	}

	public void l2Allocate(String tag, String address) {
		// TODO Auto-generated method stub
		for(int i = 0; i<this.cols.length; i++) {
			if(this.cols[i][0] == "empty") {
				this.cols[i][0] = tag;
				this.cols[i][2] = address;
				this.addTouch();
				return;
			}
		}
	}
			
}
