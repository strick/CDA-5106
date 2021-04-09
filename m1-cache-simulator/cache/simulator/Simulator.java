package cache.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulator {
	
	public static boolean debug = false;
	
	public static void main(String[] args) {

		// Create the simulator and run it.
		CacheSimulator lru_0 = new CacheSimulator(16, 1024, 2, 0, 0, 0, 0, "gcc_trace.txt");
		CacheSimulator lru_1 = new CacheSimulator(16, 1024, 1, 0, 0, 0, 0, "perl_trace.txt");
		CacheSimulator op_3 = new CacheSimulator(16, 1024, 2, 0, 0, 2, 0, "vortex_trace.txt");
		CacheSimulator l2_4 = new CacheSimulator(16, 1024, 2, 8192, 4, 0, 0, "gcc_trace.txt");
		CacheSimulator l2_5 = new CacheSimulator(16, 1024, 1, 8192, 4, 0, 0, "go_trace.txt");
		CacheSimulator inclusive_6 = new CacheSimulator(16, 1024, 2, 8192, 4, 0, 1, "gcc_trace.txt");
		CacheSimulator inclusive_7 = new CacheSimulator(16, 1024, 1, 8192, 4, 0, 1, "compress_trace.txt");
		

		
//lru_0.run();  // Works
//lru_1.run(); 	// Works
//op_3.run();	// Works
//l2_4.run();	// Works
//l2_5.run();	// Works
//inclusive_6.run();
//inclusive_7.run();

		
		CacheSimulator sim = new CacheSimulator(Integer.parseInt(args[0]), 
				Integer.parseInt(args[1]), 
				Integer.parseInt(args[2]),
				Integer.parseInt(args[3]),
				Integer.parseInt(args[4]),
				Integer.parseInt(args[5]),
				Integer.parseInt(args[6]),
				args[7]);
		
		sim.run();
		
		//experimentOne();
		//experimentTwo();
		//experimentThree();
		//experimentFour();

	}
	
public static void experimentFour() {
		
		Integer[] sizes = new Integer[6];
		for(int n = 0; n < 6; n++) {
			sizes[n] = (int) Math.pow(2, n+1);
			//System.out.println(sizes[n]);
		} 
	
		class GraphPoint {
						
			double x;
			double y;
			
			GraphPoint(double x, double d){
				this.x = x;
				this.y = d;
			}
		}
		
		GraphPoint[][]  graph = new GraphPoint[2][6];

		for(int j = 0; j<2; j++) {			
			
			for(int i=0; i<sizes.length;i++) {		
				
				int c = i+(j*10);
		
				System.out.println("Running simulation (" + c  + "/27) - CacheSimulator(32, 1024, 4, " + sizes[i]*1024 + ", 8, 0, " + j + ", \"gcc_trace.txt\");");
				
				//CacheSimulator sim = new CacheSimulator(32, sizes[i]*1024, assoc, 0, 0, j, 0, "gcc_trace.txt");			
				CacheSimulator sim = new CacheSimulator(32, 1024, 4, sizes[i]*1024, 8, 0, j, "gcc_trace.txt");
				sim.run(false);
				
				// Get the cacti value
				double cacti = 0;
				cacti = getCactValue(sizes[i]*1024, 8, 32);
				
				//System.out.println("Cacti (" + sizes[i]*1024 + "," + assoc + ") = " + cacti);
				
				graph[j][i]  = new GraphPoint(Math.log(sizes[i]*1024), sim.getL1Att(cacti));		
			}
		}
		
		for(int i = 0; i<graph.length; i++) {
			System.out.println("===============" + Math.pow(2, i) + "-way===============");
			for(int j = 0; j<graph[i].length; j++) {
				System.out.println(graph[i][j].x + "\t" + graph[i][j].y);
			}
		}
	}
	
	public static void experimentThree() {
		
		Integer[] sizes = new Integer[9];
		for(int n = 0; n < 9; n++) {
			sizes[n] = (int) Math.pow(2, n);
			//System.out.println(sizes[n]);
		} 
	
		class GraphPoint {
						
			double x;
			double y;
			
			GraphPoint(double x, double d){
				this.x = x;
				this.y = d;
			}
		}
		
		GraphPoint[][]  graph = new GraphPoint[3][9];

		for(int j = 0; j<3; j++) {			
			
			for(int i=0; i<sizes.length;i++) {		
				
				int c = i+(j*10);
				int assoc = 4;
				System.out.println("Running simulation (" + c  + "/27) - CacheSimulator(32, " + sizes[i]*1024 + ", " + assoc + ", 0, 0, " + j  + ", 0, \"gcc_trace.txt\");");
				
				CacheSimulator sim = new CacheSimulator(32, sizes[i]*1024, assoc, 0, 0, j, 0, "gcc_trace.txt");			
				sim.run(false);
				
				// Get the cacti value
				double cacti = 0;
				cacti = getCactValue(sizes[i]*1024, assoc, 32);
				
				//System.out.println("Cacti (" + sizes[i]*1024 + "," + assoc + ") = " + cacti);
				
				graph[j][i]  = new GraphPoint(Math.log(sizes[i]*1024), sim.getL1Att(cacti));		
			}
		}
		
		for(int i = 0; i<graph.length; i++) {
			System.out.println("===============" + Math.pow(2, i) + "-way===============");
			for(int j = 0; j<graph[i].length; j++) {
				System.out.println(graph[i][j].x + "\t" + graph[i][j].y);
			}
		}
	}
	
	public static void experimentOne() {
		
		Integer[] sizes = new Integer[11];
		for(int n = 0; n < 11; n++) {
			sizes[n] = (int) Math.pow(2, n);
			//System.out.println(sizes[n]);
		} 
	
		class GraphPoint {
						
			double x;
			double y;
			
			GraphPoint(double x, double d){
				this.x = x;
				this.y = d;
			}
		}
		
		GraphPoint[][]  graph = new GraphPoint[5][11];

		for(int j = 0; j<5; j++) {
			for(int i=0; i<sizes.length;i++) {		
				
				int c = i+(j*10);
				int assoc = (int) Math.pow(2,  j);
				System.out.println("Running simulation (" + c  + "/55) - CacheSimulator(32, " + sizes[i]*1024 + ", " + assoc + ", 0, 0, 0, 0, \"gcc_trace.txt\");");
				
				CacheSimulator sim = new CacheSimulator(32, sizes[i]*1024, assoc, 0, 0, 0, 0, "gcc_trace.txt");			
				sim.run(false);
				graph[j][i]  = new GraphPoint(Math.log(sizes[i]*1024), sim.getL1MissRates());		
			}
		}
		
		for(int i = 0; i<graph.length; i++) {
			System.out.println("===============" + Math.pow(2, i) + "-way===============");
			for(int j = 0; j<graph[i].length; j++) {
				System.out.println(graph[i][j].x + "\t" + graph[i][j].y);
			}
		}
		
	}
	
	public static void experimentTwo() {
		
		Integer[] sizes = new Integer[11];
		for(int n = 0; n < 11; n++) {
			sizes[n] = (int) Math.pow(2, n);
			//System.out.println(sizes[n]);
		} 
	
		class GraphPoint {
						
			double x;
			double y;
			
			GraphPoint(double x, double d){
				this.x = x;
				this.y = d;
			}
		}
		
		GraphPoint[][]  graph = new GraphPoint[5][11];

		for(int j = 0; j<5; j++) {
			for(int i=0; i<sizes.length;i++) {		
				
				int c = i+(j*10);
				int assoc = (int) Math.pow(2,  j);
				System.out.println("Running simulation (" + c  + "/55) - CacheSimulator(32, " + sizes[i]*1024 + ", " + assoc + ", 0, 0, 0, 0, \"gcc_trace.txt\");");
				
				CacheSimulator sim = new CacheSimulator(32, sizes[i]*1024, assoc, 0, 0, 0, 0, "gcc_trace.txt");			
				sim.run(false);
				
				// Get the cacti value
				double cacti = 0;
				cacti = getCactValue(sizes[i]*1024, assoc, 32);
				
				System.out.println("Cacti (" + sizes[i]*1024 + "," + assoc + ") = " + cacti);
				
				graph[j][i]  = new GraphPoint(Math.log(sizes[i]*1024), sim.getL1Att(cacti));		
			}
		}
		
		for(int i = 0; i<graph.length; i++) {
			System.out.println("===============" + Math.pow(2, i) + "-way===============");
			for(int j = 0; j<graph[i].length; j++) {
				System.out.println(graph[i][j].x + "\t" + graph[i][j].y);
			}
		}
		
	}
	
	private static double getCactValue(int size, int assoc, int block) {
		
		// TODO Auto-generated method stub
		List<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("C:\\Users\\bjstrick\\eclipse-workspace\\cache.simulator\\cacti_table.csv"))){
			scanner.nextLine();
		    while (scanner.hasNextLine()) {
		    	
		    	try (Scanner rowScanner = new Scanner(scanner.nextLine())) {
			        rowScanner.useDelimiter(",");
			        while (rowScanner.hasNext()) {
			        	
			        	String lineSize = rowScanner.next();
			        
			            if(Integer.parseInt(lineSize) == size) {
			            	rowScanner.next();
			            	String blockLine = rowScanner.next();
			            	
			            	if(Integer.parseInt(blockLine) == block) {
			            		
			            		String assocLine = rowScanner.next();
			            		if(assocLine.trim().equals("FA")) break;;
			            		
			            		
			            		if(Integer.parseInt(assocLine) == assoc) {
			            			return Double.parseDouble(rowScanner.next());
			            		}
			            		break;
			            	}
			            	break;
			            }
			            break;
			            
			        }
			        
		    	} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}
	
	
}
	
