package pacote;

import java.io.IOException;

public class SearchNewMethod extends BaseSearch {


    public SearchNewMethod(ModelReader model) {
		super(model);
		System.out.println("Instance SearchNewMethod");
	}

    public SearchNewMethod() {
		super();
		System.out.println("Instance SearchNewMethod");
	}


    @Override
    protected boolean heuristicPlanBackward(TimeManager verify) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'heuristicPlanBackward'");
    }

    @Override
    protected boolean heuristicPlanForward(TimeManager verify) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'heuristicPlanForward'");
    }

    
}
