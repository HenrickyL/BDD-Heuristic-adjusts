package org.ufc.planner.app;

import org.ufc.planner.controller.Controller;
import org.ufc.planner.controller.ProblemOptions;
import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchMethodEnum;
import org.ufc.planner.enums.SearchTypeEnum;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
//        ProblemOptions test = new ProblemOptions(
//                ProblemTypeEnum.rovers,
//                SearchTypeEnum.heuristic,
//                5,
//                 5*60*1000,//-1 // without time
//                SearchMethodEnum.NEW
//        );

        Runtime runtime =  Runtime.getRuntime();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        Controller controller = new Controller(runtime, originalOut, originalErr);
        controller.RunByArgs(args);
//        controller.Run(test);
    }
}