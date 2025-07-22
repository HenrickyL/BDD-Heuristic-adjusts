package org.ufc.planner.domain;


import java.util.*;
/**
 * Estrutura de apoio para a busca A* com BDDs.
 * Combina uma PriorityQueue para ordenação por f(n)
 * e um HashSet para verificação rápida de duplicatas.
 */
public class FrontierQueue {
    private final PriorityQueue<Node> queue;
    private final Set<Integer> index;

    /// TODO: Remove this - debug
    // cálculo da média móvel
    private double movingAverage = 0;
    private int sampleCount = 0;
    private static final int MAX_SAMPLES = 150; // Número de amostras para a média


    public FrontierQueue() {
        this.queue = new PriorityQueue<>(new NodeComparator());
        this.index = new HashSet<>();
    }

    public boolean add(Node node) {
        int hash = node.hashCode();
        if (index.contains(hash)) return false;

        /// TODO: Remove this - debug
        // Atualiza a média móvel (amostra aleatoriamente para evitar overhead)
        if (sampleCount < MAX_SAMPLES || Math.random() < 0.1) {
            updateMovingAverage(node.getBDD().nodeCount());
        }

        queue.add(node);
        index.add(hash);
        return true;
    }

    public Node poll() {
        Node node = queue.poll();
        if (node != null) {
            index.remove(node.hashCode());
        }
        return node;
    }

    public boolean contains(Node node) {
        return index.contains(node.hashCode());
    }

    public boolean replace(Node node) {
        if(this.contains(node)){
            Iterator<Node> it = queue.iterator();
            while (it.hasNext()) {
                Node current = it.next();
                int hash = node.hashCode();
                if (current.equals(node)) {
                    // só substitui se o novo custo for menor
                    if (node.getFn() < current.getFn()) {
                        it.remove();
                        index.remove(hash);
                        queue.add(node);
                        index.add(hash);
                        return true;
                    } else {
                        // não substitui, mantém o atual
                        return false;
                    }
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
        index.clear();
    }

    public int getNodeCount() {
        return queue.size();
    }
    public int getIndexCount() {
        return index.size();
    }

//    public long getEstimatedMemoryUsageInBytes() {
//        return (long) getNodeCount() * estimatedNodeSizeBytes() + (long) getHashCount() * Integer.BYTES;
//    }

    public String getSizeSummary() {
        return String.format(
                "[Frontier] Nodes: %d, Est.Memory: %.2f KB | nodeIndex: %d, Est.Memory: %.2f KB",
                getNodeCount(),
                getEstimatedQueueMemoryUsageInBytes() / 1024.0,
                getIndexCount(),
                getEstimatedIIndexMemoryUsageInBytes() / 1024.0
        );
    }


    public long getEstimatedQueueMemoryUsageInBytes() {
        int estimatedNodesPerBDD = (int) Math.ceil(movingAverage);
        long estimated = (estimatedNodesPerBDD * 20L + 80);
        long queueSize = (long) getNodeCount() * estimated;
        return  queueSize;
    }
    public long getEstimatedIIndexMemoryUsageInBytes() {
        int estimatedNodesPerBDD = (int) Math.ceil(movingAverage);
        long estimated = Integer.BYTES;
        long indexSize = (long) getIndexCount() * estimated;
        return  indexSize;
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
