package org.ufc.planner.domain;

import com.github.javabdd.BDD;

public class Node {
    private BDD bdd;
    private BDD father;
    private int fn; //f(n) = g(n) + h(n)

    public Node(BDD bdd, BDD father, int fCost) {
        this.bdd = bdd;
        this.fn = fCost;
        this.father = father;
    }

    public int getFValue() {
        return this.fn;
    }


    //getter e setter
    public BDD getFather() {
        return father;
    }

    public void setFather(BDD father) {
        this.father = father;
    }

    public BDD getBDD() {
        return bdd;
    }

    public void setBDD(BDD father) {
        this.bdd = father;
    }

    public int getFn(){
        return this.fn;
    }

}
