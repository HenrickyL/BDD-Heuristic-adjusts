package org.ufc.planner.app;

import org.ufc.planner.controller.Controller;
import org.ufc.planner.controller.ProblemOptions;
import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchTypeEnum;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
//        ProblemOptions test = new ProblemOptions(
//                ProblemTypeEnum.block_word,
//                SearchTypeEnum.heuristic,
//                1
//        );

        Runtime runtime =  Runtime.getRuntime();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        Controller controller = new Controller(runtime, originalOut, originalErr);
//        controller.Run(test);
        controller.RunByArgs(args);
    }
}