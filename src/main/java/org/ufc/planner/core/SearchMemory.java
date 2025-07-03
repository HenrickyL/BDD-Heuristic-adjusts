package org.ufc.planner.core;

import com.github.javabdd.BDD;
import java.util.HashMap;
import java.util.Map;
/**
 * Estrutura de memória para evitar redundâncias e melhorar controle de estados.
 */
public class SearchMemory {
    private final Map<Integer, BDD> memory = new HashMap<>();

    public boolean register(BDD bdd) {
        int hash = bdd.hashCode();
        if (memory.containsKey(hash)) return false;
        memory.put(hash, bdd);
        return true;
    }

    public boolean contains(BDD bdd) {
        return memory.containsKey(bdd.hashCode());
    }

    public void clear() {
        for (BDD bdd : memory.values()) {
            bdd.free();
        }
        memory.clear();
    }

    public int size() {
        return memory.size();
    }
}