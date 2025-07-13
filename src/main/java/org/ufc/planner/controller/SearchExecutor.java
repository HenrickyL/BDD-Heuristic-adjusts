package org.ufc.planner.controller;

import org.ufc.planner.core.BaseSearch;
import org.ufc.planner.core.SearchNewMethod;
import org.ufc.planner.core.SearchOldMethod;
import org.ufc.planner.core.SearchOldWithTimerMethod;
import org.ufc.planner.enums.SearchTypeEnum;
import org.ufc.planner.infrastructure.MemoryMetricTracker;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.MetricManager;
import org.ufc.planner.infrastructure.TimeMetricTracker;
import org.ufc.planner.middleware.DualPrintStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SearchExecutor {
    private final MemoryMetricTracker memoryTracker;
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private final int nodenum = 50000000;
    private final int cachesize = 5000000;

    public SearchExecutor(MemoryMetricTracker memoryTracker, PrintStream originalOut, PrintStream originalErr) {
        this.memoryTracker = memoryTracker;
        this.originalOut = originalOut;
        this.originalErr = originalErr;
    }

    public void execute(ProblemOptions options) throws Exception {
//        switch (options.getSearchMethod()){
//            case NEW -> runComparison("new", new SearchNewMethod(), options);
//            case OLD -> runComparison("old", new SearchOldMethod(), options);
//            case OLD_TIME -> runComparison("old+", new SearchOldWithTimerMethod(), options);
//        }
        runComparison("new", new SearchNewMethod(), options);

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

        long bwTime = options.getBackwardTime();
        long fwTime = options.getForwardTime();

        boolean isDebugMode = bwTime == -1;

        TimeMetricTracker timeTracker = new TimeMetricTracker(isDebugMode);
        MetricManager metric = new MetricManager(timeTracker, memoryTracker);

        metric.reset();
        try{
            if (type == SearchTypeEnum.exaustive) {
                search.ExhaustiveSearch(metric);
            } else {
                search.HeuristicSearch(metric, bwTime, fwTime);
            }
            System.out.println("Execução (" + label + ") terminou [OK].");
            memoryTracker.clear();
            metric.printSummary();
        }catch (OutOfMemoryError e) {
            memoryTracker.clear();
            System.out.println("⚠️ OutOfMemoryError catch!\n"+e);
        }
        catch (Exception e) {
            System.out.println("Execução (" + label + ") terminou [Error].");
            System.out.println("🛑 Erro: "+e);
        }
        out.close();
        System.setOut(originalOut);
        System.setErr(originalErr);
        search.clear();
    }

    private PrintStream prepareOutputFile(SearchTypeEnum type, String fileName, String label) throws FileNotFoundException {
        File dir = new File("results/" + label);
        if (!dir.exists()) dir.mkdirs();

        String prefix = (type == SearchTypeEnum.exaustive) ? "exaustiva-" : "heuristica-";
        return new PrintStream("results/" + label + "/" + prefix + fileName);
    }
}
