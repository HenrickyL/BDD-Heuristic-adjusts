package org.ufc.planner.core;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;
import org.ufc.planner.domain.ModelAction;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.MetricManager;

import java.io.IOException;
import java.util.Vector;

public class SearchOldWithTimerMethod extends  BaseSearch{
    public SearchOldWithTimerMethod(ModelReader model) {
        System.out.println("Instance SearchOldMethod");
    }

    public SearchOldWithTimerMethod() {
        super();
        System.out.println("Instance SearchOldMethod");

    }


    @Override
    protected boolean heuristicPlanForward(MetricManager metric) throws IOException {
        //	System.out.println("initial: " + initialState);
        //	System.out.println("goal: " + goal);
        BDD reached = initialState.id(); //accumulates the reached set of states.
        BDD Z = reached.id(); // Only new states reached
        BDD aux;
        BDD teste;
        int i = 0;

        System.out.println("Progressive search");

        while(Z.isZero() == false){
            System.out.println("g="+i);
            aux = Z.and(goal.id());

            if (aux.toString().equals("") == false) {
                System.out.println("The problem is solvable.");
                printSummary(metric);
                return true;
            }
            aux.free();

            /*chamar a progressão só para o BDD retornado pela função minHValue*/
            teste = minHvalue(heuristicValue, Z);
            Z = progression(teste, metric); //Z = progression(teste);
            Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
            reached = reached.or(Z); //Union with the new reachable states
            reached = reached.and(constraints);

            //Break by max time
            if(metric.verifyBreak()) {
                printSummary(metric);
                return true;
            }
            i++; //g(n)
        }

        System.out.println("The problem is unsolvable.");
        printSummary(metric);
        return false;
    }

    /* ------------------------------------------------------------------ */

        private BDD minHvalue(Vector<BDD> H, BDD X) {
        BDD result;
        int i = 0;
        while(i < H.size()) {
            result = X.and(H.get(i));
            if(result.isZero() == false) {
                return result;
            }
            ++i;
            result.free();
        }
        return null;
    }
}
