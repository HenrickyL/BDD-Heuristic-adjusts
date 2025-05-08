package org.ufc.planner.controller;

import org.ufc.planner.core.BaseSearch;
import org.ufc.planner.core.SearchOldMethod;
import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchTypeEnum;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Controller {
    private final int nodenum = 50000000;
    private final int cachesize =  5000000;
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private final Runtime runtime;
    private final long initmemory;

    public Controller(Runtime runtime, PrintStream originalOut, PrintStream originalErr){
        this.runtime = runtime;
        this.initmemory = runtime.totalMemory() - runtime.freeMemory();
        this.originalOut = originalOut;
        this.originalErr = originalErr;
    }

    public void Run(ProblemOptions options){
        Setup(options);


    }

    public void RunByArgs(String[] args, Runtime runtime, long initmemory) {
        if (args.length != 3) {
            System.err.println("Usage: java GUI <type> <problem> <test>");
            System.exit(1);
        }

        ProblemOptions options = new ProblemOptions(
                ProblemTypeEnum.valueOf(args[0]),
                SearchTypeEnum.valueOf(args[1]),
                Integer.parseInt(args[2])
        );

        try {
            Setup(options);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid arguments.");
            System.exit(1);
        }
    }


    private void Setup(ProblemOptions options)  {
        try{
            BaseSearch marisaSearch = new SearchOldMethod();
            runSearchMethod(marisaSearch, options);

//            BaseSearch henrickySearch = new SearchNewMethod();
//            runSearchMethod(henrickySearch, options);
        }catch(Exception e){
            System.out.println("Error:" + e);
            System.setOut(this.originalOut);
            System.setErr(this.originalErr);
            // Imprima a mensagem de conclusão
            System.out.println("Execução terminou [Error].");
        }finally {
            System.gc();
        }
    }


    private String GetFileNameProblem(ProblemOptions options) {
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
            case block_word:
                fileName = "BLOCK-WORD-" + testNumber + "-GROUNDED.txt";
                break;
            default:
                throw new AssertionError("Unknown problem type: " + problem);
        }
        return fileName;
    }

    private String GetPath(ProblemOptions options) {
        ProblemTypeEnum problem = options.getProblem();
        String folder = switch (problem){
            case logistics -> "logistics/";
            case rovers -> "rovers/";
            case block_word -> "block-word/";
            default -> throw new AssertionError("Unknown problem type: " + problem);
        };
        return "problems/" + folder;
    }

    static PrintStream GetPrintStream(String path) throws FileNotFoundException {
        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }
        return new PrintStream("results/"+path);
    }

    private void runSearchMethod(BaseSearch search, ProblemOptions options)  throws Exception {
        SearchTypeEnum typeTest= options.getSearch();

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
            out = GetPrintStream("exaustiva-"+fileName);
            System.setOut(out);

            verify.setMaxTime(10800000);
            verify.resetStartTime();
            search.ExaustiveSearch(verify);
            verify.PrintElapsedTime();
        }else{
            System.out.println("Heuristic search");
            System.out.println("Performing search...");
            out = GetPrintStream("heuristica-"+fileName);
            System.setOut(out);

            verify.resetStartTime();
            search.HeuristicSearch(verify);
            verify.PrintElapsedTime();
        }

        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + (memory - initmemory));
        out.close();

        System.setOut(this.originalOut);
        System.setErr(this.originalErr);

        // Imprima a mensagem de conclusão
        System.out.println("Execução terminou [OK].");
    }
}
