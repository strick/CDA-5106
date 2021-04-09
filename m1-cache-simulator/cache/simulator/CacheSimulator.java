package cache.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.ArrayList;

public class CacheSimulator {
		
	protected ArrayList<Cache> caches = new ArrayList<Cache>();
	protected String traceFile;
	protected ArrayList<Operation> operations = new ArrayList<Operation>();
	protected int blocksize = 0;
	protected ArrayList<Integer> cacheSizes = new ArrayList<>();
	protected ArrayList<Integer> cacheAssocs = new ArrayList<>();
	protected int replacementPolicy;
	protected int inclusionProperty;
	protected CacheConfig config;
	protected ReplacementPolicy policy = null;
	protected boolean print = true;
	
	public CacheSimulator(int blocksize, int l1_size, int l1_assoc, int l2_size, int l2_assoc,
			int replacement_policy, int inclusion_property, String tracefile) {
		// TODO Auto-generated constructor stub
		
		this.config = new CacheConfig(blocksize,l1_size, l1_assoc, l2_size, l2_assoc, replacement_policy, inclusion_property);
		this.traceFile = tracefile;
	}
	
	public void run(boolean print) {
		this.print = print;
		run();
	}

	public void run() {
		
		//this.readCacheConfig();
		this.setupCaches();		
		this.readTraceFile();
		
		if(this.config.getReplacementPolicy() == 0) {
			policy = new LRU(this.caches);
		}
		else if(this.config.getReplacementPolicy() == 1) {
			policy = new LRU(this.caches);
		}
		else if(this.config.getReplacementPolicy() == 2) {
			policy = new OptimalPolicy(this.caches, this.operations);
		}

		// Run policy on operations
		for(int i=0; i<this.operations.size();i++) {
		//for(int i=0; i<50 ;i++) {
			
			Operation current = this.operations.get(i);
	//		System.out.println(this.caches.get(0).getTag(current.getAddress()) + " Index: " + this.caches.get(0).getIndex(current.getAddress()));
		//	continue;

			if(current.getOperation().equals("r")) {
				
				policy.read(current.getAddress());
			}
			else {
				policy.write(current.getAddress());
			
			}
		}
		
		if(this.print) this.__toString();
		
	}
	
	protected void __toString() {
		
		System.out.println("===== Simulator configuration =====");
		System.out.println("BLOCKSIZE:             " + config.getBlocksize());
		System.out.println("L1_SIZE:               " + config.getL1Size());
		System.out.println("L1_ASSOC:              " + config.getL1Assoc());
		System.out.println("L2_SIZE:               " + config.getL2Size());
		System.out.println("L2_ASSOC:              " + config.getL2Assoc());
		System.out.println("REPLACEMENT POLICY:    " + config.getReplacementPolcyString());
		System.out.println("INCLUSION PROPERTY:    " + config.getInclusionPropertyString());
		System.out.println("trace_file:            " + this.traceFile);
		System.out.println("===== L1 contents =====");
		this.caches.get(0).printContents();
		if(policy.getL2Writes() + policy.getL2Reads() != 0) {
			System.out.println("===== L2 contents =====");
			this.caches.get(1).printContents();
		}
		System.out.println("===== Simulation results (raw) =====");
		System.out.println("a. number of L1 reads:        " + policy.getL1Reads());
		System.out.println("b. number of L1 read misses:  " + policy.getL1ReadMisses());
		System.out.println("c. number of L1 writes:       " + policy.getL1Writes());
		System.out.println("d. number of L1 write misses: " + policy.getL1WriteMisses());
		System.out.println("e. L1 miss rate:              " + String.format("%.6f", policy.getL1MissRate()));
		System.out.println("f. number of L1 writebacks:   " + policy.getL1WriteBacks());
		System.out.println("g. number of L2 reads:        " + policy.getL2Reads());
		System.out.println("h. number of L2 read misses:  " + policy.getL2ReadMisses());
		System.out.println("i. number of L2 writes:       " + policy.getL2Writes());
		System.out.println("j. number of L2 write misses: " + policy.getL2WriteMisses());
		if(policy.getL2Writes() + policy.getL2Reads() != 0)
			System.out.println("k. L2 miss rate:              " + String.format("%.6f", (double)policy.getL2ReadMisses() / (double) policy.getL2Reads()) );
		else
			System.out.println("k. L2 miss rate:              0");
		System.out.println("l. number of L2 writebacks:   " + policy.getL2WriteBacks());
		System.out.println("m. total memory traffic:      " + policy.getMemoryTraffic());
	}
	
