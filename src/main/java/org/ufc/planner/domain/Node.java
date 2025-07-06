package org.ufc.planner.domain;

import com.github.javabdd.BDD;

public class Node{
    private final BDD bdd;
    private final Node father;
    private final int fn; //f(n) = g(n) + h(n)
    private final String name;

    public Node(BDD bdd,int fCost, Node father) {
        this.bdd = bdd;
        this.fn = fCost;
        this.father = father;
        this.name = "s"+count;
        Node.count++;
    }

    public Node(BDD bdd,int fCost) {
        this.bdd = bdd;
        this.fn = fCost;
        this.father = null;
        this.name = "s"+count;
        Node.count++;
    }

    public Node(BDD bdd,int fCost, Node father, String name) {
        this.bdd = bdd;
        this.fn = fCost;
        this.father = father;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node other = (Node) o;
        BDD A = this.getBDD();
        BDD B = other.getBDD();
        return A.equals(B) && this.getFn() == other.getFn();
    }

    @Override
    public int hashCode() {
        int result = getBDD().hashCode();
        result = 31 * result + getFn(); // 31 é uma constante comum no hashCode
        return result;
    }

    public static int count =0;
    public static void resetCount(){Node.count = 0;}
    public static int nodeCount(){ return Node.count; }

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
    public int getFCost() {
        return this.fn;
    }
    public String getName(){return this.name;}
}
