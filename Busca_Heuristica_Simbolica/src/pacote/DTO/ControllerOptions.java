package pacote.DTO;

import pacote.Enums.ProblemTypeEnum;
import pacote.Enums.SearchTypeEnum;


public class ControllerOptions {
    public ProblemTypeEnum problem = ProblemTypeEnum.rovers;
    public SearchTypeEnum search = SearchTypeEnum.exaustive;

    public ControllerOptions(ProblemTypeEnum problem, SearchTypeEnum search) {
        this.problem = problem;
        this.search = search;
    }
}
