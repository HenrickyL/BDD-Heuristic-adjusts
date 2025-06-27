package org.ufc.planner.controller;

import org.ufc.planner.core.BaseSearch;
import org.ufc.planner.core.SearchNewMethod;
import org.ufc.planner.core.SearchOldMethod;
import org.ufc.planner.core.SearchOldWithTimerMethod;
import org.ufc.planner.enums.SearchTypeEnum;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;
import org.ufc.planner.middleware.DualPrintStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SearchExecutor {
    private final Runtime runtime;
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private final int nodenum = 50000000;
    private final int cachesize = 5000000;

    public SearchExecutor(Runtime runtime, PrintStream originalOut, PrintStream originalErr) {
        this.runtime = runtime;
        this.originalOut = originalOut;
        this.originalErr = originalErr;
    }

    public void execute(ProblemOptions options) throws Exception {
        runComparison("old+", new SearchOldWithTimerMethod(), options);
        runComparison("new-AStar", new SearchNewMethod((g,h)->g+h), options);
        runComparison("new-GBFS", new SearchNewMethod((g,h)->h), options);
        runComparison("old", new SearchOldMethod(), options);
    }

    private void runComparison(String label, BaseSearch search, ProblemOptions options) throws Exception {
        SearchTypeEnum type = options.getSearch();

        String fileName = ProblemFileHelper.getFileName(options.getProblem(), options.getTestNumber());
        String path = ProblemFileHelper.getPath(options.getProblem());

        ModelReader model = new ModelReader();
        model.fileReader(path + fileName, "propplan", nodenum, cachesize);
        search.SetModel(model);

        PrintStream out = prepareOutputFile(type, fileName, label);
        System.setOut(new DualPrintStream(out, originalOut));

        System.out.println("Running " + label.toUpperCase() + " method on: " + fileName);
        TimeManager timer = new TimeManager(runtime);

        int maTime = options.getMaxtime();
        if( maTime != -1){
            timer.setMaxTime(maTime);
        }else{
            timer.setDebugMode(true);
        }

        timer.resetStartTime();
        if (type == SearchTypeEnum.exaustive) {
            search.ExhaustiveSearch(timer);
        } else {
            search.HeuristicSearch(timer);
        }

        timer.PrintElapsedTime();
        runtime.gc();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory (bytes): " + usedMemory);

        out.close();
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.out.println("Execução (" + label + ") terminou [OK].");
    }

    private PrintStream prepareOutputFile(SearchTypeEnum type, String fileName, String label) throws FileNotFoundException {
        File dir = new File("results/" + label);
        if (!dir.exists()) dir.mkdirs();

        String prefix = (type == SearchTypeEnum.exaustive) ? "exaustiva-" : "heuristica-";
        return new PrintStream("results/" + label + "/" + prefix + fileName);
    }
}
