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
    private long maxTotalTime = 30*60*1000;
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
        boolean AStar = false;
        boolean GBFS = false;
        System.out.println("--- A* Forward  f=g+h ----------------------");
        try{
            AStar = ForwardMethod((g,h)->g+h,verify);
        } catch (Exception e) {
            System.out.println("# A* Error");
        }
        verify.resetStartTime();
        verify.PrintElapsedTime();
        System.out.println("--- GBFS Forward f=h -----------------------");
        GBFS = ForwardMethod((g,h)->h,verify);
        return AStar && GBFS;
    }



    private boolean ForwardMethod(BiFunction<Integer, Integer, Integer> fFunc, TimeManager verify){
        BDD initial = initialState.id();

        Node node = new Node(initial.id(), 0+ heuristicValue.size());// cost = 0 + heuristic

        FrontierQueue frontier = new FrontierQueue();
        ExploredVector explored = new ExploredVector();
        BDD aux;
        BDD current;

        int g=0;
        int h=0;
        int f=0;

        frontier.add(node);

        while(!frontier.isEmpty()) {
            System.out.println("g="+g);
            node = frontier.poll();
            current = node.getBDD();
            System.out.println(">Dequeue: "+node.getName() + "_f: "+node.getFn());

            aux = current.and(goal.id());
            if (aux.toString().equals("") == false) { //use equal?
                System.out.println("The problem is solvable.");
                clearBdds(frontier, explored);
                return true;
            }
            aux.free();
            explored.add(node);

            // Heurística: pega todos os pedaços do estado atual que batem com camadas
            Queue<Pair> heuristicQueue = splitByHeuristic(heuristicValue, current);
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
            f = fFunc.apply(g,h);
            addInFrontier(progressedState,f, node, frontier, explored);

            // Processa os demais estados resultantes da heurística
            if(!heuristicQueue.isEmpty()){
                System.out.println(">Other States of Queue to add("+heuristicQueue.size()+")" );
                while (!heuristicQueue.isEmpty()){
                    Pair p = heuristicQueue.poll();
                    f = fFunc.apply(g,p.getHeuristic());
                    addInFrontier(p.getBdd(),f, node, frontier, explored);
                }
            }

            g++;
            System.out.println(frontier.getSizeSummary());
            System.out.println(explored.getSizeSummary());
            //encerra se passar do tempo maximo
            if(CheckToBreak(verify,maxTotalTime)) {
                return true;
            }
        }
        clearBdds(frontier, explored);
        return false;
    }

    private void clearBdds(FrontierQueue frontier, ExploredVector explored){
        frontier.clear();
        explored.clear();
    }

    private void addInFrontier(BDD progressed, int f, Node parent, FrontierQueue frontier, ExploredVector explored) {
        if (progressed == null || progressed.isZero()) return;
        Node newNode = new Node(progressed, f, parent);
        if (!explored.contains(newNode) || !frontier.contains(newNode)) {
            frontier.add(newNode);
            System.out.println(">Add Frontier: "+newNode.getName());
        } else if (frontier.replace(newNode)) {
            System.out.println(">Replace Frontier: "+newNode.getName());
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

    private Queue<Pair> splitByHeuristic(Vector<BDD> heuristicLayers, BDD state) {
        BDD rest = state.id(); //copia
        Queue<Pair> queue = new ArrayDeque<>();
        for (int h = 0; h < heuristicLayers.size(); h++) {
            BDD matched = rest.and(heuristicLayers.get(h));
            if (!matched.isZero()) {
                queue.add(new Pair(matched.id(), h));
                BDD neg = matched.not(); // A - B := A ∧ ¬B
                BDD updated = rest.and(neg);
                rest.free(); neg.free(); // libera antigos
                rest = updated;
            }else{
                matched.free(); //libera
            }
            if (rest.isZero()) break;
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
