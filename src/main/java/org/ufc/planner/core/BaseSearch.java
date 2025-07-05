package org.ufc.planner.core;

import org.ufc.planner.domain.ModelAction;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;

import java.io.IOException;
import java.util.Vector;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;
import org.ufc.planner.interfaces.ISearchAlgorithm;


public abstract class BaseSearch implements ISearchAlgorithm {
    protected Vector<ModelAction> actionSet;
    protected BDD goal;
    protected BDD initialState;
    protected BDD constraints;
    protected int numProp;
    protected Vector<BDD> heuristicValue = new Vector<BDD>();
    protected  boolean exceededTime = false;
    protected boolean onHeuristicPlanBackwardHasIncomplateRegression = false;
    private static float version = 1.3f;

    /* Constructor */
    public BaseSearch(ModelReader model) {
        SetModel(model);
    }

    public BaseSearch() {
    }

    public void SetModel(ModelReader model){
        this.actionSet = model.getActionSet();
        this.initialState = model.getInitialStateBDD();
        this.goal = model.getGoalSpec();
        this.constraints = model.getConstraints();
        this.numProp = model.getPropNum();
    }

    public void ExhaustiveSearch(TimeManager verify) {
        planForward(verify);
    }

    public void HeuristicSearch(TimeManager verify) throws IOException{
        System.out.println("[Version] "+version);
        System.out.println("Start Backward...");
        onHeuristicPlanBackwardHasIncomplateRegression = false;
        exceededTime= false;
        if(heuristicPlanBackward(verify) == true) {
            verify.PrintElapsedTime();
            System.out.println("End Backward.");
            verify.resetStartTime();
//            verify.setMaxTime(-1);//3h - 10800000
            System.out.println("Start Forward...");
            heuristicPlanForward(verify);
            System.out.println("End Forward.");
        }
    }

    protected abstract boolean heuristicPlanBackward(TimeManager verify) throws IOException;
    protected abstract boolean heuristicPlanForward(TimeManager verify) throws IOException;


    public void clear(){
        clearHeuristicValues();
//        initialState.free();
//        goal.free();
    }

    private void clearHeuristicValues(){
        for(BDD bdd :heuristicValue){
            bdd.free();
        }
    }

    /* ---------------------------------------------- */

    // Forward search from the initial state, towards a goal state.
    // For exaustive execution
    protected boolean planForward(TimeManager verify){
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
    protected BDD progression(BDD formula, TimeManager verify){
        BDD reg = null;
        BDD teste = null;
        for (ModelAction a : actionSet) {
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

    /* Deterministic Progression of a formula by a set of actions */


    /* Propplan progression based on action: Qbf based computation */
    private BDD progressionQbf(BDD Y, ModelAction a) {
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

    protected BDD heuristicRegressionQbf(BDD Y, ModelAction a) {
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

    protected boolean CheckToBreak(TimeManager verify, long totalTime){
        if(verify.verifyBreak()) {
            if(!exceededTime){
                exceededTime = true;
                long time = verify.getMaxTime();
                long rest = totalTime - time;
                verify.setMaxTime(rest);
                System.out.println(">>> Exceeded Time MAX "+time/60/1000+"min [increase "+rest/60/1000+"min] --------");
            }else{
                System.out.println(">>> Exceeded Time total MAX "+totalTime/60/1000+"min --------");
                onHeuristicPlanBackwardHasIncomplateRegression = true;
                return true;
            }
        }
        return false;
    }
}
