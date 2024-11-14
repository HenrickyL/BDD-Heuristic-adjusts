package pacote;

import java.io.IOException;
import java.util.Vector;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;


public abstract class BaseSearch {
	protected Vector<Action> actionSet;		
	protected BDD goal;
	protected BDD initialState;
	protected BDD constraints;
	protected int numProp;
	protected Vector<BDD> BDDHValues = new Vector<BDD>();
	private boolean onHeuristicPlanBackwardHasIncomplateRegression = false;

	/* Constructor */
	public BaseSearch(ModelReader model) {
		this.actionSet = model.getActionSet();
		this.initialState = model.getInitialStateBDD();
		this.goal = model.getGoalSpec();		
		this.constraints = model.getConstraints();
		this.numProp = model.getPropNum();
	}


	public void ExaustiveSearch() {
		planForward();
	}

	public void HeuristicSearch(TimeManager verify) throws IOException{
		verify.setMaxTime(1000*60*1);
		System.out.println("Start Backward...");
		if(heuristicPlanBackward(verify) == true) {
			System.out.println("End Backward.");
			verify.resetStartTime();
			verify.setMaxTime(-1);//3h - 10800000
			System.out.println("Start Forward...");
			heuristicPlanForward(verify);
			System.out.println("End Forward.");
		}
	}

	protected abstract boolean heuristicPlanBackward(TimeManager verify) throws IOException;
	protected abstract boolean heuristicPlanForward(TimeManager verify) throws IOException;



	/* ---------------------------------------------- */ 
	
	// Forward search from the initial state, towards a goal state.
	// For exaustive execution
	protected boolean planForward(){
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

			Z = progression(Z); 
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
			reached = reached.or(Z); //Union with the new reachable states
			reached = reached.and(constraints);
//			if(i < 4){
//				System.out.println(i + "\n" + reached);
//			}
			i++;
		}
		
		System.out.println("The problem is unsolvable.");
		
		return false;
	}
	

		
	/* Deterministic Progression of a formula by a set of actions */
	protected BDD progression(BDD formula){
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
		}
		return reg;
	}

	/* Deterministic Progression of a formula by a set of actions */
	
	
	/* Propplan progression based on action: Qbf based computation */
	private BDD progressionQbf(BDD Y, Action a) {
		BDD reg;
		reg = Y.and(a.getPrecondition()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){
//			System.out.println("Ação aplicável: " + a.getName());
			reg = reg.exist(a.getChange()); //qbf computation
			reg = reg.and(a.getEffect()); //precondition(a) ^ E changes(a). test
			reg = reg.and(constraints);
		}
		return  reg;
 	}
	
}
