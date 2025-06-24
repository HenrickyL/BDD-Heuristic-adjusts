package org.ufc.planner.domain;

import com.github.javabdd.BDD;

public class Node {
    private final BDD bdd;
    private final Node father;
    private final int fn; //f(n) = g(n) + h(n)
    private final String name;

    public Node(BDD bdd, Node father, int fCost) {
        this.bdd = bdd;
        this.fn = fCost;
        this.father = father;
        this.name = "s"+count;
        Node.count++;
    }

    public Node(BDD bdd, Node father, int fCost, String name) {
        this.bdd = bdd;
        this.fn = fCost;
        this.father = father;
        this.name = name;
    }

    public static int count =0;
    public static void resetCount(){Node.count = 0;}

    //getter
    public Node getFather() {
        return father;
    }
    public BDD getBDD() {
        return bdd;
    }
    public int getFn(){
        return this.fn;
    }
    public int getFValue() {
        return this.fn;
    }
    public String getName(){return this.name;}
}
