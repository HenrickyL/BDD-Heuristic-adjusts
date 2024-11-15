package pacote;

import java.io.PrintStream;

public class Controller {
	static PrintStream originalOut = System.out;
	static PrintStream originalErr = System.err;
	enum E_Problem{rovers, logistics}
	enum E_Types{exaustive, heuristic}
	static Runtime runtime;
	static long initmemory;

    private int nodenum = 50000000;
    private int cachesize =  5000000;
	
    public void Run(){

		Controller.runtime = Runtime.getRuntime();
        initmemory = runtime.totalMemory() - runtime.freeMemory();

        try{
            Setup();
        }catch(Exception e){
            System.out.println("Error:" + e);
			System.setOut(GUI.originalOut);
			System.setErr(GUI.originalErr);
			// Imprima a mensagem de conclusão
			System.out.println("Execução terminou [Error].");
        }
    }


    private void Setup()  throws Exception{
        E_Types type = E_Types.heuristic;
        E_Problem problem = E_Problem.rovers;
        int testNumber = 2;


        BaseSearch marisaSearch = new SearchOldMethod();
        runSearchMethod(marisaSearch, type, problem, testNumber);
        

        BaseSearch henrickySearch = new SearchNewMethod();
        runSearchMethod(henrickySearch, type, problem, testNumber);
    }


    private void runSearchMethod(BaseSearch search, E_Types typeTest, E_Problem problem, int testNumber)  throws Exception {
        String filePath = problem == E_Problem.rovers ? "rovers/" : "logistics/";
		
		String fileName = problem == E_Problem.logistics?
				"LOGISTICS-" + testNumber + "-0-GROUNDED.txt" :
				"rovers-0" + testNumber + "-GROUNDED.txt";
		
		String type = "propplan"; //"ritanen" or "propplan"

        ModelReader model = new ModelReader();


        model.fileReader(filePath+fileName, type, nodenum, cachesize);
		search.SetModel(model);
        System.out.println(fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf(".")));
        TimeManager verify = new TimeManager( initmemory, runtime);
        
    
        if(typeTest == E_Types.exaustive) {
            System.out.println("Exaustive search");
            System.out.println("\n" + "Performing search...");
            
            PrintStream out = new PrintStream("exaustiva-"+fileName);
            System.setOut(out);
//				System.setErr(out);
            verify.setMaxTime(10800000);
            verify.resetStartTime();
            search.ExaustiveSearch(verify);
            verify.PrintElapsedTime();
            long memory = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Used memory is bytes: " + (memory - initmemory));
            
            out.close();
        }else{
            System.out.println("Heuristic search");
            System.out.println("Performing search...");
            PrintStream out2 = new PrintStream("heuristica-"+fileName);
            System.setOut(out2);
            
            verify.resetStartTime();
            search.HeuristicSearch(verify);
            verify.PrintElapsedTime();
            
            runtime.gc();
            long memory = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Used memory is bytes: " + (memory - initmemory));
            out2.close();
        }
        
        System.setOut(GUI.originalOut);
        System.setErr(GUI.originalErr);

        // Imprima a mensagem de conclusão
        System.out.println("Execução terminou [OK].");
    }
	
}
