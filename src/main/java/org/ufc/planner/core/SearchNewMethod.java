package org.ufc.planner.core;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;
import org.ufc.planner.domain.ModelAction;
import org.ufc.planner.domain.Node;
import org.ufc.planner.domain.NodeComparator;
import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
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

            SimpleEntry<BDD,Integer> heuristic = getMinHeuristic(heuristicValue, current);

            BDD filterBDD = heuristic.getKey();
            h = heuristic.getValue();
            String nameFiltered = "*s"+g+"_h:"+h;

            System.out.println(">And Heustistic Vector: "+ nameFiltered + " with h: "+h);
            if(h == -1){
                continue;
            }
            BDD progressedState = progression(filterBDD, verify); //Z = progression(teste);
            System.out.println(">progressedState: "+progressedState);

            f = g + h;
            System.out.println(">f= "+g+"+"+h+"="+f);

            if( !IsThereInExplored(explored, progressedState) ||
                    !ExistInFrontier(frontier, progressedState))
            {
                Node newNode = new Node(progressedState, node, f, nameFiltered);
                frontier.add(newNode);
//                frontier.add(new Node(progressedState, current, f, nameCurrent));
                System.out.println(">Add Frontier");

            }else if(ExistInFrontier(frontier, progressedState)) {
                System.out.println(">Replace Frontier");
                Node replaced = new Node(progressedState, node, f);
                ReplaceElementInFrontier(frontier, replaced);
                //se encontrar um bdd com mesmo valor de f e g
                //devo juntar eles
                //UpdateFrontier(frontier, teste, f); //pensar melhor
            }
//                    frontier.add(child);
//            for (ModelAction a : actionSet) {
//                childBdd = progressionQbf(current,a);
//                if(childBdd.toString().equals("")) {
//                    continue; // acao nao aplicavel ao estado current
//                }
//                //test = test.and(constraints);
//                h = minHvalue2(heuristicValue, childBdd); // if not -1
//                System.out.println("h:"+ h+" | "+ a.getName());
//
//                if(h == -1){ continue; }
//
//                int f = g + h;
//                if( !IsThereInExplored(explored, childBdd) ||
//                        !ExistInFrontier(frontier, childBdd))
//                {
//                    child = new Node(childBdd, current, f);
//                    frontier.add(child);
//                }else if(ExistInFrontier(frontier, childBdd)) {
//                    Node replaced = new Node(childBdd, current, f);
//                    ReplaceElementInFrontier(frontier, replaced);
//                    //se encontrar um bdd com mesmo valor de f e g
//                    //devo juntar eles
//                    //UpdateFrontier(frontier, teste, f); //pensar melhor
//                }
//            }
            g++;
            System.out.println("g="+g);
            verify.PrintElapsedTime();
        }
        return false;
    }


    private SimpleEntry<BDD, Integer> getMinHeuristic(Vector<BDD> H, BDD X) {
        BDD result;
        for (int i = 0; i < H.size(); i++) {
            result = X.and(H.get(i));
            //System.out.println("i: "+i);
            //H.get(i).printSet();
            if (!result.isZero()) {
                return new SimpleEntry<>(result, i);
            }
        }
        return  new SimpleEntry<>(null, -1);
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
            test = regressionQbf(formula,a);//heuristicRegressionQbf(formula,a) - henricky;
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

    private BDD regressionQbf(BDD Y, ModelAction action) {
        BDD reg;
//        reg = Y.and(action.getEffect()); //(Y ^ effect(a))
        reg = Y.and(action.getRelaxEffect()); //(Y ^ effect(a))

        if(reg.isZero() == false){
//			System.out.println("Ação aplicável: " + a.getName());
            reg = reg.exist(action.getChange()); //qbf computation
            reg = reg.and(action.getPrecondition()); //precondition(a) ^ E changes(a). test
            reg = reg.and(constraints);
        }
        return  reg;
    }

    private boolean IsThereInExplored(Vector<BDD> explored, BDD item) {
        for(BDD bdd : explored) {
            if(bdd == item) {
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

    private boolean ExistInFrontier(PriorityQueue<Node> frontier, BDD item) {
        Iterator<Node> iterator = frontier.iterator();
        while (iterator.hasNext()) {
            Node current = iterator.next();
            if (current.getBDD().equals(item)) {
                return true;
            }
        }
        return false;
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
