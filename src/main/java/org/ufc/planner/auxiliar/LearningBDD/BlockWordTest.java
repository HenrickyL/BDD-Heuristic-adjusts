package org.ufc.planner.auxiliar.LearningBDD;

import com.github.javabdd.BDD;
import org.ufc.planner.auxiliar.LearningBDD.domain.BDDPlanner;

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
    public static void test1(){
        // Todas as proposições do domínio
        String[] props = {
                "handempty", "holding_a", "holding_b",
                "ontable_a", "ontable_b", "on_a_b", "on_b_a",
                "clear_a", "clear_b"
        };

        BDDPlanner planner = new BDDPlanner();
        planner.initializePropositions(props);

        // Estado inicial (como na sua imagem)
        Set<String> initialStateProps = new HashSet<>(Arrays.asList(
                "handempty", "ontable_a", "on_b_a", "clear_b"
        ));
        BDD initialState = planner.encodeState(initialStateProps);

        print(initialState, "initialState", planner);

        // Estado meta (A sobre B)
        Set<String> goalProps = new HashSet<>(Arrays.asList(
                "handempty", "ontable_b", "on_a_b", "clear_a"
        ));
        BDD goalState = planner.encodeState(goalProps);

        print(goalState, "goalState", planner);

        // Verificando se o estado inicial é meta
        System.out.println("É estado meta? " +
                planner.isGoalState(initialState, goalState));

        /* ACTION */

        // Definindo uma ação (pegar bloco B) Unstack_B_A
        BDD actionPrecond = planner.getVar("handempty")
                .and(planner.getVar("on_b_a"))
                .and(planner.getVar("clear_b"));

        print(actionPrecond, "actionPrecond", planner);

        BDD actionEffects = planner
                //effect add
                .getVar("holding_b")
                .and(planner.getVar("clear_a"))
                //effect del
                .and(planner.getVar("handempty").not())
                .and(planner.getVar("on_b_a").not());

        print(actionEffects, "actionEffects", planner);

        // Aplicando ação
        BDD newState = planner.applyAction(initialState, actionPrecond, actionEffects);

        if (newState != null) {
            System.out.println("Novo estado após pegar bloco A:");
            System.out.println("newState: "+ newState);
            planner.visualizeBDD(newState, "newState");
        }

        // Liberando memória
        initialState.free();
        goalState.free();
        actionPrecond.free();
        actionEffects.free();
        if (newState != null) newState.free();
    }

    public static void test2(){
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

        // Estado meta (A sobre B)
        Set<String> goalProps = Set.of("handempty", "ontable_b", "on_a_b", "clear_a");
        BDD goalState = planner.encodeState(goalProps);

        /* ACTION */
        // Definindo uma ação - Unstack_B_A
        BDD actionPrecond = planner.getVar("handempty")
                .and(planner.getVar("on_b_a"))
                .and(planner.getVar("clear_b"));
        // definindo efeitos
        Set<String> add = Set.of("holding_b", "clear_a");
        Set<String> del = Set.of("handempty", "on_b_a");
        // Aplicando ação
        BDD newState = planner.progressState(initialState, actionPrecond,add, del);

        if (newState != null) {
            System.out.println("Novo estado após pegar bloco A:");
            System.out.println("newState: "+ newState);
            planner.visualizeBDD(newState, "newState");
        }
        // Liberando memória
        initialState.free();
        goalState.free();
        actionPrecond.free();
//        actionEffects.free();
        if (newState != null) newState.free();
    }


}
