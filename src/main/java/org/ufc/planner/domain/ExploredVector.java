package org.ufc.planner.domain;

import com.github.javabdd.BDD;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Conjunto de estados explorados durante a busca.
 * Armazena os BDDs e seus hashes para consulta eficiente.
 */
public class ExploredVector {
    private final Vector<BDD> list;
    private final Set<Integer> hashIndex;

    public ExploredVector() {
        this.list = new Vector<>();
        this.hashIndex = new HashSet<>();
    }

    public boolean add(BDD bdd) {
        int hash = bdd.hashCode();
        if (hashIndex.contains(hash)) return false;
        list.add(bdd);
        hashIndex.add(hash);
        return true;
    }

    public boolean contains(BDD bdd) {
        return hashIndex.contains(bdd.hashCode());
    }

    public int size() {
        return list.size();
    }

    public Vector<BDD> getRawList() {
        return list;
    }

    public void clear() {
        list.clear();
        hashIndex.clear();
    }
}