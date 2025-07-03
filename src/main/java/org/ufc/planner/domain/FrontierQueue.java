package org.ufc.planner.domain;


import com.github.javabdd.BDD;
import java.util.*;
/**
 * Estrutura de apoio para a busca A* com BDDs.
 * Combina uma PriorityQueue para ordenação por f(n)
 * e um HashSet para verificação rápida de duplicatas.
 */
public class FrontierQueue {
    private final PriorityQueue<Node> queue;
    private final Set<Node> nodeIndex;

    /// TODO: Remove this - debug
    // cálculo da média móvel
    private double movingAverage = 0;
    private int sampleCount = 0;
    private static final int MAX_SAMPLES = 150; // Número de amostras para a média


    public FrontierQueue() {
        this.queue = new PriorityQueue<>(new NodeComparator());
        this.nodeIndex = new HashSet<>();
    }

    public boolean add(Node node) {
        int hash = node.getBDD().hashCode();
        if (nodeIndex.contains(hash)) return false;

        /// TODO: Remove this - debug
        // Atualiza a média móvel (amostra aleatoriamente para evitar overhead)
        if (sampleCount < MAX_SAMPLES || Math.random() < 0.1) {
            updateMovingAverage(node.getBDD().nodeCount());
        }

        queue.add(node);
        nodeIndex.add(node);
        return true;
    }

    public Node poll() {
        Node node = queue.poll();
        if (node != null) {
            nodeIndex.remove(node.getBDD().hashCode());
        }
        return node;
    }

    public boolean contains(Node node) {
        return nodeIndex.contains(node);
    }

    public boolean replace(Node node) {
        if (!this.contains(node)) return false;

        Iterator<Node> it = queue.iterator();
        while (it.hasNext()) {
            Node current = it.next();
            if (current.equals(node)) {
                // só substitui se o novo custo for menor
                if (node.getFCost() < current.getFCost()) {
                    it.remove();
                    nodeIndex.remove(current);
                    queue.add(node);
                    nodeIndex.add(node);
                    return true;
                } else {
                    // não substitui, mantém o atual
                    return false;
                }
            }
        }
        return false;
    }
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        // Libera todos os BDDs armazenados no explored
        for (Node node : queue) {
            node.getBDD().free();
        }
        queue.clear();
        nodeIndex.clear();
    }

    public int getNodeCount() {
        return queue.size();
    }
    public int getHashCount() {
        return nodeIndex.size();
    }

//    public long getEstimatedMemoryUsageInBytes() {
//        return (long) getNodeCount() * estimatedNodeSizeBytes() + (long) getHashCount() * Integer.BYTES;
//    }

    public String getSizeSummary() {
        return String.format(
                "[Frontier] Nodes: %d, Hashes: %d, Est. Memory: %.2f KB",
                getNodeCount(),
                getHashCount(),
                getEstimatedMemoryUsageInBytes() / 1024.0
        );
    }


    public long getEstimatedMemoryUsageInBytes() {
        int estimatedNodesPerBDD = (int) Math.ceil(movingAverage);
        long hashSize = (long) getHashCount() * Integer.BYTES;
        long queueSize = (long) getNodeCount() * (estimatedNodesPerBDD * 20L + 80);

        return  queueSize + hashSize;
    }

    private void updateMovingAverage(int newNodeSize) {
        if (sampleCount < MAX_SAMPLES) {
            movingAverage = (movingAverage * sampleCount + newNodeSize) / (sampleCount + 1);
            sampleCount++;
        } else {
            // Média móvel exponencial
            movingAverage = movingAverage * 0.9 + newNodeSize * 0.1;
        }
    }

}
