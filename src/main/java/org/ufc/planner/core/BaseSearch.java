package org.ufc.planner.core;

import org.ufc.planner.domain.ModelAction;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.MetricManager;

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
    protected boolean onHeuristicPlanBackwardHasIncomplateRegression = false;
    private static String version = "1.2.1";

    public void SetModel(ModelReader model){
        this.actionSet = model.getActionSet();
        this.initialState = model.getInitialStateBDD();
        this.goal = model.getGoalSpec();
        this.constraints = model.getConstraints();
        this.numProp = model.getPropNum();
    }

    public void ExhaustiveSearch(MetricManager metric) {
        planForward(metric);
    }

    public void HeuristicSearch(MetricManager metric, long backwardTime, long forwardTime) throws IOException{
        System.out.println("[Version] "+version);
        System.out.println("Start Backward...");
        onHeuristicPlanBackwardHasIncomplateRegression = false;
        metric.setMaxTime(backwardTime);
        metric.reset();
        if(heuristicPlanBackward(metric)) {
            metric.printSummary();
            System.out.println("End Backward.");
            metric.setMaxTime(forwardTime);
            metric.reset();
            System.out.println("Start Forward...");
                heuristicPlanForward(metric);
            System.out.println("End Forward.");
        }
    }

    protected abstract boolean heuristicPlanBackward(MetricManager verify) throws IOException;
    protected abstract boolean heuristicPlanForward(MetricManager verify) throws IOException;


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
    protected boolean planForward(MetricManager metric){
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

            Z = progression(Z, metric);
            Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
            reached = reached.or(Z); //Union with the new reachable states
            reached = reached.and(constraints);
//			if(i < 4){
//				System.out.println(i + "\n" + reached);
//			}
            metric.printElapsedTime();
            if(metric.onTime()) {
                return true;
            }
            i++;
        }

        System.out.println("The problem is unsolvable.");

        return false;
    }



    /* Deterministic Progression of a formula by a set of actions */
    protected BDD progression(BDD formula, MetricManager verify){
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
}
