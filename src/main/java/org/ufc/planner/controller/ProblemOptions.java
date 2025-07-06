package org.ufc.planner.controller;

import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchMethodEnum;
import org.ufc.planner.enums.SearchTypeEnum;

public class ProblemOptions {
    private final ProblemTypeEnum problem;
    private final SearchTypeEnum search;
    private final int testNumber;
    private final long backwardTime;
    private final long forwardTime;

    private final SearchMethodEnum searchMethod;

    public ProblemOptions(
            ProblemTypeEnum problem,
            SearchTypeEnum search,
            int testNumber,
            long backwardTime,
            long forwardTime,
            SearchMethodEnum searchMethod
    ) {
        this.problem = problem;
        this.search = search;
        this.testNumber = testNumber;
        this.backwardTime = backwardTime;
        this.forwardTime = forwardTime;
        this.searchMethod = searchMethod;
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
    public long getForwardTime(){return  forwardTime;}
    public long getBackwardTime(){return backwardTime;}
    public SearchMethodEnum getSearchMethod(){return  searchMethod;}

}
