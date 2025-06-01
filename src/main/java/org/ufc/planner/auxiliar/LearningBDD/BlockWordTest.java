package org.ufc.planner.auxiliar.LearningBDD;

import com.github.javabdd.BDD;
import org.ufc.planner.auxiliar.LearningBDD.domain.Action;
import org.ufc.planner.auxiliar.LearningBDD.domain.BDDPlanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockWordTest {
    private BlockWordTest(){}

    public static void basicBDDExample(){
        BDDPlanner planner = new BDDPlanner();
        String[] props = {"on_a_b", "on_b_a", "ontable_a", "clear_a", "handempty"};
        planner.initializePropositions(props);

        // Criando expressões simples
        BDD expr1 = planner.getVar("on_a_b").and(planner.getVar("on_b_a").not());
        BDD expr2 = planner.getVar("handempty").or(planner.getVar("clear_a"));

        System.out.println("Expr1: " + expr1);
        System.out.println("Expr2: " + expr2);

        planner.visualizeBDD(expr1, "expr1");
        planner.visualizeBDD(expr2, "expr2");

        // Liberando memória
        expr1.free();
        expr2.free();
    }

    public static void exampleEncodeState(){
        String[] allProps = {
                "handempty",
                "holding_a",
                "holding_b",
                "ontable_a",
                "ontable_b",
                "on_a_b",
                "on_b_a",
                "clear_a",
                "clear_b"
        };

        BDDPlanner planner = new BDDPlanner();
        planner.initializePropositions(allProps);

        Set<String> L = new HashSet<>(Arrays.asList( //0 3 6 8
                "handempty", "ontable_a", "on_b_a", "clear_b"
        ));

        BDD initialState = planner.encodeState(L);
        System.out.println("Estado inicial codificado:");
        System.out.println("initialState: " + initialState);
        planner.visualizeBDD(initialState, "initialState");

        initialState.free();
    }

    public static void testGoal(){
        BDDPlanner planner = new BDDPlanner();
        String[] props = {"on_a_b", "on_b_a", "ontable_a", "clear_a", "handempty"};
        planner.initializePropositions(props);

        // Criando expressões simples
        BDD expr1 = planner.getVar("on_a_b").and(planner.getVar("on_b_a").not());
        BDD expr2 = planner.getVar("handempty").or(planner.getVar("clear_a"));

        boolean a = planner.isGoalState(expr1, expr1);
        boolean b = planner.isGoalState(expr1, expr2);
        System.out.println("A: " + a);
        System.out.println("B: " + b);
    }
    private static void print(BDD bdd, String title,  BDDPlanner planner){
        System.out.println(">> "+title+ ": " + bdd);
        planner.visualizeBDD(bdd, title);
    }

    public static void testProgression(){
        // Todas as proposições do domínio
        String[] props = {
                "handempty", "holding_a", "holding_b",
                "ontable_a", "ontable_b", "on_a_b", "on_b_a",
                "clear_a", "clear_b"
        };
        BDDPlanner planner = new BDDPlanner();
        planner.initializePropositions(props);

        // Estado inicial (como na sua imagem)
        Set<String> initialStateProps = Set.of("handempty", "ontable_a", "on_b_a", "clear_b");
        BDD initialState = planner.encodeState(initialStateProps);
        planner.visualizeBDD(initialState, "initialStateProps");

        // Estado meta (A sobre B)
        Set<String> goalProps = Set.of("handempty", "ontable_b", "on_a_b", "clear_a");
        BDD goalState = planner.encodeState(goalProps);

        /* ACTION */
        // Definindo uma ação - Unstack_B_A
        String[] actionPrecond = {"handempty", "on_b_a",  "clear_b"};
        // definindo efeitos
        String[] add = {"holding_b", "clear_a"};
        String[] del = {"handempty", "on_b_a"};
        Action action = new Action("unstack_B_A", planner,actionPrecond, add, del);

        // Aplicando ação
        BDD newState = planner.progressState(initialState, action, false);

        if (newState != null) {
            System.out.println("Novo estado após pegar bloco A:");
            System.out.println("newState: "+ newState);
            planner.visualizeBDD(newState, "newState");
        }
        // Liberando memória
        initialState.free();
        goalState.free();
//        actionEffects.free();
        if (newState != null) newState.free();
    }


}
