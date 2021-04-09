package cache.simulator;

public class Operation {

	protected String _operation;
	protected String _address;
	protected int _index;
	
	public Operation(String op, String addr, int index) {
		
		this._operation = op;
		this._address = addr;	
		this._index = index;
	}
	
	public int getIndex() {
		return this._index;
	}
	
	public String getOperation() {
		return this._operation;
	}
	
	public String getAddress() {
		return this._address;
	}
}

