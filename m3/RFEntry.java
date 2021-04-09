
public class RFEntry {

	public int index;
	public boolean inRf;
	public int tag;
	public int value;
	
	RFEntry(int index, int tag){
		this.index = index;
		this.tag = tag;
		this.inRf = true;		
	}

}
