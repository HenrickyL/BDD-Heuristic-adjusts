package org.ufc.planner.core;

import org.ufc.planner.domain.ModelAction;
import org.ufc.planner.domain.Node;
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
    private static String version = "1.2.2";

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
        System.out.println("> Backward max time: "+backwardTime);
        System.out.println("> Forward max time: "+forwardTime);
        onHeuristicPlanBackwardHasIncomplateRegression = false;
        metric.setMaxTime(backwardTime);
        metric.reset();
        if(heuristicPlanBackward(metric)) {
            System.out.println("End Backward.");
            metric.setMaxTime(forwardTime);
            printSummary(metric);
            System.out.println("Start Forward...");
                heuristicPlanForward(metric);
            System.out.println("End Forward.");
        }
    }

    protected abstract boolean heuristicPlanForward(MetricManager verify) throws IOException;
    protected boolean heuristicPlanBackward(MetricManager metric) throws IOException{
        System.out.println("initial: " + initialState);
        System.out.println("goal: " + goal);

        int layer = 0; // índice padronizado
        BDD reached = goal.id(); // estados já alcançados
        heuristicValue.add(layer, goal);

        BDD Z = reached.id(); // estados novos
        BDD aux;

        System.out.println("Heuristic computation (backward):");

        while (!Z.isZero()) {
            System.out.printf("Layer %d: H=%d\n", layer, heuristicValue.size());

            aux = Z.and(initialState.id());
            if (!aux.isZero()) {
                aux.free();
                System.out.println("✅The problem is solvable by backward search.");
                printSummary(metric);
                return true;
            }
            aux.free();

            Z = heuristicRegressionWithBreakTime(Z, metric); // computa camada seguinte
            Z = Z.apply(reached, BDDFactory.diff); // remove os já alcançados

            reached = reached.or(Z).and(constraints);
            layer++;

            heuristicValue.add(layer, Z);

            if (onHeuristicPlanBackwardHasIncomplateRegression) {
                heuristicValue.add(layer + 1, reached.not()); // marca os inalcançáveis
                printSummary(metric);
                return true;
            }

            metric.printElapsedTime();
        }

        System.out.println("# The problem is unsolvable (backward).");
        printSummary(metric);
        return false;
    }


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

    protected void printSummary(MetricManager metric){
        System.out.println("[Search Summary] -----------------");
        metric.printSummary();
        if(this instanceof SearchNewMethod){
            System.out.println("Node Created: " + Node.nodeCount());
        }
        metric.reset();
        System.out.println("----------------------------------");
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
    protected BDD progression(BDD formula, MetricManager metric){
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
            if(metric.onTime()) {
                System.out.println("-🛑[BREAK Intern] exceeded max time - "+ metric.getMaxTime() + " ms");
                return reg;
            }
        }
        return reg;
    }

    /* Deterministic Progression of a formula by a set of actions */


    /* Propplan progression based on action: Qbf based computation */
    protected BDD progressionQbf(BDD Y, ModelAction a) {
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

    protected BDD heuristicRegressionWithBreakTime(BDD formula, MetricManager metric){
        BDD reg = null;
        BDD teste = null;
        for (ModelAction a : actionSet) {
            //System.out.println(a.getName());
            teste = heuristicRegressionQbf(formula,a);
            teste = teste.and(constraints);
            if(reg == null){
                reg = teste;
            }else{
                reg.orWith(teste);
            }
            if(metric.onTime()) {
                onHeuristicPlanBackwardHasIncomplateRegression = true;
                System.out.println("-🛑[BREAK Intern] exceeded max time - "+ metric.getMaxTime() + " ms");
                return reg;
            }
        }
        return reg;
    }
}
