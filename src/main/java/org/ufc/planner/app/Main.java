package org.ufc.planner.app;

import org.ufc.planner.controller.Controller;
import org.ufc.planner.controller.ProblemOptions;
import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchMethodEnum;
import org.ufc.planner.enums.SearchTypeEnum;
import org.ufc.planner.infrastructure.MemoryMetricTracker;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
//        ProblemOptions test = new ProblemOptions(
//                ProblemTypeEnum.block_word,
//                SearchTypeEnum.heuristic,
//                3,
//                10*1000,//-1 //
//                5*1000,
//                SearchMethodEnum.NEW
//        );

        MemoryMetricTracker memoryTracker = new MemoryMetricTracker(Runtime.getRuntime());
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        Controller controller = new Controller(memoryTracker, originalOut, originalErr);
        controller.RunByArgs(args);
//        controller.Run(test);
    }
}