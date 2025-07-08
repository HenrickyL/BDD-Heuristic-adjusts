package org.ufc.planner.infrastructure;

import org.ufc.planner.interfaces.IMetricTracker;

public class MemoryMetricTracker implements IMetricTracker {
    private final Runtime runtime;
    private final long initialTotalMemory;
    private long startMemory;

    public MemoryMetricTracker(Runtime runtime) {
        this.runtime = runtime;
        this.initialTotalMemory = runtime.totalMemory();  // alocação inicial da JVM
        this.startMemory = 0;
    }

    @Override
    public void start() {
        runtime.gc();
        startMemory = usedMemory();
    }

    @Override
    public void reset() {
        try {
            Thread.sleep(100); // permite tempo para o GC rodar
        } catch (InterruptedException ignored) {}
        start();
    }

    @Override
    public long elapsed() {
        return usedMemory() - startMemory;
    }

    @Override
    public void printElapsed() {
        double mb = elapsed() / (1024.0 * 1024.0);
        System.out.printf("-💾 [MEMORY] Elapsed: %.3f MB%n", mb);
    }

    @Override
    public boolean checkLimitExceeded() {
        // futuro: implementar limites
        return false;
    }

    /** Memória usada atualmente sem forçar GC */
    public long currentUsed() {
        return usedMemory();
    }

    /** Diferença entre o uso atual e o total inicial da JVM */
    public long deltaToInitial() {
        return usedMemory() - initialTotalMemory;
    }

    /** Retorna memória usada: total - livre */
    private long usedMemory() {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public void clear(){
       System.out.println("💾 Clear: call garbage collector");
        runtime.gc();
    }
}
