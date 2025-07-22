package org.ufc.planner.app;

import org.ufc.planner.controller.Controller;
import org.ufc.planner.controller.ProblemOptions;
import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchMethodEnum;
import org.ufc.planner.enums.SearchTypeEnum;
import org.ufc.planner.infrastructure.MemoryMetricTracker;
import org.ufc.planner.infrastructure.TimeMetricTracker;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
//        ProblemOptions test = new ProblemOptions(
//                ProblemTypeEnum.rovers,
//                SearchTypeEnum.heuristic,
//                6,
//                3*60*1000,//-1 //
//                5*60*1000,
//                SearchMethodEnum.GBFS
//        );
        TimeMetricTracker timeTracker = new TimeMetricTracker(false);


        MemoryMetricTracker memoryTracker = new MemoryMetricTracker(Runtime.getRuntime());
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        Controller controller = new Controller(memoryTracker, originalOut, originalErr);
        controller.RunByArgs(args);
//        controller.Run(test);

        timeTracker.printElapsed();
        memoryTracker.printElapsed();
        memoryTracker.printActualUsage();
        memoryTracker.printTotalGrowth();
    }
}