package cache.simulator;

import java.util.ArrayList;

public class CacheConfig {

	protected int blocksize = 0;
	protected int l1_size = 0;
	protected int l1_assoc = 0;
	protected int l2_size = 0;
	protected int l2_assoc = 0;
	protected ArrayList<Integer> cacheSizes = new ArrayList<>();
	protected ArrayList<Integer> cacheAssocs = new ArrayList<>();
	protected int replacementPolicy;
	protected int inclusionProperty;
	
	
	public CacheConfig(int blocksize,int l1_size, int l1_assoc, int l2_size, int l2_assoc, int replacementPolicy, int inclusionProperty) {
		
		this.blocksize = blocksize;
		this.l1_size = l1_size;
		this.l1_assoc = l1_assoc;
		this.l2_size = l2_size;
		this.l2_assoc = l2_assoc;
		this.replacementPolicy = replacementPolicy;
		this.inclusionProperty = inclusionProperty;
		
	}
	
	
	public int getL1Size() {
		return this.l1_size;
	}
	
	public int getL1Assoc() {
		return this.l1_assoc;
	}
	
	public int getL2Size() {
		return this.l2_size;
	}
	
	public int getL2Assoc() {
		return this.l2_assoc;
	}
	
	public int setInclusionProperty(int property) {
		
		return this.inclusionProperty = property;
	}
	
	public int getInclusionProperty() {
		
		return this.inclusionProperty;
	}
	
	public String getInclusionPropertyString() {
		if(this.getInclusionProperty() == 0)
			return "non-inclusive";
		
		return "inclusive";
					
			
	}
	
	public String getReplacementPolcyString() {
		
		switch(this.replacementPolicy) {
		case 0: 
				return "LRU";
		case 1: 
				return "PLRU";
		case 2: 
				return "Optimal";
		default: return "";
		}
	}
	
	public int setReplacementPolicy(int policy) {
		
		return this.replacementPolicy = policy;
	}
	
	public int getReplacementPolicy() {
		
		return this.replacementPolicy;
	}
	
	public void addCacheSize(int size) {
		
		if(size == 0) return;
		this.cacheSizes.add(size);
	}
	
	public void addCacheAssoc(int assoc) {
		
		if(assoc == 0) return;
		this.cacheAssocs.add(assoc);
	}
	
	public int popCacheAssoc() {
		
		if(this.cacheAssocs.size() > 1)
			return this.cacheAssocs.remove(this.cacheAssocs.size()-1);
		else
			return this.cacheAssocs.remove(0);
	}
	
	public int popCacheSize() {
		
		if(this.cacheSizes.size() > 1)
			return this.cacheSizes.remove(this.cacheSizes.size()-1);
		else
			return this.cacheSizes.remove(0);
	}
	
	public int setBlocksize(int size) {
		
		return this.blocksize = size;
	}
	
	public int getBlocksize() {
		return this.blocksize;
		
	}
	
}
