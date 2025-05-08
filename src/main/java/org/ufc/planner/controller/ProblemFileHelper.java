package org.ufc.planner.controller;

import org.ufc.planner.enums.ProblemTypeEnum;

public class ProblemFileHelper {
    public static String getPath(ProblemTypeEnum problem) {
        return switch (problem) {
            case logistics -> "problems/logistics/";
            case rovers -> "problems/rovers/";
            case block_word -> "problems/block-word/";
        };
    }

    public static String getFileName(ProblemTypeEnum problem, int testNumber) {
        return switch (problem) {
            case logistics -> "LOGISTICS-" + testNumber + "-0-GROUNDED.txt";
            case rovers -> "rovers-0" + testNumber + "-GROUNDED.txt";
            case block_word -> "BLOCK-WORD-" + testNumber + "-GROUNDED.txt";
        };
    }
}
