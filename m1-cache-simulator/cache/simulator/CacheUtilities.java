package cache.simulator;

public class CacheUtilities {

	public static String converHexToBinary(String hex) {

		//System.out.println("returning " + hex);
		long hexDec = Long.parseLong(hex, 16);	
		String binary = Long.toBinaryString(hexDec);
		//System.out.println("returning " + binary);
		return adjustOffset(binary);
		//return new BigIteger(hex, 16).toString(2).;
	}
	
	public static String adjustOffset(String binary) {
		
		// Figure out the offset of missing 0's
		int offset = 31 - binary.length();
		
		//System.out.println(offset);
		
		while(offset > 0) {			
			binary = "0".concat(binary);			
			offset--;
			//System.out.println(offset);
		}
		
		return binary;
	}
	
	public static String convertBinaryToHex(String binary) {
		
		long hexDec = Long.parseLong(binary, 2);	
		String hex = Long.toHexString(hexDec);
		
		return hex;
	}
}
