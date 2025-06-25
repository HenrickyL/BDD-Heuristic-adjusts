package org.ufc.planner.core;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;
import org.ufc.planner.domain.ModelAction;
import org.ufc.planner.domain.Node;
import org.ufc.planner.domain.NodeComparator;
import org.ufc.planner.domain.Pair;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;

import java.util.*;
import java.io.IOException;

public class SearchNewMethod extends BaseSearch{
    private boolean onHeuristicPlanBackwardHasIncomplateRegression = false;

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

        Node node = new Node(initial, null, 0+ heuristicValue.size());// cost = 0 + heuristic
        PriorityQueue<Node> frontier = new PriorityQueue<>(new NodeComparator());
        Vector<BDD> explored = new Vector<BDD>();

        BDD aux;
        BDD current;

        int g=0;
        int h=0;
        int f=0;

        frontier.add(node);

        while(!frontier.isEmpty()) {
            node = frontier.poll();
            current = node.getBDD();
            System.out.println(">Dequeue: "+node.getName());
            String nameCurrent = "s"+g;

            aux = current.and(goal.id());
            if (aux.toString().equals("") == false) { //use equal?
                System.out.println("The problem is solvable.");
                return true;
            }
            aux.free();

            explored.add(current);

            //Queue com os elementos que deram match com o estado atual
            Queue<Pair> heuristicQueue = getMinHeuristic(heuristicValue, current);
            System.out.println(">Pairs Heuristic Queue with size: "+heuristicQueue.size());

            if(heuristicQueue.isEmpty()) continue;;

            //pego o melhor
            Pair bestHeuristic = heuristicQueue.poll();
            BDD state = bestHeuristic.getBdd();
            h = bestHeuristic.getHeuristic();
            System.out.println(">best heuristic (And Vector): "+ "_" + " with h: "+h);

            BDD progressedState = progression(state, verify); //Z = progression(teste);
            System.out.println(">Progress Best State");
//          System.out.println(">progressedState: "+progressedState);
            addInFrontier(new Pair(progressedState,h), g, node, frontier, explored);

            if(!heuristicQueue.isEmpty()){
                System.out.println(">Other States of Queue to add("+heuristicQueue.size()+")" );
            }
            while (!heuristicQueue.isEmpty()){
                Pair p = heuristicQueue.poll();
                addInFrontier(p, g, node, frontier, explored);
            }
            g++;
            System.out.println("g="+g);
            //encerra se passar do tempo
            if(verify.verifyBreak()) {
                return true;
            }
        }
        return false;
    }

    private void addInFrontier( Pair pair, int g, Node father, PriorityQueue<Node> frontier, Vector<BDD> explored){
        BDD state = pair.getBdd();
        int h = pair.getHeuristic();
        int f = g+h;
        if( !isThereInExplored(explored, state) ||
                !existInFrontier(frontier, state))
        {
            Node newNode = new Node(state, father, f);
            frontier.add(newNode);
            System.out.println(">Add Frontier:" + newNode.getName());
        }else if(existInFrontier(frontier, state)) {
            System.out.println(">Replace Frontier");
            Node replaced = new Node(state, father, f);
            ReplaceElementInFrontier(frontier, replaced);
            //se encontrar um bdd com mesmo valor de f e g
            //devo juntar eles
            //UpdateFrontier(frontier, teste, f); //pensar melhor
        }
        System.out.println(">f= "+g+"+"+h+"="+f);
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
            if (!matched.isZero()) {
                queue.add(new Pair(matched, i));
                rest = rest.and(matched.not());
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

    /* Propplan progression based on action: Qbf based computation */
    public BDD progressionQbf(BDD Y, ModelAction a) {
        BDD reg;
        reg = Y.and(a.getPrecondition()); //(Y ^ effect(a))

        if(reg.isZero() == false){
//			System.out.println("Ação aplicável: " + a.getName());
            reg = reg.exist(a.getChange()); //qbf computation
            reg = reg.and(a.getEffect()); //precondition(a) ^ E changes(a). test
            //reg = reg.and(constraints);
        }
        return  reg;
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

    private boolean isThereInExplored(Vector<BDD> explored, BDD item) {
        for(BDD bdd : explored) {
            if(bdd == item) {
                return true;
            }
        }
        return false;
    }

    private boolean existInFrontier(PriorityQueue<Node> frontier, BDD item) {
        Iterator<Node> iterator = frontier.iterator();
        while (iterator.hasNext()) {
            Node current = iterator.next();
            if (current.getBDD().equals(item)) {
                return true;
            }
        }
        return false;
    }

    private void ReplaceElementInFrontier(PriorityQueue<Node> frontier, Node element) {
        boolean found = false;
        Iterator<Node> iterator = frontier.iterator();

        while (iterator.hasNext()) {
            Node current = iterator.next();
            if (current.getBDD() == element.getBDD()) {
                iterator.remove();
                found = true;
                break;
            }
        }

        if (found) {
            frontier.add(element);
        }
    }




    private boolean GBFS(TimeManager verify){
        System.out.println("GBFS...");
        BDD initial = initialState.id();
        Node node = new Node(initial, null, 0+ heuristicValue.size());// cost = 0 + heuristic
        PriorityQueue<Node> frontier = new PriorityQueue<>(new NodeComparator());
        Vector<BDD> explored = new Vector<BDD>();

        BDD aux;
        BDD current;
        BDD childBdd;

        Node child;
        int g=1;
        int h=0;

        frontier.add(node);
        while(!frontier.isEmpty()) {
            node = frontier.poll();
            current = node.getBDD();
        }

        return false;
    }
}
