package org.ufc.planner.domain;

import com.github.javabdd.BDD;

public class Pair {
    private final BDD _bdd;
    private final int _heuristic;

    public Pair(BDD bdd, int heuristic) {
        _bdd = bdd;
        _heuristic = heuristic;
    }

    public BDD getBdd() {
        return _bdd;
    }
    public int getHeuristic() {
        return _heuristic;
    }
}
