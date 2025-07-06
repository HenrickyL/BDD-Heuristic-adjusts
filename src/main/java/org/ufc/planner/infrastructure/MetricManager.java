package org.ufc.planner.infrastructure;

import org.ufc.planner.domain.Node;

public class MetricManager {
    private final TimeMetricTracker timeTracker;
    private final MemoryMetricTracker memoryTracker;
    public MetricManager(Runtime runtime, boolean debug) {
        this.timeTracker = new TimeMetricTracker(debug);
        this.memoryTracker = new MemoryMetricTracker(runtime);
    }

    public MetricManager(TimeMetricTracker timeTracker, MemoryMetricTracker memoryTracker) {
        this.timeTracker = timeTracker;
        this.memoryTracker = memoryTracker;
    }

    public void start() {
        timeTracker.start();
        memoryTracker.start();
        Node.resetCount(); // reinicia contador de nós
    }

    public void resetStartTime() {
        timeTracker.reset();
    }
    public void reset(){
        resetStartTime();
        memoryTracker.reset();
    }

    public void printElapsedTime() {
        timeTracker.printElapsed();
    }

    public void printElapsedMemory() {
        memoryTracker.printElapsed();
    }

    public void printSummary(){
        System.out.println("SUMMARY:");
        printElapsedTime();
        printElapsedMemory();
    }

    public boolean verifyBreak() {
        if (timeTracker.isDebug()) return false;
        printElapsedTime();
        if (onTime()) {
            System.out.println("-🛑 [BREAK] exceeded max time - "+ getMaxTime() + " ms");
            return true;
        }
        return false;
    }

    public long getMaxTime(){
        return this.timeTracker.getMaxTime();
    }

    public boolean onTime() {
        return timeTracker.checkLimitExceeded();
    }
    public void setMaxTime(long time) {
        timeTracker.setMaxTime(time);
    }

    public void clear(){
        memoryTracker.clear();
    }
}
