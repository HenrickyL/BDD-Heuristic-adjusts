package org.ufc.planner.domain;

import com.github.javabdd.BDD;
import java.util.HashMap;
import java.util.Map;
/**
 * Estrutura de memória para evitar redundâncias e melhorar controle de estados.
 */
public class SearchMemory {
    private final Map<Integer, BDD> memory = new HashMap<>();

    /// TODO: remove this - debug
    // Estatísticas de uso de memória
    private double movingAverage = 0;
    private int sampleCount = 0;
    private static final int MAX_SAMPLES = 150;


    public boolean register(BDD bdd) {
        int hash = bdd.hashCode();
        if (memory.containsKey(hash)) return false;

        /// TODO: remove this - debug
        // Atualiza a média móvel com o tamanho do BDD
        if (sampleCount < MAX_SAMPLES || Math.random() < 0.1) {
            updateMovingAverage(bdd.nodeCount());
        }

        memory.put(hash, bdd);
        return true;
    }

    public boolean contains(BDD bdd) {
        return memory.containsKey(bdd.hashCode());
    }

    public BDD registerOrGet(BDD bdd) {
        int hash = bdd.hashCode();
        if (memory.containsKey(hash)) {
            bdd.free(); // o que foi passado não será usado
            return memory.get(hash);
        } else {
            memory.put(hash, bdd);
            return bdd;
        }
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

//    public long getEstimatedMemoryUsageInBytes(double averageNodeCount) {
//        return size() * (long)(averageNodeCount * 20); // 20 bytes por nó do BDD
//    }

    public String getSizeSummary() {
        return String.format(
                "[Memory] States: %d, Est. Mem: %.2f KB",
                size(),
                getEstimatedMemoryUsageInBytes() / 1024.0
        );
    }

    public long getEstimatedMemoryUsageInBytes() {
        int estimatedNodesPerBDD = (int) Math.ceil(movingAverage);
//        long bddMemory = (long) list.size() * (estimatedNodesPerBDD * 20L); // 20B por nó
        long hashMemory = (long) size() * (estimatedNodesPerBDD * 20L);
        return /*bddMemory +*/ hashMemory;
    }

    private void updateMovingAverage(int newNodeSize) {
        if (sampleCount < MAX_SAMPLES) {
            movingAverage = (movingAverage * sampleCount + newNodeSize) / (sampleCount + 1);
            sampleCount++;
        } else {
            movingAverage = movingAverage * 0.9 + newNodeSize * 0.1;
        }
    }
}