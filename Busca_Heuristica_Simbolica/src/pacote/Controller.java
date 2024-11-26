package pacote;

import java.io.PrintStream;


public class Controller {

    private int nodenum = 50000000;
    private int cachesize =  5000000;
	
    public void Run(ControllerOptions options){
        Setup(options);

        
    }

    public void RunByArgs(String[] args, Runtime runtime, long initmemory) {
        if (args.length != 3) {
           System.err.println("Usage: java GUI <type> <problem> <test>");
           System.exit(1);
        }

        ControllerOptions options = new ControllerOptions(
            ProblemTypeEnum.valueOf(args[0]),
            SearchTypeEnum.valueOf(args[1]),
            Integer.parseInt(args[2]),  
            runtime,
            initmemory
        );

        try {
            Setup(options);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid arguments.");
            System.exit(1);
        }
    }


    private void Setup(ControllerOptions options)  {
        try{
            BaseSearch marisaSearch = new SearchOldMethod();
            runSearchMethod(marisaSearch, options);
    
            // BaseSearch henrickySearch = new SearchNewMethod();
            // runSearchMethod(henrickySearch, options);
        }catch(Exception e){
            System.out.println("Error:" + e);
			System.setOut(GUI.originalOut);
			System.setErr(GUI.originalErr);
			// Imprima a mensagem de conclusão
			System.out.println("Execução terminou [Error].");
        }finally {
            System.gc();
        }
    }


    private String GetFileNameProblem(ControllerOptions options) {
        ProblemTypeEnum problem = options.getProblem();
        int testNumber = options.getTestNumber();

        String fileName;
        switch (problem) {
            case logistics:
                fileName = "LOGISTICS-" + testNumber + "-0-GROUNDED.txt";
                break;
            case rovers:
                fileName = "rovers-0" + testNumber + "-GROUNDED.txt";
                break;
            default:
                throw new AssertionError("Unknown problem type: " + problem);
        }
        return fileName;
    }

    private String GetPath(ControllerOptions options) {
        ProblemTypeEnum problem = options.getProblem();
        String path = "Problems/";
        switch (problem) {
            case logistics:
                path +="logistics/";
                break;
            case rovers:
                path +="rovers/";
                break;
            default:
                throw new AssertionError("Unknown problem type: " + problem);
        }
        return path;
    }


    private void runSearchMethod(BaseSearch search, ControllerOptions options)  throws Exception {
        SearchTypeEnum typeTest= options.getSearch(); 
        Runtime runtime = options.getRuntime();
        long initmemory = options.getInitMemory();

        String filePath = GetPath(options);
		String fileName = GetFileNameProblem(options);
		
		String type = "propplan"; //"ritanen" or "propplan"

        ModelReader model = new ModelReader();


        model.fileReader(filePath+fileName, type, nodenum, cachesize);
		search.SetModel(model);
        System.out.println(fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf(".")));
        TimeManager verify = new TimeManager();
        
        PrintStream out;
        if(typeTest == SearchTypeEnum.exaustive) {
            System.out.println("Exaustive search");
            System.out.println("\n" + "Performing search...");
            out = new PrintStream("exaustiva-"+fileName);
            System.setOut(out);

            verify.setMaxTime(10800000);
            verify.resetStartTime();
            search.ExaustiveSearch(verify);
            verify.PrintElapsedTime();
        }else{
            System.out.println("Heuristic search");
            System.out.println("Performing search...");
            out = new PrintStream("heuristica-"+fileName);
            System.setOut(out);
            
            verify.resetStartTime();
            search.HeuristicSearch(verify);
            verify.PrintElapsedTime();
        }

        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + (memory - initmemory));
        out.close();
        
        System.setOut(GUI.originalOut);
        System.setErr(GUI.originalErr);

        // Imprima a mensagem de conclusão
        System.out.println("Execução terminou [OK].");
    }
	
}
