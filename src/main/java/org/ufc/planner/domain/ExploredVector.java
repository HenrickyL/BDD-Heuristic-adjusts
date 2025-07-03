package org.ufc.planner.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Conjunto de estados explorados durante a busca.
 * Armazena os BDDs e seus hashes para consulta eficiente.
 */
public class ExploredVector {
//    private final Vector<Node> list;
    private final Set<Node> nodeIndex;

    /// TODO: remove this - debug
    // Estatísticas de uso de memória
    private double movingAverage = 0;
    private int sampleCount = 0;
    private static final int MAX_SAMPLES = 150;



    public ExploredVector() {
//        this.list = new Vector<>();
        this.nodeIndex = new HashSet<>();
    }

    public boolean add(Node node) {
//        int hash = bdd.hashCode();
        if (nodeIndex.contains(node)) return false;
        /// TODO: remove this - debug
        // Atualiza a média móvel com o tamanho do BDD
        if (sampleCount < MAX_SAMPLES || Math.random() < 0.1) {
            updateMovingAverage(node.getBDD().nodeCount());
        }
//        list.add(node);
        nodeIndex.add(node);
        return true;
    }

    public boolean contains(Node node) {
        return nodeIndex.contains(node);
    }

//    public int size() {
//        return list.size();
//    }


    public void clear() {
        // Libera todos os BDDs armazenados na fila
//        for (Node node : list) {
//            node.getBDD().free();
//        }
//        list.clear();
        nodeIndex.clear();
    }

    public long getEstimatedMemoryUsageInBytes() {
        int estimatedNodesPerBDD = (int) Math.ceil(movingAverage);
//        long bddMemory = (long) list.size() * (estimatedNodesPerBDD * 20L); // 20B por nó
        long hashMemory = (long) nodeIndex.size() * (estimatedNodesPerBDD * 20L);
        System.out.println();
        return /*bddMemory +*/ hashMemory;
    }

    public String getSizeSummary() {
        return String.format(
                "[Explored] Nodes: %d, Est. Memory: %.2f KB",
//                list.size(),
                nodeIndex.size(),
                getEstimatedMemoryUsageInBytes() / 1024.0
        );
    }
    private void updateMovingAverage(int newNodeSize) {
        if (sampleCount < MAX_SAMPLES) {
            movingAverage = (movingAverage * sampleCount + newNodeSize) / (sampleCount + 1);
            sampleCount++;
        } else {
            movingAverage = movingAverage * 0.9 + newNodeSize * 0.1;
        }
    }

//    public String getSizeSummary() {
//        return "[Explored] BDDs: " + getBDDCount() + ", Hashes: " + getHashCount();
//    }
}