	public double getL1MissRates() {
		return policy.getL1MissRate();
	}
	
	public double getL1Att(double cacti) {
		
		double Tat = (policy.getL1Reads() + policy.getL1Writes()) * cacti + (policy.getL1ReadMisses() + policy.getL1WriteMisses()) * 100;
		
		double Aat = Tat / (policy.getL1Reads() + policy.getL1Writes());
		
		return Aat;
	}

	protected void readTraceFile() {
	
		try {
			
			File myObj = new File("C:\\Users\\bjstrick\\eclipse-workspace\\cache.simulator\\traces\\" + this.traceFile);
			Scanner myReader = new Scanner(myObj);
			int opIndex = 0;
						
			// Loop through the file until the cache is setup
		    while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();  
		        
		        String[] splited = data.split("\\s+");
				
				// If a blank line *eye roll* skip
				if(splited.length < 2)
					continue;
				
				String op = splited[0].substring(splited[0].length()-1);
				String hex = splited[1];

			//	System.out.println(hex);
		        //String binary = CacheUtilities.converHexToBinary(hex);

		        this.operations.add(new Operation(op, hex, opIndex));
		     //   System.out.println(binary);
		        opIndex++;
		        
		    }
		    
		    myReader.close();
		    
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}

	
	protected void addCacheSets(Cache c, String line) {
		
		String[] setLine =  line.split("\\s+");
		
		// Get the number of tags so you can create an array to send
		int tagCount = 0;
		int tagInit = 0;
		for(int i = 2; i < setLine.length ; i++) {
			
			// if a tag, then increase
			if(!this._isDirtyBit(setLine[i])) {
				tagCount++;
			}
		}
		
		// Create a set array to contain the tags and dirty bit option
		String[][] setArray = new String[tagCount][2];
		
		// Loop through until you don't have any more columns to add
		
		String tempTag = "";

		for(int i = 2; i < setLine.length ; i++) {
			
			// If it is one character, dirty bit
			if(this._isDirtyBit(setLine[i])) {
				
				// Set the dirty bit and add this tag since the bit is the last thing
				setArray[tagInit][0] = tempTag;
				setArray[tagInit][1] = "Dirty";
				
				tagInit++;
				tempTag = "";
				
			}
			// It's not a dirty bit setting, but a tag
			else {
				
				// If the tempTag is set, then it needs to be replaced, so create a assoc
				if(!tempTag.equals("")) {
					setArray[tagInit][0] = tempTag; 
					setArray[tagInit][1] = "Not Dirty";
					
					tagInit++;
				}

				tempTag = setLine[i];	
			}

		}
		
		if(!tempTag.equals("")) {
			setArray[tagCount-1][0] = tempTag;
			setArray[tagCount-1][1] = "Not Dirty";
		}

		
		// #: -> #
		int setRowLine = Integer.parseInt(setLine[1].substring(0, setLine[1].length()-1));
		c.assignSet(setArray, setRowLine);
		c.getSetRow(setRowLine).__toString();

	}
		
	/**
	 * Lets us know if the string is a diry bit reprsentation.
	 * @param data
	 * @return
	 */
	protected boolean _isDirtyBit(String data) {

		return data.length() == 1;

	}
	
	protected void setupCaches() {		
		
		// Create the caches
		
		Cache l1 = new Cache(config.getL1Size(), config.getL1Assoc(), config.getBlocksize(), 1, this.config.getInclusionProperty());
		this.caches.add(l1);
	//	l1.__toString();
		
		if(config.getL2Size() > 0 && config.getL2Assoc() > 0) {
			Cache l2 = new Cache(config.getL2Size(), config.getL2Assoc(), config.getBlocksize(), 2, this.config.getInclusionProperty());
			this.caches.add(l2);
			//l2.__toString();
		}

	}
	
	protected String getConfigStrValue(String line) {
		
		return this.getConfigValue(line);
	}
	
	protected int getConfigIntValue(String line) {
				
		return Integer.parseInt(this.getConfigValue(line));

	}
	
	private String getConfigValue(String line) {
		
		String[] splited = line.split("\\s+");
		
		// Last one is going to be the one
		return splited[splited.length - 1];
	}
	
}
