package pacote;



public class ControllerOptions {
    private final ProblemTypeEnum problem;
    private final SearchTypeEnum search;
    private final int testNumber;
    private final Runtime runtime;
    private final long initmemory;

    public ControllerOptions(
        ProblemTypeEnum problem,
        SearchTypeEnum search,
        int testNumber,
        Runtime runtime,
        long initmemory
    ) {
        this.problem = problem;
        this.search = search;
        this.testNumber = testNumber;
        this.runtime = runtime;
        this.initmemory = initmemory;
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

    public Runtime getRuntime() {
        return runtime;
    }

    public long getInitMemory() {
        return initmemory;
    }
}

