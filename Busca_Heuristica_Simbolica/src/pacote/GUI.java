package pacote;
import java.io.IOException;
import java.io.PrintStream;

/* Implementation of the algorithms that performs backward planning, using regression.
 * The method proposed by [Fourmann, 2000] and the method proposed  by "[Ritanen, 2008]" */

public class GUI {
	
	// Guarde os valores originais de System.out e System.err
	static PrintStream originalOut = System.out;
	static PrintStream originalErr = System.err;
	enum E_Problem{rovers, logistics}
	enum E_Types{exaustive, heuristic}
	static Runtime runtime;
	static long initmemory;
	
	
	
	
	
	/* The main method receives a file containing the description of the planning  domain\problem 
	 * and calls the backward search. */
	public static void main(String[] args) throws IOException{
		//File containing the description of the planning domain-problem
		
		//String fileName = "grounded_25.txt";
		//String fileName = "rovers-01-GROUNDED.txt";
		//String fileName = "storage-01-grounded.txt"; 
		
		GUI.runtime = Runtime.getRuntime();
		initmemory = runtime.totalMemory() - runtime.freeMemory();

//		if (args.length != 3) {
//            System.err.println("Usage: java GUI <type> <problem> <test>");
//            System.exit(1);
//        }
	
		E_Types type = E_Types.heuristic;
        E_Problem problem = E_Problem.rovers;
        int test = 2;

//        try {
//        	
//            type = E_Types.valueOf(args[0].toLowerCase());
//            problem = E_Problem.valueOf(args[1].toLowerCase());
//            test = Integer.parseInt(args[2]);
//        } catch (IllegalArgumentException e) {
//            System.err.println("Invalid arguments.");
//            System.exit(1);
//        }

      
        try {
        	test(type, problem, test);
        } finally {
            System.gc();
        }


		
		
		//RUN ALL 
//		String filePath = "rovers/";
//		int[] roverTests = {1, 2, 3, 4, 5, 6, 7, 8};
//		int[] logistcTests = {4,6,8,10,12,14};
//
//		
//		
//		for(int i : logistcTests) {
//			test(E_Types.exaustive, E_Problem.logistics,i);
//			System.gc();
//			test(E_Types.heuristic, E_Problem.logistics,i);
//			System.gc();
//		}
//		
//		for(int i : roverTests) {
//			test(E_Types.exaustive, E_Problem.rovers,i);
//			System.gc();
//			test(E_Types.heuristic, E_Problem.rovers,i);
//			System.gc();
//		}
		
			
//		String fileName;
//		if(args.length == 0) {
//			Scanner ler = new Scanner(System.in);
//			System.out.printf("Informe o número para busca: 0 - exaustiva | 1 - heuristica\n");
//			t = ler.nextInt();
//			int p;	
//			
//			System.out.printf("Problema:\n");
//			p = ler.nextInt();
////			fileName = "LOGISTICS-" + p + "-0-GROUNDED.txt"; 
//			//fileName = "rovers-" + p + "-0-GROUNDED.txt"; 	
//			fileName = "rovers-0" + p + "-GROUNDED.txt";
//			
//		} else {
//			t = Integer.parseInt(args[0]);
////			fileName = "LOGISTICS-" + args[1] + "-0-GROUNDED.txt";
//			fileName = "rovers-0" + args[1] + "-GROUNDED.txt"; 
//		}
//		
//		
//		
////		String fileName = "LOGISTICS-14-0-GROUNDED.txt"; 		
//
//		
//		String type = "propplan"; //"ritanen" or "propplan"
//		
//		int nodenum = 50000000;
//		int cachesize =  5000000;
//		
//		ModelReader model = new ModelReader();	
//		model.fileReader(filePath+fileName, type, nodenum, cachesize);
//
//			
//		
//		
//		
//		System.out.println(fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf(".")));
//		
		// 0 - exaustiva | 1 - heuristica
		
//	//	int t = 0;
//		try {
//			if(t == 0) {
//				System.out.println("Exaustive search");
//				System.out.println("\n" + "Performing search...");
//			    
//			    Search s = new Search(model);
//				PrintStream out = new PrintStream("exaustiva-"+fileName);
//				System.setOut(out);
//				System.setErr(out);
//				long start = System.currentTimeMillis();
//			    s.planForward(start);
//			    long end = System.currentTimeMillis();
//			    System.out.println("Tempo gasto: " + (end - start));
//	
//			    long memory = runtime.totalMemory() - runtime.freeMemory();
//				System.out.println("Used memory is bytes: " + (memory - initmemory));
//				
//			    out.close();
//				
//				
//			}else if(t == 1) {
//				System.out.println("Heuristic search");
//				System.out.println("Performing search...");
//				Search r = new Search(model);	    
//			    PrintStream out2 = new PrintStream("heuristica-"+fileName);
//				System.setOut(out2);
//				System.setErr(out2);
//				long start = System.currentTimeMillis();
//			    r.heuristcSearch(model);
//			    long end = System.currentTimeMillis();
//			    System.out.println("Tempo gasto: " + (end - start));
//				
//				runtime.gc();
//				long memory = runtime.totalMemory() - runtime.freeMemory();
//				System.out.println("Used memory is bytes: " + (memory - initmemory));
//				out2.close();
//	
//			}
//			
//			System.setOut(GUI.originalOut);
//			System.setErr(GUI.originalErr);
//
//			// Imprima a mensagem de conclusão
//			System.out.println("Execução terminou [OK].");
//		
//		} catch (Exception e) {
//			System.out.println("Error:" + e);
//			System.setOut(GUI.originalOut);
//			System.setErr(GUI.originalErr);
//
//			// Imprima a mensagem de conclusão
//			System.out.println("Execução terminou [Error].");
//			
//		}
	}

	
	static void test(E_Types typeTest, E_Problem problem, int testNumber) {
		String filePath = problem == E_Problem.rovers ? "rovers/" : "logistics/";
		
		String fileName = problem == E_Problem.logistics?
				"LOGISTICS-" + testNumber + "-0-GROUNDED.txt" :
				"rovers-0" + testNumber + "-GROUNDED.txt";
		
		String type = "propplan"; //"ritanen" or "propplan"
		
		int nodenum = 50000000;
		int cachesize =  5000000;
		
		ModelReader model = new ModelReader();	
		try {
			model.fileReader(filePath+fileName, type, nodenum, cachesize);
			
			System.out.println(fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf(".")));
			
		
			if(typeTest == E_Types.exaustive) {
				System.out.println("Exaustive search");
				System.out.println("\n" + "Performing search...");
			    
			    Search s = new Search(model);
				PrintStream out = new PrintStream("exaustiva-"+fileName);
				System.setOut(out);
//				System.setErr(out);
				long start = System.currentTimeMillis();
				TimeManager verify = new TimeManager(start, initmemory, runtime);
				verify.setMaxTime(10800000);
			    s.planForward(verify);
			    long end = System.currentTimeMillis();
			    System.out.println("Tempo gasto: " + (end - start));
	
			    long memory = runtime.totalMemory() - runtime.freeMemory();
				System.out.println("Used memory is bytes: " + (memory - initmemory));
				
			    out.close();
				
				
			}else{
				System.out.println("Heuristic search");
				System.out.println("Performing search...");
				Search r = new Search(model);	    
			    PrintStream out2 = new PrintStream("heuristica-"+fileName);
				System.setOut(out2);
//				System.setErr(out2);
				long start = System.currentTimeMillis();
			    r.heuristcSearch(model,new TimeManager(start, initmemory, runtime));
			    long end = System.currentTimeMillis();
			    System.out.println("Tempo gasto: " + (end - start));
				
				runtime.gc();
				long memory = runtime.totalMemory() - runtime.freeMemory();
				System.out.println("Used memory is bytes: " + (memory - initmemory));
				out2.close();
	
			}
			
			System.setOut(GUI.originalOut);
			System.setErr(GUI.originalErr);

			// Imprima a mensagem de conclusão
			System.out.println("Execução terminou [OK].");
		
		} catch (Exception e) {
			System.out.println("Error:" + e);
			System.setOut(GUI.originalOut);
			System.setErr(GUI.originalErr);

			// Imprima a mensagem de conclusão
			System.out.println("Execução terminou [Error].");
			
		}
	}
	
	
}