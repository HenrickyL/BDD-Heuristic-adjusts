package org.ufc.planner.controller;

import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchMethodEnum;
import org.ufc.planner.enums.SearchTypeEnum;
import org.ufc.planner.infrastructure.MemoryMetricTracker;

import java.io.PrintStream;
import java.util.Objects;

public class Controller {
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private MemoryMetricTracker memoryTracker;

    public Controller(MemoryMetricTracker memoryTracker, PrintStream originalOut, PrintStream originalErr){
        this.memoryTracker = memoryTracker;
        this.originalOut = originalOut;
        this.originalErr = originalErr;
    }

    public void Run(ProblemOptions options){
        try {
            SearchExecutor executor = new SearchExecutor(memoryTracker, originalOut, originalErr);
            executor.execute(options);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.setOut(originalOut);
            System.setErr(originalErr);
            System.out.println("Execução terminou [Error].");
        } finally {
            System.gc();
        }
    }

    public void RunByArgs(String[] args) {
        if (args.length != 6 ) {
            System.err.println("Usage: java GUI <problem> <search:heuristic> <test> <backwardTime> <forwardTime> <searchMethod>");
            System.exit(1);
        }

        try {
            ProblemOptions options = new ProblemOptions(
                    ProblemTypeEnum.valueOf(args[0]),
                    SearchTypeEnum.valueOf(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]),
                    SearchMethodEnum.valueOf(args[5])
            );
            Run(options);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid arguments.");
            System.exit(1);
        }
    }
}
