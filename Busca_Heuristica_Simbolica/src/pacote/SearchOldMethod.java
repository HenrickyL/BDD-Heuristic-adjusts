package pacote;

import java.io.IOException;
import java.util.Vector;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

// Marisa implementations to Heuristic Search
public class SearchOldMethod extends BaseSearch {

    public SearchOldMethod(ModelReader model) {
		super(model);
		System.out.println("Instance SearchOldMethod");
	}

	public SearchOldMethod() {
		super();
		System.out.println("Instance SearchOldMethod");

	}

    @Override
    protected boolean heuristicPlanBackward(TimeManager verify) throws IOException {
        //System.out.println("Performing heuristic search in a relaxed problem");
		System.out.println("initial: " + initialState);
		System.out.println("goal: " + goal);
		
		int j = 0;
		BDD reached = goal.id(); //accumulates the reached set of states.
		BDDHValues.add(j, goal);
		
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;	
		int i = 1;
		System.out.println("Heuristic computation");
		
		while(Z.isZero() == false){
			//System.out.println(BDDHValues);
			j++; //index do vetor de BDDs com valor heurístico
			System.out.println(i); 
			
			aux = Z.and(initialState.id());
			
			if (aux.toString().equals("") == false) {
				System.out.println("END");
				//System.out.println("The problem is solvable.");	
				return true;
			}
			
			aux.free();
			//System.out.println("Z [antes da regression]" + Z);
			Z = heuristicRegression(Z);
			//System.out.println("Z-->" + Z);
		    //System.out.println("Z [depois da regression]" + Z);
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
			//adicionar o Z na posição i do vetor.
			//System.out.println("Z-->" + Z);
			BDDHValues.add(j,Z);
			
			reached = reached.or(Z); //Union with the new reachable states
			reached = reached.and(constraints);
//			if(i < 4){
//				System.out.println(reached);
//			}
			i++;
			
		}
		
		System.out.println("The problem is unsolvable.");
		return false;
    }

    @Override
    protected boolean heuristicPlanForward(TimeManager verify) throws IOException {
        //	System.out.println("initial: " + initialState);
	//	System.out.println("goal: " + goal);
		BDD reached = initialState.id(); //accumulates the reached set of states.
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;
		BDD teste;
		int i = 0;
		
		System.out.println("Progressive search");
				
		while(Z.isZero() == false){
			System.out.println(i);
			aux = Z.and(goal.id());	
	

			 if (aux.toString().equals("") == false) {
			 	System.out.println("The problem is solvable.");	
			 	return true;
			 }			
			aux.free();

			/*chamar a progressão só para o BDD retornado pela função minHValue*/
			teste = minHvalue(BDDHValues, Z);
			Z = progression(teste, verify); //Z = progression(teste);
						
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
			reached = reached.or(Z); //Union with the new reachable states
			reached = reached.and(constraints);
//			if(i < 4){
//				System.out.println(i + "\n" + reached);
//			}
			i++; //g(n)
		}
		
		
		
		
		System.out.println("The problem is unsolvable.");
		
		return false;
    }

    /* ------------------------------------------------------------------ */

    private BDD heuristicRegression(BDD formula){
		BDD reg = null;	
		BDD teste = null;
		for (Action a : actionSet) {
			//System.out.println(a.getName());
			teste = heuristicRegressionQbf(formula,a);
			teste = teste.and(constraints);
			if(reg == null){
				reg = teste;
			}else{
				reg.orWith(teste);
			}	
		}
		return reg;
	}

    public BDD heuristicRegressionQbf(BDD Y, Action a) {
		BDD reg;
		reg = Y.and(a.getRelaxEffect()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){
//		System.out.println("Ação aplicável: " + a.getName());
//		System.out.println("precondição: " + a.getPrecondition());
//		System.out.println("efeitos" + a.getEffect());
			reg = reg.exist(a.getRelaxChange()); //qbf computation	
			reg = reg.and(a.getPrecondition()); //precondition(a) ^ E changes(a). test
			//System.out.println(reg + "\n");
			reg = reg.and(constraints);
			
			}
		return  reg;
 	}

    // FORWARD

    public BDD minHvalue(Vector<BDD> H, BDD X) {
		BDD result;
		int i = 0;
		while(i < H.size()) {
			result = X.and(H.get(i)); 
			if(result.isZero() == false) {
				return result;
			}
			++i;
		}
		return null;
	}
	

}
