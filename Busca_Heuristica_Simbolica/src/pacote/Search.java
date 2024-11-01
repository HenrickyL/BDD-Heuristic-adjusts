package pacote;
import java.io.IOException;
import java.util.Vector;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import java.util.PriorityQueue;

import java.util.Iterator;



public class Search{

	Vector<Action> actionSet;		
	BDD goal;
	BDD initialState;
	BDD constraints;
	int numProp;
	Vector<BDD> BDDHValues = new Vector<BDD>();
	private boolean onHeuristicPlanBackwardHasIncomplateRegression = false;

	/* Constructor */
	public Search(ModelReader model) {
		this.actionSet = model.getActionSet();
		this.initialState = model.getInitialStateBDD();
		this.goal = model.getGoalSpec();		
		this.constraints = model.getConstraints();
		this.numProp = model.getPropNum();
	}
	
	public void heuristcSearch(ModelReader model, TimeManager verify) throws IOException {
		verify.setMaxTime(1000*60*1);
		System.out.println("Start Backward...");
		if(heuristicPlanBackward(verify) == true) {
			System.out.println("End Backward.");
			verify.resetStartTime();
			verify.setMaxTime(-1);//3h - 10800000
			System.out.println("Start Forward...");
//			heuristicPlanForward(verify);
//			heuristicPlanForward2(verify);
			heuristicPlanForwardAStar(verify);
		}
	}
	
