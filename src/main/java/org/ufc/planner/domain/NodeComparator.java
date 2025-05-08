package org.ufc.planner.domain;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    public int compare(Node b1, Node b2) {
        return Integer.compare(b1.getFValue(), b2.getFValue());
    }
}
