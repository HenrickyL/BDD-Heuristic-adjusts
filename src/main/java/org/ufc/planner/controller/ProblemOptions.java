package org.ufc.planner.controller;

import org.ufc.planner.enums.ProblemTypeEnum;
import org.ufc.planner.enums.SearchMethodEnum;
import org.ufc.planner.enums.SearchTypeEnum;

public class ProblemOptions {
    private final ProblemTypeEnum problem;
    private final SearchTypeEnum search;
    private final int testNumber;
    private final int maxTime;
    private final SearchMethodEnum searchMethod;

    public ProblemOptions(
            ProblemTypeEnum problem,
            SearchTypeEnum search,
            int testNumber,
            int maxTime,
            SearchMethodEnum searchMethod
    ) {
        this.problem = problem;
        this.search = search;
        this.testNumber = testNumber;
        this.maxTime = maxTime;
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
    public int getMaxtime(){return  maxTime;}
    public SearchMethodEnum getSearchMethod(){return  searchMethod;}

}