	/* Forward search from the initial state, towards a goal state. */
	public boolean heuristicPlanForward(TimeManager verify){
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
			Z = progression(teste!=null ? teste : Z, verify); //Z = progression(teste);
						
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
	
	//*********************************
	/* Forward search from the initial state, towards a goal state. */
	public boolean heuristicPlanForward2(TimeManager verify){	
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
	
	private boolean IsThereInExplored(Vector<BDD> explored, BDD item) {
		for(BDD bdd : explored) {
			if(bdd == item) {
				return true;
			}
		}
		return false;
	}
	
	private void UpdateFrontier(PriorityQueue<Node> frontier, BDD Z, int value) {
        BDD matchingElements = Z;
        PriorityQueue<Node> tempQueue = new PriorityQueue<>();
        BDD aux;
        BDD father= null;
        while (!frontier.isEmpty()) {
        	Node current = frontier.poll();
        	int f = current.getFValue();
        	aux = current.bdd;
            if (f == value) {
            	matchingElements = matchingElements.or(aux);
            	father = current.father;
            } else {
                tempQueue.add(current);
            }
        }

        frontier.addAll(tempQueue);
        Node newBdd = new Node(matchingElements, father, value);
        frontier.add(newBdd);
    }
	
	private void ReplaceElementInFrontier(PriorityQueue<Node> frontier, Node element) {
		boolean found = false;
		Iterator<Node> iterator = frontier.iterator();
		
		while (iterator.hasNext()) {
			Node current = iterator.next();
			if (current.bdd == element.bdd) {
				iterator.remove();
				found = true;
				break;
			}
		}
		
		if (found) {
			frontier.add(element);
		}
	}

	
	//*********************************
	public boolean heuristicPlanForwardAStar(TimeManager verify){
		System.out.println("A* Forward...");
		BDD initial = initialState.id();;
		
		Node node = new Node(initial, null, 0+ BDDHValues.size());// 
	    PriorityQueue<Node> frontier = new PriorityQueue<>(new NodeComparator());
	    Vector<BDD> explored = new Vector<BDD>();

		BDD aux;
		BDD current;
		BDD test;
		
		Node child;
		int g=1;
		int h=0;
		
		frontier.add(node);
		while(!frontier.isEmpty()) {
			node = frontier.poll();
			current = node.bdd;
			aux = current.and(goal.id());	
			if (aux.toString().equals("") == false) {
			 	System.out.println("The problem is solvable.");	
			 	return true;
			}
			aux.free();
			explored.add(current);
			
			for (Action a : actionSet) {
				test = progressionQbf(current,a);
				if(test.toString().equals("")) {
					continue; // acao a nao aplicavel ao estado current
				}
				//test = test.and(constraints);
				h = minHvalue2(BDDHValues, test); // if not -1
				System.out.println("h:"+ h+" | "+ a.getName());
				
			
				int f = g+h;
				if( !IsThereInExplored(explored, test) ||
					!frontier.contains(test)) 
				{
					child = new Node(test, current, f);
					frontier.add(child);
				}else if(frontier.contains(test)) {
					Node replaced = new Node(test, current, f);
					ReplaceElementInFrontier(frontier, replaced);
					//se encontrar um bdd com mesmo valor de f e g
					//devo juntar eles
						//UpdateFrontier(frontier, teste, f); //pensar melhor
				}
			}
			g++;
			System.out.println("g="+g);
			verify.PrintElapsedTime();
			

		}
		return false;
	}

	public int minHvalue2(Vector<BDD> H, BDD X) {
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
	
	
	
	/*Consult a tabela de valores heurísticos e 
	 * retorna o subconjunto de estados (BDD result) com menos valor heurístico*/	
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
	
	
	
	public boolean planForward(TimeManager verify){
		System.out.println("initial: " + initialState);
		System.out.println("goal: " + goal);
		BDD reached = initialState.id(); //accumulates the reached set of states.
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;	
		int i = 0;
				
		while(Z.isZero() == false){
			System.out.println(i);
			aux = Z.and(goal.id());	

			 if (aux.toString().equals("") == false) {
				System.out.println("The problem is solvable.");	
			 	return true;
			 }
			
			aux.free();

			Z = progression(Z, verify); 
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
			
			i++;
		}
		
		
		
		System.out.println("The problem is unsolvable.");
		
		return false;
	}
	

		
	/* Deterministic Progression of a formula by a set of actions */
	public BDD progression(BDD formula, TimeManager verify){
		BDD reg = null;	
		BDD teste = null;
		for (Action a : actionSet) {
			teste = progressionQbf(formula,a);
			teste = teste.and(constraints);
			if(reg == null){
				reg = teste;
			}else{
				reg.orWith(teste);
			}
			
			if(verify != null && verify.onTime()) {
				return reg;
			}
		}
		return reg;
	}
	
	/* Propplan progression based on action: Qbf based computation */
	public BDD progressionQbf(BDD Y, Action a) {
		BDD reg;
		reg = Y.and(a.getPrecondition()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){
//			System.out.println("Ação aplicável: " + a.getName());
			reg = reg.exist(a.getChange()); //qbf computation
			reg = reg.and(a.getEffect()); //precondition(a) ^ E changes(a). test
			//reg = reg.and(constraints);
		}
		return  reg;
 	}
	
	
	
	/* Backward search from the goal state, towards initial state. */
	public boolean planBackward(TimeManager verify) throws IOException{
		BDD reached = goal.id(); //accumulates the reached set of states.
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;	
		int i = 0;
				
		while(Z.isZero() == false){
			System.out.println(i);
			
			aux = Z.and(initialState.id());
			
			if (aux.toString().equals("") == false) {
				System.out.println("The problem is solvable.");	
				return true;
			}
			
			
			aux.free();
			
			Z = regression(Z, verify);
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
			reached = reached.or(Z); //Union with the new reachable states
			reached = reached.and(constraints);
			i++;				
		}
		
		System.out.println("The problem is unsolvable.");
		return false;
	}
	
	/*Busca regressiva no problema relaxado (sem efeitos negativos)*/
	/*Ao computar os estados regredidos, colocar os novos estados alcançados em um vetor de BDDs*/
	public boolean heuristicPlanBackward(TimeManager verify) throws IOException{
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
	
	public Vector<BDD> getBDDHValues() {
		return BDDHValues;
	}
	
	
	/* Deterministic Regression of a formula by a set of actions */
	public BDD regression(BDD formula, TimeManager verify){
		BDD reg = null;	
		BDD teste = null;
		for (Action a : actionSet) {
			teste = regressionQbf(formula,a);
			teste = teste.and(constraints);
			if(reg == null){
				reg = teste;
			}else{
				reg.orWith(teste);
			}
			if(verify != null &&verify.onTime()) {
				return reg;
			}
		}
		return reg;
	}
	
	public BDD heuristicRegression(BDD formula, TimeManager verify){
		BDD reg = null;	
		BDD teste = null;
		for (Action a : actionSet) {
			//System.out.println(a.getName());
			teste = regressionQbf(formula,a);//heuristicRegressionQbf(formula,a) - henricky;
			//teste = teste.and(constraints);
			if(reg == null){
				reg = teste;
			}else{
				reg.orWith(teste);
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
	
	/* Propplan regression based on action: Qbf based computation */
	public BDD regressionQbf(BDD Y, Action a) {
		BDD reg;
		reg = Y.and(a.getEffect()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){
//			System.out.println("Ação aplicável: " + a.getName());
			reg = reg.exist(a.getChange()); //qbf computation				
			reg = reg.and(a.getPrecondition()); //precondition(a) ^ E changes(a). test
			reg = reg.and(constraints);
		}
		return  reg;
 	}
	
	/* Calculo regressivo desconsiderando os efeitos negativos das acoes - relaxado (henricky)*/
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
	
	
	
	/***********************************/
	
}

