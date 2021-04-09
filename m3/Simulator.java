
public class Simulator {

	public static void main(String[] args) {
		
		// 0 fu{0} src{29,14} dst{-1} IF{0,1} ID{1,1} IS{2,1} EX{3,1} WB{4,1}
		// Thos are from the instruction itselft
		// fu{#) is the function runing, src is the next
		// 2b6420 fu()0 src-1 dst29 14
		
		// EX{3,1}  starts on cycle 3 and second is how long it takes
		
		// the 128 8 perl
		
		Handler h = new Handler(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);
		h.run(); 
		
	}
}
