package pacote;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Vector;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class SearchNewMethod extends BaseSearch {
	private boolean onHeuristicPlanBackwardHasIncomplateRegression = false;

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
        onHeuristicPlanBackwardHasIncomplateRegression = false;
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
			Z = heuristicRegression(Z, verify);
			//System.out.println("Z-->" + Z);
		    //System.out.println("Z [depois da regression]" + Z);
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
			//adicionar o Z na posição i do vetor.
			//System.out.println("Z-->" + Z);
			BDDHValues.add(j,Z);
			reached = reached.or(Z); //Union with the new reachable states
			reached = reached.and(constraints);
			// add variavel global - tratar heuristicRegression retorna incompleto
			 // -> se n deu certo: BDDHValues.add(j+1, reached.not())
			if(onHeuristicPlanBackwardHasIncomplateRegression) {
				BDDHValues.add(j+1, reached.not());//todos os estados nao alcancados receberao o mesmo valor heuristico - henricky
				return true;
			}
	
			
			verify.PrintElapsedTime();
			verify.resetStartTime();
//			if(verify != null && verify.onTime()) {
//				return true;
//			}
			i++;
        }
		
		System.out.println("The problem is unsolvable.");
		return false;
    }

    @Override
    protected boolean heuristicPlanForward(TimeManager verify) throws IOException {
        BDD reached = initialState.id(); //accumulates the reached set of states.
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;
		BDD teste;
		
		
		
		int i = 0; // pode funcionar como g - henricky
		//fila de prioridade de bdds com valor f = g + h
		//falta descobrir o valor de h para cada bdd na camada z
		
	    PriorityQueue<Node> frontier = new PriorityQueue<>(new NodeComparator());
	    frontier.add(new Node(Z,null, BDDHValues.size())); // Inicializa a fila de prioridade com g = 0
		//h(n) do estado inicial é obtido através do vetor BDDHValues, onde o ultimo elemento é o mais proximo do inicial
		//g(n) do estado inicial é zero
		System.out.println("Progressive search");
				
		while(Z.isZero() == false){
			System.out.println(i);
			
			aux = Z.and(goal.id());	
	

			 if (aux.toString().equals("") == false) {
			 	System.out.println("The problem is solvable.");	
			 	return true;
			 }			
			aux.free();
			/*1. descobrir o valor de z
			 *2.
			 * */
			/*chamar a progressão só para o BDD retornado pela função minHValue*/
			
			// Colocar minHvalue 
			minHvalue2(BDDHValues, Z);
			Node BddHeuristic = frontier.poll();
			teste = BddHeuristic.bdd.id();
			Z = progression(teste != null? teste : Z, verify); //Z = progression(teste);
						
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
			reached = reached.or(Z); //Union with the new reachable states
			reached = reached.and(constraints);
//			if(i < 4){
//				System.out.println(i + "\n" + reached);
//			}
			verify.PrintElapsedTime();
			if(verify != null && verify.onTime()) {
				return true;
			}
			
			i++; //g(n)
		}
		System.out.println("The problem is unsolvable.");
		
		return false;
    }

    private int minHvalue2(Vector<BDD> H, BDD X) {
		BDD result;	
		for (int i = 0; i < H.size(); i++) {
	        result = X.and(H.get(i));
	        //System.out.println("i: "+i);
	        //H.get(i).printSet();
	        if (!result.isZero()) {
	        	return i;
	        }
	    }
		return -1;
	}

    public BDD heuristicRegression(BDD formula, TimeManager verify){
		BDD reg = null;	
		BDD test = null;
		for (Action a : actionSet) {
			//System.out.println(a.getName());
			test = regressionQbf(formula,a);//heuristicRegressionQbf(formula,a) - henricky;
			//teste = teste.and(constraints);
			if(reg == null){
				reg = test;
			}else{
				reg.orWith(test);
			}
			/*cada camada tem 30 min para rodar, cada açao contribui com esse tempo,
			   se uma açao usa 5 min as outras tem apenas 25min para rodar*/
			if(verify != null &&verify.onTime()) {
				onHeuristicPlanBackwardHasIncomplateRegression = true;
				return reg;
			}
		}
		return reg;
	}

    public BDD regressionQbf(BDD Y, Action action) {
		BDD reg;
		reg = Y.and(action.getEffect()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){
//			System.out.println("Ação aplicável: " + a.getName());
			reg = reg.exist(action.getChange()); //qbf computation				
			reg = reg.and(action.getPrecondition()); //precondition(a) ^ E changes(a). test
			reg = reg.and(constraints);
		}
		return  reg;
 	}
    
}
