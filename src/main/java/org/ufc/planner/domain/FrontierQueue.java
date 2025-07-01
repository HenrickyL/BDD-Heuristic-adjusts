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
    private final Set<Integer> hashIndex;

    public FrontierQueue() {
        this.queue = new PriorityQueue<>(new NodeComparator());
        this.hashIndex = new HashSet<>();
    }

    public boolean add(Node node) {
        int hash = node.getBDD().hashCode();
        if (hashIndex.contains(hash)) return false;
        queue.add(node);
        hashIndex.add(hash);
        return true;
    }

    public Node poll() {
        Node node = queue.poll();
        if (node != null) {
            hashIndex.remove(node.getBDD().hashCode());
        }
        return node;
    }

    public boolean contains(BDD bdd) {
        return hashIndex.contains(bdd.hashCode());
    }

    public boolean replace(Node node) {
        if (!this.contains(node.getBDD()))
            return false;
        Iterator<Node> it = queue.iterator();
        while (it.hasNext()) {
            Node current = it.next();
            if (current.getBDD().equals(node.getBDD())) {
                // só substitui se o novo custo for menor
                if (node.getFValue() < current.getFValue()) {
                    it.remove();
                    hashIndex.remove(current.getBDD().hashCode());
                    queue.add(node);
                    hashIndex.add(node.getBDD().hashCode());
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

    public int size() {
        return queue.size();
    }

    public void clear() {
        // Libera todos os BDDs armazenados no explored
        for (Node node : queue) {
            node.getBDD().free();
        }
        queue.clear();
        hashIndex.clear();
    }

    public PriorityQueue<Node> getRawQueue() {
        return queue;
    }
}
