package org.ufc.planner.controller;

import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchTypeEnum;

public class ProblemOptions {
    private final ProblemTypeEnum problem;
    private final SearchTypeEnum search;
    private final int testNumber;


    public ProblemOptions(
            ProblemTypeEnum problem,
            SearchTypeEnum search,
            int testNumber
    ) {
        this.problem = problem;
        this.search = search;
        this.testNumber = testNumber;

    }

    public ProblemTypeEnum getProblem() {
        return problem;
    }

    public SearchTypeEnum getSearch() {
        return search;
    }

    public int getTestNumber() {
        return testNumber;
    }
}
