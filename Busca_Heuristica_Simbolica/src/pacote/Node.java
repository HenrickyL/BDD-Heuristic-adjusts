package pacote;

import java.util.Comparator;
import net.sf.javabdd.BDD;


public class Node {
    BDD bdd;
    BDD father;
    int fn; //f(n) = g(n) + h(n)

    public Node(BDD bdd, BDD father, int fCost) {
        this.bdd = bdd;
        this.fn = fCost; 
        this.father = father;
    }

    public int getFValue() {
        return this.fn;
    }
    
}

class NodeComparator implements Comparator<Node> {
    public int compare(Node b1, Node b2) {
        return Integer.compare(b1.getFValue(), b2.getFValue());
    }
}