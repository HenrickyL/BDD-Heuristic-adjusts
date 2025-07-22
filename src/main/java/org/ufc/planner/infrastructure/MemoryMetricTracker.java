package org.ufc.planner.infrastructure;

import org.ufc.planner.interfaces.IMetricTracker;

public class MemoryMetricTracker implements IMetricTracker {
    private final Runtime runtime;
    private final long initialTotalMemory;
    private long startMemory;
    private static final int time = 500;

    public MemoryMetricTracker(Runtime runtime) {
        this.runtime = runtime;
        this.initialTotalMemory = runtime.totalMemory();  // alocação inicial da JVM
        this.startMemory = 0;
    }

    @Override
    public void start() {
        startMemory = usedMemory();
    }

    @Override
    public void reset() {
        runtime.gc();
        try {
            Thread.sleep(time); // permite tempo para o GC rodar
        } catch (InterruptedException ignored) {}
        start();
    }

    @Override
    public long elapsed() {
        return usedMemory() - startMemory;
    }

    @Override
    public void printElapsed() {
        // 1. Diferença desde o startMemory SEM GC - mostra alocação bruta
        double mb = elapsed() / (1024.0 * 1024.0);
        System.out.printf("-💾 [MEMORY] Allocated since start (may include garbage): %.3f MB%n", mb);
    }

    public void printActualUsage() {
        // 2. Uso atual COM GC - mostra memória realmente em uso
        runtime.gc();
        try { Thread.sleep(time); } catch (InterruptedException ignored) {}
        double mb = usedMemory() / (1024.0 * 1024.0);
        System.out.printf("-💾 [MEMORY] Actual used memory: %.3f MB%n", mb);
    }

    public void printTotalGrowth() {
        // 3. Total alocado desde início da JVM COM GC (espera mais para coleta completa)
        runtime.gc();
        try { Thread.sleep(time); } catch (InterruptedException ignored) {}
        double mb = deltaToInitial() / (1024.0 * 1024.0);
        System.out.printf("-💾 [MEMORY] JVM heap growth since start: %.3f MB%n", mb);
    }

    @Override
    public boolean checkLimitExceeded() {
        // futuro: implementar limites
        return false;
    }

    /** Diferença entre o uso atual e o total inicial da JVM */
    public long deltaToInitial() {
        return usedMemory() - (initialTotalMemory - runtime.freeMemory());
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
