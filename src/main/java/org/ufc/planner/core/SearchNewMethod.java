package org.ufc.planner.core;

import com.github.javabdd.BDD;
import org.ufc.planner.domain.*;
import org.ufc.planner.infrastructure.MetricManager;

import java.util.*;
import java.io.IOException;
import java.util.function.BiFunction;

public class SearchNewMethod extends BaseSearch{
    private Node result;
    private final String name;
    public SearchNewMethod(String name,BiFunction<Integer, Integer, Integer> fFunc) {
        super();
        this.name = name;
        Node.setFn(fFunc);
        System.out.println("Instance SearchNewMethod");
    }

//    @Override
//    protected boolean heuristicPlanForward(MetricManager metric) throws IOException {
//        boolean AStar = false;
//        boolean GBFS = false;
//        System.out.println("--- A* Forward  f=g+h ----------------------");
//        try{
//            AStar = ForwardMethod((g,h)->g+h,metric);
//        }catch (OutOfMemoryError e) {
//            metric.clear();
//            System.out.println("⚠️ OutOfMemoryError catch!\n"+e.toString());
//        } catch (Exception e) {
//            System.out.println("🛑 A* Error\n"+e.toString());
//        }
//        System.out.println("--- END ----------------------");
////        metric.printSummary();
//        printSummary(metric);
//        System.out.println("--- GBFS Forward f=h -----------------------");
//        GBFS = ForwardMethod((g,h)->h,metric);
////        System.out.println("Node Created: " + Node.nodeCount());
//        return AStar && GBFS;
//    }

@Override
protected boolean heuristicPlanForward(MetricManager metric) throws IOException {
    System.out.println("--- "+this.name+" Forward -----------------------");
    return ForwardMethod(metric);
}


    private boolean ForwardMethod(MetricManager metric){
        BDD initial = initialState.id();

        Node node = new Node(initial.id(), 0, heuristicValue.size());// cost = 0 + heuristic

        FrontierQueue frontier = new FrontierQueue();
        ExploredVector explored = new ExploredVector();
        BDD aux;
        BDD current;

        int count=1;
        int h=0;
        int g=0;

        frontier.add(node);

        while(!frontier.isEmpty()) {
            node = frontier.poll();
            current = node.getBDD();
            System.out.println("g="+node.getG());
            System.out.println("count="+count);
            String nodeLabel = node.getName() +"_("
                    +"f: "+node.getFn()
                    +", g: "+node.getG()
                    +", h: "+node.getH()
                    + ")";
            System.out.println(">Dequeue: "+nodeLabel);

            aux = current.and(goal.id());
            if (aux.toString().equals("") == false) { //use equal?
                System.out.println("✅ The problem is solvable forward.");
                result=node;
                printSummary(metric);
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
            System.out.println(">H: "+ h + " - best heuristic (And Vector)");

            BDD progressedState = progression(state, metric); //Z = progression(teste);
            if(progressedState == null || progressedState.isZero()){
                System.out.println(">progressedState is null");
                progressedState = state;
            }
            System.out.println(">Progress Best State");

            addInFrontier(progressedState,h, node, frontier, explored);

            // Processa os demais estados resultantes da heurística
            if(!heuristicQueue.isEmpty()){
                System.out.println(">Other States of Queue to add("+heuristicQueue.size()+")" );
                while (!heuristicQueue.isEmpty()){
                    Pair p = heuristicQueue.poll();
                    h = p.getHeuristic();
                    addInFrontier(p.getBdd(),h, node, frontier, explored);
                }
            }

            count++;
            System.out.println(frontier.getSizeSummary());
            System.out.println(explored.getSizeSummary());
            //encerra se passar do tempo maximo
            if(metric.verifyBreak()) {
                printSummary(metric);
                return true;
            }
        }
        printSummary(metric);
        clearBdds(frontier, explored);
        return false;
    }

    private void clearBdds(FrontierQueue frontier, ExploredVector explored){
        System.out.println("Clear frontier & explored");
        frontier.clear();
        explored.clear();
    }

    private void addInFrontier(BDD progressed, int h, Node parent, FrontierQueue frontier, ExploredVector explored) {
        if (progressed == null || progressed.isZero()) return;
        Node newNode = new Node(progressed, parent.getG()+1,h, parent);
        if (!explored.contains(newNode) || !frontier.contains(newNode)) {
            frontier.add(newNode);
            System.out.println(">Add Frontier: "+newNode.getName());
        } else if (frontier.replace(newNode)) {
            System.out.println(">Replace Frontier: "+newNode.getName());
        }
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

    public Node result(){
        return result;
    }


}
