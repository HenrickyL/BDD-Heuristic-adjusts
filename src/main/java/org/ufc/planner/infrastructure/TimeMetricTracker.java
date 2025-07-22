package org.ufc.planner.infrastructure;

import org.ufc.planner.interfaces.IMetricTracker;

public class TimeMetricTracker implements IMetricTracker {
    private long startTime;
    private long maxTime;
    private final boolean debug;

    public TimeMetricTracker(boolean debug) {
        this.debug = debug;
    }

    public TimeMetricTracker(long maxTime) {
        this.maxTime = maxTime;
        this.debug = false;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void reset(){
        start();
    }

    @Override
    public long elapsed(){
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public void printElapsed() {
        System.out.println("-⏱️ [TIME] Elapsed: " + elapsed() + " ms");
    }

    @Override
    public boolean checkLimitExceeded() {
        if (debug) return false;
        long now = System.currentTimeMillis();
        boolean exceeded = (now - startTime) >= maxTime;
        return exceeded;
    }

    public void setMaxTime(long time){
        this.maxTime = time;
    }

    public long getMaxTime(){
        return this.maxTime;
    }
    public boolean isDebug(){
        return debug;
    }
}
