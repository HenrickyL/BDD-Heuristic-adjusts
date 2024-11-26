package pacote;
import java.io.IOException;
import java.io.PrintStream;


/* Implementation of the algorithms that performs backward planning, using regression.
 * The method proposed by [Fourmann, 2000] and the method proposed  by "[Ritanen, 2008]" */

public class GUI {
	
	// Guarde os valores originais de System.out e System.err
	static PrintStream originalOut = System.out;
	static PrintStream originalErr = System.err;
	static Runtime runtime;
	static long initmemory;
	
	
	/* The main method receives a file containing the description of the planning  domain\problem 
	 * and calls the backward search. */
	public static void main(String[] args) throws IOException{
		GUI.runtime = Runtime.getRuntime();
		initmemory = runtime.totalMemory() - runtime.freeMemory();

	
		ControllerOptions test = new ControllerOptions(
				ProblemTypeEnum.rovers, 
				SearchTypeEnum.exaustive, 
				2,  
				runtime,
				initmemory
				);
		
		Controller controller = new Controller();
		controller.Run(test);
	}

	
// 	static void test(E_Types typeTest, E_Problem problem, int testNumber) {
// 		String filePath = problem == E_Problem.rovers ? "rovers/" : "logistics/";
		
// 		String fileName = problem == E_Problem.logistics?
// 				"LOGISTICS-" + testNumber + "-0-GROUNDED.txt" :
// 				"rovers-0" + testNumber + "-GROUNDED.txt";
		
// 		String type = "propplan"; //"ritanen" or "propplan"
		
// 		int nodenum = 50000000;
// 		int cachesize =  5000000;
		
// 		ModelReader model = new ModelReader();	
// 		try {
// 			model.fileReader(filePath+fileName, type, nodenum, cachesize);
			
// 			System.out.println(fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf(".")));
			
		
// 			if(typeTest == E_Types.exaustive) {
// 				System.out.println("Exaustive search");
// 				System.out.println("\n" + "Performing search...");
			    
// 			    Search s = new Search(model);
// 				PrintStream out = new PrintStream("exaustiva-"+fileName);
// 				System.setOut(out);
// //				System.setErr(out);
// 				long start = System.currentTimeMillis();
// 				TimeManager verify = new TimeManager(initmemory, runtime);
// 				verify.setMaxTime(10800000);
// 			    s.planForward(verify);
// 			    long end = System.currentTimeMillis();
// 			    System.out.println("Tempo gasto: " + (end - start));
	
// 			    long memory = runtime.totalMemory() - runtime.freeMemory();
// 				System.out.println("Used memory is bytes: " + (memory - initmemory));
				
// 			    out.close();
				
				
// 			}else{
// 				System.out.println("Heuristic search");
// 				System.out.println("Performing search...");
// 				Search r = new Search(model);	    
// 			    PrintStream out2 = new PrintStream("heuristica-"+fileName);
// 				System.setOut(out2);
// //				System.setErr(out2);
// 				long start = System.currentTimeMillis();
// 			    r.heuristcSearch(model,new TimeManager(initmemory, runtime));
// 			    long end = System.currentTimeMillis();
// 			    System.out.println("Tempo gasto: " + (end - start));
				
// 				runtime.gc();
// 				long memory = runtime.totalMemory() - runtime.freeMemory();
// 				System.out.println("Used memory is bytes: " + (memory - initmemory));
// 				out2.close();
	
// 			}
			
// 			System.setOut(GUI.originalOut);
// 			System.setErr(GUI.originalErr);

// 			// Imprima a mensagem de conclusão
// 			System.out.println("Execução terminou [OK].");
		
// 		} catch (Exception e) {
// 			System.out.println("Error:" + e);
// 			System.setOut(GUI.originalOut);
// 			System.setErr(GUI.originalErr);

// 			// Imprima a mensagem de conclusão
// 			System.out.println("Execução terminou [Error].");
			
// 		}
// 	}
}