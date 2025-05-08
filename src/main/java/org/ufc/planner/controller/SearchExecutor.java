package org.ufc.planner.controller;

import org.ufc.planner.core.BaseSearch;
import org.ufc.planner.core.SearchOldMethod;
import org.ufc.planner.enums.SearchTypeEnum;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;

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
        BaseSearch search = new SearchOldMethod();
        SearchTypeEnum type = options.getSearch();

        String fileName = ProblemFileHelper.getFileName(options.getProblem(), options.getTestNumber());
        String path = ProblemFileHelper.getPath(options.getProblem());

        ModelReader model = new ModelReader();
        model.fileReader(path + fileName, "propplan", nodenum, cachesize);
        search.SetModel(model);

        PrintStream out = prepareOutputFile(type, fileName);
        System.setOut(out);

        TimeManager timer = new TimeManager();
        if (type == SearchTypeEnum.exaustive) {
            timer.setMaxTime(10800000);
            timer.resetStartTime();
            search.ExaustiveSearch(timer);
        } else {
            timer.resetStartTime();
            search.HeuristicSearch(timer);
        }

        timer.PrintElapsedTime();

        runtime.gc();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + usedMemory);

        out.close();
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.out.println("Execução terminou [OK].");
    }

    private PrintStream prepareOutputFile(SearchTypeEnum type, String fileName) throws FileNotFoundException {
        File dir = new File("results");
        if (!dir.exists()) dir.mkdirs();

        String prefix = (type == SearchTypeEnum.exaustive) ? "exaustiva-" : "heuristica-";
        return new PrintStream("results/" + prefix + fileName);
    }
}
