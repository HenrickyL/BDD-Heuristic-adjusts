package org.ufc.planner.domain;

import com.github.javabdd.BDD;

import java.util.HashMap;
import java.util.Map;

/**
 * Gerenciador central de BDDs com contagem de referências
 */
public class BDDPool {
    private final Map<Integer, BDDEntry> bddPool = new HashMap<>();

    private static class BDDEntry {
        final BDD bdd;
        int refCount;

        BDDEntry(BDD bdd) {
            this.bdd = bdd;
            this.refCount = 0;
        }
    }

    /**
     * Registra um BDD ou retorna uma referência existente
     */
    public BDD register(BDD bdd) {
        int hash = bdd.hashCode();
        BDDEntry entry = bddPool.get(hash);

        if (entry != null) {
            entry.refCount++;
//            bdd.free(); // Libera a cópia que não será usada
            return entry.bdd; // Retorna nova referência
        } else {
            bddPool.put(hash, new BDDEntry(bdd));
            return bdd;
        }
    }

    /**
     * Libera uma referência a um BDD
     */
    public void release(BDD bdd) {
        int hash = bdd.hashCode();
        BDDEntry entry = bddPool.get(hash);

        if (entry != null) {
            entry.refCount--;
            if (entry.refCount <= 0) {
                entry.bdd.free();
                bddPool.remove(hash);
            }
        }else{
            /// check
            bdd.free();
        }
    }

    public void clear() {
        bddPool.values().forEach(e -> e.bdd.free());
        bddPool.clear();
    }
}