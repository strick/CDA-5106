
public class RSEntry {

	public int tag;
	public boolean s1Ready;
	public boolean s2Ready;
	public int s1Value;
	public int s2Value;
	public Instruction ins;
	
	RSEntry(int tag){
		this.tag = tag;
	}

	public void print() {
		// TODO Auto-generated method stub
		System.out.println(tag + "|" + s1Ready + "|" + s1Value +  "|" + s2Ready + "|" + s2Value);
	}
	
}
