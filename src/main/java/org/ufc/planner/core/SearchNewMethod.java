package org.ufc.planner.core;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;
import org.ufc.planner.domain.*;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;

import java.util.*;
import java.io.IOException;
import java.util.function.BiFunction;

public class SearchNewMethod extends BaseSearch{
    private boolean onHeuristicPlanBackwardHasIncomplateRegression = false;
    private final BiFunction<Integer, Integer, Integer> fFunc;
    public SearchNewMethod(ModelReader model, BiFunction<Integer, Integer, Integer> fFunc) {
        super(model);
        this.fFunc = fFunc;
        System.out.println("Instance SearchNewMethod");
    }

    public SearchNewMethod(BiFunction<Integer, Integer, Integer> fFunc) {
        super();
        this.fFunc = fFunc;
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
        heuristicValue.add(j, goal);

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
//                System.out.println("END");
                System.out.println("The problem is solvable.");
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
            heuristicValue.add(j,Z);
            reached = reached.or(Z); //Union with the new reachable states
            reached = reached.and(constraints);
            // add variavel global - tratar heuristicRegression retorna incompleto
            // -> se n deu certo: BDDHValues.add(j+1, reached.not())
            if(onHeuristicPlanBackwardHasIncomplateRegression) {
                heuristicValue.add(j+1, reached.not());//todos os estados nao alcancados receberao o mesmo valor heuristico - henricky
                return true;
            }
            //Break by max time
            if(verify.verifyBreak()) {
                return true;
            }
            i++;
        }

        System.out.println("The problem is unsolvable.");
        return false;
    }

    @Override
    protected boolean heuristicPlanForward(TimeManager verify) throws IOException {
        return AStar(verify);
    }



    private boolean AStar(TimeManager verify){
        System.out.println("A* Forward...");
        BDD initial = initialState.id();

        Node node = new Node(initial.id(), null, 0+ heuristicValue.size());// cost = 0 + heuristic

        FrontierQueue frontier = new FrontierQueue();
        ExploredVector explored = new ExploredVector();
        BDD aux;
        BDD current;

        int g=0;
        int h=0;

        frontier.add(node);

        while(!frontier.isEmpty()) {
            node = frontier.poll();
            current = node.getBDD();
            System.out.println(">Dequeue: "+node.getName() + "f: "+node.getFn());

            aux = current.and(goal.id());
            if (aux.toString().equals("") == false) { //use equal?
                System.out.println("The problem is solvable.");
                return true;
            }
            aux.free();
            explored.add(current);

            // Heurística: pega todos os pedaços do estado atual que batem com camadas
            Queue<Pair> heuristicQueue = getMinHeuristic(heuristicValue, current);
            System.out.println(">Pairs Heuristic Queue with size: "+heuristicQueue.size());

            if(heuristicQueue.isEmpty()) continue;
            //pego o melhor
            Pair bestHeuristic = heuristicQueue.poll();
            BDD state = bestHeuristic.getBdd();
            h = bestHeuristic.getHeuristic();
            System.out.println(">best heuristic (And Vector): "+ "_" + " with h: "+h);

            BDD progressedState = progression(state, verify); //Z = progression(teste);
            if(progressedState == null || progressedState.isZero()){
                System.out.println(">progressedState is null");
                progressedState = state;
            }
            System.out.println(">Progress Best State");
            //System.out.println(">progressedState: "+progressedState);
            addInFrontier(progressedState,h, g, node, frontier, explored);

            // Processa os demais estados resultantes da heurística
            if(!heuristicQueue.isEmpty()){
                System.out.println(">Other States of Queue to add("+heuristicQueue.size()+")" );
                while (!heuristicQueue.isEmpty()){
                    Pair p = heuristicQueue.poll();
                    addInFrontier(p.getBdd(), p.getHeuristic(), g, node, frontier, explored);
                }
            }

            g++;
            System.out.println("g="+g);
            //encerra se passar do tempo maximo
            if(verify.verifyBreak()) {
                return true;
            }
        }
        return false;
    }

    private void addInFrontier(BDD progressed, int h, int g, Node parent, FrontierQueue frontier, ExploredVector explored) {
        if (progressed == null || progressed.isZero()) return;
        int f = this.fFunc.apply(g,h);

        if (!explored.contains(progressed) || !frontier.contains(progressed)) {
            Node newNode = new Node(progressed, parent, f);
            frontier.add(newNode);
            System.out.println(">Add Frontier: "+newNode.getName());
        } else if (frontier.contains(progressed)) {
            Node replaced = new Node(progressed, parent, f);
            frontier.replace(replaced);
            System.out.println(">Replace Frontier");
        }
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

    private Queue<Pair> getMinHeuristic(Vector<BDD> H, BDD X) {
        BDD rest = X.id();
        Queue<Pair> queue = new ArrayDeque<>();
        for (int i = 0; i < H.size(); i++) {
            BDD matched = rest.and(H.get(i));
//            matched = matched.and(constraints);
            if (!matched.isZero()) {
                queue.add(new Pair(matched, i));
                rest = rest.and(matched.not()); //A-B := A AND ~B
                continue;
            }
            if (rest.isZero()) {
                break;
            }
            matched.free();
        }
        rest.free();
        return  queue;
    }

    private BDD heuristicRegression(BDD formula, TimeManager verify){
        BDD reg = null;
        BDD test = null;
        for (ModelAction a : actionSet) {
            //System.out.println(a.getName());
            test = heuristicRegressionQbf(formula,a);//heuristicRegressionQbf(formula,a) - henricky;
            //teste = teste.and(constraints);
            if(reg == null){
                reg = test;
            }else{
                reg.orWith(test);
            }
			/*cada camada tem 30 min para rodar, cada açao contribui com esse tempo,
			   se uma açao usa 5 min as outras tem apenas 25min para rodar*/
            if(verify != null && verify.onTime()) {
                onHeuristicPlanBackwardHasIncomplateRegression = true;
                return reg;
            }
        }
        return reg;
    }
}
